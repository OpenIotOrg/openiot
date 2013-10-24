package org.openiot.ld4s.resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.lod_cloud.Person;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.restlet.data.Form;

import com.hp.hpl.jena.ontology.OntClass;


public abstract class LD4SObject{
	
	protected OntClass[] acceptedTypes;
	
	protected OntClass defaultType;

	/** Eventual remote resource hosting server URI. */
	protected String remote_uri = null;

	/** Flag for the resource being store remotely or not. */
	protected boolean stored_remotely = false;

	/** Location: keywords from Context.SpaceRelations associated with 
	 * either coordinates divided by commas (rows) or location names(columns).*/
	private String[][] space = null;

	/** Milliseconds shift from the base time as a starting point of a time range. */
	protected String start_range = null;

	/** Milliseconds shift from the base time as an ending point of a time range. */
	protected String end_range = null;

	/** Base DateTime from which to calculate shifts. */
	protected String base_datetime = null;

	/** User-defined criteria for adding external links. */
	protected Context link_criteria = null;
	
	/** Historical Data Archive URI. */
	private String archive = null;
	
	/** Name of a Preferred class type. */
	private String pref_type = null;
	
	/** Person in charge of the data generation. */
	private Person author = null;
	
	/** Description. */
	private String description = null;

	/** DateTime as a resource creation time point. */
	private String resource_time = null;
	
	/** Location name. */
	private String location_name = null;
	
	/** Location coordinates. */
	private String[] coords = null;
	
	/** Location coordinates and corresponding predicate. */
	private String[] pred_coords = null;
	
	/** Milliseconds shift from the base time as a reading collection time point. */
	private String time = null;
	
	private String conTime = null;
	private String conLocation = null;
	private String conDate = null;
	private String conCompany = null;
	private String conCountry = null;

	protected abstract void initDefaultType(); 
	protected abstract void initAcceptedTypes(); 
	public abstract String getRemote_uri();
	public abstract void setRemote_uri(String resourceHost);
	public abstract void setStoredRemotely(boolean storedRemotely);
	public abstract boolean isStoredRemotely();
	public abstract boolean isStoredRemotely(String localUri);
	public abstract void setLink_criteria(Context link_criteria);
	public abstract Context getLink_criteria();
	public abstract void setLink_criteria(String link_criteria, String localhost)throws Exception ;

	public LD4SObject(JSONObject json) throws JSONException{
		if (json != null){
			if (json.has("con"+LD4SConstants.JSON_SEPARATOR+"country")){
				this.setConCountry(LD4SDataResource.removeBrackets(
						json.getString("con"+LD4SConstants.JSON_SEPARATOR+"country")));
			}
			if (json.has("con"+LD4SConstants.JSON_SEPARATOR+"company")){
				this.setConCompany(LD4SDataResource.removeBrackets(
						json.getString("con"+LD4SConstants.JSON_SEPARATOR+"company")));
			}
			if (json.has("con"+LD4SConstants.JSON_SEPARATOR+"location")){
				this.setConLocation(LD4SDataResource.removeBrackets(
						json.getString("con"+LD4SConstants.JSON_SEPARATOR+"location")));
			}
			if (json.has("con"+LD4SConstants.JSON_SEPARATOR+"time")){
				this.setConTime(LD4SDataResource.removeBrackets(
						json.getString("con"+LD4SConstants.JSON_SEPARATOR+"time")));
			}
			if (json.has("con"+LD4SConstants.JSON_SEPARATOR+"date")){
				this.setConDate(LD4SDataResource.removeBrackets(
						json.getString("con"+LD4SConstants.JSON_SEPARATOR+"date")));
			}
			if (json.has("base"+LD4SConstants.JSON_SEPARATOR+"datetime")){
				this.setBase_datetime(LD4SDataResource.removeBrackets(
						json.getString("base"+LD4SConstants.JSON_SEPARATOR+"datetime")));
			}
			if (json.has("resource"+LD4SConstants.JSON_SEPARATOR+"datetime")){
				this.setResource_time(LD4SDataResource.removeBrackets(
						json.getString("resource"+LD4SConstants.JSON_SEPARATOR+"datetime")));
			}
			if (json.has("start"+LD4SConstants.JSON_SEPARATOR+"range")){
				this.setStart_range(LD4SDataResource.removeBrackets(
						json.getString("start"+LD4SConstants.JSON_SEPARATOR+"range")));
			}
			if (json.has("end"+LD4SConstants.JSON_SEPARATOR+"range")){
				this.setEnd_range(LD4SDataResource.removeBrackets(
						json.getString("end"+LD4SConstants.JSON_SEPARATOR+"range")));
			}
			if (json.has("archive")){
				this.setArchive(LD4SDataResource.removeBrackets(
						json.getString("archive")));
			}
			if (json.has("type")){
				this.setType(LD4SDataResource.removeBrackets(
						json.getString("type")));
			}
			if (json.has("author")){
				this.setAuthor(json.getJSONArray("author"));
			}
			if (json.has("description")){
				this.setDescription(LD4SDataResource.removeBrackets(
						json.getString("description")));
			}
			if (json.has("uri")){
				this.setRemote_uri(LD4SDataResource.removeBrackets(
						json.getString("uri")));
			}
			if (json.has("location"+LD4SConstants.JSON_SEPARATOR+"name")){
				this.setLocation_name(LD4SDataResource.removeBrackets(
						json.getString("location"+LD4SConstants.JSON_SEPARATOR+"name")));
			}
			if (json.has("location"+LD4SConstants.JSON_SEPARATOR+"predicate"+LD4SConstants.JSON_SEPARATOR+"coords")){
				this.setLocation_coords(LD4SDataResource.removeBrackets(
						json.getString("location"+LD4SConstants.JSON_SEPARATOR+"predicate"+LD4SConstants.JSON_SEPARATOR+"coords")));
			}
			if (json.has("location"+LD4SConstants.JSON_SEPARATOR+"coords")){
				this.setCoords(LD4SDataResource.removeBrackets(
						json.getString("location"+LD4SConstants.JSON_SEPARATOR+"coords")));
			}
			//spaces relation # <lat,long | name> 
			if (json.has("locations")){
				this.setSpace(json.getJSONArray("locations"));
			}
			initAcceptedTypes();
			initDefaultType();
		}
	}
	
	protected void setType(String type) {
		this.pref_type = type;
	}

	public String getType() {
		return pref_type;
	}

	public LD4SObject(Form form){
		if (form != null){
			this.setBase_datetime(
					form.getFirstValue("base_datetime"));
			this.setStart_range(
					form.getFirstValue("start_range"));
			this.setEnd_range(
					form.getFirstValue("end_range"));
			this.setArchive(
					form.getFirstValue("archive"));
			this.setDescription(
					form.getFirstValue("description"));
			this.setRemote_uri(
					form.getFirstValue("uri"));
			this.setType(
					form.getFirstValue("type"));
			//spaces relation # <lat,long | name> 
			this.setSpace(
					form.getValuesArray("locations"));
			initAcceptedTypes();
			initDefaultType();
		}
	}

	public LD4SObject(String base_datetime, String start_range, String end_range,
			String[] locations){
		this.setBase_datetime(base_datetime);
		this.setStart_range(start_range);
		this.setEnd_range(end_range);
		//spaces relation # <lat,long | name> 
		this.setSpace(locations);
		initAcceptedTypes();
		initDefaultType();
	}

	public void setEnd_range(String end_range) {
		this.end_range = end_range;
	}

	public String getEnd_range() {
		return end_range;
	}

	public void setStart_range(String start_range) {
		this.start_range = start_range;
	}

	public String getStart_range() {
		return start_range;
	}

	public void setBase_datetime(String base_datetime) {
		this.base_datetime = base_datetime;
	}

	public String getBase_datetime() {
		return base_datetime;
	}

	public void setSpace(String[][] space) {
		this.space = space;
	}

	
	/**
	 * 
	 * @param space array of strings of type: "Context.SpaceRelation # <lat,long | name>" 
	 */
	public void setSpace(String[] space){
		if (space == null){
			return;
		}
		//allow no space relation to be specified
		String[][] sparr = new String[Context.spaceRelations.length+1][space.length];
		String rel = null, elem = null;
		String[] splitarr = null;
		int row = -1, col = -1;
		for (col=0; col<space.length; col++){
			elem = space[col].trim();
			if (elem != null){
				splitarr = elem.split("#");
				if (splitarr.length == 2){
					rel = splitarr[0].trim();
					for (int b=0; b<Context.spaceRelations.length&&row==-1 ;b++){
						if (rel.compareToIgnoreCase(Context.spaceRelations[b])==0){
							row = b;
						}
					}
					//allow no space relation to be specified
					if (row == -1){
						row = Context.spaceRelations.length;
					}
					sparr[row][col] = splitarr[1].trim(); //either <lat_long> or <name>
				}
				row=-1;
			}
		}
		setSpace(sparr);
	}

	public void setResource_time(String resource_time) {
		this.resource_time = resource_time;
	}

	public String getResource_time() {
		return resource_time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}
	
	public void setSpace(JSONArray jspace) throws JSONException{
		if (jspace == null || jspace.get(0) == null){
			return;
		}
		String jstr = LD4SDataResource.removeBrackets(jspace.getString(0));
		String[] jarrcontent = jstr.split(",");
		for (int i=0; i<jarrcontent.length ;i++){
			jarrcontent[i] = LD4SDataResource.removeBrackets(jarrcontent[i]);
		}
		setSpace(jarrcontent);
	}

	public String[][] getSpace() {
		return space;
	}
	public void setArchive(String archive) {
		this.archive = archive;
	}
	public String getArchive() {
		return archive;
	}
	public void setAuthor(Person author) {
		this.author = author;
	}
	
	
	
	public void setAuthor(JSONArray objarr) throws JSONException {
		if (objarr == null || objarr.length() == 0){
			return;
		}
		Person author = new Person();
		JSONObject obj = objarr.getJSONObject(0);
		if (obj.has("firstname")){
			author.setFirstname(LD4SDataResource.removeBrackets(
					obj.getString("firstname")));
		}
		if (obj.has("surname")){
			author.setSurname(LD4SDataResource.removeBrackets(
					obj.getString("surname")));
		}
		if (obj.has("email")){
			author.setEmail(LD4SDataResource.removeBrackets(
					obj.getString("email")));
		}
		if (obj.has("weblog")){
			author.setWeblog(LD4SDataResource.removeBrackets(
					obj.getString("weblog")));
		}
		if (obj.has("homepage")){
			author.setHomepage(LD4SDataResource.removeBrackets(
					obj.getString("homepage")));
		}
		if (obj.has("nickname")){
			author.setNickname(LD4SDataResource.removeBrackets(
					obj.getString("nickname")));
		}
		setAuthor(author);
	}
	
	public Person getAuthor() {
		return author;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setAcceptedTypes(OntClass[] acceptedTypes) {
		this.acceptedTypes = acceptedTypes;
	}
	public OntClass[] getAcceptedTypes() {
		return acceptedTypes;
	}
	public void setDefaultType(OntClass defaultType) {
		this.defaultType = defaultType;
	}
	public OntClass getDefaultType() {
		return defaultType;
	}
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}
	public String getLocation_name() {
		return location_name;
	}
	public void setLocation_coords(String[] location_coords) {
		this.pred_coords = location_coords;
	}
	public void setLocation_coords(String location_coords) {
		if (location_coords != null){
			this.pred_coords = location_coords.split("_");
			//find the location name 
		}
	}
	public String[] getLocation_coords() {
		return pred_coords;
	}
	public void setCoords(String coords) {
		if (coords != null){
			setCoords(coords.split("_"));
		}
	}
	public void setCoords(String[] coords) {
		this.coords = coords;
	}
	public String[] getCoords() {
		return coords;
	}
	public String getConTime() {
		return conTime;
	}
	public void setConTime(String conTime) {
		this.conTime = conTime;
	}
	public String getConLocation() {
		return conLocation;
	}
	public void setConLocation(String conLocation) {
		this.conLocation = conLocation;
	}
	public String getConDate() {
		return conDate;
	}
	public void setConDate(String conDate) {
		this.conDate = conDate;
	}
	public String getConCompany() {
		return conCompany;
	}
	public void setConCompany(String conCompany) {
		this.conCompany = conCompany;
	}
	public String getConCountry() {
		return conCountry;
	}
	public void setConCountry(String conCountry) {
		this.conCountry = conCountry;
	}

}
