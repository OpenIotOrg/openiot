/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.client;

import java.io.IOException;
import java.util.Properties;

import org.json.JSONObject;
import org.openiot.ui.sensorschema.utils.Utils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;


/**
 * Implementation to post a request for a Device Resource to the server
 */
public class DevicePost implements ClientInterface {

	/**
	 * An object to the logger class
	 */
	private static Logger logger = Utils.getLogger(DevicePost.class);
	
	//Making a put request using the restlet API to the LD4Server
	/* (non-Javadoc)
	 * @see au.csiro.openiot.client.ClientInterface#post(org.json.JSONObject, java.lang.String, boolean, org.restlet.data.MediaType)
	 */
	@Override
	public String post(JSONObject json, String resourceId, boolean testing, MediaType serialiseFormat) throws IOException {
		
		String resourceURI = "device/";
		System.out.println("Post - Device JSON payload to Server");		
		String server_uri = "http://localhost:8182/ld4s/";
		
		if(!testing){
			//	obtain server URL
			Properties props = new Properties();
			try {
				props.load(Utils.getConfigAsInputStream("/global.properties", DevicePost.class));
				server_uri = props.getProperty("serverlocation");
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	
		String rdf=null;
		
		try{
		
			ClientResource cr = new ClientResource(server_uri+resourceURI+resourceId);	
			//Representation response = cr.post(json, MediaType.APPLICATION_RDF_XML);
			Representation response = cr.post(json, serialiseFormat); 

			Status status = cr.getStatus();
			System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
				
				
			rdf = response.getText();
			response.release();	
			
		} catch(Exception E){
			throw new IOException("The connector failed to complete the communication with the server");
		}
		
		
		System.out.println("\n\n\n==============\nTesting DEVICE JSON PUT " +
				"(annotation to be soterd locally)\n"
				+ "sent : "+json
				+server_uri+resourceId+"==============\n"+rdf);
		
			
		return rdf;
	}

}
