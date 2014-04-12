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
 *    Contact: OpenIoT mailto: info@openiot.eu
 *    @author Sofiane Sarni
 *    @author Jean-Paul Calbimonte
 */

package org.openiot.gsn.metadata.LSM;

import org.openiot.lsm.beans.*;
import org.openiot.lsm.server.LSMTripleStore;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.Date;

public class utils {

    private static final transient Logger logger = LoggerFactory.getLogger(utils.class);

    private static LSMSchema lsmSchema=new LSMSchema();
	static{
		lsmSchema.initFromConfigFile(LSMRepository.LSM_CONFIG_PROPERTIES_FILE);
	}

    public static String addSensorToLSM(LSMSensorMetaData md){
    	return addSensorToLSM(lsmSchema.getMetaGraph(), 
    			lsmSchema.getDataGraph(), 
    			md.getSensorName(), md.getAuthor(), md.getSourceType(), 
    			md.getSensorType(), md.getInformation(), md.getSource(), 
    			md.getProperties(), md.getLatitude(), md.getLongitude());
    }
    
	
    public static String addSensorToLSM(
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

            // create LSMTripleStore instance
            logger.info("Connecting to LSM: "+lsmSchema.getLsmServerUrl());
            LSMTripleStore lsmStore = new LSMTripleStore(lsmSchema.getLsmServerUrl());

            sensorID=lsmStore.sensorAdd(sensor);           

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
        }

        return sensorID;

    }

    public static boolean updateSensorDataOnLSM(
                                                String metaGraph,
                                                String dataGraph,
                                                String sensorID,
                                                String propertyType,
                                                double fieldValue,
                                                String fieldUnit,
                                                String feature,
                                                Date date) {

        boolean success = true;
        
        logger.debug("Update sensor data: "+sensorID +","+propertyType+","+metaGraph+","+dataGraph);
        
        try {
            Sensor sensor = new Sensor();
            sensor.setId(sensorID);

            LSMTripleStore lsmStore = new LSMTripleStore(lsmSchema.getLsmServerUrl());            

            //create an Observation object
            Observation obs = new Observation();
            obs.setTimes(date);

            obs.setFeatureOfInterest(feature);
            ObservedProperty obvTem = new ObservedProperty();
            obvTem.setObservationId(obs.getId());
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

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length < 1) {
            System.out.println("Error: Metadata file is missing.\n");
            System.exit(-1);
        }

        String metadataFileName = args[0];
        System.out.println("Using metadata file: " + metadataFileName);
          
        LSMSensorMetaData metaData = new LSMSensorMetaData();
        metaData.initFromConfigFile(metadataFileName);

        LSMSchema schema = new LSMSchema();
        schema.initFromConfigFile(metadataFileName);
        System.out.println(schema.toString());

        System.out.println(metaData.toString());

        //System.out.println(success);


        String SID = addSensorToLSM(
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
                .append("\nstring    : ").append(s.toString())
                .append("\nlatitude  : ").append(s.getPlace().getLat())
                .append("\nlongitude : ").append(s.getPlace().getLng())
                .append("\n------------");
    }
}
