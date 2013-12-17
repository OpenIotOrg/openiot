package org.openiot.security.mgmt;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.openiot.lsm.security.oauth.mgmt.User;

@Named("userSession")
@SessionScoped
public class UserSession implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3039632284061699902L;
	
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
