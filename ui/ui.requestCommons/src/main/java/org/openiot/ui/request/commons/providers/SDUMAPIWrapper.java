package org.openiot.ui.request.commons.providers;

import java.io.StringReader;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.ui.request.commons.providers.exceptions.APICommunicationException;
import org.openiot.ui.request.commons.providers.exceptions.APIException;

public class SDUMAPIWrapper {

	public static SdumServiceResultSet pollForReport(String serviceId) throws APICommunicationException, APIException {
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri("http://localhost:8080/sdum.core").build());
		ClientRequest discoverSensorsClientRequest = clientRequestFactory.createRelativeRequest("/rest/services/pollforreport");

		discoverSensorsClientRequest.queryParameter("serviceID", serviceId);
		discoverSensorsClientRequest.accept("application/xml");

		ClientResponse<String> response;
		String responseText = null;

		try {
			response = discoverSensorsClientRequest.get(String.class);

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
