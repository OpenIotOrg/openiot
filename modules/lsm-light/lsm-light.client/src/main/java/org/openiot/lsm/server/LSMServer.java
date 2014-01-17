package org.openiot.lsm.server;
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

import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.schema.LSMSchema;
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */

public interface LSMServer {
	
	/**
	 * add new Sensor
	 *
	 */
	
	public String sensorAdd(Sensor sensor);
	public void sensorAdd(String triple,String graphURL);
	public void sensorDelete(String sensorURL,String graphURL);
	public Sensor getSensorById(String sensorURL,String graphURL);
	public Sensor getSensorBySource(String sensorsource,String graphURL);
	
	public void sensorDataUpdate(String triples,String graphURL);
	public void sensorDataUpdate(Observation observation);
	public void deleteAllReadings(String sensorURL,String graphURL);
	public void deleteAllReadings(String sensorURL, String graphURL,String dateOperator, Date fromTime, Date toTime);
	
	public boolean pushRDF(String graphURL,String triples);
	public void deleteTriples(String graphURL, String triples);
	public void deleteTriples(String graphURL);
	public void updateTriples(String graphURL, String newTriplePatterns, String oldTriplePatterns);
	public void uploadSchema(LSMSchema schema,String name);
}
