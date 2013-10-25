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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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

	
	// =============GLOBAL VARIABLES DEFINITION/INITIALIZATION========================
    private static final String PROPERTIES_FILE = "openiot.properties";
	
	private static final String LSM_META_GRAPH = "scheduler.core.lsm.openiotMetaGraph";
	private static final String LSM_DATA_GRAPH = "scheduler.core.lsm.openiotDataGraph";
	private static final String LSM_FUNCTIONAL_GRAPH = "scheduler.core.lsm.openiotFunctionalGraph";
	private static final String LSM_USER_NAME="scheduler.core.lsm.access.username";
	private static final String LSM_PASSWORD="scheduler.core.lsm.access.password";

	private Properties props = null;
    

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
		initializeProperties();
		
		// reading proeprty LSM_META_GRAPH
		String lsmMetaGraph = props.getProperty(LSM_META_GRAPH);
		logger.debug("lsmMetaGraph: " + lsmMetaGraph);
		
		// reading proeprty LSM_DATA_GRAPH
		String lsmDataGraph = props.getProperty(LSM_DATA_GRAPH);
		logger.debug("lsmDataGraph: " + lsmDataGraph);
		
		// reading proeprty LSM_FUNCTIONAL_GRAPH
		String lsmFunctionalGraph = props.getProperty(LSM_FUNCTIONAL_GRAPH);
		logger.debug("lsmFunctionalGraph: " + lsmFunctionalGraph);
		
		// reading proeprty LSM_USER_NAME
		String lsmUserName = props.getProperty(LSM_USER_NAME);
		logger.debug("lsmUserName: " + lsmUserName);
		
		// reading proeprty LSM_PASSWORD
		String lsmPassword = props.getProperty(LSM_PASSWORD);
		logger.debug("lsmPassword: " + lsmPassword);
		
		
		
		
        
		return welcomeText;
	}
	
	
	/**
	 * Initialize the Properties
	 */
	private void initializeProperties() {

		String jbosServerConfigDir = System.getProperty("jboss.server.config.dir");
		String openIotConfigFile = jbosServerConfigDir + File.separator + PROPERTIES_FILE;
		props = new Properties();

		logger.debug("jbosServerConfigDir:" + openIotConfigFile);

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(openIotConfigFile);

		} catch (FileNotFoundException e) {
			// TODO Handle exception

			logger.error("Unable to find file: " + openIotConfigFile);

		}

		// loading properites from properties file
		try {
			props.load(fis);
		} catch (IOException e) {
			// TODO Handle exception
			logger.error("Unable to load properties from file " + openIotConfigFile);
		}

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
	public OAMO getApplication(@QueryParam("applicationID") String applicationID) {

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
	public OSMO getService(@QueryParam("serviceID") String serviceID) {

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
	public DescreptiveIDs getAvailableAppIDs(@QueryParam("userID") String userID) {

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
	public DescreptiveIDs getAvailableServiceIDs(@QueryParam("applicationID") String applicationID) {

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
	public OSDSpec getAvailableApps(@QueryParam("userID") String userID) {

		GetAvailableAppsImpl availableApps = new GetAvailableAppsImpl(userID); 

		return availableApps.getAvailableApps();
	}
	
}
