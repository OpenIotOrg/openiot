package org.openiot.ui.request.commons.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.providers.exceptions.APICommunicationException;
import org.openiot.ui.request.commons.providers.exceptions.APIException;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;


public class SchedulerAPIWrapper {

	public static SensorTypes getAvailableSensors(String userId, double lat, double lon, double radius) throws APICommunicationException, APIException {
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri("http://localhost:8080/scheduler.core").build());
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
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri("http://localhost:8080/scheduler.core").build());
		ClientRequest registerServiceRequest = clientRequestFactory.createRelativeRequest("/rest/services/registerService");

		try {
			String osdSpecString = marshalOSDSpec(osdSpec);

			registerServiceRequest.body("application/xml", osdSpecString);

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
	
	public static String marshalOSDSpec(OSDSpec osdSpec) throws APIException{
		String osdSpecString = "";
		try {
			JAXBContext jc = JAXBContext.newInstance(OSDSpec.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			java.io.StringWriter sw = new StringWriter();
			marshaller.marshal(osdSpec, sw);
			osdSpecString = sw.toString().replace("&lt;", "<").replace("&gt;", ">");

			LoggerService.log(Level.INFO, osdSpecString);
			return osdSpecString;
		} catch (Exception ex) {
			throw new APIException(ex);
		}
	}
	
	public static OSDSpec unmarshalOSDSpec(String osdSpecString) throws APIException{
		try {
			// Unserialize
			JAXBContext jaxbContext = JAXBContext.newInstance(OSDSpec.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			OSDSpec spec = (OSDSpec) um.unmarshal(new StreamSource(new StringReader(osdSpecString)));
			
			return spec;
		} catch (Exception ex) {
			throw new APIException(ex);
		}
	}

	public static OSDSpec getAvailableApps(String userId) throws APICommunicationException, APIException {
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri("http://localhost:8080/scheduler.core").build());
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
	
	public static OSDSpec getAvailableServices(String userId) throws APICommunicationException, APIException {
		ClientRequestFactory clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri("http://localhost:8080/scheduler.core").build());
		ClientRequest discoverSensorsClientRequest = clientRequestFactory.createRelativeRequest("/rest/services/getAvailableServices");

		discoverSensorsClientRequest.queryParameter("userID", userId);
		discoverSensorsClientRequest.accept("application/xml");

		ClientResponse<String> response;
		String responseText = null;

		try {
			OSDSpec spec = null;
			if (0 == 1) { // Commented out till the service endpoint is
							// implemented
				response = discoverSensorsClientRequest.get(String.class);

				if (response.getStatus() != 200) {
					throw new APICommunicationException("Error communicating with scheduler (method: getAvailableServices, HTTP error code : " + response.getStatus() + ")");
				}

				responseText = response.getEntity();

				JAXBContext jaxbContext = JAXBContext.newInstance(OSDSpec.class);
				Unmarshaller um = jaxbContext.createUnmarshaller();
				spec = (OSDSpec) um.unmarshal(new StreamSource(new StringReader(responseText)));
			} else {
				spec = getLocallyCachedServices(userId);
			}
			return spec;
		} catch (Throwable ex) {
			throw new APIException(ex);
		}
	}

	private static OSDSpec getLocallyCachedServices(String userId) throws APICommunicationException, APIException {
		InputStream is = null;
		try {
			is = SchedulerAPIWrapper.class.getClassLoader().getResourceAsStream("/org/openiot/ui/request/commons/demo/demo-osdspec.xml");
			if (is == null) {
				LoggerService.log(Level.WARNING, "Could not load demo-osdspec from classpath. Falling back to an empty OSDSpec");
				OSDSpec spec = new OSDSpec();
				spec.setUserID("nodeID://b47098");
				return spec;
			}
			String osdSpecString = org.apache.commons.io.IOUtils.toString(is);

			// Unserialize
			JAXBContext jaxbContext = JAXBContext.newInstance(OSDSpec.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			OSDSpec spec = (OSDSpec) um.unmarshal(new StreamSource(new StringReader(osdSpecString)));

			return spec;

		} catch (Exception ex) {
			throw new APIException(ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex1) {
				}
			}
		}
	}

}
