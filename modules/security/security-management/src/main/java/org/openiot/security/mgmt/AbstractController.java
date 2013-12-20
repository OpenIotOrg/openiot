
package org.openiot.security.mgmt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.openiot.lsm.security.oauth.mgmt.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2046681933562895343L;

	protected transient Logger logger = LoggerFactory.getLogger(getClass());

	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;

	/**
	 * Adds a global error message to the response.
	 * 
	 * @param summary
	 *            The message summary.
	 */
	protected void addErrorMessage(String summary) {
		FacesMessage msg = new FacesMessage(summary);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	private void addMessage(Severity severity, String message, String details) {
		FacesMessage msg = new FacesMessage(severity, message, details);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	
	protected void addInfoMessage(String message, String details) {
		addMessage(FacesMessage.SEVERITY_INFO, message, details);
	}
	
	protected void addErrorMessage(String message, String details) {
		addMessage(FacesMessage.SEVERITY_ERROR, message, details);
	}
	
	protected void addWarnMessage(String message, String details) {
		addMessage(FacesMessage.SEVERITY_WARN, message, details);
	}

	/**
	 * Convenience method to return the logged in user.
	 * 
	 * @return A {@link User} object.
	 */
	protected User getLoggedInUser() {
		User user = null;
		if (userSession != null) {
			user = userSession.getUser();
		}
		return user;
	}

	/**
	 * Handle deserialization from passivated session and restore transient
	 * fields.
	 * 
	 * @param ois
	 *            The ObjectInputStream object.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		logger = LoggerFactory.getLogger(getClass());
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}
}
