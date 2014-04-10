package org.openiot.ld4s.resource.ontoClass;

import java.io.Serializable;
import java.util.LinkedList;

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
 * Ontology Class resource.
 * This resource is usually stored as a module of the core Ontology.
 * 
 * @author Myriam Leggieri <iammyr@email.com>
 *
 */
public class OntoClass extends LD4SObject  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;
	

	
	/** Type of the new ontology Class (subClass, DataType, etc.). */
	private OntoClassType[] ontoClassTypes = null;

	/** Disjoint Classes. */
	private OntoClassSet[] disjointClasses = null;
	
	/** Equivalent Classes. */
	private OntoClassSet[] equivalentClasses = null;
	
	/** Restricted Properties. */
	private OntoClassRestriction[] restrictions = null;
	
	/** Resource ID. */
	private String resource_id = null;
	

	
	
	public OntoClass(String type, String disjointClasses,
			String equivalentClasses) 
	throws Exception{
		super(null, null, null, null);
	}

	public OntoClass(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("resource"+LD4SConstants.JSON_SEPARATOR+"id")){
			this.setResource_id(LD4SDataResource.removeBrackets(
					json.getString("resource"+LD4SConstants.JSON_SEPARATOR+"id")));
		}
		if (json.has("types")){
			this.setOntoClassTypes(json.getJSONArray("types"));
		}
		if (json.has("disjoint"+LD4SConstants.JSON_SEPARATOR+"classes")){
			this.setDisjointClasses(json.getJSONArray("disjoint"+LD4SConstants.JSON_SEPARATOR+"classes"));
		}
		if (json.has("equivalent"+LD4SConstants.JSON_SEPARATOR+"classes")){
			this.setEquivalentClasses(json.getJSONArray("equivalent"+LD4SConstants.JSON_SEPARATOR+"classes"));
		}
		if (json.has("restrictions")){
			this.setRestrictions(json.getJSONArray("restrictions"));
		}
	}

	

	public OntoClass (Form form, String localhost) throws Exception {
		super(form);
	}
	
	
	public OntoClassSet[] getDisjointClasses() {
		return disjointClasses;
	}

	public void setDisjointClasses(OntoClassSet[] disjointClasses) {
		this.disjointClasses = disjointClasses;
	}
	
	private OntoClassSet[] toSets (JSONArray objarr) throws JSONException{
		objarr = objarr.getJSONArray(0);
		OntoClassSet[] sets = null;
		if (objarr != null){
			sets = new OntoClassSet[objarr.length()];
			for (int i=0; i<objarr.length() ;i++){
				sets[i] = new OntoClassSet(objarr.getJSONObject(i));
			}
		}
		return sets;
	}
	
	private OntoClassType[] toTypes (JSONArray objarr) throws JSONException{
		objarr = objarr.getJSONArray(0);
		OntoClassType[] sets = null;
		if (objarr != null){
			sets = new OntoClassType[objarr.length()];
			for (int i=0; i<objarr.length() ;i++){
				sets[i] = new OntoClassType(objarr.getJSONObject(i));
			}
		}
		return sets;
	}
	
	public void setDisjointClasses(JSONArray objarr) throws JSONException {
		setDisjointClasses(toSets(objarr));
	}

	public OntoClassSet[] getEquivalentClasses() {
		return equivalentClasses;
	}

	public void setEquivalentClasses(OntoClassSet[] equivalentClasses) {
		this.equivalentClasses = equivalentClasses;
	}
	
	public void setEquivalentClasses(JSONArray objarr) throws JSONException {
		setEquivalentClasses(toSets(objarr));
	}

	public OntoClassRestriction[] getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(OntoClassRestriction[] restrictions) {
		this.restrictions = restrictions;
	}
	
	public void setRestrictions(JSONArray objarr) throws JSONException {
		setRestrictions(toRestrictions(objarr));
	}
	
	private OntoClassRestriction[] toRestrictions (JSONArray objarr) throws JSONException{
		objarr = objarr.getJSONArray(0);
		OntoClassRestriction[] ret = null;
		if (objarr != null){
			ret = new OntoClassRestriction[objarr.length()];
			for (int i=0; i<objarr.length() ;i++){
				ret[i] = new OntoClassRestriction(objarr.getJSONObject(i));
			}
		}
		return ret;
	}

	public OntoClassType[] getOntoClassTypes() {
		return ontoClassTypes;
	}

	public void setOntoClassTypes(OntoClassType[] ontoClassTypes) {
		this.ontoClassTypes = ontoClassTypes;
	}
	
	public void setOntoClassTypes(JSONArray objarr) throws JSONException {
		setOntoClassTypes(toTypes(objarr));
	}
	
	@Override
	public String getRemote_uri() {
		return "http://openiot.eu/ontology/ext/"+resource_uri;
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
		this.setAcceptedTypes(new OntClass[]{
				SptSnVocab.TESTBED});
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SsnVocab.PLATFORM;
	}

	public String getResource_id() {
		return resource_id;
	}

	public void setResource_id(String resource_id) {
		this.resource_id = "http://openiot.eu/ontology/ext/"+resource_id;
	}
	
	
}
