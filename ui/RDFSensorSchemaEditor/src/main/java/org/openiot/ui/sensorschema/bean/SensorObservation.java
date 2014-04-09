/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.json.JSONObject;
import org.openiot.ui.sensorschema.client.DevicePost;
import org.openiot.ui.sensorschema.client.ObservationPost;
import org.openiot.ui.sensorschema.sensormodel.Observation;
import org.openiot.ui.sensorschema.utils.Utils;
import org.slf4j.Logger;


@ManagedBean
@SessionScoped
/**
 * represents a sensor observation resource bean
 * this bean is connected to the JSF interface
 * 
 */
public class SensorObservation implements Serializable {


	private static final long serialVersionUID = 10000002L;
	
	private static Logger logger = Utils.getLogger(SensorObservation.class);
	private String values;
	private String sensorID;
	private String start_time; //start range
	private String end_time; //end range
	private String resource_time;
	private String outputMessage;
	private boolean isEditable;
	
	//Set default values for text fields from property file - ui.properties
	public SensorObservation(){
		
		
		InputStream props = Utils.getConfigAsInputStream("/ui.properties", SensorObservation.class);		
		Properties properties = new Properties();
		
		//default value for basetime
		try {
			properties.load(props);
			this.start_time= properties.getProperty("basetime.example");
			this.end_time= properties.getProperty("basetime.example");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Unable to Load the Propoerties File");
			this.start_time = "1984-03-30T00:00:00+01:00"; 
			this.end_time = "1984-03-30T00:00:00+01:00";
		}		
		setEditable(false);
	}
	
	@ManagedProperty(value="#{sensorSchema}")
	private SensorSchema sensorschema;
	
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	public String getSensorID() {
		return sensorID;
	}
	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getResource_time() {
		return resource_time;
	}
	public void setResource_time(String resource_time) {
		this.resource_time = resource_time;
	}
	
	/**
	 * 
	 * This method is invoked with the Submit button in the XHTML interface is clicked
	 * This method populates the input into a JSON object
	 * Calls the POST interface of the Observation resource to make a post request with following parameters
	 * JSON Object
	 * sensor identification
	 * Test mode - true(testing)/false (production)
	 * Return serialisation format 
	 * 
	 * @param none
	 * @return none
	 */
	public void submitov(){
		outputMessage = "";		
		System.out.println("Submit was called- Sensor Obs");
		//Process inputs. Send input to server as a JSOn in a PUt request.
		//Get response and display the response
		
		Observation sensorov = new Observation(sensorID, start_time, end_time, values.split(","), sensorschema.getBaseURI(), sensorschema.getBaseTime());
		
		JSONObject new_json = sensorov.toJson(false, false);
		ObservationPost ovPut = new ObservationPost();
		
		try {
			//post the reqeust to the server
			outputMessage = ovPut.post(new_json, sensorID, false, sensorschema.getDataformats().findSerialiseFormat(sensorschema.getCurrentoutputFormat()));
			//outputMessage = Utils.formatHTML(outputMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			outputMessage = "Error Processing Request. Please try again later.";
		}
				
		
		isEditable = true;
		
	}
	
	
	public String getOutputMessage() {
		return outputMessage;
	}
	public void setOutputMessage(String outputMessage) {
		this.outputMessage = outputMessage;
	}
	public boolean isEditable() {
		return isEditable;
	}
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	
	
	public SensorSchema getSensorschema() {
		return sensorschema;
	}
	public void setSensorschema(SensorSchema sensorschema) {
		this.sensorschema = sensorschema;
	}
	
	
}
