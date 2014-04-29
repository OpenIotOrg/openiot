package org.openiot.sdum.client.rest;

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
import org.openiot.commons.sparql.protocoltypes.model.QueryResult;
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

			logger.debug("---------Service Query result---------");
			
			for (QueryResult queryResult :sdumServiceResultSet.getQueryResult()){
				
				for (Variable var : queryResult.getSparql().getHead().getVariable()) {
					logger.debug("----var:---- " + var.getName());
				}
				
				for (Result result : queryResult.getSparql().getResults().getResult()) {
					logger.debug("----result:---- ");

					for (Binding binding : result.getBinding()) {
						logger.debug("binding name: " + binding.getName());
						logger.debug("literal: "
								+ binding.getLiteral().getContent());
					}
				}				
			}

			logger.debug("---------Service Presentation---------");

			for (Widget widget : sdumServiceResultSet.getRequestPresentation()
					.getWidget()) {
				logger.debug("WidgetID: " + widget.getWidgetID());

				for (PresentationAttr presentationAttr : widget
						.getPresentationAttr()) {
					logger.debug("WidgetAttrName: "
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
