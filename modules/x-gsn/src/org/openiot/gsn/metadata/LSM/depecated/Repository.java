/*
* DEPRECATED don't Use
* TODO: cleanup
* */

package org.openiot.gsn.metadata.LSM.depecated;

import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.utils.PropertiesReader;
import org.openiot.gsn.utils.TestLSM;
import lsm.beans.*;
import lsm.server.LSMTripleStore;
import org.apache.log4j.Logger;

import java.util.Date;

public class Repository {

    private static final transient Logger logger = Logger.getLogger(Repository.class);
    public static final String LSM_CONFIG_PROPERTIES_FILE = "conf/lsm_config.properties";
    public static final String METADATA_FILE_SUFFIX = ".metadata";
    LSMTripleStore lsmStore = null;
    User user;

    private static Repository singleton;

    private Repository() {
        /*
        * Set sensor's author
        * If you don't have LSM account, please visit LSM Home page (http://lsm.deri.ie) to sign up
        */
        User user = new User();
        user.setUsername(PropertiesReader.readProperty(LSM_CONFIG_PROPERTIES_FILE, "username"));
        user.setPass(PropertiesReader.readProperty(LSM_CONFIG_PROPERTIES_FILE, "password"));

        logger.warn("username : " + PropertiesReader.readProperty(LSM_CONFIG_PROPERTIES_FILE, "username"));
        logger.warn("password : " + PropertiesReader.readProperty(LSM_CONFIG_PROPERTIES_FILE, "password"));
    }

    public static synchronized Repository getInstance() {
        if (singleton == null)
            try {
                singleton = new Repository();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        return singleton;
    }

    public static String generateMetaDataFileName(VSensorConfig vs) {
        return vs.getFileName() + METADATA_FILE_SUFFIX;
    }

    public static SensorMetaData createSensorMetaData(VSensorConfig vs) {
        String metadataFile = generateMetaDataFileName(vs);
        SensorMetaData s = new SensorMetaData();
        s.initFromFile(metadataFile);
        return s;
    }

    public Sensor createLSMSensor(VSensorConfig vs) {
        SensorMetaData s = createSensorMetaData(vs);


        String id = TestLSM.addSensorToLSM("swissex",
                "swissex1234",
                s.getSensorName(),
                s.getAuthor(),
                s.getSourceType(),
                s.getSensorType(),
                s.getInformation(),
                s.getSource(),
                0,
                0
        );

        System.out.println("SENSOR ID published to LSM => " + id);

        if (true) return null;

        Sensor sensor = null;
        LSMTripleStore lsmStore = null;

        try {

            System.out.println("Testing LSM...");


            // 1. Create an instanse of Sensor class and set the sensor metadata
            sensor = new Sensor();
            sensor.setName(s.getSensorName());
            sensor.setAuthor(s.getAuthor());
            sensor.setSensorType(s.getSensorType());
            sensor.setSourceType(s.getSourceType());
            sensor.setInfor(s.getInformation());
            sensor.setSource(s.getSource());
            sensor.setMetaGraph("http://lsm.deri.ie/OpenIoT/test/sensormeta#");
            sensor.setDataGraph("http://lsm.deri.ie/OpenIoT/test/sensordata#");
            sensor.setTimes(new Date());

            System.out.println(sensor.getId());

            // set sensor location information (latitude, longitude, city, country, continent...)
            Place place = new Place();
            place.setLat(vs.getLatitude());
            place.setLng(vs.getLongitude());
            sensor.setPlace(place);

            sensor.setUser(user);

            // create LSMTripleStore instance
            lsmStore = new LSMTripleStore();

            //set user information for authentication
            lsmStore.setUser(user);


            System.out.printf(sensor.getId());
            //call sensorAdd method
            lsmStore.sensorAdd(sensor);

            System.out.printf(sensor.getId());

        } catch (Exception e) {
            logger.warn("cannot send the data to server");
            logger.warn(e);
        }

        return sensor;
    }

    public static Sensor createLSMSensor2(VSensorConfig vs) {
        SensorMetaData s = createSensorMetaData(vs);

        System.out.println("*************  NOW publishing *****" + "\n" + vs);
        System.out.println(s);


        Sensor sensor = new Sensor();
        sensor.setName(s.getSensorName());
        sensor.setAuthor(s.getAuthor());
        sensor.setSensorType(s.getSensorType());
        sensor.setSourceType(s.getSourceType());
        sensor.setInfor(s.getInformation());
        sensor.setSource(s.getSource());
        sensor.setTimes(new Date());


        Place place = new Place();
        place.setLat(vs.getLatitude());
        place.setLng(vs.getLongitude());
        sensor.setPlace(place);

        logger.warn("sensor created from file (metadata)");

        return sensor;
    }

    public void announceSensor(VSensorConfig vs) {

        Sensor sensor = createLSMSensor(vs);

        logger.warn("Sensor is to be announced");
        logger.warn(vs);
        logger.warn(sensor.getName());

/*
        // create LSMTripleStore instance
        LSMTripleStore lsmStore = new LSMTripleStore();

        //set user information for authentication
        lsmStore.setUser(user);

        if (sensor==null)
            logger.warn("SENSOR IS NULL");
        else
            logger.warn("sensor not null");

        //call sensorAdd method
        lsmStore.sensorAdd(sensor);
              */
    }

    public static SensorObservation createSensorObservation(VSensorConfig vs, String field) {
        String fileName = generateMetaDataFileName(vs);
        SensorObservation o = new SensorObservation();
        o.setPropertyName(PropertiesReader.readProperty(fileName, "observation." + field + "." + "propertyName"));
        o.setUnit(PropertiesReader.readProperty(fileName, "observation." + field + "." + "unit"));
        System.out.println(o.toString());
        return o;
    }

    public void publish(SensorObservation observation, SensorMetaData sensorMetaData) {

        logger.warn("asked to publish");
        logger.warn(observation);
        logger.warn(sensorMetaData);

        if (true) return;

        //create an Observation object
        Observation obs = new Observation();

        logger.warn(sensorMetaData.getSensorID());
        // set SensorURL of observation
        Sensor sensor2 = lsmStore.getSensorById(sensorMetaData.getSensorID());
        obs.setSensor(sensor2.getId());
        //set time when the observation was observed. In this example, the time is current local time.
        obs.setTimes(observation.getTime());

        ObservedProperty obvTem = new ObservedProperty();
        obvTem.setObservationId(obs.getId());
        obvTem.setPropertyName(observation.getPropertyName());
        obvTem.setValue(observation.getValue());
        obvTem.setUnit(observation.getUnit());
        obs.addReading(obvTem);
        lsmStore.sensorDataUpdate(obs);
    }


}
