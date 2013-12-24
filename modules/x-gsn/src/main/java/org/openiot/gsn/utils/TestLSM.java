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

package org.openiot.gsn.utils;

import java.util.Date;





import org.openiot.gsn.metadata.LSM.utils;
//import lsm.beans.Place;
//import lsm.beans.Sensor;
//import lsm.beans.User;
//import lsm.server.LSMTripleStore;
//import lsm.beans.Observation;
//import lsm.beans.ObservedProperty;
import org.openiot.lsm.beans.Place;
import org.openiot.lsm.beans.Sensor;
//import org.openiot.lsm.beans.User;
//import org.openiot.lsm.beans.User;
import org.openiot.lsm.server.LSMTripleStore;
import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.ObservedProperty;


public class TestLSM {

    public static void updateSensorDataOnLSM(String username,
                                             String password,
                                             String sensorID,
                                             String fieldName,
                                             double fieldValue,
                                             String fieldUnit) {
        try {
            /*
            * Set sensor's author
            * If you don't have LSM account, please visit LSM Home page (http://lsm.deri.ie) to sign up
            */
            //User user = new User();
            //user.setUsername(username);
            //user.setPass(password);

            Sensor sensor = new Sensor();
            //sensor.setId(sensorID);




            //sensor.setUser(user);

            // create LSMTripleStore instance
            LSMTripleStore lsmStore = new LSMTripleStore();

            //set user information for authentication
            //lsmStore.setUser(user);

            /*
            * An Observation is a Situation in which a Sensing method has been used to estimate or
            * calculate a value of a Property of a FeatureOfInterest.
            */


            //create an Observation object
            Observation obs = new Observation();

            // set SensorURL of observation
            obs.setSensor(sensorID);
            //set time when the observation was observed. In this example, the time is current local time.
            obs.setTimes(new Date());
            obs.setMetaGraph("http://lsm.deri.ie/OpenIoT/test/sensormeta#");
            obs.setDataGraph("http://lsm.deri.ie/OpenIoT/test/sensordata#");
            /*
            * Relation linking an Observation to the Property that was observed
            */
            ObservedProperty obvTem = new ObservedProperty();
            obvTem.setObservationId(obs.getId());
            //obvTem.setPropertyName(fieldName);
            obvTem.setPropertyType(fieldName);
            obvTem.setValue(fieldValue);
            obvTem.setUnit(fieldUnit);
            obs.addReading(obvTem);

            lsmStore.sensorDataUpdate(obs);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
        }

    }

    public static String addSensorToLSM(String username,
                                        String password,
                                        String sensorName,
                                        String sensorAuthor,
                                        String sourceType,
                                        String sensorType,
                                        String infor,
                                        String sensorSource,
                                        String metaGraph,
                                        String dataGraph,
                                        String [] properties,
                                        double latitude,
                                        double longitude) {

        String sensorID = "";

        try {
            // 1. Create an instance of Sensor class and set the sensor metadata
            Sensor sensor = new Sensor();
            sensor.setName(sensorName);
            sensor.setAuthor(sensorAuthor);
            sensor.setSourceType(sourceType);
            sensor.setSensorType(sensorType);
            sensor.setInfor(infor);
            sensor.setSource(sensorSource);
            for (String p : properties) {
				sensor.addProperty(p);
			}         
            sensor.setTimes(new Date());
            sensor.setMetaGraph(metaGraph);
            sensor.setDataGraph(dataGraph);            	                      
            // set sensor location information (latitude, longitude, city, country, continent...)
            Place place = new Place();
            place.setLat(latitude);
            place.setLng(longitude);
            sensor.setPlace(place);

            /*
            * Set sensor's author
            * If you don't have LSM account, please visit LSM Home page (http://lsm.deri.ie) to sign up
            */
            //User user = new User();
            //user.setUsername(username);
            //user.setPass(password);
            //sensor.setUser(user);

            // create LSMTripleStore instance
            LSMTripleStore lsmStore = new LSMTripleStore();

            //set user information for authentication
            //lsmStore.setUser(user);

            //call sensorAdd method
            lsmStore.sensorAdd(sensor);

            sensorID = sensor.getId();

            listSensor(sensor);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
        }


        return sensorID;

    }


    public static void main(String[] args) {

    	register();    	
    	//pushdata();
    	/*
    	  LSMTripleStore lsmStore = new LSMTripleStore();
          User user = new User();
          user.setUsername("swissex");
          user.setPass("swissex1234");
 lsmStore.setUser(user);
        Sensor sensor1 = lsmStore.getSensorById("http://lsm.deri.ie/resource/72819518859816",
        		"http://lsm.deri.ie/OpenIoT/demo/sensormeta#");
    	
    	System.out.println("naming "+sensor1.getSourceType());*/
    	/*
        String id = addSensorToLSM("swissex",
                "swissex1234",
                "lausanne_1057",
                "sofiane",
                "lausanne",
                "gsn",
                "air quality",
                "http://opensensedata.epfl.ch:22002/gsn?REQUEST=113&name=lausanne_1057",
                0,
                0
        );
*/

        //id = "http://lsm.deri.ie/resource/66582224937127";
  //      System.out.println("Calling with 64 bit Sensor ID ==> " + id);
        /*
        updateSensorDataOnLSM("swissex",
                "swissex1234",
                id,
                "Temperature",
                0.0,
                "C");
*/
        //what do you mean for this id? This is different sensor as yours. It's not 128 bit ID of your sensor.
        //id = "http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0";

        //System.out.println("Calling with 128 bit Sensor ID ==> " + id);

//        updateSensorDataOnLSM("swissex",
//                "swissex1234",
//                id,
//                "Temperature",
//                0.0,
//                "C");
    }

    
    static void register(){
    	try{ /* 
    	 * add new sensor to lsm store. For example: Air quality sensor from Lausanne 
    	 ** Sensor name: lausanne_1057 */ 
         // 1. Create an instance of Sensor class and set the sensor metadata 
    		
    		String id=org.openiot.gsn.metadata.LSM.utils.addSensorToLSM("sofiane",  "sofiane", 
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
    	
    	utils.updateSensorDataOnLSM("sofiane", "sofiane", 
    			"http://lsm.deri.ie/OpenIoT/sensormeta#", "http://lsm.deri.ie/OpenIoT/sensordata#", 
    			"http://lsm.deri.ie/resource/11015611079233", "http://lsm.deri.ie/ont/lsm.owl#AirTemperature", 9.877676, 
    			"C", new Date());
    	
    	/*
    	Observation obs = new Observation(); 
    	obs.setDataGraph("http://lsm.deri.ie/OpenIoT/demo/sensordata#");

    	obs.setMetaGraph("http://lsm.deri.ie/OpenIoT/demo/sensormeta#"); 
    	// set SensorURL of observation 
    	//for example: "http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0" is the sensorURL of lausanne_1057 sensor 
    	obs.setSensor("http://lsm.deri.ie/resource/123128090807967"); 
    	//set time when the observation was observed. In this example, the time is current local time. 
    	obs.setTimes(new Date()); 
    	 
    	ObservedProperty obvTem = new ObservedProperty();
    	obvTem.setObservationId(obs.getId());
    	obvTem.setPropertyType(ObsConstant.TEMPERATURE);
    	obvTem.setValue(9.58485958485958);
    	obvTem.setUnit("C");
    	obs.addReading(obvTem);
    	ObservedProperty obvCO = new ObservedProperty();
    	obvCO.setObservationId(obs.getId());
    	obvCO.setPropertyType(ObsConstant.HUMIDITY);
    	obvCO.setValue(0.0366300366300366);
    	obvCO.setUnit("C");
    	obs.addReading(obvCO);
    	obs.setDataGraph("http://lsm.deri.ie/OpenIoT/sensordata#");
    	obs.setMetaGraph("http://lsm.deri.ie/OpenIoT/sensormeta#");
		LSMTripleStore lsmStore = new LSMTripleStore(); 
	    
    	lsmStore.sensorDataUpdate(obs);     */
    }
    
    static void listSensor(Sensor s) {
        System.out.println("********************");
        System.out.println("author    : " + s.getAuthor());
        System.out.println("code      : " + s.getCode());
        System.out.println("infor     : " + s.getInfor());
        System.out.println("source    : " + s.getSource());
        System.out.println("datagraph : " + s.getDataGraph());
        System.out.println("id        : " + s.getId());
        System.out.println("metagraph : " + s.getMetaGraph());
        System.out.println("name : " + s.getName());
        System.out.println("sensortyp : " + s.getSensorType());
        System.out.println("sourcetyp : " + s.getSourceType());
        System.out.println("place : " + s.getPlace());
        System.out.println("times : " + s.getTimes());
        //System.out.println("user : " + s.getUser());
        System.out.println("string : " + s.toString());
        System.out.println("------------");
    }
}
