package org.openiot.lsm.functionalont.model.beans;

import java.util.ArrayList;


public class ReqPresentationBean implements java.io.Serializable
{
	
	private String id;
	private ArrayList<WidgetBean> widgetBeanLsit;
	private OSMOBean osmoBean;

	
	public ReqPresentationBean(){
	}
	public ReqPresentationBean(String id) {
		this.id = id;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public ArrayList<WidgetBean> getWidgetBeanLsit() {
		if (widgetBeanLsit == null) {
			widgetBeanLsit = new ArrayList<WidgetBean>();
        }
		return widgetBeanLsit;
	}	
	
	
	public OSMOBean getOsmoBean() {
		return osmoBean;
	}
	public void setOsmoBean(OSMOBean osmoBean) {
		this.osmoBean = osmoBean;
	}
	
	public String toStringWidget() {
		
		StringBuffer reqPre = new StringBuffer();
//		reqPre.append("{");

			reqPre.append("reqPre:");
			reqPre.append("[");
			
			for(WidgetBean widget : widgetBeanLsit) {
				reqPre.append(widget.toStringIdPreAttr());reqPre.append(",");
			}
			int lastIdx = reqPre.lastIndexOf(",");
			reqPre.deleteCharAt(lastIdx);
			
			reqPre.append("]");
			
//		reqPre.append("}");
		
		return reqPre.toString();
	}
}
