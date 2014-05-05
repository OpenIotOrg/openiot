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

package org.openiot.gsn.utils;

import java.util.Date;

import org.openiot.gsn.metadata.LSM.SensorAnnotator;

public class TestLSM {

    public static void main(String[] args) {

    	//register();    	
    	pushdata();
    
    }
 
    static void register(){
    	try{ /* 
    	 * add new sensor to lsm store. For example: Air quality sensor from Lausanne 
    	 ** Sensor name: lausanne_1057 */ 
         // 1. Create an instance of Sensor class and set the sensor metadata 
    		
    		String id=SensorAnnotator.addSensorToLSM( 
    				"http://lsm.deri.ie/OpenIoT/sensormeta#", "http://lsm.deri.ie/OpenIoT/sensordata#", 
    				"lausanne_1058", "jp", "gsntypne", "weather", "Air quality top", 
    				"http://opensensedata.epfl.ch:22002/gsn?REQUEST=113&name=lausanne_1057", 
    				new String[]{"http://lsm.deri.ie/ont/lsm.owl#AirTemperature"},46.529838, 6.596818);
    	    System.out.println(id);
    	    //http://lsm.deri.ie/resource/123128090807967

    	}
    	catch (Exception ex) { 
    		ex.printStackTrace(); 
    	}     
    }
    
    static void pushdata(){
    	/* 
    	 * An Observation is a Situation in which a Sensing method has been used to estimate or 
    	 * calculate a value of a Property of a FeatureOfInterest. 
    	 */ 
    	//create an Observation object
    	
    	SensorAnnotator.updateSensorDataOnLSM( 
    			"http://lsm.deri.ie/OpenIoT/sensormeta#", "http://lsm.deri.ie/OpenIoT/sensordata#", 
    			"http://sensordb.csiro.au/id/sensor/5010", "http://purl.oclc.org/NET/ssnx/meteo/aws#air_temperature", 9.877676, 
    			"C", null,new Date());
    	
    	
    	
    }
        
}
