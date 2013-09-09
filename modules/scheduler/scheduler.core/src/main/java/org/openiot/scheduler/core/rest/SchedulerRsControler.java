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
import javax.ws.rs.QueryParam;


import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.sensortypes.model.SensorTypes;


import org.openiot.scheduler.core.api.impl.DiscoverSensors.method2.DiscoverSensorsImpl;
import org.openiot.scheduler.core.api.impl.RegisterService.RegisterServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
@Path("/services")
@Consumes({ "application/xml", "application/json" })
@Produces({ "application/xml", "application/json" })
public class SchedulerRsControler {
	
	final static Logger logger = LoggerFactory.getLogger(SchedulerRsControler.class);
	
    

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
				+ "discoverSensors (userID:String, longitude:double, latitude:double, radius:float): SensorTypes\n"
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

		logger.debug(welcomeText);

		
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
//	@Consumes("application/xml")
	public String registerService(OSDSpec osdSpec) {

		
		
		RegisterServiceImpl registerServiceImpl = new RegisterServiceImpl(osdSpec);
		

		return registerServiceImpl.replyMessage();

	}

	@GET
	@Path("/discoverSensors")
//	@Consumes("application/xml")
//	@Produces("application/xml")
	public SensorTypes discoverSensors(@QueryParam("userID") String userID, @QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude, @QueryParam("radius") float radius) {

		
		DiscoverSensorsImpl discoverSensorsImpl = new DiscoverSensorsImpl(userID, longitude, latitude, radius);
		
		return discoverSensorsImpl.getSensorTypes();
	}
}
