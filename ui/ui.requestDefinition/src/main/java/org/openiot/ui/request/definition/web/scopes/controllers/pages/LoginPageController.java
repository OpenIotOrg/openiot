/**
 *    Copyright (c) 2011-2014, OpenIoT
 *   
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

package org.openiot.ui.request.definition.web.scopes.controllers.pages;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.openiot.ui.request.commons.providers.SchedulerAPIWrapper;
import org.openiot.ui.request.commons.providers.exceptions.APICommunicationException;
import org.openiot.ui.request.commons.providers.exceptions.APIException;
import org.openiot.ui.request.definition.web.scopes.application.ApplicationBean;
import org.openiot.ui.request.definition.web.scopes.session.SessionBean;
import org.openiot.ui.request.definition.web.scopes.session.context.pages.LoginPageContext;
import org.openiot.ui.request.definition.web.util.FaceletLocalization;

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

		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		
		applicationBean.redirect("/pages/login.xhtml?faces-redirect=true");
	}

	public void handleLogin() {
		LoginPageContext context = getContext();
		
		// Invoke user login service API
		try{
			String userId = SchedulerAPIWrapper.userLogin(context.getEmail(), context.getPassword());
			sessionBean.setUserId(userId);
			context.dispose();

			applicationBean.redirect("/pages/applicationDesign.xhtml?faces-redirect=true");
			return;
		}catch(APICommunicationException ex){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_REGISTRATION_SERVICE")));
		}catch(APIException ex){
			context.dispose();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_INVALID_CREDENTIALS")));
		}
	}

	public void handleRegistration() {
		LoginPageContext context = getContext();
		
		// Invoke user regustration service API
		try{
			String userId = SchedulerAPIWrapper.userRegister(context.getRegUserName(), context.getRegEmail(), context.getRegPassword());
			sessionBean.setUserId(userId);
			context.dispose();

			applicationBean.redirect("/pages/applicationDesign.xhtml?faces-redirect=true");
			return;
		}catch(APICommunicationException ex){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_REGISTRATION_SERVICE")));
		}catch(APIException ex){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_EMAIL_IN_USE")));
		}
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
