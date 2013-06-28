package org.openiot.scheduler.core.api.impl;

public class FullMeta 
{
	private String sensorType;
	private String measuredVal;
	private String unit;
	private String value;
	
	
	
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	
	public String getMeasuredVal() {
		return measuredVal;
	}
	public void setMeasuredVal(String measuredVal) {
		this.measuredVal = measuredVal;
	}
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
