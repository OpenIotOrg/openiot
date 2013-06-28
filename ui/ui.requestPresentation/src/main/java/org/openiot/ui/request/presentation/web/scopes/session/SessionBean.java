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
package org.openiot.ui.request.presentation.web.scopes.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.openiot.ui.request.presentation.web.scopes.session.base.DisposableContext;
import org.openiot.ui.request.commons.logging.LoggerService;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "sessionBean")
@SessionScoped
public class SessionBean implements Serializable {
	private static final long serialVersionUID = 1L;

    private Map<String, DisposableContext> contextMap;

    /**
     * Creates a new instance of SessionBean
     */
    public SessionBean() {
        contextMap = new HashMap<String, DisposableContext>();
    }

    //------------------------------------
    // Context API
    //------------------------------------
    public DisposableContext getContext(String contextName) {
        if (contextMap.containsKey(contextName)) {
            return contextMap.get(contextName);
        }

        return null;
    }

    public void purgeContext(DisposableContext context) {
        if (contextMap.containsValue(context)) {
            for (Map.Entry<String, DisposableContext> entry : contextMap.entrySet()) {
                if (entry.getValue().equals(context)) {
                    contextMap.remove(entry.getKey());
                    LoggerService.log(Level.FINER, "SessionBean: purged context '" + entry.getKey() + "' of type " + entry.getValue().getClass().getSimpleName());
                    return;
                }
            }
        }
    }

    public void registerContext(DisposableContext context) {
        if (contextMap.containsValue(context)) {
            LoggerService.log(Level.FINER, "SessionBean: overwriting old context '" + context.getContextUID() + "' of type " + context.getClass().getSimpleName());
        }
        contextMap.put(context.getContextUID(), context);
        LoggerService.log(Level.FINER, "SessionBean: registered context '" + context.getContextUID() + "' of type " + context.getClass().getSimpleName());
    }

    public String addToFlashScopeAndRedirect(String key, String value, String redirectTo) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put(key, value);
        return redirectTo;
    }

    public Map<String, DisposableContext> getContextMap() {
        return contextMap;
    }

}
