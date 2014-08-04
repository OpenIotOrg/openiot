package org.openiot.ui.sensorschema.bean;

import java.io.Serializable;



public class ObservedPropertyBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5647061293817764600L;
	
	private String observes;
	private String accuracy;
	private String frequency;
	
	//id of the observation --> currently auto generated. Will change to URL
	private long id;
	
	
	public ObservedPropertyBean(){
				
	}
	
	public ObservedPropertyBean(String observes, String accuracy, String frequency){
		this.observes = observes;
		this.accuracy = accuracy;
		this.frequency = frequency;		
	}
	
	public String getObserves() {
		return observes;
	}
	public void setObserves(String observes) {
		this.observes = observes;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	
}
