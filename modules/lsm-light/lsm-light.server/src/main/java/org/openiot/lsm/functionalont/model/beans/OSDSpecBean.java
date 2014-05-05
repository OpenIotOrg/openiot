package org.openiot.lsm.functionalont.model.beans;

import java.util.ArrayList;

import org.openiot.lsm.functionalont.model.beans.OAMOBean;
import org.openiot.lsm.functionalont.model.beans.UserBean;


public class OSDSpecBean 
{
	private String id;
	private UserBean userBean;
	private ArrayList<OAMOBean> oamoBeanList;
	
	public OSDSpecBean() {
	}	
	public OSDSpecBean(String id) {
		this.id = id;
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public ArrayList<OAMOBean> getOamoBeanList() {
		if (oamoBeanList == null) {
			oamoBeanList = new ArrayList<OAMOBean>();
        }
		return oamoBeanList;
	}
	
}
