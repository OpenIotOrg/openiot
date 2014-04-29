/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */

package org.openiot.security.mgmt;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.jasig.cas.authentication.handler.DefaultPasswordEncoder;
import org.openiot.lsm.security.oauth.mgmt.User;

/**
 * @author Mehdi Riahi
 *
 */
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
