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
 * @author Timotee Maret
*/

package org.openiot.gsn;

import org.openiot.gsn.beans.InputStream;
import org.openiot.gsn.beans.StreamSource;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.vsensor.AbstractVirtualSensor;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.SQLException;

public class VirtualSensor {

    private static final transient Logger logger = Logger.getLogger(VirtualSensor.class);

    private static final int GARBAGE_COLLECTOR_INTERVAL = 2;

    private String processingClassName;

    private AbstractVirtualSensor virtualSensor = null;

    private VSensorConfig config = null;

    private long lastModified = -1;

    private int noOfCallsToReturnVS = 0;

    public VirtualSensor(VSensorConfig config) {
        this.config = config;
        this.lastModified = new File(config.getFileName()).lastModified();
    }

    public synchronized AbstractVirtualSensor borrowVS() throws VirtualSensorInitializationFailedException {
        if (virtualSensor == null) {
            try {
                virtualSensor = (AbstractVirtualSensor) Class.forName(config.getProcessingClass()).newInstance();
                virtualSensor.setVirtualSensorConfiguration(config);
            } catch (Exception e) {
                throw new VirtualSensorInitializationFailedException(e.getMessage(), e);
            }
            if (virtualSensor.initialize() == false) {
                virtualSensor = null;
                throw new VirtualSensorInitializationFailedException();
            }
            if (logger.isDebugEnabled())
                logger.debug(new StringBuilder().append("Created a new instance for VS ").append(config.getName()));
        }
        return virtualSensor;
    }

    /**
     * The method ignores the call if the input is null
     *
     * @param o
     */
    public synchronized void returnVS(AbstractVirtualSensor o) {
        if (o == null) return;
        if (++noOfCallsToReturnVS % GARBAGE_COLLECTOR_INTERVAL == 0)
            DoUselessDataRemoval();
    }

    public synchronized void closePool() {
        if (virtualSensor != null) {
            virtualSensor.dispose();
            if (logger.isDebugEnabled())
                logger.debug(new StringBuilder().append("VS ").append(config.getName()).append(" is now released."));
        } else if (logger.isDebugEnabled())
            logger.debug(new StringBuilder().append("VS ").append(config.getName()).append(" was already released."));
    }

    public void start() throws VirtualSensorInitializationFailedException {
        for (InputStream inputStream : config.getInputStreams()) {
            for (StreamSource streamSource : inputStream.getSources()) {
                streamSource.getWrapper().start();
            }
        }
        borrowVS();
    }

    /**
     * @return the config
     */
    public VSensorConfig getConfig() {
        return config;
    }

    /**
     * @return the lastModified
     */
    public long getLastModified() {
        return lastModified;
    }

    public void dispose() {
    }

    // apply the storage size parameter to the virtual sensor table
    public void DoUselessDataRemoval() {
        if (config.getParsedStorageSize() == VSensorConfig.STORAGE_SIZE_NOT_SET) return;
        StringBuilder query;

        if (config.isStorageCountBased()) {
            query = Main.getStorage(config.getName()).getStatementRemoveUselessDataCountBased(config.getName(), config.getParsedStorageSize());
        }
        else {
            query = Main.getStorage(config.getName()).getStatementRemoveUselessDataTimeBased(config.getName(), config.getParsedStorageSize());
        }

        int effected = 0;
        try {
            if (logger.isDebugEnabled())
                logger.debug(new StringBuilder().append("Enforcing the limit size on the VS table by : ").append(query).toString());
            effected = Main.getStorage(config.getName()).executeUpdate(query);
        } catch (SQLException e) {
            logger.error("Error in executing: " + query);
            logger.error(e.getMessage(), e);
        }
        if (logger.isDebugEnabled())
            logger.debug(new StringBuilder().append(effected).append(" old rows dropped from ").append(config.getName()).toString());
    }
}

