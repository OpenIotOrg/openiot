package org.openiot.ld4s.resource.measurement_capab;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.LD4SObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.openiot.ld4s.vocabulary.SsnVocab;
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
public class MC extends LD4SObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;

	/** Milliseconds shift from the base time as a resource creation time point. */
	private String resource_time = null;

	/** Observed Property which the capability refers to 
	 * (since the capability should be associated with a platform, which normally has
	 * more than one sensor attached and then, which is 
	 * normally sensing more than one property). */
	private String observed_property = null;
	
	/** Temporarily (to enhance the link search): Feature of Interest. */
	private String foi = null;
	
	/** Measurement Properties  
	 * including for each meas. prop. more or 
	 * one of the following dataset (JSONArray itself) - JSONArray
	 * * the predicate which the value restriction applies on, - string
	 * * the restricted values - JSONObject of values (max, min, avg, etc. as keys)
	 * * the conditions to occur for the restriction to be valid - JSONArray of conditions). */
	private String[] measurement_prop_uris = null;



	public MC(String host, String[] values, String resource_time,
			String criteria, String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setRemote_uri(host);
		this.setResource_time(resource_time);
		this.setLink_criteria(criteria, localhost);
	}

	public MC(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("uri")){
			this.setRemote_uri(LD4SDataResource.removeBrackets(
					json.getString("uri")));
		}
		if (json.has("observed_property")){
			this.setObserved_property(LD4SDataResource.removeBrackets(
					json.getString("observed_property")));
		}
		if (json.has("foi")){
			this.setFoi(LD4SDataResource.removeBrackets(
					json.getString("foi")));
		}
		if (json.has("measurement_properties")){
			this.setMeasurement_prop_uris(json.getJSONArray("measurement_properties"));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
	}

	public MC (Form form, String localhost) throws Exception {
		super(form);
		this.setRemote_uri(form.getFirstValue("uri")); 
		this.setResource_time(
				form.getFirstValue("resource"+LD4SConstants.JSON_SEPARATOR+"time"));
		this.setLink_criteria(
				form.getFirstValue("context"), localhost);
	}


	@Override
	public String getRemote_uri() {
		return resource_id;
	}


	@Override
	public void setRemote_uri(String host) {
		this.resource_id = host;
	}

	public void setResource_time(String resource_time) {
		this.resource_time = resource_time;
	}

	public String getResource_time() {
		return resource_time;
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
		if (getRemote_uri() == null
				||
				(localUri.contains(getRemote_uri())
						|| getRemote_uri().contains(localUri))){
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
		this.defaultType = SsnVocab.MEASUREMENT_CAPABILITY;
	}

	public void setObserved_property(String observed_property) {
		this.observed_property = observed_property;
	}

	public String getObserved_property() {
		return observed_property;
	}

	public void setMeasurement_prop_uris(String[] measurement_prop_ids) {
		this.measurement_prop_uris = measurement_prop_ids;
	}

	public void setMeasurement_prop_uris(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null || (jsonArray=jsonArray.getJSONArray(0)) == null){
			return;
		}
		this.measurement_prop_uris = new String[jsonArray.length()];
		for (int i=0; i<jsonArray.length() ;i++){
			this.measurement_prop_uris[i] = jsonArray.getString(i);
		}
	}
	
	public String[] getMeasurement_prop_uris() {
		return measurement_prop_uris;
	}

	public void setFoi(String foi) {
		this.foi = foi;
	}

	public String getFoi() {
		return foi;
	}
}
