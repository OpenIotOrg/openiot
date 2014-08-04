package org.openiot.sdum.core.rest;

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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.sdum.core.api.impl.GetAvailableServiceIDs.GetAvailableServiceIDsImpl;
import org.openiot.sdum.core.api.impl.GetAvailableAppIDs.GetAvailableAppIDsImpl;
import org.openiot.sdum.core.api.impl.GetService.GetServiceImpl;
import org.openiot.sdum.core.api.impl.GetApplication.GetApplicationImpl;
import org.openiot.sdum.core.api.impl.PollForReport.PollForReportImpl;



import org.openiot.sdum.core.utils.SecurityUtil;
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
public class ServiceDeliveryUtilityManagerRsControler {

	//Logger's initialization
	final static Logger logger = LoggerFactory.getLogger(ServiceDeliveryUtilityManagerRsControler.class);
	

	/**
	 * @return
	 */
	@GET()
	@Produces("text/plain")
	public String welcomeMessage() {

		String welcomeText;

		welcomeText = "Welcome to Service Delivery & Utility Manager Rest Interface\n" 
				+ "=============================================================\n\n"
				+ "This Interface provides the folowing Services:\n"
				+ "subscribeForReport(serviceID: String, dest: URI): String\n"
				+ "unsubscribe (serviceID: String): boolean\n"
				+ "pollForReport (serviceID: String): SdumServiceResultSet\n"
				+ "getSubscribers(serviceID: String): List<URI>\n"
				+ "getUtilityUsage(userID: String): UtilityUsage\n"
				+ "getServiceUsage(serviceID: String): ServiceUsage\n"
				+ "getServiceStatus (serviceID: String): ServiceStatus\n"
				+ "getService(serviceID: String): OSMO\n" + "getUser(userID: String): OpenIotUser\n"
				+ "getAvailableServicesIDs(userID: String): List<DescriptiveID>\n"
				+ "getAvailableServices (userID: String): OSDSpec";

		logger.debug(welcomeText);
		
		
		
		// ============READING PROPERIES=========================
		
		PropertyManagement propertyManagement = new PropertyManagement();
		
		// reading proeprty LSM_FUNCTIONAL_GRAPH
		logger.debug("lsmFunctionalGraph: " + propertyManagement.getSdumLsmFunctionalGraph());
		
		// reading proeprty LSM_SPARQL_END_POINT
		logger.debug("lsmSparqlEndPoint: " + propertyManagement.getSdumLsmSparqlEndPoint());
		
		

		return welcomeText;
	}
		

	/**
	 * Invokes a previously defined Service having the specified serviceID.
	 * 
	 * 
	 * @param applicationID
	 * @return
	 */
	@GET
	@Path("/pollforreport")
	public SdumServiceResultSet pollForReport(@QueryParam("serviceID") String applicationID, @QueryParam("clientId") String clientId, @QueryParam("token") String token) {

		if(!SecurityUtil.hasPermission(PermissionsUtil.SDUM_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
		
		PollForReportImpl pollForReportImpl = new PollForReportImpl(applicationID);

		return pollForReportImpl.getSdumServiceResultSet();

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

		if(!SecurityUtil.hasPermission(PermissionsUtil.SDUM_ALL, token, clientId)){
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

		if(!SecurityUtil.hasPermission(PermissionsUtil.SDUM_ALL, token, clientId)){
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

		if(!SecurityUtil.hasPermission(PermissionsUtil.SDUM_ALL, token, clientId)){
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

		if(!SecurityUtil.hasPermission(PermissionsUtil.SDUM_ALL, token, clientId)){
			logger.info("Missing required permissions");
			return null;
		}
		
		GetAvailableServiceIDsImpl availableServiceIDs = new GetAvailableServiceIDsImpl(applicationID);
		
		return availableServiceIDs.getAvailableServiceIDs();
	}
}
