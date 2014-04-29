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
package org.openiot.ui.request.commons.providers;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.ui.request.commons.providers.exceptions.APICommunicationException;
import org.openiot.ui.request.commons.providers.exceptions.APIException;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.net.URI;

public class SDUMAPIWrapper {

	private static final URI SDUM_HOST_URL;

	static  {
		PropertyManagement propertyManagement = new PropertyManagement();
		SDUM_HOST_URL =  UriBuilder.fromUri(propertyManagement.getRequestCommonsSdumHostUrl()).build();
	}

	public static SdumServiceResultSet pollForReport(String serviceId) throws APICommunicationException, APIException {
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(SDUM_HOST_URL);
		ClientRequest pollForReportClientRequest = clientRequestFactory.createRelativeRequest("/rest/services/pollforreport");

		pollForReportClientRequest.queryParameter("serviceID", serviceId);
		pollForReportClientRequest.accept("application/xml");

		ClientResponse<String> response;
		String responseText = null;

		try {
			response = pollForReportClientRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new APICommunicationException("Error communicating with SDUM (method: pollForReport, serviceId: " + serviceId + ", HTTP error code : " + response.getStatus() + ")");
			}
			
			responseText = response.getEntity();

			JAXBContext jaxbContext = JAXBContext.newInstance(SdumServiceResultSet.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			SdumServiceResultSet resultSet = (SdumServiceResultSet) um.unmarshal(new StreamSource(new StringReader(responseText)));
			return resultSet;
		} catch (Throwable ex) {
			throw new APIException(ex);
		}
	}
}
