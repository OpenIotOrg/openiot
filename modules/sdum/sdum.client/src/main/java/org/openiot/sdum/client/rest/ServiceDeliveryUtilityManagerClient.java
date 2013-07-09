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
import org.openiot.commons.sparql.result.model.Binding;
import org.openiot.commons.sparql.result.model.Result;
import org.openiot.commons.sparql.result.model.Results;
import org.openiot.commons.sparql.result.model.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 */
public class ServiceDeliveryUtilityManagerClient 
{
	//logger
	final static Logger logger = LoggerFactory.getLogger(ServiceDeliveryUtilityManagerClient.class);
	
	static ClientRequestFactory clientRequestFactory;

	
	
	public ServiceDeliveryUtilityManagerClient() 
	{
		clientRequestFactory = new ClientRequestFactory(UriBuilder.
				fromUri("http://localhost:8080/sdum.core").build());
	}
	public ServiceDeliveryUtilityManagerClient(String sdumURL) 
	{
		clientRequestFactory = new ClientRequestFactory(UriBuilder.
				fromUri(sdumURL).build());
	}

	public void welcomeMessage() 
	{
		ClientRequest welcomeMessageClientRequest = 
				clientRequestFactory.createRelativeRequest("/rest/services");

		welcomeMessageClientRequest.accept(MediaType.TEXT_PLAIN);
		try {
			String str = welcomeMessageClientRequest.get(String.class).getEntity();
			logger.debug(str);
		} catch (Exception e) {
			logger.error("welcomeMessage getentity error",e);
		}
	}

	public void pollForReport(String serviceID)
	{
		ClientRequest pollForReportClientRequest = 
				clientRequestFactory.createRelativeRequest("/rest/services/pollforreport");

		pollForReportClientRequest.queryParameter("serviceID", serviceID);

		pollForReportClientRequest.accept("application/xml");

		// Handle the response
		ClientResponse<String> response;
		String str = null;

		try {
			response = pollForReportClientRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			str = response.getEntity();
			logger.debug(str);
		} catch (Exception e) {
			logger.error("pollForReport getentity error",e);
		}

		try {
			String sdumServiceResultSet_JAXB_CONTEXT = "org.openiot.commons.sdum.serviceresultset.model";

			JAXBContext context = JAXBContext
					.newInstance(sdumServiceResultSet_JAXB_CONTEXT);
			Unmarshaller um = context.createUnmarshaller();
			SdumServiceResultSet sdumServiceResultSet = (SdumServiceResultSet) um
					.unmarshal(new StreamSource(new StringReader(str)));

			System.out.println("---------Service Query result---------");

			for (Variable var : sdumServiceResultSet.getQueryResult()
					.getSparql().getHead().getVariable()) {
				System.out.println("----var:---- " + var.getName());
			}
			for (Result result : sdumServiceResultSet.getQueryResult()
					.getSparql().getResults().getResult()) {
				System.out.println("----result:---- ");

				for (Binding binding : result.getBinding()) {
					System.out.println("binding name: " + binding.getName());
					System.out.println("literal: "
							+ binding.getLiteral().getContent());
				}
			}

			System.out.println("---------Service Presentation---------");

			for (Widget widget : sdumServiceResultSet.getRequestPresentation()
					.getWidget()) {
				System.out.println("WidgetID: " + widget.getWidgetID());

				for (PresentationAttr presentationAttr : widget
						.getPresentationAttr()) {
					System.out.println("WidgetAttrName: "
							+ presentationAttr.getName() + " WidgetAttrValue: "
							+ presentationAttr.getValue());
				}
			}
		} catch (Exception e) {
			logger.error("pollForReport unmarshall error",e);
		}
	}

	
	
	public static void main(String[] args) throws Exception
	{
		ServiceDeliveryUtilityManagerClient sdumClient = 
					new ServiceDeliveryUtilityManagerClient();

		sdumClient.welcomeMessage();
		//sdumClient.pollForReport("nodeID://b47007");
	}
}
