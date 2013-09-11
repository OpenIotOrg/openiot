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

package org.openiot.gsn.vsensor;


import org.openiot.gsn.ContainerImpl;
import java.sql.SQLException;
import java.util.Date;
import java.util.TimerTask;

public class ScheduledBridgeVirtualSensor extends AbstractScheduledVirtualSensor {

	
	public boolean initialize() {
		super.initialize(); 		//get the timer settings
		TimerTask timerTask = new MyTimerTask();
		timer0.scheduleAtFixedRate(timerTask, new Date(startTime), clock_rate);
		return true;
	}

	class MyTimerTask extends TimerTask {

		public void run() {
			if(dataItem == null)
				return;	
			
			dataItem.setTimeStamp(System.currentTimeMillis());
			logger.warn(getVirtualSensorConfiguration().getName() + " Timer Event ");
			try {
				ContainerImpl.getInstance().publishData(ScheduledBridgeVirtualSensor.this, dataItem);
			} catch (SQLException e) {
				if (e.getMessage().toLowerCase().contains("duplicate entry"))
					logger.info(e.getMessage(), e);
				else
					logger.error(e.getMessage(), e);
			}
	
		}
	}

	public void dispose() {
		timer0.cancel();
		

	}

}
