package org.openiot.lsm.functionalont.model.beans;

import org.openiot.lsm.functionalont.model.beans.WidgetBean;

public class PresentationAttrBean 
{

	private String id;
	private String value;
	private String name;
	private WidgetBean widgetBean;
	
	
	//constructor
	public PresentationAttrBean(){
	}
	public PresentationAttrBean(String id){
		this.id=id;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public WidgetBean getWidgetBean() {
		return widgetBean;
	}
	public void setWidgetBean(WidgetBean widgetBean) {
		this.widgetBean = widgetBean;
	}

	public String toStringValName() {
		StringBuffer preAttr = new StringBuffer();
		preAttr.append("\"preAttr\":");
		preAttr.append("{");
		
		preAttr.append("\"value\":"+getValue());preAttr.append(",");
		preAttr.append("\"name\":"+getName());
		
		preAttr.append("}");
		return preAttr.toString();
	}
}
