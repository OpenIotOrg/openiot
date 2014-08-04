package org.openiot.ui.sensorschema.test;

import java.io.Serializable;

public class Item implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 750867378105298030L;
	private float id;
	private String value;
	
	
	public Item(float id, String value){
		this.setId(id);
		this.setValue(value);
	}
	
	public Item(){
		
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public float getId() {
		return id;
	}

	public void setId(float id) {
		this.id = id;
	}
}
