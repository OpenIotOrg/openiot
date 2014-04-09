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

package org.openiot.gsn.metadata.LSM;

import org.apache.log4j.Logger;
import org.openiot.lsm.beans.*;
import org.openiot.lsm.server.LSMTripleStore;
//import lsm.beans.*;
//import lsm.server.LSMTripleStore;


import java.util.Date;

public class utils {

    private static final transient Logger logger = Logger.getLogger(utils.class);

    private static LSMSchema lsmSchema=new LSMSchema();
	static{
		lsmSchema.initFromConfigFile(LSMRepository.LSM_CONFIG_PROPERTIES_FILE);
	}
    public static String addSensorToLSM(String username,
                                        String password,
                                        String metaGraph,
                                        String dataGraph,
                                        String sensorName,
                                        String sensorAuthor,
                                        String sourceType,
                                        String sensorType,
                                        String infor,
                                        String sensorSource,
                                        String [] properties,
                                        double latitude,
                                        double longitude) {

        String sensorID = "";
        logger.info("Add sensor: "+sensorName+","+sensorAuthor+","+sourceType+","+sensorType);
        logger.info("Graphs: "+metaGraph+","+dataGraph);
        for (String p:properties){
        logger.info("Properties: "+p);}
        try {
            // 1. Create an instance of Sensor class and set the sensor metadata
            Sensor sensor = new Sensor();
            sensor.setName(sensorName);
            sensor.setAuthor(sensorAuthor);
            sensor.setSourceType(sourceType);
            sensor.setSensorType(sensorType);
            sensor.setInfor(infor);
            sensor.setSource(sensorSource);
            for (String p:properties){
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
            logger.info("Connecting to LSM: "+lsmSchema.getLsmServerUrl());
            LSMTripleStore lsmStore = new LSMTripleStore(lsmSchema.getLsmServerUrl());

            //set user information for authentication
            //lsmStore.setUser(user);

            //call sensorAdd method
            sensorID=lsmStore.sensorAdd(sensor);           
            //System.out.println(listSensor(sensor).toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
        }

        return sensorID;

    }

    public static boolean updateSensorDataOnLSM(String username,
                                                String password,
                                                String metaGraph,
                                                String dataGraph,
                                                String sensorID,
                                                String propertyType,
                                                double fieldValue,
                                                String fieldUnit,
                                                Date date) {

        boolean success = true;
        
        logger.debug("Update sensor data: "+sensorID +","+propertyType+","+metaGraph+","+dataGraph);
        
        try {
            /*
            * Set sensor's author
            * If you don't have LSM account, please visit LSM Home page (http://lsm.deri.ie) to sign up
            */
            //User user = new User();
            //user.setUsername(username);
            //user.setPass(password);

            Sensor sensor = new Sensor();

            sensor.setId(sensorID);


            //sensor.setUser(user);

            // create LSMTripleStore instance
            LSMTripleStore lsmStore = new LSMTripleStore(lsmSchema.getLsmServerUrl());

            //set user information for authentication
            //lsmStore.setUser(user);

            /*
            * An Observation is a Situation in which a Sensing method has been used to estimate or
            * calculate a value of a Property of a FeatureOfInterest.
            */


            //create an Observation object
            Observation obs = new Observation();
          
            // set SensorURL of observation
            
            //set time when the observation was observed. In this example, the time is current local time.
            obs.setTimes(date);
            /*
            * Relation linking an Observation to the Property that was observed
            */
            ObservedProperty obvTem = new ObservedProperty();
            obvTem.setObservationId(obs.getId());
            //obvTem.setPropertyName(fieldName);  //jpc updated API
            obvTem.setPropertyType(propertyType);
            obvTem.setValue(fieldValue);
            obvTem.setUnit(fieldUnit);
            obs.addReading(obvTem);
            obs.setMetaGraph(metaGraph);
            obs.setDataGraph(dataGraph);

            obs.setSensor(sensorID);
            lsmStore.sensorDataUpdate(obs);

        } catch (Exception ex) {
            success = false;
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
        }

        return success;

    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Error: Metadata file is missing.\n");
            System.exit(-1);
        }

        String metadataFileName = args[0];
        System.out.println("Using metadata file: " + metadataFileName);

        LSMSensorMetaData metaData = new LSMSensorMetaData();
        boolean success = metaData.initFromConfigFile(metadataFileName);

        LSMSchema schema = new LSMSchema();
        schema.initFromConfigFile(metadataFileName);
        System.out.println(schema.toString());

        LSMUser user = new LSMUser();
        user.initFromConfigFile(LSMRepository.LSM_CONFIG_PROPERTIES_FILE);

        System.out.println(user.toString());

        System.out.println(metaData.toString());

        System.out.println(success);


        String SID = addSensorToLSM(user.getUser(),
                user.getPassword(),
                schema.getMetaGraph(),
                schema.getDataGraph(),
                metaData.getSensorName(),
                metaData.getAuthor(),
                metaData.getSourceType(),
                metaData.getSensorType(),
                metaData.getInformation(),
                metaData.getSource(),
                metaData.getProperties(),
                metaData.getLatitude(),
                metaData.getLongitude());

        System.out.println("Sensor registered to LSM with ID: " + SID);

        System.exit(0);
    }

    static StringBuilder listSensor(Sensor s) {
        return new StringBuilder("").append("********************")
                .append("\nauthor    : ").append(s.getAuthor())
                .append("\ncode      : ").append(s.getCode())
                .append("\ninfor     : ").append(s.getInfor())
                .append("\nsource    : ").append(s.getSource())
                .append("\ndatagraph : ").append(s.getDataGraph())
                .append("\nid        : ").append(s.getId())
                .append("\nmetagraph : ").append(s.getMetaGraph())
                .append("\nname      : ").append(s.getName())
                .append("\nsensortyp : ").append(s.getSensorType())
                .append("\nsourcetyp : ").append(s.getSourceType())
                .append("\nplace     : ").append(s.getPlace())
                .append("\ntimes     : ").append(s.getTimes())
                //.append("\nuser      : ").append(s.getUser())
                .append("\nstring    : ").append(s.toString())
                .append("\nlatitude  : ").append(s.getPlace().getLat())
                .append("\nlongitude : ").append(s.getPlace().getLng())
                .append("\n------------");
    }
}
