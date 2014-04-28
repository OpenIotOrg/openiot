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

import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.metadata.rdf.SensorMetadata;
import org.openiot.gsn.utils.Utils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LSMRepository {

    private static final transient Logger logger = Logger.getLogger(LSMRepository.class);

    public static final String LSM_CONFIG_PROPERTIES_FILE = "conf/lsm_config.properties";
    public static final String METADATA_FILE_SUFFIX = ".metadata";
    public static final String RDFTURTLE_FILE_SUFFIX = ".ttl";

    public static LSMSchema lsmSchema;

    public Map<String, LSMSensorMetaData> getLsmSensorsMetaDataLookupTable() {
        return lsmSensorsMetaDataLookupTable;
    }

    private Map<String, LSMSensorMetaData> lsmSensorsMetaDataLookupTable = new HashMap<String, LSMSensorMetaData>(); //lookup table for sensors metadata, indexed by virtual sensor name
    //TODO: unload when sensors are unloaded in VSensorLoader

    private static LSMRepository singleton;

    private LSMRepository() {
        lsmSchema = new LSMSchema();
        //lsmUser = new LSMUser();

        if (!lsmSchema.initFromConfigFile(LSM_CONFIG_PROPERTIES_FILE))
            logger.warn("Couldn't initialize LSM schema. Check your file: " + LSM_CONFIG_PROPERTIES_FILE);

        //if (!lsmUser.initFromConfigFile(LSM_CONFIG_PROPERTIES_FILE))
        //    logger.warn("Couldn't initialize LSM user. Check your file: " + LSM_CONFIG_PROPERTIES_FILE);

        logger.info("LSM Schema: " + lsmSchema);
        //logger.info("LSM User: " + lsmUser);
    }

    /*
    * Singleton
    * */
    public static synchronized LSMRepository getInstance() {
        if (singleton == null)
            try {
                singleton = new LSMRepository();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        return singleton;
    }

    public LSMSensorMetaData loadMetadata(VSensorConfig vsConfig) throws FileNotFoundException {
    	String vsName=vsConfig.getName();
    	// check if sensor already exists in lookup table, useful in order not to register again at runtime
        if (lsmSensorsMetaDataLookupTable.containsKey(vsName)) {
            logger.warn("Virtual sensor " + Utils.identify(vsConfig) + " already in lsmSensorsMetaData lookup table");
            return lsmSensorsMetaDataLookupTable.get(vsName);
        }

        String metadataFile = vsConfig.getFileName().replace(".xml", METADATA_FILE_SUFFIX);
        String rdfFile = vsConfig.getFileName().replace(".xml",RDFTURTLE_FILE_SUFFIX);
        if (new File(metadataFile).exists()){
        	// load metadata for virtual sensor specified by vsConfig
        	LSMSensorMetaData lsmSensorMetaData = new LSMSensorMetaData();
	        lsmSensorMetaData.initFromConfigFile(metadataFile);
	        // add metadata to lookup table
	        lsmSensorsMetaDataLookupTable.put(vsName, lsmSensorMetaData);
	    	return lsmSensorMetaData;
        }

        else if (new File(rdfFile).exists()){
        	SensorMetadata md = new SensorMetadata();
        	md.loadFromFile(rdfFile);
        	LSMSensorMetaData lsmMeta= md.fillSensorMetadata();
	        lsmSensorsMetaDataLookupTable.put(vsName, lsmMeta);
        	return lsmMeta;
        }
        else throw new IllegalStateException("No metadata available for Virtual Sensor "+vsName);
    }

	/**
	 * Remove the given virtual sensor from the cache so a new sensor with the
	 * same name does not re-use the old data.
	 *
	 * @param vsName The name of the sensor that needs to be removed.
	 */
	public void unloadMetaData(String vsName) {
		lsmSensorsMetaDataLookupTable.remove(vsName);
	}

    /*
    This method announces a virtual sensor identified by vsConfig to LSM
    It first checks if the virtual sensor already exists in the metadata lookup table. If not, it will add it.
    Returns True
              if the virtual sensor has been successfully added to LSM
              if the virtual sensor is already registered to LSM
    Returns False
              if the virtual sensor is already in the lookup table
              if the metadata couldn't be loaded from metadata file
              if the sensor couldn't be registered to LSM (empty sensor ID)
    TODO: in VSensorloader make sure that sensor is not loaded when false is returned
     */
    public boolean announceSensor(VSensorConfig vsConfig) throws FileNotFoundException {
        String metadataFile = vsConfig.getFileName() + METADATA_FILE_SUFFIX;

    	LSMSensorMetaData lsmSensorMetaData = loadMetadata(vsConfig);
        logger.info(lsmSensorMetaData);
        if (lsmSensorMetaData==null) return false;

        //TODO: check if sensor is already registered to LSM (to avoid duplicates)
        /*if (lsmSensorMetaData.isRegisteredToLSM()) {
            logger.info("Sensor " + Utils.identify(vsConfig) + " already registered to LSM with id " + lsmSensorMetaData.getSensorID() + ". No need to register it again.");
            return true;
        }*/

        // announce sensor to LSM
        String sensorID = MetadataCreator.addSensorToLSM(lsmSensorMetaData);

        // check returned sensor ID
        if (sensorID == "") {
            logger.warn("Couldn't register sensor " + Utils.identify(vsConfig) + " to LSM. Received empty sensor ID");
            return false;
        }

        logger.info("Sensor " + Utils.identify(vsConfig) + " registered to LSM with sensorID: " + sensorID);

        // update sensor ID in metadata file (make change persistent)
        if (!lsmSensorMetaData.updateSensorIDInConfigFile(metadataFile, sensorID)) {
            logger.warn("Couldn't write sensorID for sensor " + Utils.identify(vsConfig));
            return false;
        }

        if (!lsmSensorMetaData.setSensorAsRegistered(metadataFile)) {
            logger.warn("Couldn't set LSM registration flag for sensor " + Utils.identify(vsConfig));
            return false;
        }

        return true;
    }

    public boolean announceSensorFromConfigFile(String metadataFile, double latitude, double longitude) {

        // load metadata for virtual sensor specified by vsConfig
        LSMSensorMetaData lsmSensorMetaData = new LSMSensorMetaData();
        lsmSensorMetaData.initFromConfigFile(metadataFile);

        logger.info(lsmSensorMetaData);

        //TODO: check if sensor is already registered to LSM (to avoid duplicates)
        /*if (lsmSensorMetaData.isRegisteredToLSM()) {
            logger.info("Sensor " + lsmSensorMetaData.getSensorName() + " already registered to LSM with id " + lsmSensorMetaData.getSensorID() + ". No need to register it again.");
            return true;
        }*/

        // announce sensor to LSM
        String sensorID = MetadataCreator.addSensorToLSM(lsmSensorMetaData);
        // check returned sensor ID
        if (sensorID == "") {
            logger.warn("Couldn't register sensor " + lsmSensorMetaData.getSensorName() + " to LSM. Received empty sensor ID");
            return false;
        }

        logger.info("Sensor " + lsmSensorMetaData.getSensorName() + " registered to LSM with sensorID: " + sensorID);

        // update sensor ID in metadata file (make change persistent)
        if (!lsmSensorMetaData.updateSensorIDInConfigFile(metadataFile, sensorID)) {
            logger.warn("Couldn't write sensorID for sensor " + lsmSensorMetaData.getSensorName());
            return false;
        }

        if (!lsmSensorMetaData.setSensorAsRegistered(metadataFile)) {
            logger.warn("Couldn't set LSM registration flag for sensor " + lsmSensorMetaData.getSensorName());
            return false;
        }

        return true;
    }

    public boolean publishSensorDataToLSM(String vsName, String field, double value, Date date) {
        boolean success = true;
        field = field.toLowerCase();

        LSMSensorMetaData lsmSensorsMetaData = lsmSensorsMetaDataLookupTable.get(vsName);
        if (!lsmSensorsMetaData.getFields().containsKey(field))
          throw new IllegalArgumentException("The field "+field+" in virtual sensor "+vsName+" has no associated metadata.");
        LSMFieldMetaData lsmField= lsmSensorsMetaData.getFields().get(field);
        success = utils.updateSensorDataOnLSM(
                lsmSchema.getMetaGraph(),
                lsmSchema.getDataGraph(),
                lsmSensorsMetaData.getSensorID(),
                lsmField.getLsmPropertyName(),
                value,
                lsmField.getLsmUnit(),
                lsmSensorsMetaData.getFeatureOfInterest(),
                date);

        return success;
    }
}
