package org.openiot.scheduler.client.rest;

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

import java.io.FileNotFoundException;
import java.io.StringReader;

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

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.Widget;

import org.openiot.commons.osdspec.utils.DeserializerUtil;

/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * 
 */
public class SchedulerClient {

	private ClientRequestFactory clientRequestFactory;

	public SchedulerClient(String schedulerURL) {

		clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(schedulerURL).build());
	}

	public SchedulerClient() {

		clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(
				"http://localhost:8080/scheduler.core").build());
	}

	public void discoverSensors() {

		ClientRequest discoverSensorsClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/discoverSensors");

		
		//Prepare the request
		discoverSensorsClientRequest.queryParameter("userID", "userIDString");
		discoverSensorsClientRequest.queryParameter("longitude", 6.631622);
		discoverSensorsClientRequest.queryParameter("latitude", 46.520131);
		discoverSensorsClientRequest.queryParameter("radius", 5F);

		discoverSensorsClientRequest.accept("application/xml");

		
		
		
		//Handle the response
		ClientResponse<String> response;
		String str = null;

		try {
			response = discoverSensorsClientRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			JAXBContext context = JAXBContext.newInstance(SensorTypes.class);
			Unmarshaller um = context.createUnmarshaller();
			SensorTypes sensorTypes = (SensorTypes) um.unmarshal(new StreamSource(new StringReader(str)));

			for (SensorType sensorType : sensorTypes.getSensorType()) {
				System.out.println("sensorType.getId():" + sensorType.getId());
				System.out.println("sensorType.getName():" + sensorType.getName());
				for (MeasurementCapability measurementCapability : sensorType.getMeasurementCapability()) {
					System.out.println("measurementCapability.getId():" + measurementCapability.getId());
					System.out.println("measurementCapability.getName():" + measurementCapability.getType());

					for (Unit unit : measurementCapability.getUnit()) {
						System.out.println("unit.getName():" + unit.getName());
						System.out.println("unit.getType():" + unit.getType());

					}
				}
			}

		}

		catch (Exception e) {

			e.printStackTrace();

		}

	}

	/**
	 * welcomeMessage
	 */
	public void welcomeMessage() {
		ClientRequest welcomeMessageClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services");

		welcomeMessageClientRequest.accept(MediaType.TEXT_PLAIN);
		try {
			String str = welcomeMessageClientRequest.get(String.class).getEntity();
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * registerService
	 */
	public void registerService() {

		ClientRequest registerServiceClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/registerService");

		registerServiceClientRequest.accept("application/xml");

		
		
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
		
		
		
		
		
		
		

		registerServiceClientRequest.body("application/xml", osdSpec);

		
		
		//Handle the response
		ClientResponse<String> response;
		String str = null;
		try {
			response = registerServiceClientRequest.post(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			System.out.println("===============Registered: " + str);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * @param osdSpecFilePathName
	 */
	public void registerFromFile(String osdSpecFilePathName) {
		
		
		ClientRequest registerServiceClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services/registerService");

		registerServiceClientRequest.accept("application/xml");
		
		
		
		OSDSpec osdSpec = new OSDSpec();
		
		//Open and Deserialize OSDSPec form file
		try {
			osdSpec = DeserializerUtil.deserializeOSDSpecFile(osdSpecFilePathName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		registerServiceClientRequest.body("application/xml", osdSpec);

		
		
		//Handle the response
		ClientResponse<String> response;
		String str = null;
		try {
			response = registerServiceClientRequest.post(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			str = response.getEntity();
			System.out.println("===============Registered: " + str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
