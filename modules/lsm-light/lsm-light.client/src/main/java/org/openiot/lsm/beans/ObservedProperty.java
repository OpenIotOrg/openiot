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
import java.util.Date;

public class ObservedProperty implements java.io.Serializable{
	private String value;
	private Date times;
	private String propertyType;
	private String unit;
	private String observationId;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setValue(Double value) {
		this.value = Double.toString(value);
	}
	public void setValue(int value) {
		this.value = Integer.toString(value);
	}
	public String getObservationId() {
		return observationId;
	}
	public void setObservationId(String observationId) {
		this.observationId = observationId;
	}
	
	public Date getTimes() {
		return times;
	}
	public void setTimes(Date times) {
		this.times = times;
	}
	
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String obsClassURL) {
		this.propertyType = obsClassURL;
	}

}
