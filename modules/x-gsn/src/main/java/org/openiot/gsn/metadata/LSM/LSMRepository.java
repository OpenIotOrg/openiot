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

import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.utils.Utils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LSMRepository {

    private static final transient Logger logger = Logger.getLogger(LSMRepository.class);

    public static final String LSM_CONFIG_PROPERTIES_FILE = "conf/lsm_config.properties";
    public static final String METADATA_FILE_SUFFIX = ".metadata";

    public static LSMSchema lsmSchema;
    public static LSMUser lsmUser;

    public Map<String, LSMSensorMetaData> getLsmSensorsMetaDataLookupTable() {
        return lsmSensorsMetaDataLookupTable;
    }

    private Map<String, LSMSensorMetaData> lsmSensorsMetaDataLookupTable = new HashMap<String, LSMSensorMetaData>(); //lookup table for sensors metadata, indexed by virtual sensor name
    //TODO: unload when sensors are unloaded in VSensorLoader

    private static LSMRepository singleton;

    private LSMRepository() {
        lsmSchema = new LSMSchema();
        lsmUser = new LSMUser();

        if (!lsmSchema.initFromConfigFile(LSM_CONFIG_PROPERTIES_FILE))
            logger.warn("Couldn't initialize LSM schema. Check your file: " + LSM_CONFIG_PROPERTIES_FILE);

        if (!lsmUser.initFromConfigFile(LSM_CONFIG_PROPERTIES_FILE))
            logger.warn("Couldn't initialize LSM user. Check your file: " + LSM_CONFIG_PROPERTIES_FILE);

        logger.info("LSM Schema: " + lsmSchema);
        logger.info("LSM User: " + lsmUser);
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
    public boolean announceSensor(VSensorConfig vsConfig) {
        String vsName = vsConfig.getName();

        // check if sensor already exists in lookup table, useful in order not to register again at runtime
        if (lsmSensorsMetaDataLookupTable.containsKey(vsName)) {
            logger.warn("Virtual sensor " + Utils.identify(vsConfig) + " already in lsmSensorsMetaData lookup table");
            return false;
        }

        // load metadata for virtual sensor specified by vsConfig
        LSMSensorMetaData lsmSensorMetaData = new LSMSensorMetaData();
        String metadataFile = vsConfig.getFileName() + METADATA_FILE_SUFFIX;
        if (!lsmSensorMetaData.initFromConfigFile(metadataFile)) {
            logger.warn(new StringBuilder("Couldn't announce sensor")
                    .append(Utils.identify(vsConfig))
                    .append(" due to errors while trying to load metadata from [")
                    .append(metadataFile)
                    .append("]"));
            return false;
        }

        // add metadata to lookup table
        lsmSensorsMetaDataLookupTable.put(vsName, lsmSensorMetaData);
        logger.info(lsmSensorMetaData);

        //TODO: check if sensor is already registered to LSM (to avoid duplicates)
        if (lsmSensorMetaData.isRegisteredToLSM()) {
            logger.info("Sensor " + Utils.identify(vsConfig) + " already registered to LSM with id " + lsmSensorMetaData.getSensorID() + ". No need to register it again.");
            return true;
        }

        // announce sensor to LSM
        String sensorID = utils.addSensorToLSM(
                lsmUser.getUser(),
                lsmUser.getPassword(),
                lsmSchema.getMetaGraph(),
                lsmSchema.getDataGraph(),
                lsmSensorMetaData.getSensorName(),
                lsmSensorMetaData.getAuthor(),
                lsmSensorMetaData.getSourceType(),
                lsmSensorMetaData.getSensorType(),
                lsmSensorMetaData.getInformation(),
                lsmSensorMetaData.getSource(),
                vsConfig.getLatitude(),
                vsConfig.getLongitude());

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
        if (!lsmSensorMetaData.initFromConfigFile(metadataFile)) {
            logger.warn(new StringBuilder("Couldn't announce sensor")
                    .append(" due to errors while trying to load metadata from [")
                    .append(metadataFile)
                    .append("]"));
            return false;
        }

        logger.info(lsmSensorMetaData);

        //TODO: check if sensor is already registered to LSM (to avoid duplicates)
        if (lsmSensorMetaData.isRegisteredToLSM()) {
            logger.info("Sensor " + lsmSensorMetaData.getSensorName() + " already registered to LSM with id " + lsmSensorMetaData.getSensorID() + ". No need to register it again.");
            return true;
        }

        // announce sensor to LSM
        String sensorID = utils.addSensorToLSM(
                lsmUser.getUser(),
                lsmUser.getPassword(),
                lsmSchema.getMetaGraph(),
                lsmSchema.getDataGraph(),
                lsmSensorMetaData.getSensorName(),
                lsmSensorMetaData.getAuthor(),
                lsmSensorMetaData.getSourceType(),
                lsmSensorMetaData.getSensorType(),
                lsmSensorMetaData.getInformation(),
                lsmSensorMetaData.getSource(),
                latitude,
                longitude);

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

        success = utils.updateSensorDataOnLSM(lsmUser.getUser(),
                lsmUser.getPassword(),
                lsmSchema.getMetaGraph(),
                lsmSchema.getDataGraph(),
                lsmSensorsMetaData.getSensorID(),
                lsmSensorsMetaData.getFields().get(field).getLsmPropertyName(),
                value,
                lsmSensorsMetaData.getFields().get(field).getLsmUnit(),
                date);

        return success;
    }
}
