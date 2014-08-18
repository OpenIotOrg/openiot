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
 * 
 * 	   @author Prem Jayaraman
 */
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
	private String uom;
	
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

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}


	
}
