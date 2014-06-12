package org.openiot.ld4s.resource.platform;

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
public class Platform extends LD4SObject  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;
	
	/** Status Page URI. */
	private String statusPage = null;

	/** Feed URIs. */
	private String[] feeds = null;
	
	/** Base host name. */
	private String base_name = null;
	
	
	/** Temporal Platform Properties IDs (same base name than the main resource). */
	private String[] tpproperties = null;



	public Platform(String host, String[] tpproperties, String status_page,
			String[] feeds, String bn, String criteria, String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range, locations);
		this.setResource_id(host);
		this.setTpproperties(tpproperties);
		this.setStatus(status_page);
		this.setBase_name(bn);
		this.setLink_criteria(criteria, localhost);
	}

	public Platform(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("base"+LD4SConstants.JSON_SEPARATOR+"name")){
			this.setBase_name(LD4SDataResource.removeBrackets(
					json.getString("base"+LD4SConstants.JSON_SEPARATOR+"name")));
		}
		if (json.has("status"+LD4SConstants.JSON_SEPARATOR+"page")){
			this.setStatus(LD4SDataResource.removeBrackets(
					json.getString("status"+LD4SConstants.JSON_SEPARATOR+"page")));
		}
		if (json.has("feeds")){
			this.setFeeds(json.getJSONArray("feeds"));
		}
		if (json.has("tpproperties")){
			this.setTpproperties(json.getJSONArray("tpproperties"));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
	}

	

	public Platform (Form form, String localhost) throws Exception {
		super(form);
		this.setTpproperties(form.getValuesArray("tpproperties"));
		this.setResource_id(form.getFirstValue("uri")); 
		this.setBase_name(
				form.getFirstValue("base"+LD4SConstants.JSON_SEPARATOR+"name"));
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

	
	public void setBase_name(String base_name) {
		this.base_name = base_name;
	}

	public String getBase_name() {
		return base_name;
	}

	public void setFeeds(String[] feeds) {
		this.feeds = feeds;
	}
	
	public void setFeeds(JSONArray objarr) throws JSONException {
		objarr = objarr.getJSONArray(0);
		String[] feeds = null;
		if (objarr != null){
			feeds = new String[objarr.length()];
			for (int i=0; i<objarr.length() ;i++){
				feeds[i] = objarr.getString(i);
			}
		}
		setFeeds(feeds);
	}

	public String[] getFeeds() {
		return feeds;
	}

	public void setStatus(String status) {
		this.statusPage = status;
	}

	public String getStatus() {
		return statusPage;
	}

	public void setTpproperties(String[] tpproperties) {
		this.tpproperties = tpproperties;
	}

	public void setTpproperties(JSONArray objarr) throws JSONException {
		objarr = objarr.getJSONArray(0);
		String[] tpproperties = null;
		if (objarr != null){
			tpproperties = new String[objarr.length()];
			for (int i=0; i<objarr.length() ;i++){
				tpproperties[i] = objarr.getString(i);
			}
			setTpproperties(tpproperties);
		}
	}
	
	public String[] getTpproperties() {
		return tpproperties;
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
}
