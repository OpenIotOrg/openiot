/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.utils;

import org.restlet.data.MediaType;

/**
 * A lookup service interface to get list of serialised 
 * output formats for the LD4Sensor Server 
 */
public interface SerialiseFormatLookup {
	
	/**
	 * Returns the mediatype for the corresponding user selection 
	 * from the drop down menu in the user interface
	 *
	 * @param id 
	 * @return the MediaType (the response format from the LD4Sensor Server) 
	 */
	public MediaType findSerialiseFormat(String id);
	
	/**
	 * add a new media type and corresponding key.
	 *
	 * @param id 
	 * @param media 
	 */
	public void addSerialiseFormat(String id, MediaType media);

}
