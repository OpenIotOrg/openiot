/**
 * Copyright (c) 2011-2014, OpenIoT
 * 
* This file is part of OpenIoT.
 * 
* OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 * 
* OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
* You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 * 
* Contact: OpenIoT mailto: info@openiot.eu
 *
 * @author Ali Salehi
 * @author Mehdi Riahi
 * @author Sofiane Sarni
 * @author Aleksandar Antonic
 * @author Martina Marjanovic
 */
package org.openiot.gsn.wrappers.general;

import java.io.ByteArrayInputStream;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.log4j.Logger;

/**
 * UDP wrapper that receives data from QoS manger in OpenIoT platform
 */
public class FERUDPWrapper extends AbstractWrapper {
    
    List<String> parameters = Arrays.asList("temperature","humidity","pressure","no2","so2","co","batterys","batterymp","latitude","longitude");

    private final transient Logger logger = Logger.getLogger(FERUDPWrapper.class);

    private int threadCounter = 0;

    private AddressBean addressBean;

    private int port;

    private DatagramSocket socket;

    /*
     * Needs the following information from XML file : port : the udp port it
     * should be listening to rate : time to sleep between each packet
     */
    public boolean initialize() {
        addressBean = getActiveAddressBean();
        try {
            logger.warn(addressBean.getPredicateValue("port"));
            port = Integer.parseInt(addressBean.getPredicateValue("port"));
            socket = new DatagramSocket(port);
                socket.setReuseAddress(true);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return false;
        }
        setName("FERUDPWrapper-Thread" + (++threadCounter));
        return true;
    }

    public void run() {
        byte[] receivedData = new byte[64 * 1000];
        DatagramPacket receivedPacket = null;
        while (isActive()) {
            try {
                
                receivedPacket = new DatagramPacket(receivedData, receivedData.length);
                ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
                socket.receive(receivedPacket);
                logger.warn(addressBean.getPredicateValue("port"));
                bais.reset();
                ObjectInputStream ois = new ObjectInputStream(bais);
                try {
                    HashMap<String, Object> properties = (HashMap) ois.readObject();
                    Serializable[] dataValues = new Serializable[10];

                    int i = 0;
                    for (String measurement : parameters) {
                        dataValues[i] = (Serializable) properties.get(measurement);
                        i++;
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("FERUDPWrapper received data sized : " + properties.size());
                    }
                    long currentTime = System.currentTimeMillis();
                    if (properties.get("Timestamp") != null) {
                        currentTime = (long) properties.get("Timestamp");
                    }
                    StreamElement streamElement = new StreamElement(getOutputFormat(), dataValues, currentTime);
                    postStreamElement(streamElement);
                } catch (ClassNotFoundException ex) {
                    java.util.logging.Logger.getLogger(FERUDPWrapper.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException e) {
                logger.warn("Error while receiving data on UDP socket : " + e.getMessage());
            } finally {
                //socket.close();
            } 
                /*
                Random r = new Random();
               Serializable[] dataValues = new Serializable[10];
               for (int i=0; i<10; i++) {
                   if (i%2 == 0) {
               dataValues[i] = (Serializable) (r.nextInt(100)/10d);}
               }
               //dataValues[10] = (Serializable) (UUID.randomUUID().toString());
                postStreamElement(new StreamElement(getOutputFormat(), dataValues));*/
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(FERUDPWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public DataField[] getOutputFormat() {
        DataField[] outputFormat = new DataField[10];
        outputFormat[0] = new DataField("temperature", "double", "Average of temperature readings from the sensor network");
        outputFormat[1] = new DataField("humidity", "double", "Average of humidity from the sensor network");
        outputFormat[2] = new DataField("pressure", "double", "Average of pressure readings from the sensor network");
        outputFormat[3] = new DataField("no2", "double", "Average of NO2 readings from the sensor network");
        outputFormat[4] = new DataField("so2", "double", "Average of SO2 readings from the sensor network");
        outputFormat[5] = new DataField("co", "double", "Average of CO readings from the sensor network");
        outputFormat[6] = new DataField("batterys", "double", "Average of sensor battery readings from the sensor network");
        outputFormat[7] = new DataField("batterymp", "double", "Average of mobile phone battery readings from the sensor network");
        outputFormat[8] = new DataField("latitude", "double", "Average of latitude readings from the sensor network");
        outputFormat[9] = new DataField("longitude", "double", "Average of longitude readings from the sensor network");
               
        return outputFormat;
    }

    public void dispose() {

        threadCounter--;
    }

    public String getWrapperName() {
        return "FER UDP";
    }

    public static void main(String[] args) {
        // To check if the wrapper works properly.
        // this method is not going to be used by the system.    
    }
}
