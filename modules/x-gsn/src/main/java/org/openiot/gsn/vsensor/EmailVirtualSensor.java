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
 * @author Ali Salehi
 * @author Mehdi Riahi
 * @author Timotee Maret
*/

package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.StreamElement;

import java.util.TreeMap;

import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

/**
 * Virtual sensor for sending email. 
 * 
 * This class requires at least javamail version 1.2, which is not packed with GSN. 
 * Get it from http://java.sun.com/products/javamail/
 * 
 * Receiver's e-mail address can be defined either in the VS's configuration
 * parameters, or it can be get from the datastream.
 *
 * @deprecated Replaced by the {@link org.openiot.gsn.processor.ScriptletProcessor} class and the {@link org.openiot.gsn.utils.services.Notifications} services.
 */
public class EmailVirtualSensor extends AbstractVirtualSensor {

	private static final transient Logger logger = Logger.getLogger( EmailVirtualSensor.class );
	private static final String		DEFAULT_SUBJECT = "GSN-Notification";
	/*
	 * These values are used when extracting receiver's email-address and the message to
	 * be sent from a datastream.  
	 */
	private static final String		RECEIVER_FIELD_NAME = "RECEIVER";
	private static final String		MESSAGE_FIELD_NAME = "MESSAGE";
	/*
	 * These values must match to the parameter names in VirtualSensor's configuration file.
	 */
	private static final String		INITPARAM_SENDER = "sender-email";
	private static final String		INITPARAM_RECEIVER = "receiver-email";
	private static final String		INITPARAM_SERVER = "mail-server";
	private static final String		INITPARAM_SUBJECT = "subject";

	private String					subject    = DEFAULT_SUBJECT;
	private String					receiverEmail = "";
	private String					senderEmail;
	private String					mailServer;
	private SimpleEmail 			email;

	public boolean initialize ( ) {
		TreeMap < String , String > params = getVirtualSensorConfiguration( ).getMainClassInitialParams( );

		if(params.get(INITPARAM_SUBJECT) != null) subject = params.get(INITPARAM_SUBJECT);
		if(params.get(INITPARAM_RECEIVER) != null) receiverEmail = params.get(INITPARAM_RECEIVER);
		if(params.get(INITPARAM_SENDER) != null) senderEmail = params.get(INITPARAM_SENDER);
		else {
			logger.error( "The parameter *" + INITPARAM_SENDER + "* is missing from the virtual sensor processing class's initialization." );
			logger.error( "Loading the virtual sensor failed" );
			return false;
		}
		if(params.get(INITPARAM_SERVER) != null) mailServer = params.get(INITPARAM_SERVER);
		else {
			logger.error( "The parameter *" + INITPARAM_SERVER + "* is missing from the virtual sensor processing class's initialization." );
			logger.error( "Loading the virtual sensor failed" );
			return false;
		}
		
		try {
			email = new SimpleEmail();
			email.setHostName(mailServer);
			email.setFrom(senderEmail);
			email.setSubject( subject );
		} catch(Exception e) {
			logger.error( "Email initialization failed", e );
			return false;
		}
		return true;
	}

	/*
	 * Extracts receiver's email-address and message from stream and sends the e-mail.
	 * (non-Javadoc)
	 * @see org.openiot.gsn.vsensor.AbstractVirtualSensor#dataAvailable(java.lang.String, org.openiot.gsn.beans.StreamElement)
	 */
	public void dataAvailable ( String inputStreamName , StreamElement data ) {
		String [ ] fieldNames = data.getFieldNames( );
		String message = "";

		for(int i=0; i < fieldNames.length; i++) {
			String fn = fieldNames[i];
			if(fn.equals(RECEIVER_FIELD_NAME)) {
				receiverEmail = (String) data.getData()[i];
			} else if(fn.equals(MESSAGE_FIELD_NAME)) {
				message = (String) data.getData()[i];
			}
		}

		if(message.equals("") == false) { 
			send(message);
		}
	}

	/*
	 * Sends the previously formatted e-mail.
	 * Because there are two ways to define receiver's address, this method
	 * has to check if the address actually exists.
	 */
	private boolean send ( String message ) {
		try {
			if(receiverEmail.equals("")) {
				logger.error("Sending e-mail failed: no receiver.");
				return false;
			}
			email.addTo(receiverEmail);
			email.setContent( message , "text/plain" );
			email.send( );         
		} catch ( Exception e ) {
			logger.error( "Sending e-mail failed, trying to send to *" + receiverEmail + "*\n", e );
			return false;
		}
		return true;
	}

	public void dispose ( ) {

	}

}
