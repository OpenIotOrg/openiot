package org.openiot.ld4s.resource.temporal_property.sensor;

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
 * Temporal Sensor Property resource.
 * This resource is usually stored on the Sensor and is part of the extended annotation
 * (i.e., richer) to be transmitted rarely.
 * 
<10e2073a01080063> spt:temporal <stpInstance112> .
<stpInstance112> a spt: SensorTemporalProperty ;
spt:netRole <http://ex.org/proxy/roleInstance112> ;
spt:link <http://ex.org/proxy/linkInstance112> ;
ssn:featureOfInterest <http://ex.org/proxy/foiInstance112> ;
spt:tStart "3423532" ;
spt:tEnd "4325235" .

 * @author Myriam Leggieri <iammyr@email.com>
 *
 */
public class TempSensProp extends LD4SObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;

	
	/** Sensor ID. */
	private String sensor_id = null;

	/** Network Role. */
	private String net_role = null;

	/** Network Link IDs in use. */
	private String[] net_links = null;

	/** Feature of Interest. */
	private String foi = null;


	public TempSensProp(String host, String[] net_links, String net_role, String sens,
			String foi, String start_time, String end_time, String criteria, 
			String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setRemote_uri(host);
		this.setSensor_id(sens);;
		this.setFoi(foi);
		this.setLink_criteria(criteria, localhost);
	}

	public TempSensProp(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("uri")){
			this.setRemote_uri(LD4SDataResource.removeBrackets(
					json.getString("uri")));
		}
		if (json.has("sensor"+LD4SConstants.JSON_SEPARATOR+"id")){
			this.setSensor_id(LD4SDataResource.removeBrackets(
					json.getString("senso"+LD4SConstants.JSON_SEPARATOR+"id")));
		}
		if (json.has("foi")){
			this.setFoi(LD4SDataResource.removeBrackets(
					json.getString("foi")));
		}
		if (json.has("net"+LD4SConstants.JSON_SEPARATOR+"role")){
			this.setNet_role(LD4SDataResource.removeBrackets(
					json.getString("net"+LD4SConstants.JSON_SEPARATOR+"role")));
		}
		if (json.has("net"+LD4SConstants.JSON_SEPARATOR+"links")){
			this.setNet_links(json.getJSONArray("net"+LD4SConstants.JSON_SEPARATOR+"links"));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
	}

	public TempSensProp (Form form, String localhost) throws Exception {
		super(form);
		this.setNet_links(form.getValuesArray("net"+LD4SConstants.JSON_SEPARATOR+"links"));
		this.setRemote_uri(form.getFirstValue("uri")); 
		this.setFoi(
				form.getFirstValue("foi"));
		this.setNet_role(
				form.getFirstValue("net"+LD4SConstants.JSON_SEPARATOR+"role"));
		this.setSensor_id(
				form.getFirstValue("sensor"+LD4SConstants.JSON_SEPARATOR+"id"));
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

	public void setSensor_id(String sensor_id) {
		this.sensor_id = sensor_id;
	}

	public String getSensor_id() {
		return sensor_id;
	}

	public void setNet_role(String net_role) {
		this.net_role = net_role;
	}

	public String getNet_role() {
		return net_role;
	}

	public void setFoi(String foi) {
		this.foi = foi;
	}

	public String getFoi() {
		return foi;
	}

	
	public void setNet_links(String[] net_links) {
		this.net_links = net_links;
	}
	
	public void setNet_links(JSONArray jvalues) throws JSONException {
		if (jvalues != null && (jvalues=jvalues.getJSONArray(0)) != null){
			String[] values = new String[jvalues.length()];
			for (int i=0; i< jvalues.length(); i++){
				values[i] = jvalues.get(i).toString();
			}
			setNet_links(values);
		}
	}


	public String[] getNet_links() {
		return net_links;
	}


	@Override
	protected void initAcceptedTypes() {
		this.acceptedTypes = new OntClass[]{};
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SptVocab.SENSOR_TEMPORAL_PROPERTY;
	}

}
