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

package org.openiot.ui.request.presentation.web.scopes.application;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.openiot.ui.request.presentation.web.scopes.session.SessionBean;
import org.openiot.ui.request.commons.logging.LoggerService;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "applicationBean", eager = true)
@ApplicationScoped
public class ApplicationBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		LoggerService.setApplicationName("OpenIoT:RequestPresentation");
		LoggerService.setLevel(Level.FINE);
		LoggerService.log(Level.INFO, "Initializing");
	}

	public void redirect(String location) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext extContext = ctx.getExternalContext();
			String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, location));
			extContext.redirect(url);
		} catch (IOException ex) {
			LoggerService.log(ex);
		} catch (java.lang.IllegalStateException ex) {
		}
	}

	// ------------------------------------
	// Lookup API
	// ------------------------------------
	public static ApplicationBean lookupApplicationBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		return ((ApplicationBean) context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class));
	}

	public static SessionBean lookupSessionBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		return ((SessionBean) context.getApplication().evaluateExpressionGet(context, "#{sessionBean}", SessionBean.class));
	}

}
