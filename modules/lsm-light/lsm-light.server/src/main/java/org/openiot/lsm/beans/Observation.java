package org.openiot.lsm.beans;
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

/**
 * 
 * @author Hoan Nguyen Mau Quoc
 *
 */
import java.util.ArrayList;
import java.util.Date;
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */

public class Observation implements java.io.Serializable {
	private String id;	
	private Date times;	
	private String sensorId;
	private String featureOfInterest=""; 
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
