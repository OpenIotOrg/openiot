package org.openiot.gsn.wrappers.xBow;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.ContainerConfig;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.wrappers.AbstractWrapper;
import org.apache.log4j.Logger;

import java.util.ArrayList;

import java.lang.Thread;
import java.io.*;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.*;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This is a modified version of Lei Shu's xmlWrapper.
 * It works with a MicaZ WSN - WSN-PRO2400CA - MICAZ PROFESSIONAL KIT
 * The sensor boards contain:
 * 	a Sensirion SHT11 single chip humidity and temperature sensor;
 * 	an Intersema MS55ER barometric pressure and temperature sensor;
 * 	a TAOS Inc. TLS2550 digital light sensor and
 * 	an Analog Devices ADXL202 dual axis accelerometer.
 * @author bgpearn
 */

//TODO: This wrapper does not reconnect to a remote xServe if communication is lost.

public class xBowWrapper extends AbstractWrapper {

	private int                      DEFAULT_RATE       = 5000;
   
	private static int               threadCounter      = 0;

	private final transient Logger     logger                 = Logger.getLogger ( xBowWrapper.class );

	private static final String [ ]  FIELD_NAMES           = new String [ ] {
		"amType", "nodeid" , "voltage" , 
		"humid" , "humtemp" , "prtemp" ,"press",
		"taosch0", "taosch1", "taoch0"};
   
	private static final Byte [ ]    FIELD_TYPES           = new Byte [ ] {
	   DataTypes.INTEGER ,DataTypes.INTEGER , DataTypes.INTEGER ,  
	   DataTypes.INTEGER ,DataTypes.INTEGER , DataTypes.DOUBLE , DataTypes.DOUBLE,
	   DataTypes.INTEGER, DataTypes.INTEGER,DataTypes.DOUBLE};
   
	private static final String [ ]  FIELD_DESCRIPTION     = new String [ ] { 
	   "amType" ,"Node ID" , "Voltage of This Node" ,
	   "Humidity" , "Temperature" , "PrTemp", "Pressure", 
	   "taosch0", "taosch1", "taoch0"};
   
	private static final String [ ]  FIELD_TYPES_STRING    = new String [ ] { 
	   "int" , "int" , "int" , 
	   "int" , "int" , "double", "double",
	   "int" , "int" , "double"};
   
	private DataField[]                outputStructure;
	private String                     host;
	private int                        port;
	private int                        rate;
	private String                     inputRate           ;
	private int                        times               ;
	private String                     inputTimes          ;
   
   // fields of sensor node
	private int						amType ;
	private int                        nodeid              ;
	private int                        parent              ;
	private int                        group               ;
	private int                        voltage             ;
	private int                        humid               ;
	private int                        humtemp             ;
	private int						taosch0;
	private int						taosch1;
	private double						taoch0;
	private double                     prtemp               ;
	private double                     press               ;
	private double                     accel_x             ;
	private double                     accel_y             ;
   
   // declare the socket object for client side   
	private Socket                     xmlSocket = null    ;
	private BufferedReader             rd                  ;
	private StreamElement              streamEle           ;
	private  boolean                   add = false         ;
	private String                     s  = ""             ; // xml packet
	private String                     xmls                ; 
	private DocumentBuilderFactory     domfac              ;
	private DocumentBuilder            dombuilder          ;
	private InputSource                ins                 ;
	private Document                   doc                 ;
	private int                        k                   ;
	private String                     bs                  ;
	private int                        indexS              ;
	private int                        indexE              ;
	private boolean                    getxml              ;

	public boolean initialize (  ) {
	
		/**
		 * check the host and port parameters.
		 */
		
		AddressBean addressBean = getActiveAddressBean( );
		setName( "xbowWrapper" + ( threadCounter++ ) );
		host = addressBean.getPredicateValue ( "host" );
	
		if ( host == null || host.trim ( ).length ( ) == 0 ) {
				logger.warn ( "The >host< parameter is missing from the RemoteWrapper wrapper." );
			return false;
			}
		
		port = addressBean.getPredicateValueAsInt("port" ,ContainerConfig.DEFAULT_GSN_PORT);
		   if ( port > 65000 || port <= 0 ) {
				logger.error("Remote wrapper initialization failed, bad port number:"+port);
			return false;
			}
		
		inputRate = addressBean.getPredicateValue( "rate" );
		if ( inputRate == null || inputRate.trim( ).length( ) == 0 ) rate = DEFAULT_RATE;
			   else
			 rate = Integer.parseInt( inputRate );
		 
		ArrayList<DataField > output = new ArrayList < DataField >();
		for ( int i = 0 ; i < FIELD_NAMES.length ; i++ )
			output.add( new DataField( FIELD_NAMES[ i ] , FIELD_TYPES_STRING[ i ] , FIELD_DESCRIPTION[ i ] ) );
			outputStructure = output.toArray( new DataField[] {} );
		return true;
		 
	}

	public void run ( ) {

		try {
			 // setup the socket connection
				xmlSocket = new Socket(host, port);
				rd = new BufferedReader(new InputStreamReader(xmlSocket.getInputStream()));
			} catch (IOException e){
				logger.warn(" The xml socket connection is not set up.");
				logger.warn(" Cannot read from xmlSocket. ");
			}

		while ( isActive( ) ) {
			getxml = false;
			try {
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(),e);
			}
		
			try { // try
				s = "";
				char[] c = new char[3000];

				// initialize this char[]
				for (int j = 0; j < c.length; j++){
					c[j] = 0;
				} 
				rd.read(c);
				for (int j = 0; j < c.length; j++){
					s = s + c[j];
				} 
				s = s.trim();
				
				if (s != ""){

					try{
						indexS = s.indexOf("<?xml");
						indexE = s.indexOf("</MotePacket>");
					}catch (Exception e){
						logger.error( e.getMessage( ) , e );
					}
					if (indexS < indexE) {
						if (indexS >= 0){
							bs = s.substring(indexS,(indexE+13) );
							if (bs.length() > 2000) {
								xmls = bs;
								getxml = true;    
							}
							if (bs.length() < 2000) {
								try{	 
									 indexS = s.indexOf("<?xml", indexE);
									 indexE = s.indexOf("</MotePacket>", indexS);
								} catch (Exception e){
										logger.error( e.getMessage( ) , e );
								} 
								if (indexS < indexE) {
									if (indexS >= 0){
										bs = s.substring(indexS,(indexE+13) );
										if (bs.length() > 2000) {
											xmls = bs;
											getxml = true;    
										}	         
									}
								}
							}
						}
					}
					   
				}
			    try { // try 4
					if (getxml){ // if 1
						// Create instance of DocumentBuilderFactory
						domfac = DocumentBuilderFactory.newInstance();
						try { // try 3
						// Get the DocumentBuilder
							dombuilder = domfac.newDocumentBuilder();
						} catch (ParserConfigurationException e){ // try 3
							logger.info(e.getMessage( ) , e );
						}
						
						try { // try 2
							// Create instance of input source
							ins = new InputSource();
							// Initialize this input source as xmls
							ins.setCharacterStream(new StringReader(xmls));
							// Pass xmls stream to XML Parser
							doc = dombuilder.parse(ins);
						} catch (SAXException e){ // try 2
							logger.info(e.getMessage( ) , e );
						} catch (NullPointerException e){
						logger.info(e.getMessage( ) , e );
						}
				
						
						// Get the root element of XML packet
						Element root = doc.getDocumentElement();
						// Get the first level Node list
						NodeList fields = root.getChildNodes();
						// Get all fields' name
						// start of second layer for 3
						for(int i=0;i<fields.getLength();i++){
							
							Element field = (Element)fields.item(i);		        		
							
							String name;
							
							Element nameEle=(Element)field.getElementsByTagName("Name").item(0);
							
							name = nameEle.getTextContent();
							
							String value;
	
							Element valueEle=(Element)field.getElementsByTagName("ConvertedValue").item(0);
	
							value = valueEle.getTextContent();
							
							if (name.equals("amtype")){
								amType = Integer.parseInt(value);
							}
	
							if (name.equals("nodeid")){
									nodeid = Integer.parseInt(value);
							}
							
							if (name.equals("parent")){
									parent = Integer.parseInt(value);
							}
							
							if (name.equals("group")){
									group = Integer.parseInt(value);
							}
								
							if (name.equals("voltage")){
									voltage = Integer.parseInt(value);
							}
							
							if (name.equals("humid")){
									humid = Integer.parseInt(value);
							}
							
							if (name.equals("humtemp")){
									humtemp = Integer.parseInt(value);
							}
							
							if (name.equals("taosch0")){
								taosch0 = Integer.parseInt(value);
							}
						
							if (name.equals("taosch1")){
								taosch1 = Integer.parseInt(value);
							}
						
							if (name.equals("taoch0")){
								if (Double.isNaN(taoch0 = Double.parseDouble(value))){
								taoch0 = 0 ;
								}
							}
							if (name.equals("prtemp")){
									prtemp = Double.parseDouble(value);
							}
							
							if (name.equals("press")){
								press = Double.parseDouble(value);
							}
						
							if (name.equals("accel_x")){
									accel_x = Double.parseDouble(value);
							}
							
							if (name.equals("accel_y")){
							
								accel_y = Double.parseDouble(value);
							}
																								
						} // end of second layer for 3
	
						if (amType == 11) {		        	
							try { // try 1
								streamEle = new StreamElement( FIELD_NAMES , FIELD_TYPES , 
									new Serializable [ ] { 
									amType, nodeid , voltage , 
									humid , humtemp , prtemp, 
									press, taosch0, taosch1, taoch0 } );	
								postStreamElement (streamEle);
							}catch (Exception e){ // try 1
								logger.info(e.getMessage( ) , e );
							}
					
						} 
					}; // end of if
				   
					}catch (Exception e) { // try 4
						logger.error( e.getMessage( ) , e );
					}

			} catch (Exception e) { // try
				logger.error( e.getMessage( ) , e );
			}
		}   // while
	}  // run

	   // for GSNv2
	   public void dispose ( ) {

			   threadCounter--;
			  }
		   
	   // for release 1249 and later
	   public void finalize ( ) { 

			   threadCounter--;
			  }
		
	public String getWrapperName() {
		    return "xBowWrapper";
	   }

	public  DataField[] getOutputFormat ( ) {
		      return outputStructure;
		   }
		   
}
