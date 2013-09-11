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

package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.StreamElement;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class SensorInternetVS extends AbstractVirtualSensor {

	private static final String SI_URL = "si-url";
	private URL siUrl = null;

	private static final String SI_USERNAME = "si-username";
	private String siUsername = null;

	private static final String SI_PASSWORD = "si-password";
	private String siPassword = null;

	private static final String SI_STREAM_MAPPING = "si-stream-mapping";
	private Integer[] siStreamMapping = null;

	private static final String REQUEST_AGENT = "GSN (Global Sensors Networks) Virtual Sensor" ;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss") ;

	private static transient Logger logger  = Logger.getLogger ( SensorInternetVS.class );

	@Override
	public boolean initialize() {
		TreeMap <  String , String > params = getVirtualSensorConfiguration( ).getMainClassInitialParams( ) ;
		String param = null;

		param = params.get(SI_URL);
		if (param != null)
			try {
				siUrl = new URL (param) ;
			} catch (MalformedURLException e) {
				logger.error(e.getMessage(), e);
				return false;
			}
			else {
				logger.error("The required parameter: >" + SI_URL + "<+ is missing from the virtual sensor configuration file.");
				return false;
			}

		param = params.get(SI_USERNAME) ;
		if (param != null) {
			siUsername = param ;
		}
		else {
			logger.error("The required parameter: >" + SI_USERNAME + "<+ is missing from the virtual sensor configuration file.");
			return false;
		}

		param = params.get(SI_PASSWORD);
		if (param != null) {
			siPassword = param;
		}
		else {
			logger.error("The required parameter: >" + SI_PASSWORD + "<+ is missing from the virtual sensor configuration file.");
			return false;
		}

		param = params.get(SI_STREAM_MAPPING) ;
		if (param != null) {
			siStreamMapping = initStreamMapping(param) ;
			if (siStreamMapping == null) {
				logger.error("Failed to parse the required parameter: >" + SI_STREAM_MAPPING + "< (" + param + ")");
				return false;
			}
		}
		else {
			logger.error("The required parameter: >" + SI_STREAM_MAPPING + "<+ is missing from the virtual sensor configuration file.");
			return false;
		}

		// Enabling Basic authentication
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication (siUsername, siPassword.toCharArray());
			}
		});

		return true;
	}

	@Override
	public void dataAvailable(String inputStreamName, StreamElement streamElement) {
		try {
			
			// Init the HTTP Connection
			HttpURLConnection siConnection = (HttpURLConnection) siUrl.openConnection();
			siConnection.setRequestMethod("POST");
			siConnection.setDoOutput(true);
			siConnection.setRequestProperty( "User-Agent", REQUEST_AGENT );
			siConnection.connect();

			// Build and send the parameters
			PrintWriter out = new PrintWriter(siConnection.getOutputStream());
			String postParams = buildParameters(streamElement.getFieldNames(), streamElement.getData(), streamElement.getTimeStamp()) ;
			logger.debug("POST parameters: " + postParams) ;
			out.print(postParams);
			out.flush();
			out.close();


			if (siConnection.getResponseCode() == 200) {
				logger.debug("data successfully sent");
			}
			else {
				logger.error("Unable to send the data. Check you configuration file. " + siConnection.getResponseMessage() + " Code (" + siConnection.getResponseCode() + ")");
			}
		} catch (IOException e) {
			logger.error(e.getMessage()) ;
		}
	}

	@Override
	public void dispose() {

	}

	private String buildParameters (String[] fieldsNames, Serializable[] data, long timestamp) {

		StringBuilder sb = new StringBuilder () ;
		//
		for (int i = 0 ; i < fieldsNames.length ; i++) {
			if (i < siStreamMapping.length) {
				if (i != 0) sb.append("&");
				sb.append(createPostParameter ("time[" + i + "]=", dateFormat.format(new Date (timestamp))));
				sb.append("&");
				sb.append(createPostParameter ("data[" + i + "]=", data[i].toString()));
				sb.append("&");
				sb.append(createPostParameter ("key[" + i + "]=", Integer.toString(siStreamMapping[i])));
			}
			else {
				logger.warn("The field >" + fieldsNames[i] + "< is not mapped in your configuration file.");
			}
		}
		return sb.toString();
	}

	private String createPostParameter (String paramName, String paramValue) {
		try {
			return paramName + URLEncoder.encode(paramValue, "UTF-8") ;
		} catch (UnsupportedEncodingException e) {
			logger.debug(e.getMessage(), e);
		}
		return null;
	}

	private Integer[] initStreamMapping (String param) {
		String[] mps = param.split(",");
		Integer[] mapping = new Integer[mps.length] ;
		try {
			for (int i = 0 ; i < mps.length ; i++) {
				mapping[i] = Integer.parseInt(mps[i]);
			}
		}
		catch (java.lang.NumberFormatException e) {
			logger.error(e.getMessage());
			return null;
		}
		return mapping;
	}
}
