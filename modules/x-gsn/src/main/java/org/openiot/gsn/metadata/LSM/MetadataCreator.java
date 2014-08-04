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
import org.openiot.lsm.beans.Place;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.server.LSMTripleStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataCreator {
    private static final transient Logger logger = LoggerFactory.getLogger(utils.class);
    private static LSMSchema lsmSchema=new LSMSchema();
	static{
		lsmSchema.initFromConfigFile(LSMRepository.LSM_CONFIG_PROPERTIES_FILE);
	}

    public static String addSensorToLSM(LSMSensorMetaData md){
    	return addSensorToLSM(lsmSchema.getMetaGraph(),lsmSchema.getDataGraph(),
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


	public static void addRdfMetadatatoLSM(SensorMetadata metadata){
        LSMTripleStore lsmStore = new LSMTripleStore(lsmSchema.getLsmServerUrl());
        logger.info("Connecting to LSM: "+lsmSchema.getLsmServerUrl());
        lsmSchema.getMetaGraph();
        lsmStore.pushRDF(lsmSchema.getMetaGraph(),metadata.serializeRDF());

	}

	/**
	 * Deletes the sensor and all sensor readings from the LSM data store.
	 *
	 * @param sensorId The sensorId of the sensor for which to delete all data.
	 */
	public static void deleteSensorFromLSM(String sensorId) {
		LSMTripleStore lsmStore = new LSMTripleStore(lsmSchema.getLsmServerUrl());
		lsmStore.deleteAllReadings(sensorId, lsmSchema.getDataGraph());
		lsmStore.sensorDelete(sensorId, lsmSchema.getMetaGraph());
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
		    LSMSchema schema = new LSMSchema();
		    schema.initFromConfigFile(metadataFileName);
		    String SID = addSensorToLSM(metaData);
		    logger.info("Sensor registered to LSM with ID: " + SID);

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
