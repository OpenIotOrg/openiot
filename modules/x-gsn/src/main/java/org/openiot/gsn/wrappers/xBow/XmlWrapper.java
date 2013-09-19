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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The XML wrapper is used to get the XML stream from xServer of xBow sensor network.
 * This is a specialized wrapper because the XML packet length should be verified.
 * In xBow sensor network, the basic length of a XML packet is char[2840].
 */

/**
 * This Lei Shu'w wrapper from http://lei.shu.deri.googlepages.com/xmlwrapper.rar 
 */

/* 
 * The actual xml packet 
<?xml version="1.0" ?><MotePacket><ParsedDataElement><Name>amtype</Name><ConvertedValue>11</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>nodeid</Name><ConvertedValue>1156</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>parent</Name><ConvertedValue>0</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>group</Name><ConvertedValue>125</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>socketid</Name><ConvertedValue>51</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>board_id</Name><ConvertedValue>133</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>packet_id</Name><ConvertedValue>134</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>voltage</Name><ConvertedValue>3003</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>humid</Name><ConvertedValue>45</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>humtemp</Name><ConvertedValue>26</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibW0</Name><ConvertedValue>46520</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibW1</Name><ConvertedValue>50839</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibW2</Name><ConvertedValue>43118</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibW3</Name><ConvertedValue>46252</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>prtemp</Name><ConvertedValue>25.852833</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>press</Name><ConvertedValue>992.968689</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>taosch0</Name><ConvertedValue>65518</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>taosch1</Name><ConvertedValue>0</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>accel_x</Name><ConvertedValue>1440.000000</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>accel_y</Name><ConvertedValue>500.000000</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>taoch0</Name><ConvertedValue>883.890015</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibB0</Name><ConvertedValue>184</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibB1</Name><ConvertedValue>181</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibB2</Name><ConvertedValue>151</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibB3</Name><ConvertedValue>198</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibB4</Name><ConvertedValue>110</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibB5</Name><ConvertedValue>168</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibB6</Name><ConvertedValue>172</ConvertedValue></ParsedDataElement><ParsedDataElement><Name>calibB7</Name><ConvertedValue>180</ConvertedValue></ParsedDataElement></MotePacket> 

 * The parsed fields:
 amtype = 11 ; 
 nodeid = 1156 ; 
 parent = 0 ; 
 group = 125 ; 
 socketid = 51 ; 
 board_id = 133 ; 
 packet_id = 134 ; 
 voltage = 3003 ; 
 humid = 46 ; 
 humtemp = 25 ; 
 calibW0 = 46520 ; 
 calibW1 = 50839 ; 
 calibW2 = 43118 ; 
 calibW3 = 46252 ; 
 prtemp = 25.596191 ; 
 press = 993.296448 ; 
 taosch0 = 65515 ; 
 taosch1 = 0 ; 
 accel_x = 1440.000000 ; 
 accel_y = 500.000000 ; 
 taoch0 = 796.950012 ; 
 calibB0 = 184 ; 
 calibB1 = 181 ; 
 calibB2 = 151 ; 
 calibB3 = 198 ; 
 calibB4 = 110 ; 
 calibB5 = 168 ; 
 calibB6 = 172 ; 
 calibB7 = 180 ;
*/ 

public class XmlWrapper extends AbstractWrapper {

	   private int                      DEFAULT_RATE       = 5000;
	   
	   private static int               threadCounter      = 0;
	
	   private final transient Logger     logger                 = Logger.getLogger ( XmlWrapper.class );

	   //private static  DataField [] outputStructure     = new DataField[] { new DataField( "nodeid" , "INTEGER", "Node ID" ) , new DataField( "parent" , "INTEGER", "Parent Node ID" ) , new DataField( "group" , "INTEGER" , "Group ID" ) , new DataField( "voltage" , "INTEGER" , "Voltage of This Node" ) , new DataField( "humid" , "INTEGER" , "Humidity" ) , new DataField( "humtemp" , "INTEGER" , "Temperature" ) , new DataField( "press" , "DOUBLE" , "Press" ) , new DataField( "accel_x" , "DOUBLE" , "accel_x" ) ,new DataField( "accel_y" ,"DOUBLE" , "accel_y" )};
	   //private static  DataField [] outputStructure     = new DataField[] { new DataField( "nodeid" , "INTEGER", "Node ID" ) , new DataField( "humtemp" , "INTEGER", "Parent Node ID" )};
	   //private static  DataField [] outputStructure     = new DataField[] { new DataField( "nodeid" , DataTypes.INTEGER_NAME, "Node ID" ) , new DataField( "parent" , DataTypes.INTEGER_NAME, "Parent Node ID" ) , new DataField( "group" , DataTypes.INTEGER_NAME , "Group ID" )};
	   
	   private static final String [ ]  FIELD_NAMES           = new String [ ] { "nodeid" , "voltage" , "humid" , "humtemp" , "press" };
	   
	   private static final Byte [ ]    FIELD_TYPES           = new Byte [ ] { DataTypes.INTEGER , DataTypes.INTEGER , DataTypes.INTEGER , DataTypes.INTEGER , DataTypes.DOUBLE };
	   
	   private static final String [ ]  FIELD_DESCRIPTION     = new String [ ] { "Node ID" , "Voltage of This Node" , "Humidity" , "Temperature" , "Press" };
	   
	   private static final String [ ]  FIELD_TYPES_STRING    = new String [ ] { "int" , "int" , "int" , "int" , "double" };
	   
	   private DataField[]                outputStructure      ;
	   
	   private String                     host                ;
	   
	   private int                        port                ;
	   
	   private int                        rate                ;
	   
	   private String                     inputRate           ;
	   
	   private int                        times               ;
	   
	   private String                     inputTimes          ;
	   
	   // fields of sensor node
	   private int                        nodeid              ;
	   
	   private int                        parent              ;
	   
	   private int                        group               ;
	   
	   private int                        voltage             ;
	   
	   private int                        humid               ;
	   
	   private int                        humtemp             ;
	   
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
 	   
 	   private boolean                    notEnd = true       ; 
 	   
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
		
		setName( "XmlWrapper" + ( threadCounter++ ) );
		
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

           //		   int n=0;
		   
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
			   
            //			   if (xmlSocket.isConnected()){
            //				   logger.info("Socket is connected");
            //			   }
	
			// testing
            //			   n = n + 1 ;
            //			   logger.info("while times n:"+n);
	  
			   try {
					Thread.sleep(rate);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(),e);
				}
			
			   try { // try
				   
				   s = "";
		   
				   // *** IMPORTANT Factors for getting data *** 
				   // 1) when reading data from buffer, it is already good to read more data.
			       // For example, the actual length of an XML packet is around 2843, but set the buffer as 3200. 
				   // If two sensor node 2843 * 2, but set the buffer as 6000.
				   // 2) the sampling rate is another important factor. 
				   // Setting different sampling rate, getting different results.
				   // ***
				   
			        char[] c = new char[3000];

			        // initialize this char[]
		            for (int j = 0; j < c.length; j++){
			        	c[j] = 0;
			        } 
	   
				    rd.read(c);
		        
			        for (int j = 0; j < c.length; j++){
			        	s = s + c[j];
			        } 
				    
			        //logger.info("S before processing: "+s);
			        
			        s = s.trim();
			        
			        //logger.info("S after trim processing: "+s);
			        //logger.info("s.length: "+ s.length());
			        
			        if (s != ""){

                            try{
			           			 indexS = s.indexOf("<?xml");
						         indexE = s.indexOf("</MotePacket>");
                             	//logger.info("original indexS: "+indexS);
                            	//logger.info("original indexE: "+indexE);
                            }catch (Exception e){
							      logger.error( e.getMessage( ) , e );
							}
						         
						    if (indexS < indexE) {
                                if (indexS >= 0){
                                	//logger.info("indexS: "+indexS);
                                	//logger.info("indexE: "+indexE);
                                	bs = s.substring(indexS,(indexE+13) );
                                	//logger.info("bs.length: "+ bs.length());
								    if (bs.length() > 2000) {
									    xmls = bs;
									    getxml = true;    
								         }
								    if (bs.length() < 2000) {
									    try{	 
									      	 indexS = s.indexOf("<?xml", indexE);
									      	 indexE = s.indexOf("</MotePacket>", indexS);
									      	//logger.info("new indexS: "+indexS);
		                                	//logger.info("new indexE: "+indexE);
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
			    	   
			    	   //logger.info("S after processing: "+ xmls);
			    	   //logger.info("getxml : "+ getxml);

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
			        	
			        	//logger.info("xmls.length(): "+ xmls.length());
			        	
			        	// Initialize this input source as xmls
			        	ins.setCharacterStream(new StringReader(xmls));
			        
			        	//logger.info(ins);

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
				        	
				        	// Double.parseDouble(aString)
				        	
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
      
			        	
			        	try { // try 1
			        		
			        	
			        	streamEle = new StreamElement( FIELD_NAMES , FIELD_TYPES , new Serializable [ ] { nodeid , voltage , humid , humtemp , press } );	
		        	
			        	postStreamElement (streamEle);
			        	
			        	 }catch (Exception e){ // try 1
					    	   logger.info(e.getMessage( ) , e );
					       }
		        	
//			        	if (isActive( )){
//			        		logger.info("Thread is still active.");}
//			            if (isActive( ) == false){
//			        		logger.info("Thread is not still active.");
//			            }
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
		   
	   // for release 1249
	   public void finalize ( ) { 

			   threadCounter--;
			  }
		
	public String getWrapperName() {
		    return "XmlWrapper";
	   }

	public  DataField[] getOutputFormat ( ) {
		      return outputStructure;
		   }
		   
}
