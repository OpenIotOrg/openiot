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



	/** Temporal Sensor Properties IDs (same base name than the main resource). */
	private String[] observed_properties = null;

/** Original Source (in case the sensor data and metadata come from a third party provider/api). */
	private String source = null;



	public Device(String host, String[] values, String uom,
			String op, String bn, String bovn, String criteria, String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setResource_id(host);
		this.setLink_criteria(criteria, localhost);
	}

	public Device(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("source")){
			this.setSource(LD4SDataResource.removeBrackets(
					json.getString("source")));
		}
		if (json.has("observed"+LD4SConstants.JSON_SEPARATOR+"properties")){
			this.setObserved_properties(json.getJSONArray("observed"+LD4SConstants.JSON_SEPARATOR+"properties"));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
		
	}

	

	public Device (Form form, String localhost) throws Exception {
		super(form);
		this.setObserved_properties(form.getValuesArray("tsproperties"));
		this.setResource_id(form.getFirstValue("uri")); 
		this.setType(
				form.getFirstValue("type"));
		this.setLink_criteria(
				form.getFirstValue("context"), localhost);
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
		if (getResource_id() == null
				||
				(localUri.contains(getResource_id())
						|| getResource_id().contains(localUri))){
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

	public void setObserved_properties(String[] observedProperties)
	{
		this.observed_properties = observedProperties;
	}
	
	public void setObserved_properties(JSONArray jvalues) throws JSONException {
		String[] values = new String[jvalues.length()];
		for (int i=0; i< jvalues.length(); i++){
			values[i] = jvalues.get(i).toString();
		}
		setObserved_properties(values);
	}

	public String[] getObservedProperties() {
		return observed_properties;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	
}
