package org.openiot.security.mgmt;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.jasig.cas.authentication.handler.DefaultPasswordEncoder;
import org.openiot.lsm.security.oauth.mgmt.User;

@ManagedBean
@ViewScoped
public class SignupController extends AbstractController {

	private static final long serialVersionUID = -246517359642720816L;

	private User user;

	private DefaultPasswordEncoder passwordEncoder;

	@ManagedProperty(value = "#{securityManagerService}")
	private SecurityManagerService securityManagerService;

	public SignupController() {
		user = new User();
		passwordEncoder = new DefaultPasswordEncoder("MD5");
		passwordEncoder.setCharacterEncoding("UTF-8");
	}

	public String signup() {
		if (isValidUser()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			securityManagerService.addUser(user);
			return "login";
		}
		return null;
	}

	/**
	 * This method checks to see if the username already exists.
	 * 
	 * @return A boolean value.
	 */
	private boolean isValidUser() {
		boolean valid = false;

		String email = user.getEmail();
		if (email == null) {
			FacesMessage msg = new FacesMessage();
			msg.setDetail("Email cannot be empty.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			if (securityManagerService.getUserByEmail(email) != null) {
				FacesMessage msg = new FacesMessage();
				msg.setDetail("A user already exists with the provided email address.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} else
				valid = true;
		}

		if (valid) {
			String username = user.getUsername();
			if (username == null) {
				FacesMessage msg = new FacesMessage();
				msg.setDetail("Username cannot be empty.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
				valid = false;
			} else {
				if (securityManagerService.getUserByUsername(username) != null) {
					valid = false;
					FacesMessage msg = new FacesMessage();
					msg.setDetail("Username already exists. Please choose another username.");
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
		}

		return valid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setSecurityManagerService(SecurityManagerService securityManagerService) {
		this.securityManagerService = securityManagerService;
	}

}
