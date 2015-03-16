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

package org.openiot.gsn.wrappers;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.openiot.csiro.netatmoapi.MeasureParameters;
import org.openiot.csiro.netatmoapi.Netatmo;
import org.openiot.csiro.netatmoapi.User;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;

/**
 * This wrapper presents API implementation to fetch data from 
 * Netatmo Webservice.
 * 
 * The user authentication parameters and data fetch parameters including
 * device id and observations needs to be specified in the virtual
 * sensor configuration file
 * 
 */
public class NetatmoWrapper extends AbstractWrapper {
  private DataField[] collection = new DataField[] { 
		  new DataField("temperature", "double", "Current Temperature."), 
		  new DataField("humidity", "double", "Current Humidity."),
		  new DataField("co2", "double", "Current CO2."),
		  new DataField("pressure", "double", "Current Pressure."),
		  new DataField("noise", "double", "Current Sound.")};
  private final transient Logger logger = Logger.getLogger(NetatmoWrapper.class);
  private int counter;

  private long rate = 1000;
  private Netatmo netatmo;
  private User user;
  //defines the netatmo selection scale from the VS configuration
  
  private String username = "", pwd="", 
		  clientID="", clientSecret="", 
		  scale = "", deviceID="", moduleID="";

  public boolean initialize() {
	  
	
	
    setName("NetatmoWrapper" + counter++);
    
    //obtain the input parameters
    
   
    AddressBean params = getActiveAddressBean();
    
    if ( params.getPredicateValue( "rate" ) != null ) {
      this.rate = (long) Integer.parseInt( params.getPredicateValue("rate"));
      logger.info("Sampling rate set to " + params.getPredicateValue( "rate") + " msec.");
    }

    if ( params.getPredicateValue( "username" ) != null ) {
        this.username = params.getPredicateValue("username");
    }
    
    if ( params.getPredicateValue( "pwd" ) != null ) {
        this.pwd = params.getPredicateValue("pwd");
    }
    
    if ( params.getPredicateValue( "clientid" ) != null ) {
        this.clientID = params.getPredicateValue("clientid");
    }
    if ( params.getPredicateValue( "clientsecret" ) != null ) {
        this.clientSecret = params.getPredicateValue("clientsecret");
    }
    if ( params.getPredicateValue( "scale" ) != null ) {
        this.scale = params.getPredicateValue("scale");
    }
    if ( params.getPredicateValue( "deviceid" ) != null ) {
        this.deviceID = params.getPredicateValue("deviceid");
    }
    if ( params.getPredicateValue( "moduleid" ) != null) {
        this.moduleID = params.getPredicateValue("moduleid");
    }
    
    
    //get an instance of the netanomo api
    netatmo = Netatmo.getInstance();
    user = new User();

	if (this.clientID != "" && this.clientSecret!="" && 
			this.username!="" && this.pwd!="" && 
			this.scale!=null){
		user.setClientID(this.clientID);
		user.setClientSecret(this.clientSecret);
		user.setUserName(this.username);
		user.setPassword(this.pwd);
		
	}
	else{
		//stop loading the wrapper if these parameters are incorrect.
		logger.error("Netatmo Account details are either missing in the VS confiuration or incorrect.");
		return false;
	}

    return true;
  }

  public void run() {
	  
    double humidity = 0.0, temperature = 0.0, co2 = 0.0, pressure = 0.0, noise = 0.0;
    //authenticate user and obtain a Access token
    boolean login = netatmo.authenticate(user);
   	if (!login){
   		logger.error ("Failed to get New Token. Please check authentication details");
   		logger.error("No data produced by the wrapper.");
   	}
    
    while (isActive()) {
      try {
        // delay 
        Thread.sleep(this.rate);
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
      }
                        
      MeasureParameters parameters = new MeasureParameters();
      parameters.setDevice_id("70:ee:50:01:57:e0");
      parameters.addType("Temperature");
      parameters.addType("Humidity");
      parameters.addType("CO2");
      parameters.addType("Pressure");
      parameters.addType("Noise");
      parameters.setScale(this.scale);
      parameters.setLimit(2);
      
      
      String value = "";
      
   	  String json_data = null;
   	  try {
   		  json_data = netatmo.getMeasure(parameters);
   	  } catch (IOException e) {
		// 	TODO Auto-generated catch block
   		  e.printStackTrace();
   	  } catch (URISyntaxException e) {
		// TODO Auto-generated catch block
   		  e.printStackTrace();
   	  } catch (HttpException e) {
		// TODO Auto-generated catch block
   		  e.printStackTrace();
   	  }   	  
   	  
   	  //if current token has expired. Re-authenticate and obtain new token
   	  if (json_data.contains("Get New Token")){
    	  	   logger.error("Error while fetching data from Netatmo Webservice. Token Expired"
      	  		+ "Fetching new token. Will try again in " + rate + "msecs"); 

   		  boolean success = netatmo.authenticate(user);
   		  if (!success){
   			  logger.error ("Unable to get New Token. Please check authentication details");
   			  logger.error("No data produced by the wrapper");
   		  }
    	  	  continue;
   	  }
   	  else if(json_data!=null)
   		  value = unpackJson(json_data);

      if(value!=""){    	  
    	  String values[]  = value.split(",");
    	  temperature= Double.parseDouble(values[0]);
    	  humidity = Double.parseDouble(values[1]);
    	  co2 = Double.parseDouble(values[2]);
    	  pressure = Double.parseDouble(values[3]);
    	  noise = Double.parseDouble(values[4]);
          // post the data to GSN
          postStreamElement(new Serializable[] { temperature, humidity, co2, pressure, noise });
      }
      else{
    	  logger.error("Error while fetching data from Netatmo Webservice. "
    	  		+ "Please check if Netatmo is running. Will try again in " + rate + "msecs");    	  
      }
    }
  }

  
  private String unpackJson(String json_data){
	  
	  String vals= "";
	  String _netatmodata;
	  try {
			JsonRootNode json = new JdomParser().parse(json_data);				
			List<JsonNode> jsonNodes = json.getArrayNode("body");				
			for (JsonNode node: jsonNodes){					
				List<JsonNode> _valueNode = node.getArrayNode("value", 0);
				
				_netatmodata =  _valueNode.get(0).getText();
				if (_netatmodata != null)
					vals = vals + _netatmodata + ",";
				else
					vals = vals + "0" + ";";

				_netatmodata =  _valueNode.get(1).getText();
				if (_netatmodata != null)
					vals = vals + _netatmodata + ",";
				else
					vals = vals + "0" + ";";

				_netatmodata =  _valueNode.get(2).getText();
				if (_netatmodata != null)
					vals = vals + _netatmodata + ",";
				else
					vals = vals + "0" + ";";

				_netatmodata =  _valueNode.get(3).getText();
				if (_netatmodata != null)
					vals = vals + _netatmodata + ",";
				else
					vals = vals + "0" + ";";

				_netatmodata =  _valueNode.get(4).getText();
				if (_netatmodata != null)
					vals = vals + _netatmodata;
				else
					vals = vals + "0";							
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	  return vals;
  }
  
  public DataField[] getOutputFormat() {
    return collection;
  }

  public String getWrapperName() {
    return "Netatmo Wrapper";
  }  

  public void dispose() {
    counter--;
  }
}
