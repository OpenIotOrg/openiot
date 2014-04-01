package org.openiot.ld4s.resource.ontoClass;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;

public class OntoClassSet {
	
	public static enum Type {
		intersectionOf,
		complementOf,
		unionOf,
		singleClass
	};
	
	private Type type;
	
	private LinkedList<OntoClassSet> list;
	
	private String id;

	public OntoClassSet(JSONObject json) throws JSONException{
		if (json.has("set"+LD4SConstants.JSON_SEPARATOR+"type")){
			this.setType(json.getString("set"+LD4SConstants.JSON_SEPARATOR+"type"));
		}
		if (json.has("set"+LD4SConstants.JSON_SEPARATOR+"id")){
			this.setId(json.getString("set"+LD4SConstants.JSON_SEPARATOR+"id"));
		}
		if (json.has("set"+LD4SConstants.JSON_SEPARATOR+"list")){
			this.setList(json.getJSONArray("set"+LD4SConstants.JSON_SEPARATOR+"list"));
		}
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public void setType(String type) {
		try{
			setType(Type.valueOf(type));
		}catch(IllegalArgumentException e){
			System.err.println("The submitted type of Ontology Class Set ("+type+") does" +
					"not match any of the accepted ones: "+Type.values());
		}
	}

	public LinkedList<OntoClassSet> getList() {
		return list;
	}

	public void setList(LinkedList<OntoClassSet> list) {
		this.list = list;
	}
	
	public void setList(JSONArray objarr) throws JSONException{
		objarr = objarr.getJSONArray(0);
		LinkedList<OntoClassSet> sets = new LinkedList<OntoClassSet>();
		if (objarr != null){
			for (int i=0; i<objarr.length() ;i++){
				sets.add(new OntoClassSet(objarr.getJSONObject(i)));
			}
			setList(sets);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
