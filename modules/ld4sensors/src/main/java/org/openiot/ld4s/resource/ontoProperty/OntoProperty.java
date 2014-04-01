package org.openiot.ld4s.resource.ontoProperty;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.resource.LD4SObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.openiot.ld4s.vocabulary.SptSnVocab;
import org.openiot.ld4s.vocabulary.SsnVocab;
import org.restlet.data.Form;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * Ontology Property resource.
 * This resource is usually stored as a module of the core Ontology.
 * 
 * @author Myriam Leggieri <iammyr@email.com>
 *
 */
public class OntoProperty extends LD4SObject  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;
	

	public static enum Type {
		Property,
		SubProperty,
		TransitiveProperty,
		SymmetricProperty,
		InverseFunctionalProperty,
		FunctionalProperty,
		ObjectProperty,
		DataTypeProperty,
		AnnotationProperty
	}
	
	/** Type of the new ontology property (subProperty, transitive, etc.). */
	private Type[] ontoPropTypes = null;

	/** Inverse Properties. */
	private String[] inverseProperties = null;
	
	/** Equivalent Properties. */
	private String[] equivalentProperties = null;
	
	/** Domain. */
	private String domain = null;
	
	/** Range. */
	private String range = null;
	
	/** Super Property (in case type==subPropertyOf). */
	private String superProperty = null;
	

	
	
	public OntoProperty() throws Exception{
		super(null, null, null, null);
	}

	public OntoProperty(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("types")){
			this.setOntoPropTypes(json.getJSONArray("types"));
		}
		if (json.has("inverse"+LD4SConstants.JSON_SEPARATOR+"properties")){
			this.setInverseProperties(json.getJSONArray("inverse"+
		LD4SConstants.JSON_SEPARATOR+"properties"));
		}
		if (json.has("equivalent"+LD4SConstants.JSON_SEPARATOR+"properties")){
			this.setEquivalentProperties(json.getJSONArray("equivalent"+
		LD4SConstants.JSON_SEPARATOR+"properties"));
		}
		if (json.has("domain")){
			this.setDomain(json.getString("domain"));
		}
		if (json.has("range")){
			this.setRange(json.getString("range"));
		}
		if (json.has("super")){
			this.setRange(json.getString("super"));
		}
	}

	

	public OntoProperty (Form form, String localhost) throws Exception {
		super(form);
	}
	
	
	public Type[] getOntoPropTypes() {
		return ontoPropTypes;
	}

	public void setOntoPropTypes(Type[] ontoPropTypes) {
		this.ontoPropTypes = ontoPropTypes;
	}
	
	public void setOntoPropTypes(JSONArray objarr) throws JSONException{
		setOntoPropTypes(toTypes(objarr));
	}

	public String[] getInverseProperties() {
		return inverseProperties;
	}

	public void setInverseProperties(String[] inverseProperties) {
		this.inverseProperties = inverseProperties;
	}
	
	public void setInverseProperties(JSONArray objarr) throws JSONException {
		setInverseProperties(toStringArr(objarr));
	}

	public String[] getEquivalentProperties() {
		return equivalentProperties;
	}

	public void setEquivalentProperties(String[] equivalentProperties) {
		this.equivalentProperties = equivalentProperties;
	}
	
	public void setEquivalentProperties(JSONArray objarr) throws JSONException {
		setEquivalentProperties(toStringArr(objarr));
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}
	
	
	private Type[] toTypes (JSONArray objarr) throws JSONException{
		objarr = objarr.getJSONArray(0);
		Type[] ret = null;
		if (objarr != null){
			ret = new Type[objarr.length()];
			for (int i=0; i<objarr.length() ;i++){
				try{
					ret[i] = Type.valueOf(objarr.getString(i));
				}catch (IllegalArgumentException e){
					System.err.println("One of the sumitted types of properties" +
						"is not supported by the system");
				}
			}
		}
		return ret;
	}
	
	private String[] toStringArr (JSONArray objarr) throws JSONException{
		objarr = objarr.getJSONArray(0);
		String[] ret = null;
		if (objarr != null){
			ret = new String[objarr.length()];
			for (int i=0; i<objarr.length() ;i++){
				ret[i] = objarr.getString(i);
			}
		}
		return ret;
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

	@Override
	protected void initAcceptedTypes() {
		this.setAcceptedTypes(new OntClass[]{
				SptSnVocab.TESTBED});
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SsnVocab.PLATFORM;
	}

	public String getSuperProperty() {
		return superProperty;
	}

	public void setSuperProperty(String superProperty) {
		this.superProperty = superProperty;
	}

	
	
	
}
