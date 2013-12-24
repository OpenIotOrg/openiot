package org.openiot.security.mgmt;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.openiot.security.client.AccessControlUtil;

@ManagedBean
@ViewScoped
public class LoginController extends AbstractController {

	private static final long serialVersionUID = 664312101246983262L;

	public String signInWithOpenIoT() {
		logger.debug("Debut de la methode");
		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			AccessControlUtil accessControlUtil = AccessControlUtil.getInstance();
			final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			final HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
			final HttpServletResponse resp = (HttpServletResponse) externalContext.getResponse();
			try {
				logger.debug("Redirecting to CAS login");
				accessControlUtil.redirectToLogin(req, resp);
				logger.debug("Redirected to CAS login");
				return "home";
			} catch (IOException e) {
				logger.error("Authentication redirect exception", e);
				return "error";
			}
		} else {
			logger.debug("User is already logged in");
			return "home";
		}
	}

}
