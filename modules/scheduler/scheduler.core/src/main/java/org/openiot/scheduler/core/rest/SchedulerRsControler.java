package org.openiot.scheduler.core.rest;

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





import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.sensortypes.model.SensorType;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.commons.sparql.protocoltypes.model.QueryResult;

/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
@Path("/services")
public class SchedulerRsControler {

	/**
	 * @return
	 */
	@GET()
	@Produces("text/plain")
	public String welcomeMessage() {

		String welcomeText;

		welcomeText = "Welcome to Scheduler's Rest Interface\n"
				+ "=====================================\n\n"
				+ "This Interface provides the folowing Services:\n"
				+ "discoverService(userID: String, sparqlQuery:QueryRequest): SparqlResultsDoc\n"
				+ "registerService(osdSpec: OSDSpec): String\n"
				+ "unregisterService(String serviceID): void\n"
				+ "updateService(osdSpec: OSDSpec): void\n"
				+ "enableService(serviceID: String): void\n"
				+ "disableService(serviceID: String): void\n"
				+ "getServiceStatus (serviceID: String): ServiceStatus\n"
				+ "getService (serviceID: String): OSMO\n"
				+ "getUser (userID: String): OpenIotUser\n"
				+ "getAvailableServiceIDs (userID: String): List<DescriptiveID>\n"
				+ "getAvailableServices (userID: String): OSDSpec";

		return welcomeText;
	}

	/**
	 * @param osdSpec
	 * @return
	 * 
	 *         <p>
	 *         registerService(osdSpec: OSDSpec): String
	 *         <p>
	 */
	@POST
	@Path("/registerService")
	@Consumes("application/xml")
	public String registerService(OSDSpec osdSpec) {

		// TODO: Fill the registerService method

		return "Success";

	}

	@GET
	@Path("/discoverSensors")
	@Consumes("application/xml")
	@Produces("application/xml")
	public SensorTypes discoverSensors(String userID, double longitude, double latitude, float radius) {

		
		//Test sensortypes to return 
		SensorTypes testSensorTypes = new SensorTypes();
		
		SensorType testSensorType1 = new SensorType();
		//Fill the testSensorType1
		
		
		testSensorTypes.getSensorType().add(testSensorType1);
		
		SensorType testSensorType2 = new SensorType();
		//Fill the testSensorType2
		
		
		
		testSensorTypes.getSensorType().add(testSensorType2);
		
		
		
		
		
		return testSensorTypes;

	}

}
