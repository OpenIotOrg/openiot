/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.bean;
/**
 * @author Prem Jayaraman
 * 
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.json.JSONObject;
import org.openiot.ui.sensorschema.client.DevicePost;
import org.openiot.ui.sensorschema.sensormodel.Device;
import org.openiot.ui.sensorschema.utils.Constants;
import org.openiot.ui.sensorschema.utils.SerialiseFormatMap;
import org.openiot.ui.sensorschema.utils.Utils;
import org.slf4j.Logger;


@ManagedBean(name="sensorSchema")
@SessionScoped
public class SensorSchema implements Serializable {

	
	private static final long serialVersionUID = 10000001L;
	
	private static Logger logger = Utils.getLogger(SensorSchema.class);
	
	//properties to be displayed on the UI
	private String id;
	private String baseTime;
	private String baseURI;
	private String sensorReadings;
	private String sensorReadingsURI;
	private String observedProperty;
	private String unitOfMeasurement;
	private String sensorTemporalProperty;
	private String[] deviceTypes;	
	private String currentDeviceType;
	private boolean isEditable;
	private String locationName;
	private String location;
	private String[] outputFormats;
	private String currentoutputFormat;
	
	private String outputMessage;
	private SerialiseFormatMap dataformats;
	
	public SerialiseFormatMap getDataformats() {
		return dataformats;
	}

	//Set default values for text fields from property file - ui.properties
	public SensorSchema(){
		
		
		InputStream props = Utils.getConfigAsInputStream("/ui.properties", SensorSchema.class);		
		Properties properties = new Properties();
		
		//default value for basetime
		try {
			properties.load(props);
			this.id = "demo";
			
			this.baseTime= properties.getProperty("basetime.example");

			this.baseURI= properties.getProperty("baseuri.example");
			
			this.sensorReadingsURI= properties.getProperty("readinguri.example");
			
			this.sensorTemporalProperty = properties.getProperty("sensortemporal.example");
			
			//load device lists
			deviceTypes = properties.getProperty("devicetype.example").split(",");
						
			dataformats = new SerialiseFormatMap();
			outputFormats = dataformats.toArray();
			
			currentoutputFormat = outputFormats[0];
						

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Unable to Load the Propoerties File");
			this.baseTime = "1984-03-30T00:00:00+01:00"; 
		}		
		
		
		
		
		
		setEditable(false);
	}
	
	//getter and setters for the properties
	//managed by JSF bean
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBaseTime() {
		return baseTime;
	}
	public void setBaseTime(String baseTime) {
		this.baseTime = baseTime;
	}
	public String getBaseURI() {
		return baseURI;
	}
	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}
	public String getSensorReadings() {
		return sensorReadings;
	}
	public void setSensorReadings(String sensorReadings) {
		this.sensorReadings = sensorReadings;
	}
	public String getSensorReadingsURI() {
		return sensorReadingsURI;
	}
	public void setSensorReadingsURI(String sensorReadingsURI) {
		this.sensorReadingsURI = sensorReadingsURI;
	}
	public String getObservedProperty() {
		return observedProperty;
	}
	public void setObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
	}
	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}
	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}
	public String getSensorTemporalProperty() {
		return sensorTemporalProperty;
	}
	public void setSensorTemporalProperty(String sensorTemporalProperty) {
		this.sensorTemporalProperty = sensorTemporalProperty;
	}
	public List<SelectItem> getAvailableDeviceType() {
		List<SelectItem> tempList = Utils.toList(deviceTypes);
		return tempList;
	}
	
	public List<SelectItem> getAvailableOutputFormats() {
		List<SelectItem> tempList = Utils.toList(outputFormats);
		return tempList;
	}

	public String getCurrentDeviceType() {
		return currentDeviceType;
	}

	public void setCurrentDeviceType(String currentDeviceType) {
		this.currentDeviceType = currentDeviceType;
	}
	
	
	/** This method is invoked with the Submit button in the XHTML interface is clicked
	 * This method populates the input into a JSON object
	 * Calls the POST interface of the Sensor Device to make a post request with following parameters
	 * JSON Object
	 * sensor identification
	 * Test mode - true(testing)/false (production)
	 * Return serialisation format */ 	
	
	public void submit(){
		outputMessage = "";

		//need to process all inputs and generate the json object
		//pass the json object to the server
		//get the result from the server
		//display it in the screen
		
		Device device = new Device(id, baseTime, baseURI, sensorReadings.split(","), sensorReadingsURI,
				observedProperty, unitOfMeasurement, sensorTemporalProperty.split(","), location, locationName,
				currentDeviceType);
		
		JSONObject new_json = device.toJson(false, false);
		DevicePost devicePost = new DevicePost();
		
		try {
			//make a post request depending on the serialisation type selected by user
			//curretnyl 4 output serialisation formats are supported
			
			//System.out.println("Data Format is" + currentoutputFormat);
			//System.out.println("Data Format is" + dataformats.findSerialiseFormat(currentoutputFormat));
			//System.out.println("Bean Posting!!");
			
			
			outputMessage = devicePost.post(new_json, id, false, dataformats.findSerialiseFormat(currentoutputFormat));
			//outputMessage = Utils.formatHTML(outputMessage);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			outputMessage = "Error Processing Request. Please try again later.";
		}
		
//		outputMessage = String.format("Details of Input are %s %s %s %s %s %s %s %s %s",
//		id,
//		baseTime,
//		baseURI,
//		sensorReadings,
//		sensorReadingsURI,
//		observedProperty,
//		unitOfMeasurement,
//		sensorTemporalProperty,		
//		currentDeviceType);
//		
//		logger.info(outputMessage);
		
		setEditable(true);					
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

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCurrentoutputFormat() {
		return currentoutputFormat;
	}

	public void setCurrentoutputFormat(String currentoutputFormat) {
		this.currentoutputFormat = currentoutputFormat;
	}



}
