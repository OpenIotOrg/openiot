package org.openiot.lsm.functionalont.model.beans;

import java.util.ArrayList;


public class QueryRequestBean implements java.io.Serializable
{
	
	private String id;
	private String query;
	private ArrayList<DefaultGraphBean> defaultGraphBeanList;
	private ArrayList<NamedGraphBean> namedGraphBeanList;
	private OSMOBean osmoBean;
	
	
	public QueryRequestBean() {
	}
	public QueryRequestBean(String id) {
		this.id = id;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	public OSMOBean getOsmoBean() {
		return osmoBean;
	}
	public void setOsmoBean(OSMOBean osmoBean) {
		this.osmoBean = osmoBean;
	}
	
	public ArrayList<DefaultGraphBean> getDefaultGraphBeanList() {
		if (defaultGraphBeanList == null) {
			defaultGraphBeanList = new ArrayList<DefaultGraphBean>();
        }
		return defaultGraphBeanList;
	}
	
	public ArrayList<NamedGraphBean> getNamedGraphBeanList() {
		if (namedGraphBeanList == null) {
			namedGraphBeanList = new ArrayList<NamedGraphBean>();
        }
		return namedGraphBeanList;
	}

	
	public String toStringQueryStr() {
		
		StringBuffer q = new StringBuffer();
		q.append("qreq:");
		q.append("{");

		q.append("queryString:"+getQuery());
			
		q.append("}");
		
		return q.toString();
	}
}
