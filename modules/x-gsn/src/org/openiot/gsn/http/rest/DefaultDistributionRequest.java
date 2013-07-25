package org.openiot.gsn.http.rest;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.storage.SQLValidator;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class DefaultDistributionRequest implements DistributionRequest {

	private static transient Logger       logger     = Logger.getLogger ( DefaultDistributionRequest.class );

	private long startTime;

    private long lastVisitedPk = -1;

	private String query;

	private DeliverySystem deliverySystem;

	private VSensorConfig vSensorConfig;

    private DefaultDistributionRequest(DeliverySystem deliverySystem, VSensorConfig sensorConfig, String query, long startTime) throws IOException, SQLException {
		this.deliverySystem = deliverySystem;
		vSensorConfig = sensorConfig;
		this.query = query;
		this.startTime = startTime;
		DataField[] selectedColmnNames = SQLValidator.getInstance().extractSelectColumns(query,vSensorConfig);
		deliverySystem.writeStructure(selectedColmnNames);
	}

	public static DefaultDistributionRequest create(DeliverySystem deliverySystem, VSensorConfig sensorConfig,String query, long startTime) throws IOException, SQLException {
		DefaultDistributionRequest toReturn = new DefaultDistributionRequest(deliverySystem,sensorConfig,query,startTime);
		return toReturn;
	}

	public String toString() {
		return new StringBuilder("DefaultDistributionRequest Request[[ Delivery System: ")
                .append(deliverySystem.getClass().getName())
                .append("],[Query:").append(query)
                .append("],[startTime:")
                .append(startTime)
                .append("],[VirtualSensorName:")
                .append(vSensorConfig.getName())
                .append("]]").toString();
	}

    public boolean deliverKeepAliveMessage() {
        return deliverySystem.writeKeepAliveStreamElement();
    }

	public boolean deliverStreamElement(StreamElement se) {		
		boolean success = deliverySystem.writeStreamElement(se);
//		boolean success = true;
		if (success) {
			//startTime=se.getTimeStamp();
            lastVisitedPk = se.getInternalPrimayKey();
        }
		return success;
	}


	public long getStartTime() {
		return startTime;
	}

    public long getLastVisitedPk() {
        return lastVisitedPk;
    }

	
	public String getQuery() {
		return query;
	}

	
	public VSensorConfig getVSensorConfig() {
		return vSensorConfig;
	}

	
	public void close() {
		deliverySystem.close();
	}

	
	public boolean isClosed() {
		return deliverySystem.isClosed();
	}

	public DeliverySystem getDeliverySystem() {
		return deliverySystem;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultDistributionRequest that = (DefaultDistributionRequest) o;

        if (deliverySystem != null ? !deliverySystem.equals(that.deliverySystem) : that.deliverySystem != null)
            return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (vSensorConfig != null ? !vSensorConfig.equals(that.vSensorConfig) : that.vSensorConfig != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (deliverySystem != null ? deliverySystem.hashCode() : 0);
        result = 31 * result + (vSensorConfig != null ? vSensorConfig.hashCode() : 0);
        return result;
    }
}
	
