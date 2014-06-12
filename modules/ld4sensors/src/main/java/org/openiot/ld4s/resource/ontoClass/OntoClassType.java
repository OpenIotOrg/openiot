package org.openiot.ld4s.resource.ontoClass;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.LD4SConstants;

public class OntoClassType {

	public static enum Choices {
		Class,
		Datatype,
		Subclass
	}

	/** Chosen type of Ontology Class. */
	private Choices choice = null;
	
	/** Class URI (in case choice==Class). */
	private String uri = null;

	/** Enumerated Values (in case choice==Datatype). */
	private LinkedList<String> enumValues = null;

	/** Super Class (in case choice==subClassOf). */
	private String superClass = null;

	public OntoClassType(JSONObject json) throws JSONException{
		if (json.has("type"+LD4SConstants.JSON_SEPARATOR+"choice")){
			this.setChoice(LD4SDataResource.removeBrackets(
					json.getString("type"+LD4SConstants.JSON_SEPARATOR+"choice")));
		}
		if (json.has("type"+LD4SConstants.JSON_SEPARATOR+"super")){
			this.setSuperClass(LD4SDataResource.removeBrackets(
					json.getString("type"+LD4SConstants.JSON_SEPARATOR+"super")));
		}
		if (json.has("type"+LD4SConstants.JSON_SEPARATOR+"uri")){
			this.setUri(LD4SDataResource.removeBrackets(
					json.getString("type"+LD4SConstants.JSON_SEPARATOR+"uri")));
		}
		if (json.has("type"+LD4SConstants.JSON_SEPARATOR+"allowed"+
				LD4SConstants.JSON_SEPARATOR+"values")){
			this.setEnumValues(json.getJSONArray("type"+LD4SConstants.JSON_SEPARATOR+"allowed"+
					LD4SConstants.JSON_SEPARATOR+"values"));
		}
	}

	public LinkedList<String> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(LinkedList<String> enumValues) {
		this.enumValues = enumValues;
	}
	
	public void setEnumValues(JSONArray objarr) throws JSONException{
		setEnumValues(toListOfStrings(objarr));
	}
	
	private LinkedList<String> toListOfStrings(JSONArray objarr) throws JSONException{
		objarr = objarr.getJSONArray(0);
		LinkedList<String> ret = null;
		if (objarr != null){
			ret = new LinkedList<String>();
			for (int i=0; i<objarr.length() ;i++){
				ret.add(objarr.getString(i));
			}
		}
		return ret;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public Choices getChoice() {
		return choice;
	}

	public void setChoice(Choices choices) {
		this.choice = choices;
	}

	public void setChoice(String choice) {
		try{
			setChoice(Choices.valueOf(choice));	
		}catch(IllegalArgumentException e){
			System.err.println("Chosen type of Ontology Class ("+ choice+") does not" +
					"match any of those supported by OWL Full: "+Choices.values());
		}

	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
