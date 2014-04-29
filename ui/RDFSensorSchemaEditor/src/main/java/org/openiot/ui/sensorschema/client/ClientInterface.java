/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.client;

import java.io.IOException;

import org.json.JSONObject;
import org.restlet.data.MediaType;

/**
 * An Interface to the LD4Server 
 */
public interface ClientInterface {
	
	/**
	 * interface definition to post a
	 * request to the server.
	 *
	 * @param json 
	 * @param resourceId 
	 * @param testing 
	 * @param serializeFormat 
	 * @return 
	 * @throws IOException 
	 */
	
	
	
	public String post(JSONObject json, String resourceId, boolean testing, MediaType serializeFormat) throws IOException;
}
