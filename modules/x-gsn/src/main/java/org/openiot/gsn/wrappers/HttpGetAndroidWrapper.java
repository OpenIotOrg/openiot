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
 * @author Sofiane Sarni
*/

/*******************************************************************************
 * Copyright 2012 Colin Cachia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.openiot.gsn.wrappers;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class HttpGetAndroidWrapper extends AbstractWrapper {
   
   private int                      DEFAULT_RATE       = 2000;
   
   private static int               threadCounter      = 0;
   
   private final transient Logger   logger             = Logger.getLogger( HttpGetAndroidWrapper.class );
   
   private static String                   urlPath, Longitude, Latitude;
   
   private HttpURLConnection httpURLConnection;
   
   private URL url;
   
  private AddressBean              addressBean;
   
   private String                   inputRate;
   
   private static final String       FIELD_NAME_LONG                       = "Longitude";
   
   private static final String       FIELD_NAME_LAT 						= "Latitude";
      
   private static final String [ ]   FIELD_NAMES                           = new String [ ] { FIELD_NAME_LONG , FIELD_NAME_LAT };
   
   
   private int                      rate;
   
   //private transient final DataField [] outputStructure = new  DataField [] { new DataField( "longitude" , "Double" , "Longitude Data from Network-Connected Android Smartphone." ), new DataField( "latitude" , "Double" , "Latitude Data from Network-Connected Android Smartphone." ) };
  
   /**
    * From XML file it needs the followings :
    * <ul>
    * <li>url</li> The full url for retriving the binary data.
    * <li>rate</li> The interval in msec for updating/asking for new information.
    * <li>mime</li> Type of the binary data.
    * </ul>
    */
   
   public boolean initialize (  ) {
      this.addressBean =getActiveAddressBean( );
      urlPath = this.addressBean.getPredicateValue( "url" );
      try 
	  {
		url = new URL(urlPath);
	  } 
		catch (MalformedURLException e) 
	  {
		logger.error("Loading the http android wrapper failed : "+e.getMessage(),e);
		return false;
	  }
      
	  inputRate = this.addressBean.getPredicateValue( "rate" );
      if ( inputRate == null || inputRate.trim( ).length( ) == 0 ) rate = DEFAULT_RATE;
      else
         rate = Integer.parseInt( inputRate );
      setName( "HttpAndroidReceiver-Thread" + ( ++threadCounter ) );
      if ( logger.isDebugEnabled( ) ) logger.debug( "AndroidWrapper is now running @" + rate + " Rate." );
      return true;
   }
   
   public void run ( ) {
   
      while ( isActive( ) ) {
    	
		parseXML();
		
	  }
      
   }
   
   public String getWrapperName() {
    return "Http Android Receiver";
}
   
   public void dispose (  ) {
      threadCounter--;
   }
   
   public  DataField[] getOutputFormat ( ) {
      
		DataField[] outputFormat = new DataField[2];
		outputFormat[0] = new DataField("Longitude", "Double",
		"Longitude value from Android Smartphone");
		outputFormat[1] = new DataField("Latitude", "Double",
		"Latitude value from Android Smartphone");
		return outputFormat;

   }
   
   private void parseXML() 
	{
		URL url;
		
		try 
		{
			url = new URL(urlPath);
			URLConnection conn = url.openConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(conn.getInputStream());
	        
	        NodeList nodes = doc.getElementsByTagName("stream-element");

	        for (int i = 0; i < nodes.getLength(); i++) 
	        {
	            Element element = (Element) nodes.item(i);

	            NodeList Long = element.getElementsByTagName("long");
	            Element line = (Element) Long.item(0);
	            Longitude = getCharacterDataFromElement(line);

	            NodeList Lat = element.getElementsByTagName("lat");
	            line = (Element) Lat.item(0);
	            Latitude = getCharacterDataFromElement(line);
	         }

				StreamElement streamElement = new StreamElement( FIELD_NAMES , new Byte [ ] { DataTypes.DOUBLE , DataTypes.DOUBLE} , new Serializable [ ] { Double.parseDouble(Longitude) , Double.parseDouble(Latitude) } , System.currentTimeMillis( ) );
				postStreamElement( streamElement );
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   
   public static String getCharacterDataFromElement(Element e) 
	{
		    Node child = e.getFirstChild();
		    if (child instanceof CharacterData) {
		       CharacterData cd = (CharacterData) child;
		       return cd.getData();
		    }
		    return "?";
	 }
   
}
