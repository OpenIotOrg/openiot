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
 * @author Jerome Rousselot
 * @author gsn_devs
 * @author Ali Salehi
 * @author Timotee Maret
*/

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
