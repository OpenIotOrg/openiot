package org.openiot.ld4s.resource.link_review;

import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context;
import org.openiot.ld4s.lod_cloud.Person;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.LD4SObject;
import org.openiot.ld4s.vocabulary.RevVocab;
import org.openiot.ld4s.vocabulary.SptVocab;

import com.hp.hpl.jena.ontology.OntClass;



public class LinkReview extends LD4SObject{
	private double vote = 0;
	private String comment = null;
	private String data_link = null;
	
	public LinkReview(JSONObject json, String localhost) throws Exception {
		super(json);
		if (json.has("vote")){
			this.setVote(Double.valueOf(LD4SDataResource.removeBrackets(json.getString("vote"))));
		}
		if (json.has("link")){
			this.setLink(LD4SDataResource.removeBrackets(
					json.getString("link")));
		}
		if (json.has("comment")){
			this.setComment(LD4SDataResource.removeBrackets(json.getString("comment")));
		}	}

	public LinkReview(Person author, double vote, String comment, String datetime){
		super(datetime, null, null, null);
		
		this.vote = vote;
		this.comment= comment;
	}
	public LinkReview(){
		super(null, null, null, null);
		new LinkReview(null, 0.0, null, null);
	}
	public void setVote(double vote) {
		this.vote = vote;
	}
	public double getVote() {
		return vote;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getComment() {
		return comment;
	}
	public void setLink(String link) {
		this.setData_link(link);
	}
	public String getLink() {
		return getData_link();
	}

	@Override
	protected void initDefaultType() {
		this.defaultType = SptVocab.LINKREVIEW;
	}

	@Override
	protected void initAcceptedTypes() {
		this.acceptedTypes = new OntClass[]{RevVocab.FEEDBACK};
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

	public void setData_link(String data_link) {
		this.data_link = data_link;
	}

	public String getData_link() {
		return data_link;
	}
	
}
