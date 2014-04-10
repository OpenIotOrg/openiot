package org.openiot.ld4s.resource.owlclass;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.LD4SObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.restlet.data.Form;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Client-defined OWL Type resource.
 * This resource is usually stored on a gateway.
 * It assumes the form of
* owl_new_type
- rdf:type or rdfs:subClassOf
- a_class
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
public class OwlClass extends LD4SObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;

	/** OWL Type conditions. */
	private OwlClassRestriction[] conditions = null;
	
	/** Predicate URI of the OWL Type. */
	private String predicate = null;
	
	/** Value of the property. */
	private String value = null;
	

	public OwlClass(String host, String[] values, String resource_time,
			String criteria, String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setRemote_uri(host);
		this.setResource_time(resource_time);
		this.setLink_criteria(criteria, localhost);
	}

	public OwlClass(JSONObject json, String localhost) throws Exception {
		super(json);
	    if (json.has("new"+LD4SConstants.JSON_SEPARATOR+"type"+LD4SConstants.JSON_SEPARATOR+"predicate")){
			this.setPredicate(LD4SDataResource.removeBrackets(
					json.getString("new"+LD4SConstants.JSON_SEPARATOR+"type"+LD4SConstants.JSON_SEPARATOR+"predicate")));
		}
		if (json.has("value")){
			this.setValue(LD4SDataResource.removeBrackets(
					json.getString("value")));
		}
		if (json.has("conditions")){
			this.setConditions(json.getJSONArray("conditions"));
		}
	}

	public OwlClass (Form form, String localhost) throws Exception {
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
		this.defaultType = null;
	}

	public void setConditions(OwlClassRestriction[] conditions) {
		this.conditions = conditions;
	}
	
	public OwlClassRestriction[] getConditions(){
		return this.conditions;
	}

	public void setConditions(JSONArray jarr) throws JSONException {
		if (jarr == null){
			return;
		}
		this.conditions = new OwlClassRestriction[jarr.length()];
		JSONObject elem = null;
		OwlClassRestriction cond = null;
		for (int i=0; i<jarr.length() ;i++){
			elem = jarr.getJSONArray(i).getJSONObject(0);
			cond = new OwlClassRestriction();
			if (elem.has("oncondition"+LD4SConstants.JSON_SEPARATOR+"property")){
				cond.onPredicate = LD4SDataResource.removeBrackets(
						elem.getString("oncondition"+LD4SConstants.JSON_SEPARATOR+"property"));
			}
			if (elem.has("oncondition"+LD4SConstants.JSON_SEPARATOR+"value")){
				cond.value = LD4SDataResource.removeBrackets(
						elem.getString("oncondition"+LD4SConstants.JSON_SEPARATOR+"value"));
			}
			if (elem.has("oncondition"+LD4SConstants.JSON_SEPARATOR+"predicate")){
				cond.restrictionPredicate = LD4SDataResource.removeBrackets(
						elem.getString("oncondition"+LD4SConstants.JSON_SEPARATOR+"predicate"));
			}
			if (elem.has("oncondition"+LD4SConstants.JSON_SEPARATOR+"uom")){
				cond.uom = LD4SDataResource.removeBrackets(
						elem.getString("oncondition"+LD4SConstants.JSON_SEPARATOR+"uom"));
			}
			this.conditions[i] = cond;
		}
	}

	public void setPredicate(String string) {
		if (string.compareTo(RDF.type.getURI()) == 0 
				| string.compareTo(RDFS.subClassOf.getURI()) == 0){
			this.predicate = string;
		}else{
			this.predicate = RDF.type.getURI();
		}
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
}
