package org.openiot.lsm.functionalont.model.beans;

public class DynamicAttrMaxValueBean 
{
	
	private String id;
	private String value;
	private String name;
	
	public DynamicAttrMaxValueBean(){
	}	
	public DynamicAttrMaxValueBean(String id) {
		this.id = id;
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
}
