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

import lsm.beans.*;
import lsm.server.LSMTripleStore;

import java.util.Date;

public class utils {

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
            sensor.setMetaGraph(metaGraph);
            sensor.setDataGraph(dataGraph);

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

            System.out.println(listSensor(sensor).toString());

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
                                                String fieldName,
                                                double fieldValue,
                                                String fieldUnit,
                                                Date date) {

        boolean success = true;
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
            obs.setTimes(date);
            obs.setMetaGraph(metaGraph);
            obs.setDataGraph(dataGraph);
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
            success = false;
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
        }

        return success;

    }

    //TODO: add date
    public static boolean updateSensorDataOnLSM(String username,
                                                String password,
                                                String metaGraph,
                                                String dataGraph,
                                                String sensorID,
                                                String fieldName,
                                                double fieldValue,
                                                String fieldUnit) {
        System.out.println(" *********** INSIDE ******** RUNNING **********");

        System.out.println("updateSensorDataOnLSM(\"" + username + "\",");
        System.out.println("\t\t\"" + password + "\",");
        System.out.println("\t\t\"" + metaGraph + "\",");
        System.out.println("\t\t\"" + dataGraph + "\",");
        System.out.println("\t\t\"" + sensorID + "\",");
        System.out.println("\t\t\"" + fieldName + "\",");
        System.out.println("\t\t" + fieldValue + ",");
        System.out.println("\t\t\"" + fieldUnit + "\");");

        boolean success = true;
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
            obs.setMetaGraph(metaGraph);
            obs.setDataGraph(dataGraph);
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
            success = false;
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
        }

        return success;

    }

    static void register_opensense_sensors() {
        String s = "opensense_3";
        String id = addSensorToLSM("swissex",
                "swissex1234",
                "http://lsm.deri.ie/OpenIoT/test/sensormeta#",
                "http://lsm.deri.ie/OpenIoT/test/sensordata#",
                s,
                "opensense",
                "lausanne",
                "gsn",
                "OpenSense station 3",
                "http://opensensedata.epfl.ch:22002/gsn?REQUEST=113&name="+s,
                46.521989,
                6.639634
        );
        System.out.println(s+ " => ID => " + id);
    }



    /*

    source=http\://planetdata.epfl.ch\:22002/gsn?REQUEST\=113&name\=mountain_1
sensorName=mountain_1
dataGraph=http\://lsm.deri.ie/OpenIoT/test/sensordata\#
field.temperature.unit=C
registered=true
field.humidity.unit=Percent
field.temperature.propertyName=Temperature
sensorID=http\://lsm.deri.ie/resource/101982049433950
author=swissex
metaGraph=http\://lsm.deri.ie/OpenIoT/test/sensormeta\#
fields=humidity,temperature,wind_speed
field.wind_speed.unit=m/s
sensorType=mountain
field.wind_speed.propertyName=WindSpeed
sourceType=gsn
information=Mountain station 1
field.humidity.propertyName=Humidity
     */


    public static void main(String[] args) {

        register_opensense_sensors();

        System.exit(0);

        String id = addSensorToLSM("swissex",
                "swissex1234",
                "http://lsm.deri.ie/OpenIoT/test/sensormeta#",
                "http://lsm.deri.ie/OpenIoT/test/sensordata#",
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
                "http://lsm.deri.ie/OpenIoT/test/sensormeta#",
                "http://lsm.deri.ie/OpenIoT/test/sensordata#",
                id,
                "Temperature",
                0.0,
                "C");

        updateSensorDataOnLSM("swissex",
                "swissex1234",
                "http://lsm.deri.ie/OpenIoT/test/sensormeta#",
                "http://lsm.deri.ie/OpenIoT/test/sensordata#",
                "http://lsm.deri.ie/resource/88301583363908",
                "Temperature",
                81.86806200000001,
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
                .append("\nuser      : ").append(s.getUser())
                .append("\nstring    : ").append(s.toString())
                .append("\nlatitude  : ").append(s.getPlace().getLat())
                .append("\nlongitude : ").append(s.getPlace().getLng())
                .append("\n------------");
    }
}
