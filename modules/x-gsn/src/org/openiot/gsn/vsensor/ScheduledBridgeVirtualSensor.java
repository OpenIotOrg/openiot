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
