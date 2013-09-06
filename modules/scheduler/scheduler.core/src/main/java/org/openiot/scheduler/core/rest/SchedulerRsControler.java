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


import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.sensortypes.model.SensorTypes;


import org.openiot.scheduler.core.api.impl.DiscoverSensorsImpl;
import org.openiot.scheduler.core.api.impl.RegisterServiceImpl;


import org.openiot.commons.descriptiveids.model.DescreptiveIDs;

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
	 * 
	 * Used to help applications build a request by using existing sensor
	 * classes. Requires as input the UserID, in String format, the location
	 * longitude/ latitude and the radius of interest. Returns a SensorTypes
	 * object which includes all the available sensors with their metadata.
	 * 
	 * @param userID
	 * @param longitude
	 * @param latitude
	 * @param radius
	 * @return
	 */
	@GET
	@Path("/discoverSensors")
//	@Consumes("application/xml")
//	@Produces("application/xml")
	public SensorTypes discoverSensors(@QueryParam("userID") String userID, @QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude, @QueryParam("radius") float radius) {

		
		DiscoverSensorsImpl discoverSensorsImpl = new DiscoverSensorsImpl(userID, longitude, latitude, radius);
		
		return discoverSensorsImpl.getSensorTypes();
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

	
	
	
	
	/**
	 * Used to unregister/delete a registered/running service. Requires as input
	 * the Application ID.
	 * 
	 * @param applicationID
	 */
	@POST
	@Path("/unregisterApp")
	public void unregisterApp(String applicationID) {

		


	}	
	
	
	/**
	 * Used to update a registered service. Requires as input the OSD Specification.
	 * 
	 * @param osdSpec
	 */
	@POST
	@Path("/updateApp")
	public void updateApp(OSDSpec osdSpec) {



	}	
	
	
	/**
	 * Used to retrieve the description (OAMO) of an available Application.
	 * Requires as input the Application ID
	 * 
	 * @param applicationID
	 * @return
	 */
	@GET
	@Path("/getApplication")
	public OAMO getApplication(@QueryParam("applicationID") String applicationID) {

		OAMO oamo = new OAMO();

		return oamo;
	}
	
	
	
	

	/**
	 * Used to retrieve the description (OSMO) of an available service. Requires
	 * as input the Service ID
	 * 
	 * @param serviceID
	 * @return
	 */
	@GET
	@Path("/getService")
	public OSMO getService(@QueryParam("serviceID") String serviceID) {

		OSMO osmo = new OSMO();

		return osmo;
	}
	
	
	
	
	/**
	 * Used to retrieve the available applications (a list of
	 * applicationID/ServiceName/ServiceDescription triplet) already registered
	 * by a specific user. Requires as input the User ID.
	 * 
	 * @param userID
	 * @return
	 */
	@GET
	@Path("/getAvailableAppIDs")
	public DescreptiveIDs getAvailableAppIDs(@QueryParam("userID") String userID) {

		DescreptiveIDs descreptiveIDs = new DescreptiveIDs();

		return descreptiveIDs;
	}
	
	
	

	/**
	 * Used to retrieve the available services (a list of
	 * serviceID/ServiceName/ServiceDescription triplet) already registered by a
	 * specific user. Requires as input the Service ID.
	 * 
	 * @param applicationID
	 * @return
	 */
	@GET
	@Path("/getAvailableServiceIDs")
	public DescreptiveIDs getAvailableServiceIDs(@QueryParam("applicationID") String applicationID) {

		DescreptiveIDs descreptiveIDs = new DescreptiveIDs();

		return descreptiveIDs;
	}
	
	/**
	 * Used to retrieve the services defined by a user. It returns an OpenIoT
	 * Service Description Specification. Requires as input the User ID.
	 * 
	 * @param userID
	 * @return
	 */
	@GET
	@Path("/getAvailableApps")
	public OSDSpec getAvailableApps(@QueryParam("userID") String userID) {

		OSDSpec osdSpec = new OSDSpec();

		return osdSpec;
	}
	
}
