/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/

package org.openiot.gsn;

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.http.rest.DeliverySystem;
import org.openiot.gsn.http.rest.DistributionRequest;
import org.openiot.gsn.storage.DataEnumerator;
import org.openiot.gsn.storage.SQLValidator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.openiot.gsn.storage.StorageManager;
import org.apache.log4j.Logger;

public class DataDistributer implements VirtualSensorDataListener, VSensorStateChangeListener, Runnable {

    public static final int KEEP_ALIVE_PERIOD =  15 * 1000;  // 15 sec.

    private static int keepAlivePeriod = -1;

    private javax.swing.Timer keepAliveTimer = null;

    private static transient Logger logger = Logger.getLogger(DataDistributer.class);

    private static HashMap<Class<? extends DeliverySystem>, DataDistributer> singletonMap = new HashMap<Class<? extends DeliverySystem>, DataDistributer>();
    private Thread thread;

    private DataDistributer() {
        try {
            //conn = Main.getStorage().getConnection();
            thread = new Thread(this);
            thread.start();
            // Start the keep alive Timer -- Note that the implementation is backed by one single thread for all the RestDelivery instances.
            keepAliveTimer = new  javax.swing.Timer(getKeepAlivePeriod(), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // write the keep alive message to the stream
                    synchronized (listeners) {
                        ArrayList<DistributionRequest> clisteners = (ArrayList<DistributionRequest>) listeners.clone();
                        for (DistributionRequest listener : clisteners) {
                            if ( ! listener.deliverKeepAliveMessage()) {
                                logger.debug("remove the listener.");
                                removeListener(listener);
                            }
                        }
                    }
                }
            });
            keepAliveTimer.start();
        //} catch (SQLException e) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DataDistributer getInstance(Class<? extends DeliverySystem> c) {
        DataDistributer toReturn = singletonMap.get(c);
        if (toReturn == null)
            singletonMap.put(c, (toReturn = new DataDistributer()));
        return toReturn;
    }

    public static int getKeepAlivePeriod() {
        if (keepAlivePeriod == -1)
            keepAlivePeriod = System.getProperty("remoteKeepAlivePeriod") == null ? KEEP_ALIVE_PERIOD : Integer.parseInt(System.getProperty("remoteKeepAlivePeriod"));
        return keepAlivePeriod;
    }

    private HashMap<DistributionRequest, PreparedStatement> preparedStatements = new HashMap<DistributionRequest, PreparedStatement>();

    private ArrayList<DistributionRequest> listeners = new ArrayList<DistributionRequest>();

    //private Connection conn;

    private ConcurrentHashMap<DistributionRequest, DataEnumerator> candidateListeners = new ConcurrentHashMap<DistributionRequest, DataEnumerator>();

    private LinkedBlockingQueue<DistributionRequest> locker = new LinkedBlockingQueue<DistributionRequest>();

    private ConcurrentHashMap<DistributionRequest, Boolean> candidatesForNextRound = new ConcurrentHashMap<DistributionRequest, Boolean>();

    public void addListener(DistributionRequest listener) {
        synchronized (listeners) {
            if (!listeners.contains(listener)) {
                logger.warn("Adding a listener to Distributer:" + listener.toString());
                boolean needsAnd = SQLValidator.removeSingleQuotes(SQLValidator.removeQuotes(listener.getQuery())).indexOf(" where ") > 0;
                String query = SQLValidator.addPkField(listener.getQuery());
                if (needsAnd)
                    query += " AND ";
                else
                    query += " WHERE ";
                query += " timed > " + listener.getStartTime() + " and pk > ? order by timed asc ";
                PreparedStatement prepareStatement = null;
                try {
                    prepareStatement = getPersistantConnection(listener.getVSensorConfig()).prepareStatement(query); //prepareStatement = StorageManager.getInstance().getConnection().prepareStatement(query);
                    prepareStatement.setMaxRows(1000); // Limit the number of rows loaded in memory.
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                preparedStatements.put(listener, prepareStatement);
                listeners.add(listener);
                addListenerToCandidates(listener);

            } else {
                logger.warn("Adding a listener to Distributer failed, duplicated listener! " + listener.toString());
            }
        }
    }


    private void addListenerToCandidates(DistributionRequest listener) {
        /**
         * Locker variable should be modified EXACTLY like candidateListeners variable.
         */
        logger.debug("Adding the listener: " + listener.toString() + " to the candidates.");
        DataEnumerator dataEnum = makeDataEnum(listener);
        if (dataEnum.hasMoreElements()) {
            candidateListeners.put(listener, dataEnum);
            locker.add(listener);
        }
    }

    private void removeListenerFromCandidates(DistributionRequest listener) {
        /**
         * Locker variable should be modified EXACTLY like candidateListeners variable.
         */
        logger.debug("Updating the candidate list [" + listener.toString() + " (removed)].");
        if (candidatesForNextRound.contains(listener)) {
            candidateListeners.put(listener, makeDataEnum(listener));
            candidatesForNextRound.remove(listener);
        } else {
            locker.remove(listener);
            candidateListeners.remove(listener);
        }
    }

    /**
     * This method only flushes one single stream element from the provided data enumerator.
     * Returns false if the flushing the stream element fails. This method also cleans the prepared statements by removing the listener completely.
     *
     * @param dataEnum
     * @param listener
     * @return
     */
    private boolean flushStreamElement(DataEnumerator dataEnum, DistributionRequest listener) {
        if (listener.isClosed()) {
            logger.debug("Flushing an stream element failed, isClosed=true [Listener: " + listener.toString() + "]");
            return false;
        }

        if (!dataEnum.hasMoreElements()) {
            logger.debug("Nothing to flush to [Listener: " + listener.toString() + "]");
            return true;
        }

        StreamElement se = dataEnum.nextElement();
        //		boolean success = true;
        boolean success = listener.deliverStreamElement(se);
        if (!success) {
            logger.debug("FLushing an stream element failed, delivery failure [Listener: " + listener.toString() + "]");
            return false;
        }
        logger.debug("Flushing an stream element succeed [Listener: " + listener.toString() + "]");
        return true;
    }

    public void removeListener(DistributionRequest listener) {
        synchronized (listeners) {
            if (listeners.remove(listener)) {
                try {
                    candidatesForNextRound.remove(listener);
                    removeListenerFromCandidates(listener);
                    preparedStatements.get(listener).close();
                    listener.close();
                    logger.warn("Removing listener completely from Distributer [Listener: " + listener.toString() + "]");
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    preparedStatements.remove(listener);
                }
            }
        }
    }

    public void consume(StreamElement se, VSensorConfig config) {
        synchronized (listeners) {
            for (DistributionRequest listener : listeners)
                if (listener.getVSensorConfig() == config) {
                    logger.debug("sending stream element " + (se == null ? "second-chance-se" : se.toString()) + " produced by " + config.getName() + " to listener =>" + listener.toString());
                    if (!candidateListeners.containsKey(listener)) {
                        addListenerToCandidates(listener);
                    } else {
                        candidatesForNextRound.put(listener, Boolean.TRUE);
                    }
                }
        }
    }

    public void run() {
        while (true) {
            try {
                if (locker.isEmpty()) {
                    logger.debug("Waiting(locked) for requests or data items, Number of total listeners: " + listeners.size());
                    locker.put(locker.take());
                    logger.debug("Lock released, trying to find interest listeners (total listeners:" + listeners.size() + ")");
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }


            for (Entry<DistributionRequest, DataEnumerator> item : candidateListeners.entrySet()) {
                boolean success = flushStreamElement(item.getValue(), item.getKey());
                if (success == false)
                    removeListener(item.getKey());
                else {
                    if (!item.getValue().hasMoreElements()) {
                        removeListenerFromCandidates(item.getKey());
                        // As we are limiting the number of elements returned by the JDBC driver
                        // we consume the eventual remaining items.
                        consume(null, item.getKey().getVSensorConfig());
                    }
                }
            }
        }
    }

    public boolean vsLoading(VSensorConfig config) {
        return true;
    }

    public boolean vsUnLoading(VSensorConfig config) {
        synchronized (listeners) {
            logger.debug("Distributer unloading: " + listeners.size());
            ArrayList<DistributionRequest> toRemove = new ArrayList<DistributionRequest>();
            for (DistributionRequest listener : listeners) {
                if (listener.getVSensorConfig() == config)
                    toRemove.add(listener);
            }
            for (DistributionRequest listener : toRemove) {
                try {
                    removeListener(listener);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    private DataEnumerator makeDataEnum(DistributionRequest listener) {

        PreparedStatement prepareStatement = preparedStatements.get(listener);
        try {
            //prepareStatement.setLong(1, listener.getStartTime());
            prepareStatement.setLong(1, listener.getLastVisitedPk());
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            return new DataEnumerator();
        }

        DataEnumerator dataEnum = new DataEnumerator(Main.getStorage(listener.getVSensorConfig().getName()), prepareStatement, false, true);
        return dataEnum;
    }

    public void release() {
        synchronized (listeners) {
            while (!listeners.isEmpty())
                removeListener(listeners.get(0));
        }
        if (keepAliveTimer != null)
            keepAliveTimer.stop();
    }

    public boolean contains(DeliverySystem delivery) {
        synchronized (listeners) {
            for (DistributionRequest listener : listeners)
                if (listener.getDeliverySystem().equals(delivery))
                    return true;
            return false;
		}

	}

    //

    private HashMap<StorageManager, Connection> connections = new HashMap<StorageManager, Connection>();
    public Connection getPersistantConnection(VSensorConfig config) throws Exception {
        StorageManager sm = Main.getStorage(config);
        Connection c = connections.get(sm);
        if (c == null) {
            c = sm.getConnection();
            c.setReadOnly(true);
	    connections.put(sm, c);
        }
        return c;
    }

}


