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
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.ui.request.commons.providers.exceptions.APICommunicationException;
import org.openiot.ui.request.commons.providers.exceptions.APIException;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.net.URI;


public class SchedulerAPIWrapper {

	private static PropertyManagement propertyManagement = new PropertyManagement();

	private static URI getSchedulerUri() {
		return UriBuilder.fromUri(propertyManagement.getSchedulerHostUrl()).build();
	}

	public static SensorTypes getAvailableSensors(String userId, double lat, double lon, double radius) throws APICommunicationException, APIException {

		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(getSchedulerUri());
		ClientRequest discoverSensorsClientRequest = clientRequestFactory.createRelativeRequest("/rest/services/discoverSensors");

		discoverSensorsClientRequest.queryParameter("userID", userId);
		discoverSensorsClientRequest.queryParameter("latitude", lat);
		discoverSensorsClientRequest.queryParameter("longitude", lon);
		discoverSensorsClientRequest.queryParameter("radius", radius);

		discoverSensorsClientRequest.accept("application/xml");

		ClientResponse<String> response;
		String responseText = null;

		try {
			response = discoverSensorsClientRequest.get(String.class);

			if (response.getStatus() != 200) {
				throw new APICommunicationException("Error communicating with schedulerr (method: discoverSensors, HTTP error code : " + response.getStatus() + ")");
			}

			responseText = response.getEntity();

			JAXBContext jaxbContext = JAXBContext.newInstance(SensorTypes.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			SensorTypes sensorTypes = (SensorTypes) um.unmarshal(new StreamSource(new StringReader(responseText)));
			return sensorTypes;
		} catch (Throwable ex) {
			throw new APIException(ex);
		}
	}

	public static void registerService(OSDSpec osdSpec) throws APICommunicationException, APIException {

		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(getSchedulerUri());
		ClientRequest registerServiceRequest = clientRequestFactory.createRelativeRequest("/rest/services/registerService");

		try {
			registerServiceRequest.body("application/xml", osdSpec);

			ClientResponse<String> response;

			try {
				response = registerServiceRequest.post(String.class);

				if (response.getStatus() != 200) {
					throw new APICommunicationException("Error communicating with scheduler (method: registerService, HTTP error code : " + response.getStatus() + ")");
				}

			} catch (Throwable ex) {
				throw new APIException(ex);
			}
		} catch (Exception ex) {
			throw new APIException(ex);
		}
	}

	public static OSDSpec getAvailableApps(String userId) throws APICommunicationException, APIException {
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(getSchedulerUri());
		ClientRequest getAvailableAppsClientRequest = clientRequestFactory.createRelativeRequest("/rest/services/getAvailableApps");

		getAvailableAppsClientRequest.queryParameter("userID", userId);
		getAvailableAppsClientRequest.accept("application/xml");

		ClientResponse<String> response;
		String responseText = null;

		try {
				OSDSpec spec = null;
				response = getAvailableAppsClientRequest.get(String.class);

				if (response.getStatus() != 200) {
					throw new APICommunicationException("Error communicating with scheduler (method: getAvailableApps, HTTP error code : " + response.getStatus() + ")");
				}

				responseText = response.getEntity();

				JAXBContext jaxbContext = JAXBContext.newInstance(OSDSpec.class);
				Unmarshaller um = jaxbContext.createUnmarshaller();
				return (OSDSpec) um.unmarshal(new StreamSource(new StringReader(responseText)));
		} catch (Throwable ex) {
			throw new APIException(ex);
		}
	}

	public static String userLogin(String email, String password) throws APICommunicationException, APIException {
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(getSchedulerUri());
		ClientRequest userLoginClientRequest = clientRequestFactory.createRelativeRequest("/rest/services/userLogin");

		userLoginClientRequest.queryParameter("userMail", email);
		userLoginClientRequest.queryParameter("userPaswrd", password);

		ClientResponse<String> response;
		String responseText = null;

		try {
				response = userLoginClientRequest.get(String.class);

				if (response.getStatus() != 200) {
					throw new APICommunicationException("Error communicating with scheduler (method: userLogin, HTTP error code : " + response.getStatus() + ")");
				}

				responseText = response.getEntity();
				if("error checking if mail exists, cannot init repository".equals(responseText)){
					throw new APICommunicationException("Could not init user repository");
				}

				if( "user mail not found".equals(responseText) || "wrong password".equals(responseText)){
					throw new APIException("Login failed: " + responseText);
				}

				// Response text should be user id
				return responseText;

		} catch (Throwable ex) {
			throw new APIException(ex);
		}
	}

	public static String userRegister(String name, String email, String password) throws APICommunicationException, APIException {
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(getSchedulerUri());
		ClientRequest userRegisterClientRequest = clientRequestFactory.createRelativeRequest("/rest/services/userRegister");

		userRegisterClientRequest.queryParameter("userName", name);
		userRegisterClientRequest.queryParameter("userMail", email);
		userRegisterClientRequest.queryParameter("description", "");
		userRegisterClientRequest.queryParameter("password", password);

		ClientResponse<String> response;
		String responseText = null;

		try {
				response = userRegisterClientRequest.get(String.class);

				if (response.getStatus() != 200) {
					throw new APICommunicationException("Error communicating with scheduler (method: userRegister, HTTP error code : " + response.getStatus() + ")");
				}

				responseText = response.getEntity();
				if("error checking if mail already exists".equals(responseText) || "register user error".equals(responseText)){
					throw new APICommunicationException("Could not init user repository");
				}

				if( "mail already exists".equals(responseText)){
					throw new APIException("Registration failed: " + responseText);
				}

				// Response text should be new user id
				return responseText;

		} catch (Throwable ex) {
			throw new APIException(ex);
		}
	}

}
