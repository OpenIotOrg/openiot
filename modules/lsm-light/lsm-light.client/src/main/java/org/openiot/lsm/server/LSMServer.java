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
<<<<<<< HEAD
	
	public String sensorAdd(Sensor sensor);
	public void sensorAdd(String triple,String graphURL);
	public void sensorDelete(String sensorURL,String graphURL);
	public Sensor getSensorById(String sensorURL,String graphURL);
	public Sensor getSensorBySource(String sensorsource,String graphURL);
=======

	public void sensorDelete(String sensorURL,String graphURL,String clientId, String token);
	public Sensor getSensorById(String sensorURL,String graphURL,String clientId, String token);
>>>>>>> b3d75264e4d20574aee65295733c46234ae5da49
	
	public void sensorDataUpdate(Observation observation,String clientId, String token);
	public void deleteAllReadings(String sensorURL,String graphURL,String clientId, String token);
	public void deleteAllReadings(String sensorURL, String graphURL,String dateOperator, Date fromTime, Date toTime,String clientId, String token);
	
<<<<<<< HEAD
	public boolean pushRDF(String graphURL,String triples);
	public void deleteTriples(String graphURL, String triples);
	public void deleteTriples(String graphURL);
	public void updateTriples(String graphURL, String newTriplePatterns, String oldTriplePatterns);
	public void uploadSchema(LSMSchema schema,String name);
=======
	public void deleteTriples(String graphURL, String triples,String clientId, String token);
	public void deleteTriples(String graphURL,String clientId, String token);
	public void updateTriples(String graphURL, String newTriplePatterns, String oldTriplePatterns,String clientId, String token);
	public void uploadSchema(LSMSchema schema,String name,String clientId, String token);
	public String sensorAdd(Sensor sensor, String clientId, String token);
	boolean pushRDF(String graphURL, String triples, String clientId,
			String token);

>>>>>>> b3d75264e4d20574aee65295733c46234ae5da49
}
