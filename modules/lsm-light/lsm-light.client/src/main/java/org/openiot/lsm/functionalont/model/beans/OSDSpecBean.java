package org.openiot.lsm.functionalont.model.beans;

import java.util.ArrayList;


public class OSDSpecBean implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
