package org.openiot.lsm.sdum.model.beans;

import java.util.ArrayList;

import org.openiot.lsm.sdum.model.beans.OSDSpecBean;


public class UserBean 
{
	private String id;
	private String name;
	private String email;
	private String description;
	private String passwd;
	private ArrayList<OSDSpecBean> osdSpecBeanList;
	
	public UserBean()	{		
	}

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}


	public ArrayList<OSDSpecBean> getOsdSpecBean() {
		if (osdSpecBeanList == null) {
			osdSpecBeanList = new ArrayList<OSDSpecBean>();
        }
		return osdSpecBeanList;
	}
				
}
