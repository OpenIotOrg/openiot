/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.bean;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONObject;
import org.junit.Test;
import org.openiot.ui.sensorschema.client.DevicePost;
import org.openiot.ui.sensorschema.client.ObservationPost;
import org.openiot.ui.sensorschema.sensormodel.Device;
import org.openiot.ui.sensorschema.sensormodel.Observation;
import org.restlet.data.MediaType;


/**
 * 
 */
public class TestSensorObservationBean {

	/**
	 * Testing the Observation post request
	 * outputMessage is null, the test failed
	 * 
	 * if output message has some rdf data, test is a success
	 * currently comments as maven test takes time due to LD4Sensor server response time
	 */
	//@Test
	public void test() {
		String id="demo";
		String time="1984-03-30T00:00:00+01:00";
		
		
		String outputMessage;
		
		Observation sensorov = new Observation(id, time, time,new String[]{"1.1","1.2"}, "http://www.example.com", time);
		
		JSONObject new_json = sensorov.toJson(false, false);
		ObservationPost ovPut = new ObservationPost();
		
		try {
			outputMessage = ovPut.post(new_json, id, true, MediaType.APPLICATION_RDF_XML);
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			outputMessage = null;
		}					
		assertNotNull(outputMessage);
	}

}
