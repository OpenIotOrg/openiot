/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;

/**
 * 
 */
public class SerialiseFormatMap implements SerialiseFormatLookup{

	/**
	 * A Map that stores the various output serialisation 
	 * formats supported by the application
	 */
	private Map<String,MediaType> formats;
	
	/**
	 * Constructor
	 */
	public SerialiseFormatMap(){
		formats = new HashMap<String, MediaType>();
		addSerialiseFormat(Constants.LANG_RDFXML, MediaType.APPLICATION_RDF_XML);
		addSerialiseFormat(Constants.LANG_RDFJSON, MediaType.register(Constants.MEDIA_TYPE_RDF_JSON,Constants.MEDIA_TYPE_RDF_JSON));
		addSerialiseFormat(Constants.LANG_TURTLE, MediaType.APPLICATION_RDF_TURTLE);
		addSerialiseFormat(Constants.LANG_NTRIPLE, MediaType.TEXT_RDF_NTRIPLES);
	}
	
	
	/* (non-Javadoc)
	 * @see au.csiro.openiot.utils.SerialiseFormatLookup#findSerialiseFormat(java.lang.String)
	 */
	@Override
	public MediaType findSerialiseFormat(String id) {
		if (id != null) {	
			return(formats.get(id));
		
		} else {
			return(null);
		}
	}

	/* (non-Javadoc)
	 * @see au.csiro.openiot.utils.SerialiseFormatLookup#addSerialiseFormat(java.lang.String, org.restlet.data.MediaType)
	 */
	@Override
	public void addSerialiseFormat(String id, MediaType media) {
		formats.put(id, media);		
	}
	
	/**
	 * return the list of keys as an array.
	 * This method is used to populate the user interface with corresponding 
	 * mediaType descriptions supported by this application
	 *
	 * @return 
	 */
	public String[] toArray(){
		
		return formats.keySet().toArray(new String[0]);		
		
	}
	

}
