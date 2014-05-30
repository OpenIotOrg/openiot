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

/**
 * @author Mehdi Riahi
 *
 */
@ManagedBean
@ViewScoped
public class LoginController extends AbstractController {

	private static final long serialVersionUID = 664312101246983262L;

	public String signInWithOpenIoT() {
		logger.debug("Debut de la methode");
		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			final HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
			final HttpServletResponse resp = (HttpServletResponse) externalContext.getResponse();
			try {
				logger.debug("Redirecting to CAS login");
				Utils.acUtil.redirectToLogin(req, resp);
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
