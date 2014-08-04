package org.openiot.scheduler.core.rest;

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




import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.scheduler.core.api.impl.DiscoverSensors.DiscoverSensorsImpl;
import org.openiot.scheduler.core.api.impl.GetApplication.GetApplicationImpl;
import org.openiot.scheduler.core.api.impl.GetAvailableAppIDs.GetAvailableAppIDsImpl;
import org.openiot.scheduler.core.api.impl.GetAvailableApps.GetAvailableAppsImpl;
import org.openiot.scheduler.core.api.impl.GetAvailableServiceIDs.GetAvailableServiceIDsImpl;
import org.openiot.scheduler.core.api.impl.GetService.GetServiceImpl;
import org.openiot.scheduler.core.api.impl.RegisterService.RegisterServiceImpl;
import org.openiot.scheduler.core.api.impl.UserLogin.UserLoginImpl;
import org.openiot.scheduler.core.api.impl.UserRegister.UserRegisterImpl;
//import org.openiot.scheduler.core.api.impl.DiscoverSensorsImpl;
//import org.openiot.scheduler.core.api.impl.RegisterServiceImpl;
import org.openiot.scheduler.core.utils.SecurityUtil;
import org.openiot.security.client.PermissionsUtil;
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
		
		
		// ============READING PROPERIES=========================
		
		PropertyManagement propertyManagement = new PropertyManagement();
		
		
		
		// reading proeprty LSM_META_GRAPH
		logger.debug("lsmMetaGraph: " + propertyManagement.getSchedulerLsmMetaGraph());
		
		// reading proeprty LSM_DATA_GRAPH
		logger.debug("lsmDataGraph: " + propertyManagement.getSchedulerLsmDataGraph());
		
		// reading proeprty LSM_FUNCTIONAL_GRAPH
		logger.debug("lsmFunctionalGraph: " + propertyManagement.getSchedulerLsmFunctionalGraph());
		
		// reading proeprty LSM_USER_NAME
		logger.debug("lsmUserName: " + propertyManagement.getSchedulerLsmUserName());
		
		// reading proeprty LSM_PASSWORD
		logger.debug("lsmPassword: " + propertyManagement.getSchedulerLsmPassword());
		
		
		
		
        
		return welcomeText;
	}
	

	
	/**
	 *
	 */
	@GET
	@Path("/userRegister")
	//@Consumes("application/xml")
	public String userRegister(@QueryParam("userName") String userName, @QueryParam("userMail") String userMail, @QueryParam("description") String description, @QueryParam("password") String passwd) {
		
		UserRegisterImpl userRegister = new UserRegisterImpl(userName, userMail, description, passwd);		
		return userRegister.getReplyMessage();
	}
	
	
	
	@GET
	@Path("/userLogin")
//	@Consumes("application/xml")
//	@Produces("application/xml")
	public String userLogin(@QueryParam("userMail") String userMail,@QueryParam("userPaswrd") String userPaswrd  ) {
		
		UserLoginImpl userLogin = new UserLoginImpl(userMail,userPaswrd);	
		return userLogin.getReplyMessage();
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
	public SensorTypes discoverSensors(@QueryParam("userID") String userID, @QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude,
			@QueryParam("radius") float radius, @QueryParam("clientId") String clientId, @QueryParam("token") String token) {

		if(!SecurityUtil.hasPermission(PermissionsUtil.SCHEDULER_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
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
	 * @throws JAXBException 
	 */
	@POST
	@Path("/registerService")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public String registerService(OSDSpec osdSpec,  @QueryParam("clientId") String clientId, @QueryParam("token") String token) throws JAXBException {

		System.out.println("osdSpec: " + osdSpec);
		System.out.println(String.format("clientId: %s, token: %s", clientId, token));
		
		if(!SecurityUtil.hasPermission(PermissionsUtil.SCHEDULER_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
		
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

		
		//TODO: Implement this functionality

	}	
	
	
	/**
	 * Used to update a registered service. Requires as input the OSD Specification.
	 * 
	 * @param osdSpec
	 */
	@POST
	@Path("/updateApp")
	public void updateApp(OSDSpec osdSpec) {

		//TODO: Implement this functionality

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
	public OAMO getApplication(@QueryParam("applicationID") String applicationID, @QueryParam("clientId") String clientId, @QueryParam("token") String token) {

		if(!SecurityUtil.hasPermission(PermissionsUtil.SCHEDULER_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
		
		GetApplicationImpl application = new GetApplicationImpl(applicationID);		

		return application.getOAMO();
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
	public OSMO getService(@QueryParam("serviceID") String serviceID, @QueryParam("clientId") String clientId, @QueryParam("token") String token) {

		if(!SecurityUtil.hasPermission(PermissionsUtil.SCHEDULER_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
		
		GetServiceImpl service =  new GetServiceImpl(serviceID);		
		
		return service.getService();
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
	public DescreptiveIDs getAvailableAppIDs(@QueryParam("userID") String userID, @QueryParam("clientId") String clientId, @QueryParam("token") String token) {

		if(!SecurityUtil.hasPermission(PermissionsUtil.SCHEDULER_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
		
		GetAvailableAppIDsImpl availableAppIDs = new GetAvailableAppIDsImpl(userID);

		return availableAppIDs.getAvailableAppIDs();
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
	public DescreptiveIDs getAvailableServiceIDs(@QueryParam("applicationID") String applicationID, @QueryParam("clientId") String clientId, @QueryParam("token") String token) {

		if(!SecurityUtil.hasPermission(PermissionsUtil.SCHEDULER_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
		
		GetAvailableServiceIDsImpl availableServiceIDs = new GetAvailableServiceIDsImpl(applicationID);
				
		return availableServiceIDs.getAvailableServiceIDs();
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
	public OSDSpec getAvailableApps(@QueryParam("userID") String userID, @QueryParam("clientId") String clientId, @QueryParam("token") String token) {

		if(!SecurityUtil.hasPermission(PermissionsUtil.SCHEDULER_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
		
		GetAvailableAppsImpl availableApps = new GetAvailableAppsImpl(userID); 

		return availableApps.getAvailableApps();
	}
	
}
