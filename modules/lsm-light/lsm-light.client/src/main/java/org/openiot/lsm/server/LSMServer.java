/**
 * 
 */
package org.openiot.lsm.server;
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

import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.schema.LSMSchema;





/**
 * @author root
 *
 */

public interface LSMServer {
	
	/**
	 * add new Sensor
	 *
	 */
	public String sensorAdd(Sensor sensor);
	public boolean sensorAdd(String triple);
	public boolean sensorDelete(String sensorURL);
	public Sensor getSensorById(String sensorURL);
	public Sensor getSensorBySource(String sensorsource);
	
	public boolean sensorDataUpdate(String triples);
	public boolean sensorDataUpdate(Observation observation);
	public boolean deleteAllReadings(String sensorURL);
	public boolean deleteAllReadings(String sensorURL, String dateOperator, Date fromTime, Date toTime);
	
	public boolean pushRDF(String graphURL,String triples);
	public boolean deleteTriples(String graphURL, String triples);
	public boolean deleteTriples(String graphURL);
	
	public void uploadSchema(LSMSchema schema,String name);
}
