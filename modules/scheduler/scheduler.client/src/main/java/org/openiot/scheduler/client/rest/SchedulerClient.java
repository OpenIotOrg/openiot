package org.openiot.scheduler.client.rest;

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

import java.io.FileNotFoundException;
import java.io.StringReader;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;

import org.openiot.commons.sensortypes.model.MeasurementCapability;
import org.openiot.commons.sensortypes.model.SensorType;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.sensortypes.model.Unit;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.descriptiveids.model.DescriptiveID;
import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.Widget;

import org.openiot.commons.osdspec.utils.Utilities;
import org.openiot.commons.osdspec.utils.Utilities.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 */
public class SchedulerClient 
{
	//logger
	final static Logger logger = LoggerFactory.getLogger(SchedulerClient.class);
	
	private ClientRequestFactory clientRequestFactory;
	
	public SchedulerClient() 
	{
		clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(
				"http://localhost:8080/scheduler.core").build());
	}
	public SchedulerClient(String schedulerURL) 
	{
		clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(
				schedulerURL).build());
	}
	
	/**
 	 * Prints the available services of the scheduler interface. 
	 * Can be used to check that the scheduler service is alive.
	 * 
	 * @return the welcome message 
	 */
	public String welcomeMessage() 
	{
		ClientRequest welcomeMessageClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services");

		welcomeMessageClientRequest.accept(MediaType.TEXT_PLAIN);
		try {
			String str = welcomeMessageClientRequest.get(String.class).getEntity();
			logger.debug(str);
			return str;
		} catch (Exception e) {
			logger.error("WelcomeMessage getEntity error",e);
			return null;
		}
	}

	/**
	 * Returns the properties of all the sensors deployed in the area defined 
	 * by lon,lat and radius.
	 *  
	 * @param longitude 
	 * @param lat
	 * @param radius
	 * 
	 * @return the sensortypes discovered 
	 */
	public SensorTypes discoverSensors(double longitude, double lat, float radius, String clientId, String token) 
	{
		ClientRequest discoverSensorsClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/discoverSensors");
		
		//Prepare the request
		discoverSensorsClientRequest.queryParameter("userID", "userIDString");
		discoverSensorsClientRequest.queryParameter("longitude", longitude);
		discoverSensorsClientRequest.queryParameter("latitude", lat);
		discoverSensorsClientRequest.queryParameter("radius", radius);
		discoverSensorsClientRequest.queryParameter("clientId", clientId);
		discoverSensorsClientRequest.queryParameter("token", token);

		discoverSensorsClientRequest.accept("application/xml");
		
		//Handle the response		
		String str = null;
		try {
			ClientResponse<String> response = discoverSensorsClientRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			logger.debug(str);
		} catch (Exception e) {
			logger.error("discoverSensors getEntity error",e);
			//no need to proceed to umarshalling
			return null;
		}

		try {
			String sensorTypes_JAXB_CONTEXT = "org.openiot.commons.sensortypes.model";						
			
			JAXBContext context = JAXBContext.newInstance(sensorTypes_JAXB_CONTEXT);
			Unmarshaller um = context.createUnmarshaller();
			SensorTypes sensorTypes = (SensorTypes) um.unmarshal(new StreamSource(new StringReader(str)));

			//debug
			for (SensorType sensorType : sensorTypes.getSensorType()) {
				logger.debug("sensorType.getId():" + sensorType.getId());
				logger.debug("sensorType.getName():" + sensorType.getName());
				
				for (MeasurementCapability measurementCapability : sensorType.getMeasurementCapability()) {
					logger.debug("measurementCapability.getId():" + measurementCapability.getId());
					logger.debug("measurementCapability.getName():" + measurementCapability.getType());

					for (Unit unit : measurementCapability.getUnit()) {
						logger.debug("unit.getName():" + unit.getName());
						logger.debug("unit.getType():" + unit.getType());
					}
				}
			}
			
			return sensorTypes;
		}
		catch (Exception e) {
			logger.error("Unmarshal SensorTypes error",e);
			return null;
		}
	}
	
	
	public String userRegister(String userName, String userMail,String description, String passwd) 
	{
		ClientRequest registerUserClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/userRegister");

		

		registerUserClientRequest.queryParameter("userName", userName);
		registerUserClientRequest.queryParameter("userMail", userMail);
		registerUserClientRequest.queryParameter("description", description);
		registerUserClientRequest.queryParameter("password", passwd);
	
		registerUserClientRequest.accept("application/xml");
		
		//Handle the response
		try {
			ClientResponse<String> response = registerUserClientRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			String responseStr = response.getEntity();
			logger.debug(responseStr);
			return responseStr;
		} catch (Exception e) {
			logger.error("register user get response entity error",e);
			return null;
		}
	}
	
	public String userLogin(String userMail, String passwd) 
	{
		ClientRequest registerUserClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/userLogin");

		registerUserClientRequest.queryParameter("userMail", userMail);
		registerUserClientRequest.queryParameter("userPaswrd", passwd);
	
		registerUserClientRequest.accept("application/xml");
		
		//Handle the response
		try {
			ClientResponse<String> response = registerUserClientRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			String responseStr = response.getEntity();
			logger.debug(responseStr);
			return responseStr;
		} catch (Exception e) {
			logger.error("login user get response entity error",e);
			return null;
		}
	}
	
	/**
	 * Stores a service created by the user.
	 * 
	 * @param osdSpec the service specification
	 * 
	 * @return the response from the server, null if something went wrong
	 * 
	 */	
	public String registerService(OSDSpec osdSpec, String clientId, String token) 
	{
		ClientRequest registerServiceClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/registerService");

		registerServiceClientRequest.queryParameter("clientId", clientId);
		registerServiceClientRequest.queryParameter("token", token);
		registerServiceClientRequest.body(MediaType.APPLICATION_XML, osdSpec);
		
		//Handle the response
		try {
			ClientResponse<String> response = registerServiceClientRequest.post(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			String responseStr = response.getEntity();
			logger.debug("Service registered successfully: " + responseStr);
			return responseStr;
		} catch (Exception e) {
			logger.error("register service get response entity error",e);
			return null;
		}
	}

	public OAMO getApplication(String applicationID, String clientId, String token)
	{
		ClientRequest getApplicationRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/getApplication");
		
		//Prepare the request
		getApplicationRequest.queryParameter("applicationID", applicationID);
		getApplicationRequest.queryParameter("clientId", clientId);
		getApplicationRequest.queryParameter("token", token);
		
		getApplicationRequest.accept("application/xml");
		
		//Handle the response		
		String str = null;
		try {
			ClientResponse<String> response = getApplicationRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			logger.debug(str);
		} catch (Exception e) {
			logger.error("getApplicationRequest getEntity error",e);
			//no need to proceed to umarshalling
			return null;
		}
		
		try {
			String sensorTypes_JAXB_CONTEXT = "org.openiot.commons.osdspec.model";						
			
			JAXBContext context = JAXBContext.newInstance(sensorTypes_JAXB_CONTEXT);
			Unmarshaller um = context.createUnmarshaller();
			OAMO oamo = (OAMO) um.unmarshal(new StreamSource(new StringReader(str)));
			
			logger.debug("oamo.getId():" + oamo.getId());
			logger.debug("oamo.getName():" + oamo.getName());
			logger.debug("oamo.getDescription():" + oamo.getDescription());
			logger.debug("oamo.oamo.getGraphMeta():" + oamo.getGraphMeta());
			

			//debug
			for (OSMO osmo : oamo.getOSMO()) {
				logger.debug("osmo.getId():" + osmo.getId());
				logger.debug("osmo.getName():" + osmo.getName());
				logger.debug("osmo.getName():" + osmo.getDescription());
				
				for (QueryRequest qr : osmo.getQueryRequest()) {
					logger.debug("qr.getQuery():" + qr.getQuery());
				}
				
				for (Widget w : osmo.getRequestPresentation().getWidget()) {
					
					logger.debug("w.getWidgetID():" + w.getWidgetID());
					for (PresentationAttr pattr : w.getPresentationAttr()) {
						logger.debug("pattr.getName():" + pattr.getName());
						logger.debug("pattr.getValue():" + pattr.getValue());
					}
				}
			}
			
			return oamo;
		}
		catch (Exception e) {
			logger.error("Unmarshal OAMO error",e);
			return null;
		}
	}
	
	public OSMO getService(String serviceID, String clientId, String token)
	{
		ClientRequest getServiceRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/getService");
		
		//Prepare the request
		getServiceRequest.queryParameter("serviceID", serviceID);
		getServiceRequest.queryParameter("clientId", clientId);
		getServiceRequest.queryParameter("token", token);
		
		getServiceRequest.accept("application/xml");
		
		
		
		//Handle the response		
		String str = null;
		try {
			ClientResponse<String> response = getServiceRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			logger.debug(str);
		} catch (Exception e) {
			logger.error("getServiceRequest getEntity error",e);
			//no need to proceed to umarshalling
			return null;
		}
		
		try {
			String sensorTypes_JAXB_CONTEXT = "org.openiot.commons.osdspec.model";						
			
			JAXBContext context = JAXBContext.newInstance(sensorTypes_JAXB_CONTEXT);
			Unmarshaller um = context.createUnmarshaller();
			OSMO osmo = (OSMO) um.unmarshal(new StreamSource(new StringReader(str)));
					

			//debug			
			logger.debug("osmo.getId():" + osmo.getId());
			logger.debug("osmo.getName():" + osmo.getName());
			logger.debug("osmo.getName():" + osmo.getDescription());
			
			for (QueryRequest qr : osmo.getQueryRequest()) {
				logger.debug("qr.getQuery():" + qr.getQuery());
			}
			
			for (Widget w : osmo.getRequestPresentation().getWidget()) {
				
				logger.debug("w.getWidgetID():" + w.getWidgetID());
				for (PresentationAttr pattr : w.getPresentationAttr()) {
					logger.debug("pattr.getName():" + pattr.getName());
					logger.debug("pattr.getValue():" + pattr.getValue());
				}
			}
			
			
			return osmo;
		}
		catch (Exception e) {
			logger.error("Unmarshal OAMO error",e);
			return null;
		}
	}
	
	public DescreptiveIDs getAvailableAppIDs(String userID, String clientId, String token)
	{
		ClientRequest getServiceRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/getAvailableAppIDs");
		
		//Prepare the request
		getServiceRequest.queryParameter("userID", userID);
		getServiceRequest.queryParameter("clientId", clientId);
		getServiceRequest.queryParameter("token", token);
		
		getServiceRequest.accept("application/xml");
		
		//Handle the response		
		String str = null;
		try {
			ClientResponse<String> response = getServiceRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			logger.debug(str);
		} catch (Exception e) {
			logger.error("getServiceRequest getEntity error",e);
			//no need to proceed to umarshalling
			return null;
		}
		
		try {
			String sensorTypes_JAXB_CONTEXT = "org.openiot.commons.descriptiveids.model";						
			
			JAXBContext context = JAXBContext.newInstance(sensorTypes_JAXB_CONTEXT);
			Unmarshaller um = context.createUnmarshaller();
			DescreptiveIDs dids = (DescreptiveIDs) um.unmarshal(new StreamSource(new StringReader(str)));

			//debug			
			for (DescriptiveID did : dids.getDescriptiveID()) {				
				logger.debug("did.getId():" + did.getId());
				logger.debug("did.getName():" + did.getName());
				logger.debug("did.getDescription():" + did.getDescription());				
			}
			
			return dids;
		}
		catch (Exception e) {
			logger.error("Unmarshal DescriptiveID error",e);
			return null;
		}
	}
	
	public DescreptiveIDs getAvailableServiceIDs(String applicationID, String clientId, String token)
	{
		ClientRequest getServiceRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/getAvailableServiceIDs");
		
		//Prepare the request
		getServiceRequest.queryParameter("applicationID", applicationID);
		getServiceRequest.queryParameter("clientId", clientId);
		getServiceRequest.queryParameter("token", token);
		
		getServiceRequest.accept("application/xml");
		
		//Handle the response		
		String str = null;
		try {
			ClientResponse<String> response = getServiceRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			logger.debug(str);
		} catch (Exception e) {
			logger.error("getServiceRequest getEntity error",e);
			//no need to proceed to umarshalling
			return null;
		}
		
		try {
			String sensorTypes_JAXB_CONTEXT = "org.openiot.commons.descriptiveids.model";						
			
			JAXBContext context = JAXBContext.newInstance(sensorTypes_JAXB_CONTEXT);
			Unmarshaller um = context.createUnmarshaller();
			DescreptiveIDs dids = (DescreptiveIDs) um.unmarshal(new StreamSource(new StringReader(str)));

			//debug			
			for (DescriptiveID did : dids.getDescriptiveID()) {				
				logger.debug("did.getId():" + did.getId());
				logger.debug("did.getName():" + did.getName());
				logger.debug("did.getDescription():" + did.getDescription());				
			}
			
			return dids;
		}
		catch (Exception e) {
			logger.error("Unmarshal DescriptiveID error",e);
			return null;
		}
	}
	
	public OSDSpec getAvailableApps(String userID, String clientId, String token)
	{
		ClientRequest getServiceRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/getAvailableApps");
		
		//Prepare the request
		getServiceRequest.queryParameter("userID", userID);
		getServiceRequest.queryParameter("clientId", clientId);
		getServiceRequest.queryParameter("token", token);
		
		getServiceRequest.accept("application/xml");
		
		//Handle the response		
		String str = null;
		try {
			ClientResponse<String> response = getServiceRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			logger.debug(str);
		} catch (Exception e) {
			logger.error("getAvailableApps getEntity error",e);
			//no need to proceed to umarshalling
			return null;
		}
		
		try {
			String sensorTypes_JAXB_CONTEXT = "org.openiot.commons.osdspec.model";						
			
			JAXBContext context = JAXBContext.newInstance(sensorTypes_JAXB_CONTEXT);
			Unmarshaller um = context.createUnmarshaller();
			OSDSpec osdspec = (OSDSpec) um.unmarshal(new StreamSource(new StringReader(str)));

			logger.debug("osdspec.getUserID():" + osdspec.getUserID());
			for (OAMO oamo : osdspec.getOAMO()) 
			{				
				logger.debug("oamo.getId():" + oamo.getId());
				logger.debug("oamo.getName():" + oamo.getName());
				logger.debug("oamo.getDescription():" + oamo.getDescription());
				

				//debug
				for (OSMO osmo : oamo.getOSMO()) {
					logger.debug("osmo.getId():" + osmo.getId());
					logger.debug("osmo.getName():" + osmo.getName());
					logger.debug("osmo.getName():" + osmo.getDescription());
					
					for (QueryRequest qr : osmo.getQueryRequest()) {
						logger.debug("qr.getQuery():" + qr.getQuery());
					}
					
					for (Widget w : osmo.getRequestPresentation().getWidget()) {
						
						logger.debug("w.getWidgetID():" + w.getWidgetID());
						for (PresentationAttr pattr : w.getPresentationAttr()) {
							logger.debug("pattr.getName():" + pattr.getName());
							logger.debug("pattr.getValue():" + pattr.getValue());
						}
					}
				}
			}
			
			return osdspec;
		}
		catch (Exception e) {
			logger.error("Unmarshal DescriptiveID error",e);
			return null;
		}
	}
	
	
	// helper methods //	
	
	/**
	 * Creates a predifined spec and calls registerService(OSDSpec osdSpec) to
	 * register it. 
	 * 
	 * @return the response from the server, null if something went wrong
	 */
	public String registerDemoService(String clientId, String token) 
	{
		//Prepare the request
		OSDSpec osdSpec = new OSDSpec();
		osdSpec.setUserID("Nikos-Kefalakis");
//		
//		//set it and forget it
//		OAMO oamo1 = new OAMO();
//		
//		oamo1.setId("OpenIoTApplicationModelObject_1");
//		oamo1.setName("OpenIoTApplicationModelObject1Name");
//
//		
//		
//		//equivalent to service entity
//		OSMO osmo1 = new OSMO();
//		
//		osmo1.setDescription("The New Hyper Service test");
//				
//		//(kane select apo thn vash gia to Service ID)
//		osmo1.setId("SensorModelObjectServiceID");
//		osmo1.setDescription("OpenIoT Sensor Model Object 1");
//		osmo1.setName("SensorModelObjectName");
//		
//		
//		
//		//ADD WIDGET
//		Widget widget1 = new Widget();
//		
//		//to WidgetID tha sto stelnei katheytheian o achileas (equivalent to widgetAvailable)
//		widget1.setWidgetID("TheYperwidgetID");
//		
//		PresentationAttr presentationAttr1 = new PresentationAttr();
//		presentationAttr1.setName("widget1PresentationAttr1Name");
//		presentationAttr1.setValue("widget1PresentationAttr1Value");
//		
//		PresentationAttr presentationAttr2 = new PresentationAttr();
//		presentationAttr2.setName("widget1PresentationAttr2Name");
//		presentationAttr2.setValue("widget1PresentationAttr2Value");
//		
//		
//		widget1.getPresentationAttr().add(presentationAttr1);
//		widget1.getPresentationAttr().add(presentationAttr2);
//		
//		osmo1.getRequestPresentation().getWidget().add(widget1);
//
//		
//		
//		//ADD QUERY REQUEST
//		
//		osmo1.getQueryRequest().setQuery("SELECT * FROM <openiot> WHERE {?s ?p ?o}");
//		
//		
//			
//		oamo1.getOSMO().add(osmo1);
//		
//		osdSpec.getOAMO().add(oamo1);
		
		return registerService(osdSpec, clientId, token);
	}
		
	/**
	 * Registers a service from an xml file 
	 * 
	 * @param osdSpec  the path of the osdspec XML file
	 * 
	 * @return the response from the server, null if something went wrong
	 */
	public String registerFromFile(String osdSpecFilePathName, String clientId, String token) throws FileNotFoundException,Exception
	{				
		OSDSpec osdSpec = null;
		
		//Open and Deserialize OSDSPec form file
		try {
			osdSpec = Utilities.Deserializer.deserializeOSDSpecFile(osdSpecFilePathName);
		} catch (FileNotFoundException e) {
			logger.error("File Not Found",e);
			throw e;
		} catch (Exception e) {
			logger.error("error creating osdspec object",e);
			throw e;
		}
		
		return registerService(osdSpec, clientId, token);		
	}
}
