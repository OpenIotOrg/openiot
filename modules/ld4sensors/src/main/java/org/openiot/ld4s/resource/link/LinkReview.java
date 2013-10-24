package org.openiot.ld4s.resource.link;



public class LinkReview {
	private String author = null;
	private double vote = 0;
	private String comment = null;
	private String datetime = null;
	private String linkuri = null;
	
	public LinkReview(String author, double vote, String comment, String datetime){
		this.author = author;
		this.vote = vote;
		this.comment= comment;
		this.datetime = datetime;
	}
	public LinkReview(){
		new LinkReview(null, 0.0, null, null);
	}
	
	
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthor() {
		return author;
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
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setLink(String link) {
		this.linkuri = link;
	}
	public String getLink() {
		return linkuri;
	}
	
}
