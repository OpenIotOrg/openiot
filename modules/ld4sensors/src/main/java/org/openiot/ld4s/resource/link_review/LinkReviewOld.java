package eu.spitfire_project.ld4s.resource.link_review;

import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntClass;

import eu.spitfire_project.ld4s.lod_cloud.Context;
import eu.spitfire_project.ld4s.lod_cloud.Person;
import eu.spitfire_project.ld4s.resource.LD4SDataResource;
import eu.spitfire_project.ld4s.resource.LD4SObject;
import eu.spitfire_project.ld4s.vocabulary.RevVocab;
import eu.spitfire_project.ld4s.vocabulary.SptVocab;



public class LinkReviewOld extends LD4SObject{
	private double vote = 0;
	private String comment = null;
	private String data_link = null;
	
	public LinkReviewOld(JSONObject json, String localhost) throws Exception {
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

	public LinkReviewOld(Person author, double vote, String comment, String datetime){
		super(datetime, null, null, null);
		
		this.vote = vote;
		this.comment= comment;
	}
	public LinkReviewOld(){
		super(null, null, null, null);
		new LinkReviewOld(null, 0.0, null, null);
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
	public String getRemote_uri() {
		return this.remote_uri;
	}

	@Override
	public void setRemote_uri(String resourceHost) {
		this.remote_uri = resourceHost;		
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
