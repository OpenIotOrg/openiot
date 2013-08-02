package org.openiot.gsn;

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.storage.StorageManager;
import org.openiot.gsn.vsensor.AbstractVirtualSensor;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class ContainerImpl  {

	private static transient Logger                                      logger                             = Logger.getLogger( ContainerImpl.class );

	/**
	 * The <code> waitingVirtualSensors</code> contains the virtual sensors that
	 * recently produced data. This variable is useful for batch processing timed
	 * couple virtual sensor produce data.
	 */
	/*
	 * In the <code>registeredQueries</code> the key is the local virtual
	 * sensor name.
	 */

	
	private static ContainerImpl singleton;
	
	private static final Object                                          psLock                             = new Object( );

	private ContainerImpl() {

	}

	public static ContainerImpl getInstance() {
		if (singleton == null)
			singleton = new ContainerImpl();
		return singleton;
	}


	public void publishData ( AbstractVirtualSensor sensor ,StreamElement data) throws SQLException {
		String name = sensor.getVirtualSensorConfiguration( ).getName( ).toLowerCase();
		StorageManager storageMan = Main.getStorage(sensor.getVirtualSensorConfiguration().getName());
		synchronized ( psLock ) {
			storageMan.executeInsert( name ,sensor.getVirtualSensorConfiguration().getOutputStructure(), data );
		}
		
		for (VirtualSensorDataListener listener : dataListeners) {
			listener.consume(data, sensor.getVirtualSensorConfiguration());
		}
	}

	private ArrayList<VirtualSensorDataListener> dataListeners = new ArrayList<VirtualSensorDataListener>();

	public synchronized void addVSensorDataListener(VirtualSensorDataListener listener) {
		if (!dataListeners.contains(listener))
			dataListeners.add(listener);
	}

	public synchronized void removeVSensorDataListener(VirtualSensorDataListener listener) {
		dataListeners.remove(listener);
	}

}
