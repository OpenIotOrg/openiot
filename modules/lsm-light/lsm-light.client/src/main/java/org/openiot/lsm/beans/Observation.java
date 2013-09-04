package org.openiot.lsm.beans;
/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
import java.util.ArrayList;
import java.util.Date;


public class Observation implements java.io.Serializable {
	private String id;	
	private Date times;	
	private String sensorId;
	private String featureOfInterest; 
	private ArrayList<ObservedProperty> readings;
	private String metaGraph;
	private String dataGraph;
	
	public Observation(){
		id = ""+System.nanoTime();
		readings = new ArrayList<ObservedProperty>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getTimes() {
		return times;
	}
	public void setTimes(Date times) {
		this.times = times;
	}
	public String getSensor() {
		return sensorId;
	}
	public void setSensor(String sensorId) {
		this.sensorId = sensorId;
	}
	public String getFeatureOfInterest() {
		return featureOfInterest;
	}
	public void setFeatureOfInterest(String featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}
	public ArrayList<ObservedProperty> getReadings() {
		return readings;
	}
	public void setReadings(ArrayList<ObservedProperty> readings) {
		this.readings = readings;
	}
	
	public void addReading(ObservedProperty reading){
		readings.add(reading);
	}
	
	public void removeReading(ObservedProperty reading){
		readings.remove(reading);
	}

	public String getMetaGraph() {
		return metaGraph;
	}

	public void setMetaGraph(String metaGraph) {
		this.metaGraph = metaGraph;
	}

	public String getDataGraph() {
		return dataGraph;
	}

	public void setDataGraph(String dataGraph) {
		this.dataGraph = dataGraph;
	}
	
}
