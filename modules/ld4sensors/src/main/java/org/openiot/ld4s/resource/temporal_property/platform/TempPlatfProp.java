package org.openiot.ld4s.resource.temporal_property.platform;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.lod_cloud.Person;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.LD4SObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.data.Form;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * Temporal Platform Property resource.
 * This resource is usually stored on the Sensor and is part of the extended annotation
 * (i.e., richer) to be transmitted rarely.
 * 
<10e2073a01080063p12> a spt-sn:iSensePlatform ;
spt:temporal <ptpInstance112> ;
clf:bn <http://ex1.org> .
<ptpInstance112> a spt: PlatformTemporalProperty ;
ssn:attachedSystem <http://ex.org/10e2073a01080063> ;
spt:ownedBy <http://ex.org/proxy/jamesSmith> ;
spt:wornBy <http://ex.org/proxy/johnDoe> ;
ssn:implements <http://ex.org/proxy/algo112> ;
ssn:hasMeasurementCapabilities <http://ex.org/proxy/capab112> ;
ssn:inDeployment <http://ex.org/proxy/deployment112> ;
spt:tStart "3423532" ;
spt:tEnd "4325235" .

 * @author Myriam Leggieri <iammyr@email.com>
 *
 */
public class TempPlatfProp extends LD4SObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845385924519981423L;


	/** Platform ID. */
	private String platform_id = null;

	/** Agents (who are wearing this platform) URIs. */
	private String[] wornby = null;

	/** Agents Details (necessary to search for Agent URIs). */
	private Person[] person_wornby = null;

	/** Attached systems. */
	private String[] systems = null;

	/** Owners Details (necessary to search for Owners URIs). */
	private Person[] person_owners = null;

	/** Implemented Algorithms. */
	private String[] algorithms = null;
	
	/** Measurement Capabilities URI. */
	private String measurement_capability = null;

	/** Deployment URI. */
	private String deployment = null;


	public TempPlatfProp(String host, String[] wornby, String deployment, 
			String platform,
			String[] algs, String[] owners, String[] systems, String criteria, 
			String localhost,
			String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.setRemote_uri(host);
		this.setPlatform_id(platform);
		this.setWornby(wornby);
		this.setDeployment(deployment);
		this.setAlgorithms(algs);
		this.setOwners(owners);
		this.setSystems(systems);
		this.setLink_criteria(criteria, localhost);
	}

	public TempPlatfProp(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("uri")){
			this.setRemote_uri(LD4SDataResource.removeBrackets(
					json.getString("uri")));
		}
		if (json.has("platform"+LD4SConstants.JSON_SEPARATOR+"id")){
			this.setPlatform_id(LD4SDataResource.removeBrackets(
					json.getString("platform"+LD4SConstants.JSON_SEPARATOR+"id")));
		}
		if (json.has("deployment")){
			this.setDeployment(LD4SDataResource.removeBrackets(
					json.getString("deployment")));
		}
		if (json.has("worn"+LD4SConstants.JSON_SEPARATOR+"by")){
			this.setWornby(json.getJSONArray("worn"+LD4SConstants.JSON_SEPARATOR+"by"));
		}
		if (json.has("owners")){
			this.setOwners(json.getJSONArray("owners"));
		}
		if (json.has("systems")){
			this.setSystems(json.getJSONArray("systems"));
		}
		if (json.has("algorithms")){
			this.setAlgorithms(json.getJSONArray("algorithms"));
		}
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
	}

	public void setAlgorithms(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null || (jsonArray=jsonArray.getJSONArray(0)) == null){
			return;
		}
		this.algorithms = new String[jsonArray.length()];
		for (int i=0; i<jsonArray.length() ;i++){
			this.algorithms[i] = jsonArray.getString(i);
		}

	}

	public void setSystems(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null || (jsonArray=jsonArray.getJSONArray(0)) == null){
			return;
		}
		this.systems = new String[jsonArray.length()];
		for (int i=0; i<jsonArray.length() ;i++){
			this.systems[i] = jsonArray.getString(i);
		}
	}

	public void setOwners(JSONArray j) throws JSONException {
		if (j == null){
			return;
		}
		JSONArray jsonArray = j.getJSONArray(0);
		Person owner = null;
		JSONObject jsonobj = null;
		for (int i=0; i<jsonArray.length() ;i++){
				owner = new Person();
				jsonobj = jsonArray.getJSONObject(i);
				if (jsonobj.has("uri")){
					owner.setUri(jsonobj.getString("uri"));
				}
				if (jsonobj.has("email")){
					owner.setEmail(jsonobj.getString("email"));
				}
				if (jsonobj.has("firstname")){
					owner.setFirstname(jsonobj.getString("firstname"));
				}
				if (jsonobj.has("surname")){
					owner.setSurname(jsonobj.getString("surname"));
				}
				if (jsonobj.has("nickname")){
					owner.setNickname(jsonobj.getString("nickname"));
				}
				if (jsonobj.has("homepage")){
					owner.setHomepage(jsonobj.getString("homepage"));
				}
				if (jsonobj.has("weblog")){
					owner.setWeblog(jsonobj.getString("weblog"));
				}
				if (this.person_owners == null){
					this.person_owners = new Person[jsonArray.length()];
				}
				this.person_owners[i] = owner;
			}

			
		}

	public void setWornby(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null || (jsonArray=jsonArray.getJSONArray(0)) == null){
			return;
		}
		String wornby_uri = null;
		Person wornby = null;
		JSONObject jsonobj = null;
		for (int i=0; i<jsonArray.length() ;i++){
			//if the JSONArray is made of strings of URIs
			if (jsonArray.get(i) instanceof String){
				wornby_uri = jsonArray.getString(i);
				if (wornby_uri.startsWith("http://")){
					if (this.wornby == null){
						this.wornby = new String[jsonArray.length()];
					}
					this.wornby[i] = wornby_uri;	
				}
			}// if the JSONArray is made of JSONObjects with wornbys' details
			else if (jsonArray.get(i) instanceof JSONObject){
				wornby = new Person();
				jsonobj = jsonArray.getJSONObject(i);
				if (jsonobj.has("email")){
					wornby.setEmail(jsonobj.getString("email"));
				}
				if (jsonobj.has("firstname")){
					wornby.setFirstname(jsonobj.getString("firstname"));
				}
				if (jsonobj.has("surname")){
					wornby.setSurname(jsonobj.getString("surname"));
				}
				if (jsonobj.has("nickname")){
					wornby.setNickname(jsonobj.getString("nickname"));
				}
				if (jsonobj.has("homepage")){
					wornby.setHomepage(jsonobj.getString("homepage"));
				}
				if (jsonobj.has("weblog")){
					wornby.setWeblog(jsonobj.getString("weblog"));
				}
				if (this.person_wornby == null){
					this.person_wornby = new Person[jsonArray.length()];
				}
				this.person_wornby[i] = wornby;
			}			
		}
	}

	public TempPlatfProp (Form form, String localhost) throws Exception {
		super(form);
		this.setRemote_uri(form.getFirstValue("uri"));
		this.setPlatform_id(
				form.getFirstValue("sensor"+LD4SConstants.JSON_SEPARATOR+"id"));
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

	public void setPlatform_id(String sensor_id) {
		this.platform_id = sensor_id;
	}

	public String getPlatform_id() {
		return platform_id;
	}

	public void setStart_range(String start_range) {
		this.start_range = start_range;
	}

	public String getStart_range() {
		return start_range;
	}

	public void setEnd_range(String end_range) {
		this.end_range = end_range;
	}

	public String getEnd_range() {
		return end_range;
	}

	public void setDeployment(String deployment) {
		this.deployment = deployment;
	}

	public String getDeployment() {
		return deployment;
	}

	public void setAlgorithms(String[] algorithms) {
		this.algorithms = algorithms;
	}

	public String[] getAlgorithms() {
		return algorithms;
	}

	public void setOwners(String[] owners) {
		this.person_owners = new Person[owners.length];
		for (int i=0; i<owners.length ;i++){
			if (owners[i].startsWith("http://")){
				this.person_owners[i] = new Person(null, null, null, null, null, null, owners[i]);
			}else if (owners[i].contains("@")){
				this.person_owners[i] = new Person(null, null, null, owners[i], null, null, null);
			}else {
				this.person_owners[i] = new Person(null, null, owners[i], null, null, null, null);
			}
		}
	}

	public void setSystems(String[] systems) {
		this.systems = systems;
	}

	public String[] getSystems() {
		return systems;
	}

	public void setWornby(String[] wornby) {
		this.wornby = wornby;
	}

	public String[] getWornby() {
		return wornby;
	}

	public void setPerson_owners(Person[] person_owners) {
		this.person_owners = person_owners;
	}

	public Person[] getPerson_owners() {
		return person_owners;
	}

	public void setPerson_wornby(Person[] person_wornby) {
		this.person_wornby = person_wornby;
	}

	public Person[] getPerson_wornby() {
		return person_wornby;
	}

	@Override
	protected void initAcceptedTypes() {
		this.acceptedTypes = new OntClass[]{};
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SptVocab.PLATFORM_TEMPORAL_PROPERTY;
	}

	public void setMeasurement_capability(String measurement_capability) {
		this.measurement_capability = measurement_capability;
	}

	public String getMeasurement_capability() {
		return measurement_capability;
	}
}
