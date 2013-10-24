package org.openiot.ld4s.resource.ov;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.resource.LD4SDataResource;
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


	public OV(String host, String[] values, String resource_time,
			String criteria, String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setRemote_uri(host);
		this.setResource_time(resource_time);
		this.setValues(values);
		this.setLink_criteria(criteria, localhost);
	}

	public OV(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("uri")){
			this.setRemote_uri(LD4SDataResource.removeBrackets(
					json.getString("uri")));
		}
		if (json.has("resource"+LD4SConstants.JSON_SEPARATOR+"time")){
			this.setResource_time(LD4SDataResource.removeBrackets(
					json.getString("resource"+LD4SConstants.JSON_SEPARATOR+"time")));
		}
		if (json.has("values")){
			this.setValues(json.getJSONArray("values"));
		}
		if (json.has("sensor"+LD4SConstants.JSON_SEPARATOR+"id")){
			this.setSensor_id(LD4SDataResource.removeBrackets(
					json.getString("senso"+LD4SConstants.JSON_SEPARATOR+"id")));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
	}

	public OV (Form form, String localhost) throws Exception {
		super(form);
		this.setValues(form.getValuesArray("values"));
		this.setRemote_uri(form.getFirstValue("uri")); 
		this.setResource_time(
				form.getFirstValue("resource"+LD4SConstants.JSON_SEPARATOR+"time"));
		this.setLink_criteria(
				form.getFirstValue("context"), localhost);
	}


	@Override
	public String getRemote_uri() {
		return remote_uri;
	}


	@Override
	public void setRemote_uri(String host) {
		this.remote_uri = host;
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
		this.defaultType = SptVocab.OV;
	}
}
