package org.openiot.gsn.reports.beans;

import java.util.Collection;

public class VirtualSensor {
	
	private String virtualSensorName;
	
	private String latitude;
	
	private String longitude;
	
	private Collection<Stream> reportFields;
	
	public VirtualSensor (String virtualSensorName, String latitude, String longitude, Collection<Stream> reportFields) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.virtualSensorName = virtualSensorName;
		this.reportFields = reportFields;
	}

	public Collection<Stream> getReportFields() {
		return reportFields;
	}

	public String getVirtualSensorName() {
		return virtualSensorName;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	
}
