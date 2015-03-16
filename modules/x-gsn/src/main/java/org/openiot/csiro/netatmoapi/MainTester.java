package org.openiot.csiro.netatmoapi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpException;

import argo.jdom.JdomParser;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;

public class MainTester {
	public static void main(String[] str){
		User user = new User();
		user.setClientID("5269c1171877592b44185038");
		user.setClientSecret("5awYYo3XnUbuInV9LNOQcl6dAvbVbNYJzGJ9A2hKnChgS");
		user.setUserName("arkady.zaslavsky@csiro.au");
		user.setPassword("rustavi");
		
		
		
		Netatmo netatmo = Netatmo.getInstance();
		//call authenticate before making any calls to api's
		
		netatmo.authenticate(user);
		System.out.println(netatmo.getAccessToken());
		System.out.println(netatmo.getTokenExpiry());
		
		
		try {
			String string = netatmo.getNetatmotUser();
			System.out.println(string);
			
			string = netatmo.getNetatmotUserDeviceList();
			System.out.println(string);
			
			MeasureParameters parameters = new MeasureParameters();
			parameters.setDevice_id("70:ee:50:01:57:e0");
			parameters.addType("Temperature");
			parameters.addType("Humidity");
			parameters.addType("CO2");
			parameters.addType("Pressure");
			parameters.addType("Noise");
			
			
			/*
			 * possible value for  
			 * */
			 
			parameters.setScale("1month");
			
			//sample format -> dd/mm/yyy hh:mm a
			
			//parameters.setDate_begin("22/10/2013 08:23");
			parameters.setLimit(10);
		
			string = netatmo.getMeasure(parameters);
			System.out.println(string);
			
			String json_data = netatmo.getMeasure(parameters);
	    	
			try {
				JsonRootNode json = new JdomParser().parse(json_data);				
				List<JsonNode> jsonNodes = json.getArrayNode("body");				
				for (JsonNode node: jsonNodes){					
					List<JsonNode> _valueNode = node.getArrayNode("value", 0);
					System.out.println(_valueNode.get(0).getText()); //temperature
					System.out.println(_valueNode.get(1).getText()); //humidity
					System.out.println(_valueNode.get(2).getText()); //co2
					System.out.println(_valueNode.get(3).getText()); //pressure
					System.out.println(_valueNode.get(4).getText()); //noise
										
				}
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	
		
	    	
	    	 
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
