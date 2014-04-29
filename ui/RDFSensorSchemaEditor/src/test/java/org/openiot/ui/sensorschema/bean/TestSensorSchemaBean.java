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
import org.openiot.ui.sensorschema.sensormodel.Device;
import org.restlet.data.MediaType;


public class TestSensorSchemaBean {
	/**
	 * Testing the Device post request
	 * outputMessage is null, the test failed
	 * 
	 * if output message has some rdf data, test is a success
	 * currently comments as maven test takes time due to LD4Sensor server response time
	 */
	//@Test
	public void test() {
		
		String id="demo";
		
		
		String outputMessage;
		
		Device device = new Device(id, "1984-03-30T00:00:00+01:00", "http://www.example.com/device/", new String[]{"reading1","reading2"}, "http://www.example.com/",
				"Temperature", "Centigrade", new String[]{"http://www.example.com/stp/1", "http://www.example.com/stp/2"},"0,0", "Canberra",
				"Device");
		
		JSONObject new_json = device.toJson(false, false);
		DevicePost devicePut = new DevicePost();
		try {
			outputMessage = devicePut.post(new_json, id, true, MediaType.APPLICATION_RDF_XML);
		} catch (IOException e) {
			outputMessage = null;
		}
		
		assertNotNull(outputMessage);
	}

}
