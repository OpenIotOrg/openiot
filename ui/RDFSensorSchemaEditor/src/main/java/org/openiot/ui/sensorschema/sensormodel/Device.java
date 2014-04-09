/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.sensormodel;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.sensorschema.utils.Constants;
import org.openiot.ui.sensorschema.utils.Utils;
import org.slf4j.Logger;


/**
 * Device resource.
 * @author Prem Jayaraman
 *
 */
public class Device extends Resource implements Serializable{


	private static final long serialVersionUID = 8845385924519981423L;
	private static Logger logger = Utils.getLogger(Device.class);
	
	/** Unit of Measurement. */
	private String unit_of_measurement = null;

	/** Observed Property. */
	private String observed_property = null;

	/** Temporal Sensor Properties IDs (same base name than the main resource). */
	private String[] tsproperties = null;

	/** Observation Value IDs. */
	private String[] ov = null;
	
	/** Observation Value URIS. */
	private String ov_uri = null;
	
	/**
	 * List of Location
	 */
	protected String[] locations = null;
	
	/**
	 * Feature of Interest.
	 */
	private String foi = null;



	/**
	 * Device Constructor
	 *
	 * @param resource_id 
	 * @param base_datetime 
	 * @param base_uri 
	 * @param ov_values 
	 * @param ov_uri 
	 * @param op 
	 * @param uom 
	 * @param temporal 
	 * @param locations 
	 * @param locationName 
	 * @param deviceType 
	 */
	public Device( 			
			String resource_id,
			String base_datetime,
			String base_uri,
			String[] ov_values,
			String ov_uri,
			String op,
			String uom,
			String[] temporal,
			String locations,
			String locationName, String deviceType) {
		
		setResourceId(resource_id);
		this.setBase_datetime(base_datetime);
		this.setBase_uri(base_uri);
		this.setOv(ov_values);
		this.setOv_uri(ov_uri);
		
		this.setObserved_property(op);
		this.setUnit_of_measurement(uom);
		this.setTsproperties(temporal);
		
		setLocation_coordinates(locations);
		setLocationName(locationName);
		setType(deviceType);
			
	}
	

	public Device(){
		
	}

	/**
	 * A new device object based on a JSON response from the server
	 *
	 * @param json 
	 * @throws Exception 
	 */
	public Device(JSONObject json) throws Exception {
		
		//remote_uri
		if (json.has("uri")){
			this.setRemote_uri(Utils.removeBrackets(
					json.getString("uri")));
		}
		
		//unit of measurement
		if (json.has("uom")){
			this.setUnit_of_measurement(Utils.removeBrackets(
					json.getString("uom")));
		}
		
		//observed propoerty
		if (json.has("observed"+Constants.JSON_SEPARATOR+"property")){
			this.setObserved_property(Utils.removeBrackets(
					json.getString("observed"+Constants.JSON_SEPARATOR+"property")));
		}
		
		if (json.has("foi")){
			this.setFoi(Utils.removeBrackets(
					json.getString("foi")));
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
		
		//sensor reading uri - base_ov_uri - ov_uri
		if (json.has("base"+Constants.JSON_SEPARATOR+"ov"+Constants.JSON_SEPARATOR+"name")){
			this.setOv_uri(Utils.removeBrackets(
					json.getString("base"+Constants.JSON_SEPARATOR+"ov"+Constants.JSON_SEPARATOR+"name")));
		}
		
		//observation_values - sensor reading ID's
		if (json.has("observation"+Constants.JSON_SEPARATOR+"values")){
			this.setOv_values(json.getJSONArray("observation"+Constants.JSON_SEPARATOR+"values"));
		}
		
		//temporal sensor property
		if (json.has("tsproperties")){
			this.setTsproperties(json.getJSONArray("tsproperties"));
		}
	}

		
	public void setOv(String[] values) {
		this.ov = values;
	}

	public void setOv_values(JSONArray jvalues) throws JSONException {
		String[] values = new String[jvalues.length()];
		for (int i=0; i< jvalues.length(); i++){
			values[i] = jvalues.get(i).toString();
		}
		setOv(values);
	}

	public String[] getOv() {
		return ov;
	}

	

	public void setUnit_of_measurement(String unit_of_measurement) {
		this.unit_of_measurement = unit_of_measurement;
	}

	public String getUnit_of_measurement() {
		return unit_of_measurement;
	}

	/**
	 * Search for an external observed property resource uri 
	 * @param observed_property
	 */
	public void setObserved_property(String observed_property) {
		this.observed_property = observed_property;
	}

	/**
	 * @return Observed Property URI
	 */
	public String getObserved_property() {
		return observed_property;
	}



	public void setTsproperties(String[] tsproperties) {
		this.tsproperties = tsproperties;
	}
	
	public void setTsproperties(JSONArray jvalues) throws JSONException {
		String[] values = new String[jvalues.length()];
		for (int i=0; i< jvalues.length(); i++){
			values[i] = jvalues.get(i).toString();
		}
		setTsproperties(values);
	}

	public String[] getTsproperties() {
		return tsproperties;
	}

	public void setFoi(String foi) {
		this.foi = foi;
	}

	public String getFoi() {
		return foi;
	}



	public String getOv_uri() {
		return ov_uri;
	}

	public void setOv_uri(String ov_uri) {
		this.ov_uri = ov_uri;
	}
	
	
	/**
	 * Convert the Device Object to a JSON representation
	 * The JSON fields are defined by LD4Sensor 
	 *
	 * @param isRemote 
	 * @param isEnriched 
	 * @return 
	 */
	public JSONObject toJson(boolean isRemote, boolean isEnriched){
		JSONObject json= null;
		json = new JSONObject();
		try {	
			if (isRemote){
				json.append("uri", remote_uri);
			}else{
				json.append("uri", null);
			}
			if (isEnriched){
				//json.append("context", filters);	
			}else{
				json.append("context", null);
			}
			
			json.append("base_datetime", base_datetime);
			json.append("base_name", base_uri);
			json.append("base_ov_name", ov_uri);
			json.append("observed_property", observed_property);
			json.append("foi", foi);
			json.append("uom", unit_of_measurement);

			//device type
			json.append("type", type);
			
			// Current We have a default author which is - openiot
			JSONObject obj = new JSONObject();
			Author author = new Author();
			if (author.getFirstname() != null){
				obj.append("firstname", author.getFirstname());
			}
			if (author.getSurname() != null){
				obj.append("surname", author.getSurname());
			}
			if (author.getEmail() != null){
				obj.append("email", author.getEmail());
			}
			if (author.getHomepage() != null){
				obj.append("homepage", author.getHomepage());
			}
			if (author.getNickname() != null){
				obj.append("nickname", author.getNickname());
			}
			if (author.getWeblog() != null){
				obj.append("weblog", author.getWeblog());
			}
			json.append("author", obj);
			
			//observation values
			JSONArray vals = new JSONArray();
			for (int i=0; i<ov.length ;i++){
				vals.put(ov[i]);
			}
			json.append("values", vals);
			
			//tsproperties
			vals = new JSONArray();
			for (int i=0; i<tsproperties.length ;i++){
				vals.put(tsproperties[i]);
			}
			json.append("tsproperties", vals);
			
			
			json.append("location-name", locationName);
			json.append("location-coords", location_coordinates);
		} 
		catch (JSONException e1) {
			logger.error(e1.getMessage());
		}		
		return json;
	}
}

