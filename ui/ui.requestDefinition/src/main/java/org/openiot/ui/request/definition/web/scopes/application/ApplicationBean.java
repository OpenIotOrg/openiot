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
package org.openiot.ui.request.definition.web.scopes.application;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.openiot.ui.request.definition.web.scopes.session.SessionBean;
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
    	LoggerService.setApplicationName("OpenIoT:RequestDefinition");
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
