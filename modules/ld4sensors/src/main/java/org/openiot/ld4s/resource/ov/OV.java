package org.openiot.ld4s.resource.ov;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.resource.LD4SObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.data.Form;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * Observation Value resource.
 * This resource is usually stored on the Sensor and transmitted frequently.
 * 
1st Case: Single Value Transmission
<obval11204id> <http://www.w3.org/2000/01/rdf-schema#type> <http://spitfire-project.eu/ontology/ns/OV> .
<obval11204id> <http://purl.org/NET/corelf#t> "2356511" .
<obval11204id> <http://spitfire-project.eu/ontology/ns/val> "12.343" .

2nd Case: Multiple Values Transmission
@prefix spt: <http://spitfire-project.eu/ontology/ns/> .
@prefix clf: <http://purl.org/NET/corelf#> .
<obval11204id> a spt:OV ;
clf:t "23565" ;
spt:val "12.343" ;
spt:val "10.002" ;
.
.
.
spt:val "11.240" ;
spt:tStart "3423532" ;
spt:tEnd "4325235" .

 * @author Myriam Leggieri <iammyr@email.com>
 *
 */
public class OV extends LD4SObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;

	/** Observed values. */
	private String[] values = null;

	/** Sensor ID. */
	private String sensor_id = null;
	
	/** Observation. */
	private String observation = null;
	
	/** Unit of Measurement. */
	private String unit_of_measurement = null;
	
	/** Observed Property. */
	private String observed_property = null;
	
	/** Feature of Interest. If provided, it will be associated with the Observed Property. */
	private String foi = null;
	


	public OV(String host, String[] values, String resource_time,
			String criteria, String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setResource_id(host);
		this.setResult_time(resource_time);
		this.setValues(values);
		this.setLink_criteria(criteria, localhost);
	}

	public OV(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("uom")){
			this.setUnit_of_measurement(json.getString("uom"));
		}
		if (json.has("foi")){
			this.setFoi(json.getString("foi"));
		}
		if (json.has("observation")){
			this.setObservation(json.getString("observation"));
		}
		if (json.has("values")){
			this.setValues(json.getJSONArray("values"));
		}
		if (json.has("sensor"+LD4SConstants.JSON_SEPARATOR+"id")){
			this.setSensor_id(json.getString("sensor"+LD4SConstants.JSON_SEPARATOR+"id"));
		}
		if (json.has("observed"+LD4SConstants.JSON_SEPARATOR+"property")){
			this.setObserved_property(json.getString("observed"+LD4SConstants.JSON_SEPARATOR+"property"));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
	}

	public OV (Form form, String localhost) throws Exception {
		super(form);
		this.setValues(form.getValuesArray("values"));
		this.setResource_id(form.getFirstValue("uri")); 
		this.setResult_time(
				form.getFirstValue("resource"+LD4SConstants.JSON_SEPARATOR+"time"));
		this.setLink_criteria(
				form.getFirstValue("context"), localhost);
	}




	public void setValues(String[] values) {
		this.values = values;
	}

	public void setValues(JSONArray j) throws JSONException {
		if (j != null){
			String[] values = new String[j.length()];
			for (int i=0; i< j.length(); i++){
				values[i] = j.getString(i);
			}
			setValues(values);
		}
	}

	public void setSensor_id(String sensor_id) {
		this.sensor_id = sensor_id;
	}

	public String getSensor_id() {
		return sensor_id;
	}
	
	public String[] getValues() {
		return values;
	}

	@Override
	public void setStoredRemotely(boolean storedRemotely) {
		this.stored_remotely = storedRemotely;		
	}

	@Override
	public boolean isStoredRemotely() {
		return this.stored_remotely;
	}

	@Override
	public boolean isStoredRemotely(String localUri) {
		if (getResource_id() == null
				||
				(localUri.contains(getResource_id())
						|| getResource_id().contains(localUri))){
			return false;
		}
		return true;
	}

	@Override
	public void setLink_criteria(Context link_criteria) {
		this.link_criteria = link_criteria;
	}

	@Override
	public void setLink_criteria(String link_criteria, String localhost) throws Exception {
		this.link_criteria = new Context(link_criteria, localhost);
	}

	@Override
	public Context getLink_criteria() {
		return this.link_criteria;
	}

	@Override
	protected void initAcceptedTypes() {
		this.acceptedTypes = new OntClass[]{};
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SptVocab.OV;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}
	
	public void setUnit_of_measurement(String unit_of_measurement) {
		this.unit_of_measurement = unit_of_measurement;
	}

	public String getUnit_of_measurement() {
		return unit_of_measurement;
	}
	
	public void setObserved_property(String observed_property) {
		this.observed_property = observed_property;
	}

	/**
	 * @return Observed Property URI
	 */
	public String getObserved_property() {
		return observed_property;
	}

	public String getFoi() {
		return foi;
	}

	public void setFoi(String foi) {
		this.foi = foi;
	}

	
}
