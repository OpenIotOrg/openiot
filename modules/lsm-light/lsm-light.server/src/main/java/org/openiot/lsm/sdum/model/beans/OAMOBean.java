package org.openiot.lsm.sdum.model.beans;

import java.util.ArrayList;

import org.openiot.lsm.sdum.model.beans.OSMOBean;

public class OAMOBean 
{
	private String id;	
	private String description;
	private String graphMeta;
	private String name;
	private ArrayList<OSMOBean> osmoBeanList;
	
	public OAMOBean(){		
	}	
	public OAMOBean(String id) {		
		this.id = id;		
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGraphMeta() {
		return graphMeta;
	}
	public void setGraphMeta(String graphMeta) {
		this.graphMeta = graphMeta;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<OSMOBean> getOsmoBeanList() {
		if (osmoBeanList == null) {
			osmoBeanList = new ArrayList<OSMOBean>();
        }
		return osmoBeanList;
	}

}
