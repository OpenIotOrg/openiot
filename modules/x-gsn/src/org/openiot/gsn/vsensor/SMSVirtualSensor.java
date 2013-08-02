package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.log4j.Logger;

/**
 * Virtual sensor for sending SMS'es.
 * 
 * This VS only formats the message to be sent and outputs it to datastream
 * along with phone number and an e-mail address.<br>
 * 
 * E-mail address is formatted in a way that it can be used with SMSLink-gateway
 * <url>http://smslink.sourceforge.net</url> 
 */
public class SMSVirtualSensor extends AbstractVirtualSensor {

	private static final transient Logger logger = Logger.getLogger( SMSVirtualSensor.class );

	private static final String		PHONENUMBER_FIELD_NAME = "PHONENUMBER";
	private static final String     MESSAGE_FIELD_NAME = "MESSAGE";
	private static final String     RECEIVER_FIELD_NAME = "RECEIVER";
	private static final String		ADD_COMMAND_NAME = "add-receiver";
	private static final String		REMOVE_COMMAND_NAME = "remove-receiver";
	private static final String		PHONENUMBER_UPLOAD_FIELD_NAME = "phonenumber";

	/*
	 * These values must match to the parameter names in VS's configuration file.
	 */
	private static final String		INITPARAM_PHONE_NUMBER = "phone-number";
	private static final String		INITPARAM_PASSWORD = "password";
	private static final String		INITPARAM_SMS_SERVER = "sms-server";
	private static final String		INITPARAM_MESSAGE_FORMAT = "message-format";
	
	private static final String [ ] FIELD_NAMES = new String [ ] { 
		MESSAGE_FIELD_NAME, PHONENUMBER_FIELD_NAME, RECEIVER_FIELD_NAME };

	private transient final DataField [] outputStructure = new  DataField [] { 
			new DataField( "message" , "varchar(255)" , "SMS-message to be sent." ),
			new DataField( "phonenumber" , "varchar(255)" , "Phone number of the recipient" ),
			new DataField( "receiver" , "varchar(255)" , "Receiver-field of the email" ),
	};   

	private String					password;
	private String					smsServer;
	private String					messageFormat;

	private static ConcurrentLinkedQueue<String>	phoneNumbers;

	public boolean initialize ( ) {
		phoneNumbers = new ConcurrentLinkedQueue<String>();
		TreeMap<String, String> params = getVirtualSensorConfiguration( ).getMainClassInitialParams( );

		if(params.get(INITPARAM_PHONE_NUMBER) != null) phoneNumbers.add(params.get(INITPARAM_PHONE_NUMBER));
		if(params.get(INITPARAM_PASSWORD) != null) password = params.get(INITPARAM_PASSWORD);
		else {
			logger.error( "The parameter *" + INITPARAM_PASSWORD + "* is missing from the virtual sensor processing class's initialization." );
			logger.error( "Loading the virtual sensor failed" );
			return false;
		}
		if(params.get(INITPARAM_SMS_SERVER) != null) smsServer = params.get(INITPARAM_SMS_SERVER);
		else {
			logger.error( "The parameter *" + INITPARAM_SMS_SERVER + "* is missing from the virtual sensor processing class's initialization." );
			logger.error( "Loading the virtual sensor failed" );
			return false;
		}
		if(params.get(INITPARAM_MESSAGE_FORMAT) != null) messageFormat = params.get(INITPARAM_MESSAGE_FORMAT);
		else {
			logger.error( "The parameter *" + INITPARAM_MESSAGE_FORMAT + "* is missing from the virtual sensor processing class's initialization." );
			logger.error( "Loading the virtual sensor failed" );
			return false;
		}
		return true;
	}

	/**
	 * Formats an e-mail address so that it can be used with SMSLink.
	 * 
	 * @param phone
	 * @param password
	 * @param smsServer
	 * @return 
	 */
	private String formatReceiverEmail(String phone, String password, String smsServer) {
		return phone + password + "@" + smsServer;
	}

	public void dataAvailable ( String inputStreamName , StreamElement data ) {
		/*
		 * The current time has to be extracted here in order to have same timestamp
		 * for all the sent messages. This makes it easier to show them all
		 * in GSN's web-interface at the same time.
		 */
		long time = System.currentTimeMillis();
		Iterator<String> it = phoneNumbers.iterator();

		while(it.hasNext()) {
			String number = it.next();
			String receiverEmail = formatReceiverEmail(number, password, smsServer);
			String message = prepareMessage(data, messageFormat);
			StreamElement out = new StreamElement( FIELD_NAMES , 
					new Byte[ ] { DataTypes.VARCHAR, DataTypes.VARCHAR, DataTypes.VARCHAR } , 
					new Serializable [ ] { message, number, receiverEmail } , time );
			dataProduced( out );
		}

	}

	/**
	 * Formats the message with StringTemplate-syntax. In brief, replaces field names
	 * with values, for example "Temperature: $TEMP$" where TEMP has some value
	 * in StreamElement.
	 * 
	 * @param streamElement - stream to be formatted
	 * @param messageTemplate - template which is used for formatting 
	 * @return - resulting message as a string
	 */
	public static String prepareMessage ( StreamElement streamElement , String messageTemplate ) {
		StringTemplate template = new StringTemplate( messageTemplate );
		/**
		 * @todo : Do checks for attributes with more than one value ?
		 */
		String [ ] fieldNames = streamElement.getFieldNames( );
		for ( int i = 0 ; i < fieldNames.length ; i++ ) {
			template.setAttribute( streamElement.getFieldNames( )[ i ] , streamElement.getData( )[ i ] );
		}
		String resultMessage = template.toString( );
		return resultMessage;
	}

	public synchronized boolean dataFromWeb ( String action, String[] paramNames, Serializable[] paramValues ) {
		boolean retval;

		if(action.equals(ADD_COMMAND_NAME))
			retval = addPhoneNumber(paramNames, paramValues);
		else if(action.equals(REMOVE_COMMAND_NAME))
			retval = removePhoneNumber(paramNames, paramValues);
		else {
			logger.warn("Wrong action got from web, uploaded data discarded.");
			return false;
		}
		return retval;
	}

	/**
	 * Adds phone numbers to the list of sms-receivers.
	 * 
	 * @param paramNames - array of parameter names
	 * @param paramValues -  array of parameter values (has to be String also)
	 * @return
	 */
	public boolean addPhoneNumber(String[] paramNames, Serializable[] paramValues) {
		String phoneNumber = "";

		if((paramNames.length != 1) || (paramValues.length != 1)) {
			logger.warn("Wrong number of parameters receiver from web, data discarded.");
			return false;
		}

		/*
		 * @todo Check if the received string is actually formatted like a phone number
		 */
		if(paramNames[0].equals(PHONENUMBER_UPLOAD_FIELD_NAME))
			phoneNumber = (String) paramValues[0];
		else {
			logger.warn("Received field is not a phone number, data discarded.");
			return false;
		}

		if(phoneNumber.equals("")) {
			logger.warn("Phone number missing from uploaded values, data dicarded.");
			return false;
		}

		phoneNumbers.add(phoneNumber);
		return true;
	}

	/**
	 * Removes phone numbers from the list of sms-receivers. If the number that wanted
	 * to be removed doesn't exist in the list, method returns true anyway.
	 * 
	 * @param paramNames - array of parameter names
	 * @param paramValues - array of parameter values (has to be String also)
	 * @return
	 */
	public boolean removePhoneNumber(String[] paramNames, Serializable[] paramValues) {
		String phoneNumber = "";

		if((paramNames.length != 1) || (paramValues.length != 1)) {
			logger.warn("Wrong number of parameters receiver from web, data discarded.");
			return false;
		}

		if(paramNames[0].equals(PHONENUMBER_UPLOAD_FIELD_NAME))
			phoneNumber = (String) paramValues[0];
		else {
			logger.warn("Received field is not a phone number, data discarded.");
			return false;
		}

		if(phoneNumber.equals("")) {
			logger.warn("Phone number missing from uploaded values, data dicarded.");
			return false;
		}

		for( String entry : phoneNumbers ) {
			if(entry.equals(phoneNumber)) {
				phoneNumbers.remove(entry);
			}
		}
		return true;
	}

	public void dispose ( ) {

	}

	public  DataField[] getOutputFormat ( ) {
		return outputStructure;
	}

}
