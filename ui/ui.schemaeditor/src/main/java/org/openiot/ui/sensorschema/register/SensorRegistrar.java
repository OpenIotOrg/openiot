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
package org.openiot.ui.sensorschema.register;

import java.util.ArrayList;
import java.util.List;

import org.openiot.ui.sensorschema.bean.SensorMetaDataBean;


/*
 * Interface for sensor registration
 * 
 */
public interface SensorRegistrar {
	
	public String registerSensorInstance(SensorMetaDataBean metadata);
	public boolean unregisterSensorInstance(String sensorID); 
	
	public boolean registerSensorType(String rdf);
	public boolean checkSensorTypeRegistration(String sensorID);
	
	public List<String> getSensorList();
	public ArrayList<String> getSensorDescription(String sensorTypeName);
	
	public boolean checkSensorInstanceRegistrationbyName(String sensorName);
	
	
}
