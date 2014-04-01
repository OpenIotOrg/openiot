package org.openiot.ld4s.resource.ontoClass;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;

public class OntoClassRestriction {

	public static enum Amount {
		/** Whose range is xsd:nonNegativeInteger. */
		cardinality,
		minCardinality,
		maxCardinality,
		/** Whose range is a class or a blank node of type owl:DataRange*/
		allValuesFrom,
		someValuesFrom,
		/** Whose range is an individual or data value. */
		hasValue
	}

	private String property = null;

	private Amount amount = null;

	private OntoClassType classValue = null;

	private String dataValue = null;

	public OntoClassRestriction(JSONObject json) throws JSONException{
		if (json.has("restriction"+LD4SConstants.JSON_SEPARATOR+"on"+
				LD4SConstants.JSON_SEPARATOR+"property")){
			this.setProperty(json.getString("restriction"+LD4SConstants.JSON_SEPARATOR+"on"+
					LD4SConstants.JSON_SEPARATOR+"property"));
		}
		if (json.has("restriction"+LD4SConstants.JSON_SEPARATOR+"amount"
				+LD4SConstants.JSON_SEPARATOR+"type")){
			this.setAmount(json.getString("restriction"+LD4SConstants.JSON_SEPARATOR+"amount"
					+LD4SConstants.JSON_SEPARATOR+"type"));
		}
		if (json.has("restriction"+LD4SConstants.JSON_SEPARATOR+"amount"
				+LD4SConstants.JSON_SEPARATOR+"value")){
			try{
				this.setDataValue(json.getString("restriction"+LD4SConstants.JSON_SEPARATOR
						+"amount"+LD4SConstants.JSON_SEPARATOR+"value"));
			}catch (JSONException e){
				System.out.println("INFO: The restriction amount value submitted " +
						"is not a data value.");
				this.setClassValue(json.getJSONObject("restriction"+LD4SConstants.JSON_SEPARATOR
						+"amount"+LD4SConstants.JSON_SEPARATOR+"value"));
			}
		}
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Amount getAmount() {
		return amount;
	}

	public void setAmount(Amount amount) {
		this.amount = amount;
	}

	public void setAmount(String amount) {
		try{
			setAmount(Amount.valueOf(amount));
		}catch (IllegalArgumentException e){
			System.err.println("The Restriction Amount submitted ("+amount+")" +
					"does not match any pf the supported ones: "+Amount.values());
		}
	}

	public OntoClassType getClassValue() {
		return classValue;
	}

	public void setClassValue(OntoClassType classValue) {
		this.classValue = classValue;
	}
	
	public void setClassValue(JSONObject obj) throws JSONException {
		setClassValue(new OntoClassType(obj));
	}

	public String getDataValue() {
		return dataValue;
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}


}
