package org.openiot.ui.sensorschema.test;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean
@SessionScoped
public class HelloBean {

	private static final long serialVersionUID = 1L;
	 
	private String name;
 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
