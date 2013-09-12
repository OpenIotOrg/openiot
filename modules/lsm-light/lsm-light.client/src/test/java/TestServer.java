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

import java.util.Date;
import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.ObservedProperty;
import org.openiot.lsm.beans.Place;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.beans.User;
import org.openiot.lsm.server.LSMTripleStore;
import org.openiot.lsm.utils.ObsConstant;





public class TestServer {
	public static LSMTripleStore lsmStore = new LSMTripleStore();
	public static Observation updateData(){
		
	/*
	 * An Observation is a Situation in which a Sensing method has been used to estimate or 
	 * calculate a value of a Property of a FeatureOfInterest.
	 */
	
	//create an Observation object
	Observation obs = new Observation();
	
	// set SensorURL of observation
	//for example: "http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0" is the sensorURL of lausanne_1057 sensor
//	obs.setSensor("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0");
	//set time when the observation was observed. In this example, the time is current local time.
	obs.setTimes(new Date());
	/*
	 * Relation linking an Observation to the Property that was observed
	 */
	ObservedProperty obvTem = new ObservedProperty();
	obvTem.setObservationId(obs.getId());
	obvTem.setPropertyType(ObsConstant.TEMPERATURE);
	obvTem.setValue(9.58485958485958);
	obvTem.setUnit("C");
	obs.addReading(obvTem);
	
	ObservedProperty obvCO = new ObservedProperty();
	obvCO.setObservationId(obs.getId());
	obvCO.setPropertyType(ObsConstant.HUMIDITY);
	obvCO.setValue(0.0366300366300366);
	obvCO.setUnit("C");
	obs.addReading(obvCO);
	obs.setDataGraph("http://lsm.deri.ie/OpenIoT/new/sensordata#");
	obs.setMetaGraph("http://lsm.deri.ie/OpenIoT/new/sensormeta#");
	
//	LSMTripleStore lsmStore = new LSMTripleStore();
//	lsmStore.sensorDataUpdate(obs);
		return obs;
	}
	/**
	 * @param args
	 */
	
	public static Sensor addNewSensor(){
	     Sensor sensor  = new Sensor();
	     sensor.setName("lab_temp_hp");
	     sensor.setAuthor("admin");
		 sensor.setSourceType("peania");
		 sensor.setInfor("Temperature sensor inside lab");
		 sensor.setSource("http://www.ait.gr/sensor/test1");
		 sensor.addProperty(ObsConstant.TEMPERATURE);
		 sensor.addProperty(ObsConstant.HUMIDITY);
		 sensor.setTimes(new Date());
		 sensor.setDataGraph("http://lsm.deri.ie/OpenIoT/new/sensordata#");
		 sensor.setMetaGraph("http://lsm.deri.ie/OpenIoT/new/sensormeta#");
		 
		// set sensor location information (latitude, longitude, city,
		// country, continent...)
		 Place place = new Place();
		 place.setLat(37.943267); 
		 place.setLng(23.870287);
		 sensor.setPlace(place);
      
		 User user = new User();
		 user.setUsername("admin");
		 user.setPass("admin");
		 sensor.setUser(user);		       
         
         lsmStore.setUser(user);
         lsmStore.sensorAdd(sensor);		
         return sensor;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub		        
        try{  
        	Sensor sensor = addNewSensor();         
        	User user = new User();
   		 	user.setUsername("admin");
   		 	user.setPass("admin");       
            
            lsmStore.setUser(user);
            Observation obs = TestServer.updateData();
	        obs.setSensor(sensor.getId());
	        System.out.println(lsmStore.sensorDataUpdate(obs));
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send the string to servlet");                                            }  
        }
}
