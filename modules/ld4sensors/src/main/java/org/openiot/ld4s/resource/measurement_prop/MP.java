package org.openiot.ld4s.resource.measurement_prop;

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
 * Measurement Property resource.
 * This resource is usually stored on a gateway.
 * It is associated with one Measurement Capability referring to a sensed property (e.g. Temp.) 
 * and it assumes the form of
* measurement_property_data
- property_predicate
- property_value
- property_uom
- property_conditions 
-------- oncondition_property_1
-------- oncondition_predicate_1
-------- oncondition_value_1
-------- oncondition_uom_1
...
...
-------- oncondition_property_n
-------- oncondition_predicate_n
-------- oncondition_value_n
-------- oncondition_uom_n


 * @author Myriam Leggieri <iammyr@email.com>
 *
 */
public class MP extends LD4SObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;

	/** Milliseconds shift from the base time as a resource creation time point. */
	private String resource_time = null;


	/** Measurement Property conditions. */
	private MPCondition[] conditions = null;
	
	/** Predicate URI of the measurement property. */
	private String predicate = null;
	
	/** Value of the property. In case of a range, the two values are divided by "_". */
	private String value = null;
	
	/** Unit of measurement for the measurement property value. */
	private String uom = null;



	public MP(String host, String[] values, String resource_time,
			String criteria, String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setRemote_uri(host);
		this.setResource_time(resource_time);
		this.setLink_criteria(criteria, localhost);
	}

	public MP(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("uri")){
			this.setRemote_uri(LD4SDataResource.removeBrackets(
					json.getString("uri")));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
		if (json.has("predicate")){
			this.setPredicate(LD4SDataResource.removeBrackets(
					json.getString("predicate")));
		}
		if (json.has("value")){
			this.setValue(LD4SDataResource.removeBrackets(
					json.getString("value")));
		}
		if (json.has("uom")){
			this.setUnit_of_measurement(LD4SDataResource.removeBrackets(
					json.getString("uom")));
		}
		if (json.has("conditions")){
			this.setConditions(json.getJSONArray("conditions"));
		}
	}

	public MP (Form form, String localhost) throws Exception {
		super(form);
		this.setRemote_uri(form.getFirstValue("uri")); 
		this.setResource_time(
				form.getFirstValue("resource"+LD4SConstants.JSON_SEPARATOR+"time"));
		this.setLink_criteria(
				form.getFirstValue("context"), localhost);
	}


	@Override
	public String getRemote_uri() {
		return resource_uri;
	}


	@Override
	public void setRemote_uri(String host) {
		this.resource_uri = host;
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
		this.acceptedTypes = new OntClass[]{SsnVocab.ACCURACY, SsnVocab.DETECTION_LIMIT,
				SsnVocab.DRIFT, SsnVocab.FREQUENCY, SsnVocab.LATENCY, SsnVocab.PRECISION,
				SsnVocab.RESOLUTION, SsnVocab.RESPONSE_TIME, SsnVocab.SELECTIVITY,
				SsnVocab.SENSITIVITY};
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SsnVocab.MEASUREMENT_PROPERTY;
	}

	public void setConditions(MPCondition[] conditions) {
		this.conditions = conditions;
	}
	
	public MPCondition[] getConditions(){
		return this.conditions;
	}

	public void setConditions(JSONArray jarr) throws JSONException {
		if (jarr == null){
			return;
		}
		this.conditions = new MPCondition[jarr.length()];
		JSONObject elem = null;
		MPCondition cond = null;
		for (int i=0; i<jarr.length() ;i++){
			elem = jarr.getJSONArray(i).getJSONObject(0);
			cond = new MPCondition();
			if (elem.has("oncondition_property")){
				cond.observed_property = LD4SDataResource.removeBrackets(
						elem.getString("oncondition_property"));
			}
			if (elem.has("oncondition_value")){
				cond.value = LD4SDataResource.removeBrackets(
						elem.getString("oncondition_value"));
			}
			if (elem.has("oncondition_predicate")){
				cond.predicate = LD4SDataResource.removeBrackets(
						elem.getString("oncondition_predicate"));
			}
			if (elem.has("oncondition_uom")){
				cond.uom = LD4SDataResource.removeBrackets(
						elem.getString("oncondition_uom"));
			}
			this.conditions[i] = cond;
		}
	}

	public void setPredicate(String string) {
		this.predicate = string;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setUnit_of_measurement(String uom) {
		this.uom = uom;
	}

	public String getUnit_of_measurement() {
		return uom;
	}
}
