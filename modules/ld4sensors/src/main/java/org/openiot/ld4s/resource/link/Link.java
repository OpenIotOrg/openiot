package org.openiot.ld4s.resource.link;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.LD4SObject;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.data.Form;

import com.hp.hpl.jena.ontology.OntClass;

public class Link extends LD4SObject{

	private String linkUri = null;
	private String from = null;
	private String to = null;
	private String datetime = null;
	
	/**feedback uris.*/
	private LinkedList<String> feedbacks = null; 
	private Context context = null;

	private String title = null;
	private double bytes = -1;


	public Link() throws Exception{
		super(null, null, null, null);
		new Link(null, null, null, null, null, 0.0, null, null, null, null, null);
	}
	
	public Link(String from, String to, String author, LinkedList<String> feedbacks,
			String title, double bytes, Context context, String base_datetime, String start_range, String end_range, 
			String[] locations) 
	throws Exception{
		super(base_datetime, start_range, end_range,locations);
		this.from = from;
		this.to = to;
		this.feedbacks = feedbacks;
//		for (String rev : this.feedbacks){
//			rev.setLink(this.linkUri);
//		}
		this.context = context;
		this.setTitle(title);
		this.bytes = bytes;
	}

	public Link(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("rank")){
			this.context.setConfidence(
					Double.valueOf(LD4SDataResource.removeBrackets(json.getString("rank"))));
		}
		if (json.has("explicit"+LD4SConstants.JSON_SEPARATOR+"content"+LD4SConstants.JSON_SEPARATOR+"length")){
			this.setBytes(Double.valueOf(LD4SDataResource.removeBrackets(
					json.getString("explicit"+LD4SConstants.JSON_SEPARATOR+"content"+LD4SConstants.JSON_SEPARATOR+"length"))));
		}
		if (json.has("updated")){
			this.setDatetime(LD4SDataResource.removeBrackets(json.getString("updated")));
		}
		if (json.has("title")){
			this.setTitle(LD4SDataResource.removeBrackets(
					json.getString("title")));
		}
		if (json.has("feedbacks")){
			this.setReviews(json.getJSONArray("feedbacks"));
		}  		
		if (json.has("from")){
			setFrom(LD4SDataResource.removeBrackets(json.getString("from")));
		}

		if (json.has("to")){
			setTo(LD4SDataResource.removeBrackets(json.getString("to")));

		}		
		if (json.has("context")){
			this.setLink_criteria(json.getString("context"), localhost);
		}
	}



	private void setReviews(JSONArray arr) throws JSONException {
		this.feedbacks = new LinkedList<String>();
		if ((arr=arr.getJSONArray(0)) != null){
			for (int i=0; i<arr.length() ;i++){
				this.feedbacks.add(arr.getString(i));
			}
		}
	}

//	private void setReviews(String[] arr){
//		this.feedbacks = new LinkedList<String>();
//		String[] obj = null;
//		for (int i=0; i<arr.length ;i++){
//			obj = arr[i].split("_");
//			if (obj.length == 4){
//				lr = new LinkReview();
//				if (obj[0] != null){
//					lr.setAuthor(LD4SDataResource.removeBrackets(obj[0]));
//				}
//				if (obj[1] != null){
//					lr.setComment(LD4SDataResource.removeBrackets(obj[1]));
//				}
//				if (obj[2] != null){
//					lr.setDatetime(LD4SDataResource.removeBrackets(obj[2]));
//				}
//				lr.setLink(this.linkUri);
//				if (obj[3] != null){
//					lr.setVote(Double.valueOf(LD4SDataResource.removeBrackets(obj[3])));
//				}
//			}
//		}
//	}

	public Link(Form form, String localhost) {
		super(form);
		if (this.context == null){
			this.context = new Context(localhost);
		}
		if (form.getFirstValue("rank") != null){
			this.context.setConfidence(Double.valueOf(form.getFirstValue("rank")));
		}
		if (form.getFirstValue("explicit"+LD4SConstants.JSON_SEPARATOR+"content"+LD4SConstants.JSON_SEPARATOR+"length") != null){
			this.setBytes(Double.valueOf(form.getFirstValue("explicit"+LD4SConstants.JSON_SEPARATOR+"content"+LD4SConstants.JSON_SEPARATOR+"length")));
		}
		setDatetime(form.getFirstValue("updated"));
		setTitle(form.getFirstValue("title"));
		setFrom(form.getFirstValue("from"));
		setTo(form.getFirstValue("to"));
//		setReviews(form.getValuesArray("reviews"));
	}


	public void setFrom(String from) {
		if (from != null && !from.endsWith("/")){
			from += "/";
		}
		this.from = from;
	}
	public String getFrom() {
		return from;
	}
	public void setTo(String to) {
		if (to != null && !to.endsWith("/")){
			to += "/";
		}
		this.to = to;
	}
	public String getTo() {
		return to;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setFeedbacks(LinkedList<String> feedbacks) {
		this.feedbacks = feedbacks;
	}
	public LinkedList<String> getFeedbacks() {
		return feedbacks;
	}
	//	public void setContext(Context context) {
	//		this.context = context;
	//	}
	//	public Context getContext() {
	//		return context;
	//	}

	public void setConfidence(double confidence) {
		this.context.setConfidence(confidence);
	}

	public double getConfidence() {
		if (this.context == null){
			return -1;
		}
		return this.context.getConfidence();
	}
	public void setDomains(Domain[] domains) {
		this.context.setDomains(domains);
	}
	public void setDomains(String[] domains) {
		this.context.setDomains(domains);
	}
	public void setDomains(String domains) {
		this.context.setDomains(domains);
	}
	public Domain[] getDomain() {
		if (this.context == null){
			return null;
		}
		return this.context.getDomains();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setBytes(double bytes) {
		this.bytes = bytes;
	}

	public double getBytes() {
		return bytes;
	}

	public void setLinkUri(String linkUri) {
		this.linkUri = linkUri;
	}

	public String getLinkUri() {
		return linkUri;
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SptVocab.DATALINKING;
	}

	@Override
	protected void initAcceptedTypes() {
		this.acceptedTypes = new OntClass[]{};
	}

	@Override
	public String getResource_id() {
		return this.resource_id;
	}

	@Override
	public void setResource_id(String resourceHost) {
		this.resource_id = resourceHost;		
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
	public Context getLink_criteria() {
		return this.link_criteria;
	}

	@Override
	public void setLink_criteria(String link_criteria, String localhost) throws Exception {
		this.link_criteria = new Context(link_criteria, localhost);
	}
}
