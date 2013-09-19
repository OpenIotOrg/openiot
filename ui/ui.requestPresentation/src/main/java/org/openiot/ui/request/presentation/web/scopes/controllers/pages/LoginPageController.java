/*******************************************************************************
 * Copyright (c) 2011-2014, OpenIoT
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it either under the terms of the GNU Lesser General Public
 *  License version 2.1 as published by the Free Software Foundation
 *  (the "LGPL"). If you do not alter this
 *  notice, a recipient may use your version of this file under the LGPL.
 *  
 *  You should have received a copy of the LGPL along with this library
 *  in the file COPYING-LGPL-2.1; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 *  This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 *  OF ANY KIND, either express or implied. See the LGPL  for
 *  the specific language governing rights and limitations.
 *  
 *  Contact: OpenIoT mailto: info@openiot.eu
 ******************************************************************************/
package org.openiot.ui.request.presentation.web.scopes.controllers.pages;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.openiot.ui.request.presentation.web.scopes.application.ApplicationBean;
import org.openiot.ui.request.presentation.web.scopes.session.SessionBean;
import org.openiot.ui.request.presentation.web.scopes.session.context.pages.LoginPageContext;
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "loginPageController")
@RequestScoped
public class LoginPageController implements Serializable {
	private static final long serialVersionUID = 1L;

	// Cached context
	private LoginPageContext cachedContext;
	// Injected properties
	@ManagedProperty(value = "#{applicationBean}")
	protected transient ApplicationBean applicationBean;
	@ManagedProperty(value = "#{sessionBean}")
	protected transient SessionBean sessionBean;
	protected transient ResourceBundle messages;

	public LoginPageController() {
		this.messages = FaceletLocalization.getLocalizedResourceBundle();
	}

	public LoginPageContext getContext() {
		if (cachedContext == null) {
			cachedContext = (LoginPageContext) (sessionBean == null ? ApplicationBean.lookupSessionBean() : sessionBean).getContext("loginPageContext");
			if (cachedContext == null) {
				cachedContext = new LoginPageContext();
			}
		}
		return cachedContext;
	}

	// ------------------------------------
	// Controllers
	// ------------------------------------
	public void handleLogout() {
		sessionBean.setUserId(null);
		getContext().dispose();

		applicationBean.redirect("/pages/login.xhtml?faces-redirect=true");
	}

	public void handleLogin() {
		LoginPageContext context = getContext();
		
		// @todo: Invoke user login service API
		if ("sensap".equals(context.getUserName()) && "sensap".equals(context.getPassword())) {
			sessionBean.setUserId("nodeID://b47890");
			context.dispose();

			applicationBean.redirect("/pages/requestPresentation.xhtml?faces-redirect=true");
			return;
		}
		context.dispose();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_INVALID_CREDENTIALS")));
	}

	// ------------------------------------
	// Helpers
	// ------------------------------------
	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}
}
