package org.openiot.gsn.wrappers;

import org.openiot.gsn.acquisition2.wrappers.MigMessageParameters;
import org.openiot.gsn.acquisition2.wrappers.MigMessageWrapper2;

import org.apache.log4j.Logger;

public class MigMessageSensorscopeWrapper extends MigMessageWrapper2 {

	private static final int MAINTENANCE_MESSAGE_AM = 138;
	
	private long last_timestamp_offset = 0;

	private final transient Logger logger = Logger.getLogger( MigMessageSensorscopeWrapper.class );

	@Override
	public void run() {
		super.run();
		if (parameters.getTinyosVersion() == MigMessageParameters.TINYOS_VERSION_1) {
			logger.error("NOT implemented for TinyOS v 1.x");
		}
		else {
			logger.debug("Register maintenance message to source.");
			net.tinyos.message.Message messageMaintenance = (net.tinyos.message.Message) new org.openiot.gsn.wrappers.tinyos.SensorscopeMaintenance();
			moteIFTinyOS2x.registerListener(messageMaintenance, this);
		}
	}

	@Override
	public void messageReceived(int to, net.tinyos1x.message.Message tosmsg) {
		if (tosmsg.amType() == MAINTENANCE_MESSAGE_AM) { 
			logger.debug("TinyOS 1.x Message received");
			logger.debug("Sensorscope Maintenance message received");
			logger.error("NOT implemented for TinyOS v 1.x");
		}
		else {
			// update the timestamp in the message
			logger.debug("Sensorscope Data message received, timestamp not updated");
			super.messageReceived(to, tosmsg);
		}
	}

	@Override
	public void messageReceived(int to, net.tinyos.message.Message tosmsg) {		
		if (tosmsg.amType() == MAINTENANCE_MESSAGE_AM) { 
			logger.debug("TinyOS 2.x Message received");
			logger.debug("Sensorscope Maintenance message received");
			// update the timestamp offset
			org.openiot.gsn.wrappers.tinyos.SensorscopeMaintenance messageMaintenance = (org.openiot.gsn.wrappers.tinyos.SensorscopeMaintenance) tosmsg;
			last_timestamp_offset = messageMaintenance.get_timestamp_offset();
			logger.debug("New Sensorscope timestamp offset >" + last_timestamp_offset + "<");
		}
		else {
			logger.debug("Sensorscope Data message received");
			if (tosmsg instanceof org.openiot.gsn.wrappers.tinyos.RuedlingenData) {
				// update the timestamp in the message
				org.openiot.gsn.wrappers.tinyos.RuedlingenData m = (org.openiot.gsn.wrappers.tinyos.RuedlingenData) tosmsg;
				long updatedTimeStamp = last_timestamp_offset + (m.get_timestamp() / 1024);
				logger.debug("timestamp from message >" + m.get_timestamp() + "<");
				logger.debug("timestamp with offset >" + updatedTimeStamp + "<");
				m.set_timestamp(updatedTimeStamp);
			}
			else {
				logger.error("Unknow message");
			}
			super.messageReceived(to, tosmsg);
		}
	}

	@Override
	public String getWrapperName() {
		return "TinyOS Sensorscope packet wrapper";
	}

}
