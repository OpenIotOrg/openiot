package org.openiot.lsm.sdum.model.beans;


public class NamedGraphBean 
{
	
	private String id;
	private String namedGraphURI;
	
	//constructor
	public NamedGraphBean()	{		
	}	
	public NamedGraphBean(String id) {
		this.id = id;
	}


	public String getNamedGraphURI() {
		return namedGraphURI;
	}
	public void setNamedGraphURI(String namedGraphURI) {
		this.namedGraphURI = namedGraphURI;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
