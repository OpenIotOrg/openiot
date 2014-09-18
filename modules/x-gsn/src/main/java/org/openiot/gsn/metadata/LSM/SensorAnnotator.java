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

import java.io.FileNotFoundException;
import java.util.Date;

import org.openiot.gsn.metadata.rdf.SensorMetadata;
import org.openiot.gsn.utils.CASUtils;
import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.ObservedProperty;
import org.openiot.lsm.beans.Place;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.server.LSMTripleStore;
import org.openiot.security.client.OAuthorizationCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SensorAnnotator {
    private static final transient Logger logger = LoggerFactory.getLogger(SensorAnnotator.class);
    private static String lsmServer="";
    private static String metaGraph="";
    private static String dataGraph="";
	static{
		Config conf=ConfigFactory.load();//LSMRepository.LSM_CONFIG_PROPERTIES_FILE);
		metaGraph=conf.getString("metaGraph");
		dataGraph=conf.getString("dataGraph");
		lsmServer=conf.getString("lsm.server");
	}

    public static String addSensorToLSM(LSMSensorMetaData md){
    	return addSensorToLSM(metaGraph,dataGraph, 
    			md.getSensorName(), md.getAuthor(), md.getSourceType(), 
    			md.getSensorType(), md.getInformation(), md.getSource(), 
    			md.getProperties(), md.getLatitude(), md.getLongitude());
    }
    
	
    public static String addSensorToLSM(String metaGraph,String dataGraph,
                                        String sensorName,String sensorAuthor,
                                        String sourceType,String sensorType,
                                        String infor,String sensorSource,
                                        String [] properties,
                                        double latitude,double longitude) {

        String sensorID = "";
        logger.info("Add sensor: "+sensorName+","+sensorAuthor+","+sourceType+","+sensorType);
        logger.info("Graphs: "+metaGraph+","+dataGraph);
        for (String p:properties){
        	logger.info("Properties: "+p);
        }
        try {
            Sensor sensor = new Sensor();
            sensor.setName(sensorName);
            sensor.setAuthor(sensorAuthor);
            sensor.setSensorType(sensorType);
            sensor.setInfor(infor);
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
            logger.info("Connecting to LSM: "+lsmServer);
            LSMTripleStore lsmStore = new LSMTripleStore(lsmServer);
            OAuthorizationCredentials cred=CASUtils.getTokenAndId();            
            sensorID=lsmStore.sensorAdd(sensor,cred.getClientId(),cred.getAccessToken());                       
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
        }
        return sensorID;
    }

	/**
	 * Deletes the sensor and all sensor readings from the LSM data store.
	*
	* @param sensorId The sensorId of the sensor for which to delete all data.
	*/
	public static void deleteSensorFromLSM(String sensorId) {
		LSMTripleStore lsmStore = new LSMTripleStore(lsmServer);
        OAuthorizationCredentials cred=CASUtils.getTokenAndId();            
		lsmStore.deleteAllReadings(sensorId, dataGraph,cred.getClientId(),cred.getAccessToken());
		lsmStore.sensorDelete(sensorId, metaGraph,cred.getClientId(),cred.getAccessToken());
	}


    public static boolean updateSensorDataOnLSM(String vsName, String fieldName, 
    		String propertyUri,Object value, Date date) {
        boolean success = true;
        
        //String field = fieldName.toLowerCase();
        LSMSensorMetaData lsmSensorsMetaData = LSMRepository.getInstance().getLsmSensorsMetaDataLookupTable().get(vsName);
        if (!lsmSensorsMetaData.getFields().containsKey(propertyUri))
          throw new IllegalArgumentException("The field "+fieldName+" in virtual sensor "+vsName+" has no associated metadata. PropertyUri: "+propertyUri);
        LSMFieldMetaData lsmField= lsmSensorsMetaData.getFields().get(propertyUri);
        success = updateSensorDataOnLSM(metaGraph, dataGraph,
                lsmSensorsMetaData.getSensorID(), lsmField.getLsmPropertyName(),
                value, lsmField.getLsmUnit(),
                lsmSensorsMetaData.getFeatureOfInterest(), date);
        return success;
    }
    
    public static boolean updateSensorDataOnLSM(String metaGraph,String dataGraph,
            String sensorID,String propertyType,
            Object fieldValue,String fieldUnit,
            String feature,Date date) {

    	boolean success = true;

    	logger.debug("Update sensor data: "+sensorID +","+propertyType+","+metaGraph+","+dataGraph+","+feature+","+fieldValue);

    	try {
    		Sensor sensor = new Sensor();
    		sensor.setId(sensorID);

    		LSMTripleStore lsmStore = new LSMTripleStore(lsmServer);            

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
            OAuthorizationCredentials cred=CASUtils.getTokenAndId();
    		lsmStore.sensorDataUpdate(obs,cred.getClientId(),cred.getAccessToken());

    	} catch (Exception ex) {
    		success = false;
    		ex.printStackTrace();
    		System.out.println("cannot send the data to server");
    	}

    	return success;

    }
    
	public static void addRdfMetadatatoLSM(SensorMetadata metadata){
        LSMTripleStore lsmStore = new LSMTripleStore(lsmServer);
        logger.info("Connecting to LSM: "+lsmServer);
        OAuthorizationCredentials cred=CASUtils.getTokenAndId();            
        lsmStore.pushRDF(metaGraph,metadata.serializeRDF(),cred.getClientId(),cred.getAccessToken());

	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		if (args.length < 1) {
			System.out.println("Error: Metadata file is missing.\n");
			System.exit(-1);
		}
	    String metadataFileName = args[0];
	    logger.info("Using metadata file: " + metadataFileName);
		//properties file
		if (args.length==1){	        
			LSMSensorMetaData metaData = new LSMSensorMetaData();
			try{
		    metaData.initFromConfigFile(metadataFileName);
			} catch (Exception e){
				logger.error("Error loading metadata file: "+e.getMessage());
				System.exit(0);
			}
		    //LSMSchema schema = new LSMSchema();
		    //schema.initFromConfigFile(metadataFileName);
		    String SID = addSensorToLSM(metaData);
		    System.out.println("--------------------"+metadataFileName);
		    logger.info("Sensor registered to LSM with ID: " + SID);
	        try {
	        	logger.debug("Rewrite sensorId for: "+metadataFileName);
				LSMRepository.getInstance().writeSensorId(metadataFileName, SID);
			} catch (Exception e) {
				logger.error("Error "+e.getMessage());
				e.printStackTrace();
			}

		}
		//rdf file
		else if (args[1].equals("-rdf")){
			SensorMetadata metadata=new SensorMetadata();
			try {
			  metadata.loadFromFile(metadataFileName);
			} catch (Exception ex){
				logger.error("Error loading metadata or Invilid metadata: "+ex.getMessage());
				System.exit(0);
			}
			String sensorId=metadata.fillSensorMetadata().getSensorID();
			addRdfMetadatatoLSM(metadata);
			logger.info("Sensor metadata registered to LSM: "+sensorId);
		}
		else {
			logger.error("Invalid arguments, arguments should be either <METADATA_FILE> or <METADATA_FILE> -rdf.");
		}

      System.exit(0);

	}    
}
