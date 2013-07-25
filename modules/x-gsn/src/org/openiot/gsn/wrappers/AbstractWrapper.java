package org.openiot.gsn.wrappers;

import org.openiot.gsn.Main;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.StreamSource;
import org.openiot.gsn.beans.windowing.LocalTimeBasedSlidingHandler;
import org.openiot.gsn.beans.windowing.RemoteTimeBasedSlidingHandler;
import org.openiot.gsn.beans.windowing.SlidingHandler;
import org.openiot.gsn.beans.windowing.TupleBasedSlidingHandler;
import org.openiot.gsn.beans.windowing.WindowType;
import org.openiot.gsn.utils.GSNRuntimeException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;

public abstract class AbstractWrapper extends Thread {

	private final static transient Logger logger = Logger
			.getLogger(AbstractWrapper.class);

	protected final List<StreamSource> listeners = Collections
			.synchronizedList(new ArrayList<StreamSource>());

	private AddressBean activeAddressBean;

	private boolean isActive = true;

	private SlidingHandler tupleBasedSlidingHandler;

	private SlidingHandler timeBasedSlidingHandler;

	private HashMap<Class, SlidingHandler> slidingHandlers = new HashMap<Class, SlidingHandler>();

	private boolean usingRemoteTimestamp = false;

	private Long lastInOrderTimestamp;

	public static final int GARBAGE_COLLECT_AFTER_SPECIFIED_NO_OF_ELEMENTS = 2;

	/**
	 * Returns the view name created for this listener. Note that, GSN creates
	 * one view per listener.
	 * 
	 * @throws SQLException
	 */
	public void addListener(StreamSource ss) throws SQLException {
		if (WindowType.isTimeBased(ss.getWindowingType())) {
			if (timeBasedSlidingHandler == null) {
				timeBasedSlidingHandler = isUsingRemoteTimestamp() == false ? new LocalTimeBasedSlidingHandler(
						this)
						: new RemoteTimeBasedSlidingHandler(this);
				addSlidingHandler(timeBasedSlidingHandler);
			}
		} else {
			if (tupleBasedSlidingHandler == null)
				tupleBasedSlidingHandler = new TupleBasedSlidingHandler(this);
			addSlidingHandler(tupleBasedSlidingHandler);
		}

		for (SlidingHandler slidingHandler : slidingHandlers.values()) {
			if (slidingHandler.isInterestedIn(ss))
				slidingHandler.addStreamSource(ss);
		}

		listeners.add(ss);
		if (logger.isDebugEnabled())
			logger.debug("Adding listeners: " + ss.toString());
	}

	public void addSlidingHandler(SlidingHandler slidingHandler) {
		slidingHandlers.put(slidingHandler.getClass(), slidingHandler);
	}

	/**
	 * Removes the listener with it's associated view.
	 * 
	 * @throws SQLException
	 */
	public void removeListener(StreamSource ss) throws SQLException {
		listeners.remove(ss);
		// getStorageManager( ).executeDropView( ss.getUIDStr() );
		for (SlidingHandler slidingHandler : slidingHandlers.values()) {
			if (slidingHandler.isInterestedIn(ss))
				slidingHandler.removeStreamSource(ss);
		}
		if (listeners.size() == 0) {
			releaseResources();
		}
		
	}

	/**
	 * @return the listeners
	 */
	public List<StreamSource> getListeners() {
		return listeners;
	}

	//protected StorageManager getStorageManager() {
	//	return StorageManager.getInstance();
    //
	//}

	/**
	 * This method is called whenever the wrapper wants to send a data item back
	 * to the source where the data is coming from. For example, If the data is
	 * coming from a wireless sensor network (WSN), This method sends a data
	 * item to the sink node of the virtual sensor. So this method is the
	 * communication between the System and actual source of data. The data sent
	 * back to the WSN could be a command message or a configuration message.
	 * 
	 * @param dataItem
	 *            : The data which is going to be send to the source of the data
	 *            for this wrapper.
	 * @return True if the send operation is successful.
	 * @throws OperationNotSupportedException
	 *             If the wrapper doesn't support sending the data back to the
	 *             source. Note that by default this method throws this
	 *             exception unless the wrapper overrides it.
	 */

	public boolean sendToWrapper(String action, String[] paramNames,
			Object[] paramValues) throws OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"This wrapper doesn't support sending data back to the source.");
	}

	public final AddressBean getActiveAddressBean() {
		if (this.activeAddressBean == null) {
			throw new RuntimeException(
					"There is no active address bean associated with the wrapper.");
		}
		return activeAddressBean;
	}

	/**
	 * Only sets if there is no other activeAddressBean configured.
	 * 
	 * @param newVal
	 *            the activeAddressBean to set
	 */
	public void setActiveAddressBean(AddressBean newVal) {
		if (this.activeAddressBean != null) {
			throw new RuntimeException(
					"There is already an active address bean associated with the wrapper.");
		}
		this.activeAddressBean = newVal;
	}

	private long noOfCallsToPostSE = 0;

	private final transient int aliasCode = Main.getWindowStorage().tableNameGenerator();
	private final CharSequence aliasCodeS = Main.getWindowStorage().tableNameGeneratorInString(aliasCode);

	public int getDBAlias() {
		return aliasCode;
	}

	public CharSequence getDBAliasInStr() {
		return aliasCodeS;
	}

	public abstract DataField[] getOutputFormat();

	public boolean isActive() {
		return isActive;
	}

	protected void postStreamElement(Serializable... values) {
		StreamElement se = new StreamElement(getOutputFormat(), values, System
				.currentTimeMillis());
		postStreamElement(se);
	}

	protected void postStreamElement(long timestamp, Serializable[] values) {
		StreamElement se = new StreamElement(getOutputFormat(), values,
				timestamp);
		postStreamElement(se);
	}

	/**
	 * This method gets the generated stream element and notifies the input
	 * streams if needed. The return value specifies if the newly provided
	 * stream element generated at least one input stream notification or not.
	 * 
	 * @param streamElement
	 * @return If the method returns false, it means the insertion doesn't
	 *         effected any input stream.
	 */

	protected Boolean postStreamElement(StreamElement streamElement) {
		if (streamElement == null) {
			logger.info("postStreamElement is called with null ! Wrapper "
					+ getWrapperName() + " might has a problem !");
			return false;
		}
		try {
			if (!isActive() || listeners.size() == 0)
				return false;
			if (!insertIntoWrapperTable(streamElement))
				return false;
			boolean toReturn = false;

			if (logger.isDebugEnabled())
				logger.debug("Size of the listeners to be evaluated - "
						+ listeners.size());

			for (SlidingHandler slidingHandler : slidingHandlers.values()) {
				toReturn = slidingHandler.dataAvailable(streamElement)
						|| toReturn;
			}

			if (++noOfCallsToPostSE
					% GARBAGE_COLLECT_AFTER_SPECIFIED_NO_OF_ELEMENTS == 0) {
				int removedRaws = removeUselessValues();
			}
			return toReturn;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger
					.error("Produced data item from the wrapper couldn't be propagated inside the system.");
			return false;
		}
	}

	/**
	 * Updates the table representing the data items produced by the stream
	 * element. Returns false if the update fails or doesn't change the state of
	 * the table.
	 * 
	 * @param se
	 *            Stream element to be inserted to the table if needed.
	 * @return true if the stream element is successfully inserted into the
	 *         table.
	 * @throws SQLException
	 */
	public boolean insertIntoWrapperTable(StreamElement se) throws SQLException {
		if (listeners.size() == 0)
			return false;

		Connection conn = null;
		try {
            if (isOutOfOrder(se)) {
				logger.debug("Out of order data item detected, it is not propagated into the system : [" + se.toString() + "]");
				return false;
			}
			conn = Main.getWindowStorage().getConnection();
			Main.getWindowStorage().executeInsert(aliasCodeS, getOutputFormat(), se, conn);
            lastInOrderTimestamp = se.getTimeStamp();
            return true;
		} finally {
			Main.getWindowStorage().close(conn);
		}
	}

    public boolean isOutOfOrder(StreamElement se) throws SQLException {
        if (listeners.size() == 0)
			return false;
        Connection conn = null;
		try {
			// Checks if the stream element is out of order
            if (lastInOrderTimestamp == null) {
                conn = Main.getWindowStorage().getConnection();
                StringBuilder query = new StringBuilder();
				query.append("select max(timed) from ").append(aliasCodeS);

				ResultSet rs = Main.getWindowStorage().executeQueryWithResultSet(query,
						conn);
				if (rs.next()) {
					lastInOrderTimestamp = rs.getLong(1);
				} else {
					lastInOrderTimestamp = Long.MIN_VALUE; // Table is empty
				}
			}
            return (se.getTimeStamp() <= lastInOrderTimestamp);
		} finally {
			Main.getWindowStorage().close(conn);
		}
    }

	/**
	 * This method is called whenever the wrapper wants to send a data item back
	 * to the source where the data is coming from. For example, If the data is
	 * coming from a wireless sensor network (WSN), This method sends a data
	 * item to the sink node of the virtual sensor. So this method is the
	 * communication between the System and actual source of data. The data sent
	 * back to the WSN could be a command message or a configuration message.
	 * 
	 * @param dataItem
	 *            : The data which is going to be send to the source of the data
	 *            for this wrapper.
	 * @return True if the send operation is successful.
	 * @throws OperationNotSupportedException
	 *             If the wrapper doesn't support sending the data back to the
	 *             source. Note that by default this method throws this
	 *             exception unless the wrapper overrides it.
	 */

	public boolean sendToWrapper(Object dataItem)
			throws OperationNotSupportedException {
		if (isActive == false)
			throw new GSNRuntimeException(
					"Sending to an inactive/disabled wrapper is not allowed !");
		throw new OperationNotSupportedException(
				"This wrapper doesn't support sending data back to the source.");
	}

	/**
	 * Removes all the listeners, drops the views representing them, drops the
	 * sensor table, stops the TableSizeEnforce thread.
	 * 
	 */
	public StringBuilder getUselessWindow() {
		long minTimed = -1;
		synchronized (slidingHandlers) {
			for (SlidingHandler slidingHandler : slidingHandlers.values()) {
				long timed = slidingHandler.getOldestTimestamp();
				logger.debug("***** Oldest timestamp : " + timed);
				if (timed == -1) {
					minTimed = -1;
					break;
				} else {
					minTimed = (minTimed != -1) ? Math.min(minTimed, timed)
							: timed;
				}
			}
		}

		logger.debug("Oldest timestamp : " + minTimed);

		if (minTimed == -1)
			return null;
		StringBuilder sb = new StringBuilder("delete from ").append(
				getDBAliasInStr()).append(" where ");
		sb.append(" timed < ").append(minTimed);
		return sb;
	}

	public int removeUselessValues() throws SQLException {
		StringBuilder query = getUselessWindow();
		if (query == null)
			return 0;
		if (logger.isDebugEnabled())
			logger.debug(new StringBuilder().append(
					"RESULTING QUERY FOR Table Size Enforce ").append(query)
					.toString());
		int deletedRows = Main.getWindowStorage().executeUpdate(query);
		if (logger.isDebugEnabled())
			logger.debug(new StringBuilder().append(deletedRows).append(
					" old rows dropped from ").append(getDBAliasInStr())
					.toString());
		return deletedRows;
	}

	public void releaseResources() throws SQLException {
		isActive = false;
		dispose();
		if (logger.isInfoEnabled())
			logger.info("dispose called");
		listeners.clear();
		for (SlidingHandler slidingHandler : slidingHandlers.values()) {
			slidingHandler.dispose();
		}
		Main.getWindowStorage().executeDropTable(aliasCodeS);
	}

	public static final String TIME_FIELD = "timed";

	/**
	 * The addressing is provided in the ("ADDRESS",Collection<KeyValue>). If
	 * the DataSource can't initialize itself because of either internal error
	 * or inaccessibility of the host specified in the address the method
	 * returns false. The dbAliasName of the DataSource is also specified with
	 * the "DBALIAS" in the context. The "STORAGEMAN" points to the
	 * StorageManager which should be used for querying.
	 * 
	 * @return True if the initialization do successfully otherwise false;
	 */

	public abstract boolean initialize();

	public abstract void dispose();

	public abstract String getWrapperName();

	/**
	 * Indicates whether we use GSN's time (local time) or the time already
	 * exists in the data (remote time) for the timestamp of generated stream
	 * elements.
	 * 
	 * @return <code>false</code> if we use local time <br>
	 *         <code>true</code> if we use remote time
	 */
	protected boolean isUsingRemoteTimestamp() {
		return usingRemoteTimestamp;
	}

	/**
	 * 
	 * @param usingRemoteTimestamp
	 */
	protected void setUsingRemoteTimestamp(boolean usingRemoteTimestamp) {
		this.usingRemoteTimestamp = usingRemoteTimestamp;
	}

	/**
	 * Returns true if the wrapper can produce multiple different data items
	 * [stream elements] with the same timestamp. If this is true, then all the
	 * stream elements with the same timestamp will be accepted. If this method
	 * returns false (default value), duplicates override each other and the
	 * latest received duplicate is the one which is going to be persisted.
	 */
	public boolean isTimeStampUnique() {
		return true;
	}

	public boolean manualDataInsertion(StreamElement se) {
		throw new RuntimeException(
				"Manual data insertion is not supported by this wrapper");
	}
}
