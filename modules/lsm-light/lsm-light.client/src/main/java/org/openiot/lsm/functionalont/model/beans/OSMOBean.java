package org.openiot.lsm.functionalont.model.beans;

import java.util.ArrayList;


public class OSMOBean implements java.io.Serializable
{

	private String id;
	private String description;
	private String name;
	private QueryControlsBean queryControlsBean;
	private ReqPresentationBean reqPresentationBean;	
	private ArrayList<QueryRequestBean> queryRequestBeanList;
	private ArrayList<DynamicAttrMaxValueBean> dynamicAttrMaxValueBeanList;
	private OAMOBean oamoBean;
	
	//constructor
	public OSMOBean() {		
	}
	public OSMOBean(String id) {
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public QueryControlsBean getQueryControlsBean() {
		return queryControlsBean;
	}
	public void setQueryControlsBean(QueryControlsBean queryControlsBean) {
		this.queryControlsBean = queryControlsBean;

	}
	public ReqPresentationBean getReqPresentationBean() {
		return reqPresentationBean;
	}
	public void setReqPresentationBean(ReqPresentationBean reqPresentationBean) {
		this.reqPresentationBean = reqPresentationBean;
	}
	
	
	public ArrayList<QueryRequestBean> getQueryRequestBean() {
		if (queryRequestBeanList == null) {
			queryRequestBeanList = new ArrayList<QueryRequestBean>();
        }
		return queryRequestBeanList;
	}
		
	public ArrayList<DynamicAttrMaxValueBean> getDynamicAttrMaxValueBeanList() {
		if (dynamicAttrMaxValueBeanList == null) {
			dynamicAttrMaxValueBeanList = new ArrayList<DynamicAttrMaxValueBean>();
        }
		return dynamicAttrMaxValueBeanList;
	}
	
	
	public OAMOBean getOamoBean() {
		return oamoBean;
	}
	public void setOamoBean(OAMOBean oamoBean) {
		this.oamoBean = oamoBean;
	}
	
}
