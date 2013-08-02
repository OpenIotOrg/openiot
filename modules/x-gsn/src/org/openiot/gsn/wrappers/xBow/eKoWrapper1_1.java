package org.openiot.gsn.wrappers.xBow;

/**
 * This is wrapper works with version 1.1 Crossbow eKo wireless sensor network.
 * Currently handles packets from the following eKo Sensor packages:
 * 	eN2100 Internal Sensors;
 * 	eS1101 Soil Moisture and Temperature Sensor v2;
 * 	eS1201 Ambient Temperature and Humidity Sensor;
 * 	ET159 Decagon 5ET Soil Moisture Sensor
 *  ET22 Weather Sensor 
=======
 * @author bgpearn
 */

//TODO: This wrapper does not reconnect to a remote xServe if communication is lost.


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

public class eKoWrapper1_1 extends AbstractWrapper {

	private int                      DEFAULT_RATE       = 5000;
	private static int               threadCounter      = 0;
	private final transient Logger     logger   = Logger.getLogger ( eKoWrapper.class );
	private static final String [ ]  FIELD_NAMES = new String [ ] { "amtype", "nodeid", "packetname",
														"batteryV", "solarV", "enTemp",
														"soilmoisture", "soiltemp",
														"es1201Temp","es1201humid","es1201Dp",
														"Rain", "RainRate", "RainTotal", "Temp", 
														"Humidity", "Solar", "BP", "TempInt", "DewPoint",
														"EtDp", "EtVWC", "EtEc", "EtTemp",
														"sensorTable"};

	private static final Byte [ ]    FIELD_TYPES = new Byte [ ] { DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.VARCHAR, 
									DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE,
									DataTypes.DOUBLE, DataTypes.DOUBLE, 
									DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE,
									DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE,
									DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE,
									DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE,
									DataTypes.VARCHAR };

	private static final String [ ]  FIELD_DESCRIPTION = new String [ ] { "amType", "Node ID", "Packet Type", 
			"Battery Volts", "Solar Volts", "Internal Temp",
			"Soil Moisture", "Soil Temperature",
			"Ambient Temperature", "Ambient Humidity", "Ambient Dewpoint",
			"Rain", "Rain Rate", "Rain Total", "Temperature", 
			"Humidity", "Solar", "Barometer", "Temperature", "Dew Point",
			"Dielectric Permittivity", "Water Content VWC (%)", "Electrical Conductivity (accurate to 7 dS/m)", "Temperature (degC)",
			"Sensor Table" };

	private static final String [ ]  FIELD_TYPES_STRING = new String [ ] { "int", "int", "varchar(50)", 
							"double", "double", "double", 
							"double", "double", 
							"double", "double", "double", 
							"double", "double", "double", "double", 
							"double", "double", "double", "double",  "double",
							"double", "double", "double", "double", 
							"varchar(50)" };

	private DataField[]	outputStructure      ;
	private String			host                ;
	private int			port                ;
	private int			rate                ;
	private String			inputRate           ;
	private Integer		amType;
	private int			nodeid              ;
	private String 		packetName;
	private Double 		batteryV;
	private Double 		solarV;
	private Double 		enTemp;
	private Double 		soilMoisture;
	private Double 		soilTemp;
	private Double 		es1201Temp;
	private Double 		es1201Humidity;
	private Double 		es1201Dp;
	private Double 		Rain;
	private Double 		RainRate;
	private Double 		RainTotal;
	private Double 		Temp;
	private Double 		Humidity;
	private Double 		Solar;
	private Double 		BP;
	private Double 		TempInt;
	private Double 		DewPoint;
	private Double 		EtDp;
	private Double 		EtVWC;
	private Double 		EtEc;
	private Double 		EtTemp;
	private String 		sensorTable;

	// declare the socket object for client side   
	private Socket                     xmlSocket = null    ;
	private BufferedReader             rd                  ;
	private StreamElement              streamEle           ;
	private String                     s  = ""             ; // xml packet
	private DocumentBuilderFactory     domfac              ;
	private DocumentBuilder            dombuilder          ;
	private InputSource                ins                 ;
	private Document                   doc                 ;
	private String                     bs                  ;
	private int                        indexS              ;
	private int                        indexE              ;
	private boolean                    getxml              ;
	public boolean initialize (  ) {

		/**
		 * check the host and port parameters.
		 */
		AddressBean addressBean = getActiveAddressBean( );
		setName( "eKoWrapper1_1" + ( threadCounter++ ) );

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
		for ( int i = 0 ; i < FIELD_NAMES.length ; i++ ) {
			output.add( new DataField( FIELD_NAMES[ i ] , FIELD_TYPES_STRING[ i ] , FIELD_DESCRIPTION[ i ] ) );
		}
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
		logger.info("Is Timestamp Unique: " + isTimeStampUnique());
		while ( isActive( ) ) {
			getxml = false;

			try {
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(),e);
			}  // sleep

			try { // try 1

				char[] c = new char[8000];
				// initialize this char[]
				for (int j = 0; j < c.length; j++){
					c[j] = 0;
				}

				try {
					rd.read(c);
				} catch (Exception e) {
					// TODO: handle exception
					logger.warn("** Read Exception **");
				}  // try read				logger.debug("S = "+ s.toString())  ;

				for (int j = 0; j < c.length; j++){
					s = s + c[j];
				} 

				logger.debug("Size of S = "+ s.length())  ;
				logger.debug("S = "+ s.toString())  ;
				s = s.trim();
				getxml = true;
				while (getxml) {
					getxml = false;
					logger.debug("Size of S = "+ s.length()) ;
					logger.debug("S = "+ s.toString())  ;
					try{
						indexS = s.indexOf("<?xml");
						indexE = s.indexOf("</MotePacket>", (indexS+17));
						logger.debug("indexS = "+ indexS + " indexE = "+ indexE)  ;
					}catch (Exception e){
						logger.error( e.getMessage( ) , e );}
					if (indexS >= 0)  {
						
						if (indexE > indexS) {
							bs = s.substring(indexS, indexE + 13);
							processXmlString(bs);
							s = s.substring(indexE);
							getxml = true;
						} else {
							s = s.substring(indexS);
						}
					}

					else {
						s = "";
					}
				}
			} catch (Exception e) { // try 1
				logger.error( e.getMessage( ) , e );}
		}   // while ( isActive( )
	}  // run()

	   // for GSNv2
	   public void dispose ( ) {
			   threadCounter--;
			  }
		   
	   // for release 1249 and later
	   public void finalize ( ) { 
			   threadCounter--;
			  }
		
	public String getWrapperName() {
		return "eKoWrapper";
	}

	public  DataField[] getOutputFormat ( ) {
		return outputStructure;
	}

	public void processXmlString(String xmls) {

		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		}
		
		try { // try 4

			logger.info("[Try 4] xmls: "+ xmls);
			logger.info("getxml : "+ getxml);

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
				logger.info(e.getMessage( ) , e );}

			// Get the root element of XML packet
			Element root = doc.getDocumentElement();
			// Get the first level Node list
			NodeList fields = root.getChildNodes();
			// Get all fields' name
			// start of second layer for 3
			// Element 0 is <PacketName>eN2100 Internal Sensors</PacketName>
			packetName = fields.item(0).getTextContent();
			// Element 1 is <NodeId>2.000000</NodeId>
			//packetName = fields.item(1).getTextContent();
			// Element 2 is <Port>eN2100 Internal Sensors</Port>
			//packetName = fields.item(2).getTextContent();


			/* The remaining elements are in the form:
			 * 	<ParsedDataElement>
			 * 		<Name>nodeId</Name>
			 * 		<SpecialType>nodeid</SpecialType>    // only some element and not used here.
			 * 		<ConvertedValue>2</ConvertedValue>
			 * 		<ConvertedValueType>uint16</ConvertedValueType>
			 * 	</ParsedDataElement>
			 */
			
			// Reset sensor values
			batteryV = null;
			solarV = null;
			enTemp = null;
			soilMoisture = null;
			soilTemp = null;
			EtDp = null;
			EtVWC = null;
			EtEc = null;
			EtTemp = null;
			es1201Temp = null;
			es1201Humidity = null;
			es1201Dp = null;
			Rain = null;
			RainRate = null;
			RainTotal = null;
			Temp = null;
			Humidity = null;
			Solar = null;
			BP = null;
			TempInt = null;
			DewPoint = null;

			for(int i=3; i<fields.getLength() - 1; i++){

				Element field = (Element)fields.item(i);		        		
				String name;

				Element nameEle=(Element)field.getElementsByTagName("Name").item(0);

				name = nameEle.getTextContent();

				String value;

				Element valueEle=(Element)field.getElementsByTagName("ConvertedValue").item(0);

				value = valueEle.getTextContent();

				if (name.equalsIgnoreCase("amtype")){
					amType = Integer.parseInt(value);
				}

				if (name.equalsIgnoreCase("nodeid")){
					nodeid = Integer.parseInt(value);
				}

				if (name.equals("batteryV")){
					batteryV = Double.parseDouble(value);
				}

				if (name.equals("solarV")){
					solarV = Double.parseDouble(value);
				}

				if (name.equals("internalTemp")){
					enTemp = Double.parseDouble(value);
				}
				if (name.equals("soilMoisture")){
					soilMoisture = Double.parseDouble(value);
				}

				if (name.equals("soilTemperature")){
					soilTemp = Double.parseDouble(value);
				}

				if (name.equals("temperature")){
					es1201Temp = Double.parseDouble(value);
				}
				if (name.equals("humidity")){
					es1201Humidity = Double.parseDouble(value);
				}
				if (name.equals("dewPoint")){
					es1201Dp = Double.parseDouble(value);
				}
				if (name.equals("Dp")){
					EtDp = Double.parseDouble(value);
				}

				if (name.equals("Rain")){
					Rain = Double.parseDouble(value);
				}
				if (name.equals("RainRate")){
					RainRate= Double.parseDouble(value);
				}

				if (name.equals("RainTotal")){
					RainTotal= Double.parseDouble(value);
				}
				if (name.equals("Temp")){
					Temp= Double.parseDouble(value);
				}
				if (name.equals("Humidity")){
					Humidity= Double.parseDouble(value);
				}
				if (name.equals("Solar")){
					Solar= Double.parseDouble(value);
				}
				if (name.equals("BP")){
					BP= Double.parseDouble(value);
				}
				if (name.equals("TempInt")){
					TempInt= Double.parseDouble(value);
				}
				if (name.equals("DewPoint")){
					DewPoint = Double.parseDouble(value);
				}
				
				if (name.equals("VWC")){
					EtVWC = Double.parseDouble(value);
				}
				if (name.equals("Ec")){
					EtEc = Double.parseDouble(value);
				}
				if (name.equals("Temp")){
					EtTemp = Double.parseDouble(value);
				}
			}	
				/* The last element is:
				 * 	<internal>
				 * 		<nodeId>2</nodeId>		// only in eN2100
				 * 		<SensorDeviceParentId>1</SensorDeviceParentId>
				 * 		<SensorDeviceSubAddress>1</SensorDeviceSubAddress>
				 * 		<SensorDeviceSensorId>1</SensorDeviceSensorId>
				 * 		<yieldAppId>1</yieldAppId>
				 * 		<sensorTable>eN2100_internal_sensor_results</sensorTable>
				 * 	</internal>
				 */ 
				sensorTable = null;
				if (amType.equals(11)) {
					Element internal_field = (Element)fields.item(fields.getLength() - 1);
					Element sensorTableEle=(Element)internal_field.getElementsByTagName("sensorTable").item(0);
					sensorTable = sensorTableEle.getTextContent();
				}
			 // end of second layer for 3
			logger.info("amType: " +amType +" Node ID: " + nodeid + " Packet Name  " + packetName );

			try { // try 1

				streamEle = new StreamElement( FIELD_NAMES , FIELD_TYPES , new Serializable [ ] { amType, nodeid, packetName,
							batteryV, solarV, enTemp,
							soilMoisture, soilTemp,
							es1201Temp, es1201Humidity, es1201Dp,
							Rain, RainRate, RainTotal, Temp, 
							Humidity, Solar, BP, TempInt, DewPoint,
							EtDp, EtVWC, EtEc, EtTemp,
							sensorTable } );	

				postStreamElement (streamEle);

			}catch (Exception e){ // try 1
				logger.info(e.getMessage( ) , e );}


		}catch (Exception e) { // try 4
			logger.error( e.getMessage( ) , e );}

	}
	
	public boolean isTimeStampUnique() {
		  return false;
		}
}
