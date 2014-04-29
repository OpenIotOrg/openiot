/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.sensormodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.sensorschema.utils.Constants;
import org.openiot.ui.sensorschema.utils.Utils;
import org.slf4j.Logger;


/**
 * Represents the observation resource
 * for more details refer to the LD4Sensor - Spitfire ontology  
 */
public class Observation extends Resource {

	private static Logger logger = Utils.getLogger(Observation.class);
	
	/**
	 * Start Time of the observation
	 */
	private String startTime;
	
	/**
	 * End Time of the observations
	 */
	private String endTime;
	
	/**
	 * values of the observation
	 */
	private String[] values;

	/** DateTime as a resource creation time point. */
	private String resource_time = null;
	
	/**
	 * Observation Constructor
	 *
	 * @param resourceId 
	 * @param startTime 
	 * @param endTime 
	 * @param values 
	 * @param baseURI 
	 * @param baseTime 
	 */
	public Observation(String resourceId, String startTime, String endTime, String[] values, String baseURI,
			String baseTime){
		this.resourceId = resourceId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.values = values;
		this.base_datetime = baseTime;
		this.base_uri = baseURI;
	}
	
	
	/**
	 * Observation Constructor creating a observation object from a JSON object
	 *
	 * @param json 
	 * @param localhost 
	 * @throws Exception 
	 */
	public Observation(JSONObject json, String localhost) throws Exception {
		
		//remote_uri
		if (json.has("uri")){
			this.setRemote_uri(Utils.removeBrackets(
					json.getString("uri")));
		}

		if (json.has("resource"+Constants.JSON_SEPARATOR+"time")){
			this.setResource_time(Utils.removeBrackets(
					json.getString("resource"+Constants.JSON_SEPARATOR+"time")));
		}
		if (json.has("values")){
			this.setValues(json.getJSONArray("values"));
		}
		
		if (json.has("start"+Constants.JSON_SEPARATOR+"range")){
			this.setStartTime(Utils.removeBrackets(
					json.getString("start"+Constants.JSON_SEPARATOR+"range")));
		}
		if (json.has("end"+Constants.JSON_SEPARATOR+"range")){
			this.setEndTime(Utils.removeBrackets(
					json.getString("end"+Constants.JSON_SEPARATOR+"range")));
		}

		//base uri - base_name
		if (json.has("base"+Constants.JSON_SEPARATOR+"name")){
			this.setBase_uri(Utils.removeBrackets(
					json.getString("base"+Constants.JSON_SEPARATOR+"name")));
		}

		//base time - base_datetime
		if (json.has("base"+Constants.JSON_SEPARATOR+"datetime")){
			this.setBase_datetime(Utils.removeBrackets(
					json.getString("base"+Constants.JSON_SEPARATOR+"datetime")));
		}				
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public String[] getValues() {
		return values;
	}


	public void setValues(String[] values) {
		this.values = values;
	}
	
	public void setValues(JSONArray jvalues) throws JSONException {
		JSONArray j = jvalues.getJSONArray(0);
		if (j != null){
			String[] values = new String[j.length()];
			for (int i=0; i< j.length(); i++){
				values[i] = j.getString(i);
			}
			setValues(values);
		}
	}


	public String getResource_time() {
		return resource_time;
	}


	public void setResource_time(String resource_time) {
		this.resource_time = resource_time;
	}
	
	/**
	 * Convert the Observation object into JSON object
	 *
	 * @param isRemote 
	 * @param isEnriched 
	 * @return 
	 */
	public JSONObject toJson(boolean isRemote, boolean isEnriched){
		JSONObject json = new JSONObject();
		try {
			if (isRemote){
				json.append("uri", remote_uri);
			}else{
				json.append("uri", null);
			}
//			if (isEnriched){
//				json.append("context", );	
//			}else{
//				json.append("context", null);
//			}

			json.append("resource_time", resource_time);

			json.append("base_time", base_datetime);
			json.append("start_range", startTime);
			json.append("end_range", endTime);

			JSONArray vals = new JSONArray();
			for (int i=0; i<values.length ;i++){
				vals.put(values[i]);
			}
			json.append("values", vals);
			
		} catch (JSONException e1) {
			logger.error(e1.getMessage());
		}
		return json;
	}

}
