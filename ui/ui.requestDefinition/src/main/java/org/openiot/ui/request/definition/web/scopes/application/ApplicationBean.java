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

package org.openiot.ui.request.definition.web.scopes.application;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.openiot.ui.request.definition.web.scopes.session.SessionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "applicationBean", eager = true)
@ApplicationScoped
public class ApplicationBean implements Serializable {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationBean.class);
	private static final long serialVersionUID = 1L;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing");
    }

    public void redirect(String location) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ExternalContext extContext = ctx.getExternalContext();
            String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, location));
            extContext.redirect(url);
        } catch (IOException ex) {
            LOGGER.error("", ex);
        } catch (java.lang.IllegalStateException ex) {
        }
    }


    //------------------------------------
    // Lookup API
    //------------------------------------
    public static ApplicationBean lookupApplicationBean() {
        FacesContext context = FacesContext.getCurrentInstance();
        return ((ApplicationBean) context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class));
    }

    public static SessionBean lookupSessionBean() {
        FacesContext context = FacesContext.getCurrentInstance();
        return ((SessionBean) context.getApplication().evaluateExpressionGet(context, "#{sessionBean}", SessionBean.class));
    }

}
