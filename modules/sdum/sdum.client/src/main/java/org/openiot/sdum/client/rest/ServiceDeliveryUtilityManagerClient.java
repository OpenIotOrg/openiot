package org.openiot.sdum.client.rest;

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

import java.io.StringReader;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.openiot.commons.sdum.serviceresultset.model.PresentationAttr;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.commons.sdum.serviceresultset.model.Widget;
import org.openiot.commons.sensortypes.model.SensorTypes;


/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
public class ServiceDeliveryUtilityManagerClient {

	static ClientRequestFactory clientRequestFactory;

	
	
	public ServiceDeliveryUtilityManagerClient(String sdumURL){
		
		
		clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(sdumURL).build());
		
	}
	
	
	
	public ServiceDeliveryUtilityManagerClient(){
		
		
		clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(
				"http://localhost:8080/sdum.core").build());
		
	}
	
	
//	public static void main(String[] args) throws Exception {
//
//		clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(
//				"http://localhost:8080/sdum.core").build());
//
//		 welcomeMessage();
//		//discoverSensors();
//
//	}

	public void pollForReport() {
	
			
			
			
			ClientRequest pollForReportClientRequest = clientRequestFactory.createRelativeRequest("/rest/services/pollforreport");

			pollForReportClientRequest.queryParameter("serviceID", "osmo-graphNode_722933770218514");

			pollForReportClientRequest.accept("application/xml");


			
			//Handle the response
			ClientResponse<String> response;
			String str = null;

			try {
				response = pollForReportClientRequest.get(String.class);				
				
				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
				}

				str = response.getEntity();
				System.out.println(str);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			try {
				String sdumServiceResultSet_JAXB_CONTEXT = "org.openiot.commons.sdum.serviceresultset.model";
				
				
				
				JAXBContext context = JAXBContext.newInstance(sdumServiceResultSet_JAXB_CONTEXT);
				Unmarshaller um = context.createUnmarshaller();
				SdumServiceResultSet sdumServiceResultSet = (SdumServiceResultSet) um.unmarshal(new StreamSource(new StringReader(str)));
	
				
				for (Widget widget : sdumServiceResultSet.getRequestPresentation().getWidget()){
					
					System.out.println("WidgetID: "+widget.getWidgetID());
					
					
					for(PresentationAttr presentationAttr: widget.getPresentationAttr()){
					
						System.out.println("WidgetAttrName: "+presentationAttr.getName()+ "WidgetAttrValue: "+presentationAttr.getValue());

					}
					
				}
				
			}

			catch (Exception e) {

				e.printStackTrace();

			}

		
			
	}

	
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

}
