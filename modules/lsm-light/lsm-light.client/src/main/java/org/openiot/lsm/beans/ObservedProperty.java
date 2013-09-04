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
