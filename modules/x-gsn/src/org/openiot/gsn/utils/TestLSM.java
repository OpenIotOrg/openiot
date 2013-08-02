package org.openiot.gsn.utils;

import java.util.Date;

import lsm.beans.Place;
import lsm.beans.Sensor;
import lsm.beans.User;
import lsm.server.LSMTripleStore;
import lsm.beans.Observation;
import lsm.beans.ObservedProperty;


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
            User user = new User();
            user.setUsername(username);
            user.setPass(password);

            Sensor sensor = new Sensor();

            sensor.setId(sensorID);


            sensor.setUser(user);

            // create LSMTripleStore instance
            LSMTripleStore lsmStore = new LSMTripleStore();

            //set user information for authentication
            lsmStore.setUser(user);

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
            obvTem.setPropertyName(fieldName);
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
                                        double latitude,
                                        double longitude) {

        String sensorID = "";

        try {
            // 1. Create an instance of Sensor class and set the sensor metadata
            Sensor sensor = new Sensor();
            sensor.setName(sensorName);
            sensor.setAuthor(sensorAuthor);
            sensor.setSourceType(sourceType);
            sensor.setInfor(infor);
            sensor.setSource(sensorSource);

            sensor.setSensorType(sensorType);
            //your original code
//            sensor.setMetaGraph("http://lsm.deri.ie/yourMetaGraphURL");
//            sensor.setDataGraph("http://lsm.deri.ie/yourDataGraphURL");

            //in this case, for OpenIoT, you have to set:
            sensor.setMetaGraph("http://lsm.deri.ie/OpenIoT/test/sensormeta#");
            sensor.setDataGraph("http://lsm.deri.ie/OpenIoT/test/sensordata#");

            sensor.setTimes(new Date());
            // set sensor location information (latitude, longitude, city, country, continent...)
            Place place = new Place();
            place.setLat(latitude);
            place.setLng(longitude);
            sensor.setPlace(place);

            /*
            * Set sensor's author
            * If you don't have LSM account, please visit LSM Home page (http://lsm.deri.ie) to sign up
            */
            User user = new User();
            user.setUsername(username);
            user.setPass(password);
            sensor.setUser(user);

            // create LSMTripleStore instance
            LSMTripleStore lsmStore = new LSMTripleStore();

            //set user information for authentication
            lsmStore.setUser(user);

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


        //id = "http://lsm.deri.ie/resource/66582224937127";
        System.out.println("Calling with 64 bit Sensor ID ==> " + id);
        updateSensorDataOnLSM("swissex",
                "swissex1234",
                id,
                "Temperature",
                0.0,
                "C");

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
        System.out.println("user : " + s.getUser());
        System.out.println("string : " + s.toString());
        System.out.println("------------");
    }
}
