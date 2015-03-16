package org.openiot.csiro.netatmoapi;

import java.util.ArrayList;

public class MeasureParameters {
	
	private String device_id;
	private String module_id;
	private String scale;
	private ArrayList<String> type;
	private String date_begin; //utc timestamp date
	private String date_end; //utc timestamp date
	private int limit; //max can be 1024
	private boolean optimize; //to support mobile apps that will provide less data
	
	public MeasureParameters(){
		type = new ArrayList<String>();
		device_id = "";
		module_id = "";
		scale = "";
		date_begin = "";
		date_end = "";
		limit = 50; //default
		optimize = false;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getModule_id() {
		return module_id;
	}

	public void setModule_id(String module_id) {
		this.module_id = module_id;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public ArrayList<String> getType() {
		return type;
	}

	public void addType(String type) {
		this.type.add(type);
	}

	public String getDate_begin() {
		return date_begin;
	}

	public void setDate_begin(String date_begin) {
		this.date_begin = date_begin;
	}

	public String getDate_end() {
		return date_end;
	}

	public void setDate_end(String date_end) {
		this.date_end = date_end;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isOptimize() {
		return optimize;
	}

	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}
	
	
}
