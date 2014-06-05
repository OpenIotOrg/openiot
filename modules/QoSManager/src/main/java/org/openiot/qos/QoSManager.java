/*    Copyright (c) 2011-2014, OpenIoT
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
package org.openiot.qos;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;
import org.openiot.cupus.entity.mobilebroker.MobileBroker;
import org.openiot.cupus.util.LogWriter;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.proj.coords.MGRSPoint;
import java.io.IOException;
import java.util.Arrays;

/**
 * 
 * @author Martina
 */
public class QoSManager implements QoSManagerInterface {
	 
	 private QoSLogic qosLogic;
	 private MonitoringAndManagement qosMatcher;  
	 private MobileBroker qosMB;
	 
	 private LogWriter log;
	 private boolean logWriting = true;
	 private boolean testing = true;
	 
	 private String brokerName;
	 private String brokerIP;
	 private int brokerPort;
	 private double highBatteryLevel;
	 private double lowBatteryLevel;
	 private int numOfActiveSensors;
         List<String> sensorParameters;
         List<String> sensorTypes;
         List<String> lsmProperty;
         List<String> lsmUnit;
         
         private String gsnAddress;
         private int wrapperPort;	 
		 
         public QoSManager(File configFile) {
	        
	        //reads properties and instantiates and sets everything...
	        try {
	            Properties prop = new Properties();
	            FileInputStream fileIn = new FileInputStream(configFile);
	            prop.load(fileIn);
	            fileIn.close();

	            this.brokerName = prop.getProperty("qos.name");
	            if (this.brokerName == null) {
	                throw new NullPointerException("Name must be defined!");
	            }
	            this.brokerIP = prop.getProperty("qos.brokerIP");
	            this.brokerPort = Integer.parseInt(prop.getProperty("qos.brokerPort"));	            
	            this.numOfActiveSensors = Integer.parseInt(prop.getProperty("qos.numberOfSensors"));
	            
	            this.highBatteryLevel = Double.parseDouble(prop.getProperty("qos.highBatteryLevel"));
	            this.lowBatteryLevel = Double.parseDouble(prop.getProperty("qos.lowBatteryLevel"));
                    
                    String s = prop.getProperty("qos.sensorParameters");
                    String[] parameters =  s.split(",");
                    this.sensorParameters = Arrays.asList(parameters);
                    
                    String t = prop.getProperty("qos.sensorTypes");
                    String[] types =  t.split(",");
                    this.sensorTypes = Arrays.asList(types);
                    
                    String l = prop.getProperty("qos.lsmProperty");
                    String[] property =  l.split(",");
                    this.lsmProperty = Arrays.asList(property);
                    
                    String u = prop.getProperty("qos.lsmUnit");
                    String[] unit =  u.split(",");
                    this.lsmUnit = Arrays.asList(unit);
	                      
	            if (prop.getProperty("qos.testing", "false").toLowerCase().equals("false")) {
	                this.testing = false;
	            } else if (prop.getProperty("qos.testing").toLowerCase().equals("true")) {
	                this.testing = true;
	            } else {
	                System.err.println("Config param \"testing\" should be either true or false! Setting to default false.");
	                this.testing = false;
	            }
	            if (prop.getProperty("qos.logWriting", "true").toLowerCase().equals("true")) {
	                this.logWriting = true;
	            } else if (prop.getProperty("qos.logWriting").toLowerCase().equals("false")) {
	                this.logWriting = false;
	            } else {
	                System.err.println("Config param \"logWriting\" should be either true or false! Setting to default true.");
	                this.logWriting = true;
	            }
                    //this.testing = true;
                    //this.logWriting = true;
                    
                    this.gsnAddress = prop.getProperty("qos.gsnAddress");
	            this.wrapperPort = Integer.parseInt(prop.getProperty("qos.wrapperPort"));
	        } catch (IOException | NullPointerException | NumberFormatException e) {
	            System.exit(-1);
	        }

	        log = new LogWriter(this.brokerName + ".log", logWriting, testing);
	        
	        log.writeToLog("", true); //empty line

	        qosLogic = new QoSLogic (this.log, this.numOfActiveSensors,this.highBatteryLevel,this.lowBatteryLevel);
	 	qosMB = new MobileBroker(this.brokerName, this.brokerIP, this.brokerPort);
	 	qosMatcher = new MonitoringAndManagement(this.log, this.qosMB, this.qosLogic, this.sensorParameters, this.sensorTypes, this.lsmProperty, this.lsmUnit, this.gsnAddress, this.wrapperPort);
	        startQoSMobileBroker();

	        log.writeToLog("", true); //empty line               
                
	    
	 }
        
         
	 public void shutdown(){
            qosMB.disconnectFromBroker();
            log.close();
            System.exit(-1);
	 }
	 
	 public void setBatteryLevels (double highPriorityLevel, double lowPriorityLevel){
	    	qosLogic.setBatteryLevels(highPriorityLevel, lowPriorityLevel);
	 }
	    
	 public void setNumberOfActiveSensors (int numOfActiveSensors){
	  	qosLogic.setNumberOfActiveSensors(numOfActiveSensors);
	 }
	    
	 public Set<String> getAllSensorsInArea (String area){
	  	return this.qosMatcher.getAllSensorsInArea(area);
	 }
	    
	 public Set<String> getActiveSensorsInArea (String area){
	  	return this.qosMatcher.getActiveSensorsInArea(area);
	 }
	 
	 public List<TripletSubscription> getAllSubscriptionsInArea (String area){
            return this.qosMatcher.getAllSubscriptionsInArea(area);             
	 }
	    
	 public Set<String> getAllAvailableSensors (){
	  	return this.qosMatcher.getAllCurrentlyKnownSensors();
	 }
	 
	 public List<Float> getLatLongFromArea (String area){
	  	List<Float> latLong = new ArrayList<Float>();
	 	LatLonPoint llpoint = MGRSPoint.MGRStoLL(new MGRSPoint(area));
	   	latLong.add(0, llpoint.getLatitude());
	   	latLong.add(1, llpoint.getLongitude());
	   	return latLong;
	 }
	    
	 public String getAreaFromLatLong (double lat, double lng, int accuracy){
	  	MGRSPoint mgrsp = MGRSPoint.LLtoMGRS(new LatLonPoint.Double(lat, lng));
	   	if (accuracy==1)
	   		mgrsp.setAccuracy(MGRSPoint.ACCURACY_1_METER);
	   	else if (accuracy==10)
	   		mgrsp.setAccuracy(MGRSPoint.ACCURACY_10_METER);
	   	else if (accuracy==100)
	   		mgrsp.setAccuracy(MGRSPoint.ACCURACY_100_METER);
	   	else if (accuracy==1000)
	   		mgrsp.setAccuracy(MGRSPoint.ACCURACY_1000_METER);
	   	else if (accuracy==10000)
	   		mgrsp.setAccuracy(MGRSPoint.ACCURACY_10000_METER);
	   	    	
	   	String area = mgrsp.getMGRS();
	   	return area;
	 }
	    
	 public HashtablePublication getAverageSensorReadingsInArea(String area){
	   	return this.qosMatcher.getAverageSensorReadingsInArea(area);
	 }
	 
         public void defineNewSubscriptionInArea(String area) {
             TripletSubscription sub = new TripletSubscription(-1, System.currentTimeMillis());
             sub.addPredicate(new Triplet("Area", area, Operator.EQUAL));
             sub.addPredicate(new Triplet("Type", "SensorReading", Operator.EQUAL));            
             qosMB.subscribe(sub);
         }
          
	 private void startQoSMobileBroker (){
	 		
             QoSNotificationListener qosListener = new QoSNotificationListener(this.qosMatcher);
	     qosMB.setNotificationListener(qosListener);
	     qosMB.connect();
	        
	         
	     //define an announcement which is unlimited 
	     TripletAnnouncement ta = new TripletAnnouncement(-1, System.currentTimeMillis());
	     //announce numerical data (i.e. its range is <-inf, +inf> , implementation is <       
	     ta.addTextualPdredicate("Area", "", Operator.CONTAINS_STRING);
	     ta.addNumericalPredicate("Temperature");
	     ta.addNumericalPredicate("Humidity");
	     ta.addNumericalPredicate("Pressure");
	     ta.addNumericalPredicate("CO");
	     ta.addNumericalPredicate("NO2");
	     ta.addNumericalPredicate("SO2");  
	     ta.addTextualPdredicate("Type", "AverageReading", Operator.EQUAL);
	     //announce previously defined announcement
	     qosMB.announce(ta);
	      
	     TripletAnnouncement ta1 = new TripletAnnouncement(-1, System.currentTimeMillis());
	     ta1.addTextualPdredicate("SensorID", "", Operator.CONTAINS_STRING);
	     ta1.addTextualPdredicate("Type", "SensorControl", Operator.EQUAL);
	     qosMB.announce(ta1);
	      
	     TripletAnnouncement ta2 = new TripletAnnouncement(-1, System.currentTimeMillis());
	     ta2.addTextualPdredicate("Type", "SensorReading", Operator.EQUAL);
	     ta2.addTextualPdredicate("Area", "", Operator.CONTAINS_STRING);
	     ta2.addNumericalPredicate("Temperature");
	     ta2.addNumericalPredicate("Humidity");
	     ta2.addNumericalPredicate("Pressure");
	     ta2.addNumericalPredicate("CO");
	     ta2.addNumericalPredicate("NO2");
	     ta2.addNumericalPredicate("SO2");
	     qosMB.announce(ta2);
	                
	     TripletSubscription ts = new TripletSubscription(-1, System.currentTimeMillis());
	     ts.addPredicate(new Triplet ("Type", "SensorReading", Operator.EQUAL));
	     qosMB.subscribe(ts);         
	 
	 }   

   
	        	       
}


