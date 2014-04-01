package org.openiot.ld4s.resource.device;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.LD4SObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.openiot.ld4s.vocabulary.SptSnVocab;
import org.openiot.ld4s.vocabulary.SsnVocab;
import org.restlet.data.Form;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * Sensing Device resource.
 * This resource is usually stored on the Sensor and transmitted rarely.
 * 
<10e2073a01080063> a spt-sn:WS ;
clf:bn <http://ex.org> ;
clf:bt "12-06-22T17:00Z" ;
spt:uom qudt:unit/Abampere ;
spt:obs <electricCurrentInstance112> ;
spt:out <obval11204id> .

 * @author Myriam Leggieri <iammyr@email.com>
 *
 */
public class Device extends LD4SObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;
	/** Temporarily (to enhance the link search): Feature of Interest. */
	private String foi = null;


	/** Unit of Measurement. */
	private String unit_of_measurement = null;

	/** Observed Property. */
	private String observed_property = null;

	/** Base host name. */
	private String base_name = null;
	
	/** Base OV host name. */
	private String base_ov_name = null;

	/** Temporal Sensor Properties IDs (same base name than the main resource). */
	private String[] tsproperties = null;

	/** OV IDs (ov base name). */
	private String[] values = null;



	public Device(String host, String[] values, String uom,
			String op, String bn, String bovn, String criteria, String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setRemote_uri(host);
		this.setValues(values);
		this.setUnit_of_measurement(uom);
		this.setObserved_property(op);
		this.setBase_name(bn);
		this.setBase_ov_name(bovn);
		this.setLink_criteria(criteria, localhost);
	}

	public Device(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("uri")){
			this.setRemote_uri(LD4SDataResource.removeBrackets(
					json.getString("uri")));
		}
		if (json.has("uom")){
			this.setUnit_of_measurement(LD4SDataResource.removeBrackets(
					json.getString("uom")));
		}
		if (json.has("observed"+LD4SConstants.JSON_SEPARATOR+"property")){
			this.setObserved_property(LD4SDataResource.removeBrackets(
					json.getString("observed"+LD4SConstants.JSON_SEPARATOR+"property")));
		}
		if (json.has("foi")){
			this.setFoi(LD4SDataResource.removeBrackets(
					json.getString("foi")));
		}
		if (json.has("base"+LD4SConstants.JSON_SEPARATOR+"name")){
			this.setBase_name(LD4SDataResource.removeBrackets(
					json.getString("base"+LD4SConstants.JSON_SEPARATOR+"name")));
		}
		
		if (json.has("base"+LD4SConstants.JSON_SEPARATOR+"ov"+LD4SConstants.JSON_SEPARATOR+"name")){
			this.setBase_ov_name(LD4SDataResource.removeBrackets(
					json.getString("base"+LD4SConstants.JSON_SEPARATOR+"ov"+LD4SConstants.JSON_SEPARATOR+"name")));
		}
		if (json.has("observation"+LD4SConstants.JSON_SEPARATOR+"values")){
			this.setValues(json.getJSONArray("observation"+LD4SConstants.JSON_SEPARATOR+"values"));
		}
		if (json.has("mobile"+LD4SConstants.JSON_SEPARATOR+"context")){
			this.setTsproperties(json.getJSONArray("mobile"+
					LD4SConstants.JSON_SEPARATOR+"context"));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
	}

	

	public Device (Form form, String localhost) throws Exception {
		super(form);
		this.setValues(form.getValuesArray("observation"+LD4SConstants.JSON_SEPARATOR+"values"));
		this.setTsproperties(form.getValuesArray("tsproperties"));
		this.setRemote_uri(form.getFirstValue("uri")); 
		this.setUnit_of_measurement(
				form.getFirstValue("uom"));
		this.setBase_name(
				form.getFirstValue("base"+LD4SConstants.JSON_SEPARATOR+"name"));
		this.setType(
				form.getFirstValue("type"));
		this.setBase_ov_name(
				form.getFirstValue("base"+LD4SConstants.JSON_SEPARATOR+"ov"+LD4SConstants.JSON_SEPARATOR+"name"));
		this.setObserved_property(
				form.getFirstValue("observed"+LD4SConstants.JSON_SEPARATOR+"property"));
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
		String[] values = new String[jvalues.length()];
		for (int i=0; i< jvalues.length(); i++){
			values[i] = jvalues.get(i).toString();
		}
		setValues(values);
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

	public void setBase_name(String base_name) {
		this.base_name = base_name;
	}

	public String getBase_name() {
		return base_name;
	}

	public void setBase_ov_name(String base_ov_name) {
		this.base_ov_name = base_ov_name;
	}

	public String getBase_ov_name() {
		return base_ov_name;
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

	@Override
	protected void initAcceptedTypes() {
		this.setAcceptedTypes(new OntClass[]{
				SptSnVocab.ACTUATOR, SptSnVocab.TRANSDUCER,
				SptSnVocab.ACCELEROMETER,
				SptSnVocab.GPS,
				SptSnVocab.HUMIDITY_SENSOR,
				SptSnVocab.LIGHT_SENSOR,
				SptSnVocab.MOTION_SENSOR,
				SsnVocab.SENSING_DEVICE
		});
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SsnVocab.SENSOR;
	}

	public void setFoi(String foi) {
		this.foi = foi;
	}

	public String getFoi() {
		return foi;
	}
	
	
}
