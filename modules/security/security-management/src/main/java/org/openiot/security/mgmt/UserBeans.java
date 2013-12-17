package org.openiot.security.mgmt;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean(name = "userBeans")
public class UserBeans {

	private static final String ADMIN = "admin";
	
	private String username;
	private String password;

	public UserBeans() {
		// TODO Auto-generated constructor stub
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String login() {
		if(getUsername().equalsIgnoreCase(ADMIN) && getPassword().equalsIgnoreCase(ADMIN)) 
//			return "home";
			return "userHome";
		else {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("usename", new FacesMessage("Invalid username and password"));
			return "login";
		}
	}
	
	
}
