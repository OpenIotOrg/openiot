package org.openiot.lsm.functionalont.model.beans;

import java.util.ArrayList;



public class WidgetBean implements java.io.Serializable
{
	private String id;
	private ReqPresentationBean reqPresentationBean;
	private ArrayList<PresentationAttrBean> presentationAttrBeanList;
	
	public WidgetBean()	{
	}	
	public WidgetBean(String id) {
		this.id = id;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public ArrayList<PresentationAttrBean> getPresentationAttrBeanList() {
		if (presentationAttrBeanList == null) {
			presentationAttrBeanList = new ArrayList<PresentationAttrBean>();
        }
		return presentationAttrBeanList;
	}
	
	public ReqPresentationBean getReqPresentationBean() {
		return reqPresentationBean;
	}
	public void setReqPresentationBean(ReqPresentationBean reqPresentationBean) {
		this.reqPresentationBean = reqPresentationBean;
	}
	
	public String toStringIdPreAttr() {
		
		StringBuffer widget = new StringBuffer();
		widget.append("widget:");
		widget.append("{");
		widget.append("id:"+getId());widget.append(",");
		
			widget.append("preAttrs:");
			widget.append("[");
			for(PresentationAttrBean preAttrr : presentationAttrBeanList) {
				widget.append(preAttrr.toStringValName());widget.append(",");
			}
			
			int lastIdx = widget.lastIndexOf(",");
			widget.deleteCharAt(lastIdx);
			
			widget.append("]");
			
		widget.append("}");
		
		return widget.toString();
	}
}
