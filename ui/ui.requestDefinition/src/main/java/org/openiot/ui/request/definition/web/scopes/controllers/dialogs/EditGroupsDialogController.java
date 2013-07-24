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
package org.openiot.ui.request.definition.web.scopes.controllers.dialogs;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.definition.web.model.EditablePropertyField;
import org.openiot.ui.request.definition.web.scopes.application.ApplicationBean;
import org.openiot.ui.request.definition.web.scopes.session.SessionBean;
import org.openiot.ui.request.definition.web.scopes.session.context.dialogs.EditGroupsDialogContext;
import org.openiot.ui.request.definition.web.scopes.session.context.dialogs.EditVariableDialogContext;
import org.openiot.ui.request.definition.web.util.FaceletLocalization;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "editGroupsDialogController")
@RequestScoped
public class EditGroupsDialogController implements Serializable {
	private static final long serialVersionUID = 1L;

	// Injected properties
    @ManagedProperty(value = "#{applicationBean}")
    protected transient ApplicationBean applicationBean;
    @ManagedProperty(value = "#{sessionBean}")
    protected transient SessionBean sessionBean;
    protected transient ResourceBundle messages;
    // Cached context
    private EditGroupsDialogContext cachedContext;

    public EditGroupsDialogController() {
        this.messages = FaceletLocalization.getLocalizedResourceBundle();
    }

    public EditGroupsDialogContext getContext() {
        if (cachedContext == null) {
            cachedContext = (EditGroupsDialogContext) (sessionBean == null ? ApplicationBean.lookupSessionBean() : sessionBean).getContext("editGroupsDialogContext");
        }
        return cachedContext;
    }

    public void prepareDialog(GraphNode node, EditablePropertyField field) {
        cachedContext = new EditGroupsDialogContext(node, field);
    }
    
    public void applyChanges(){
    	EditGroupsDialogContext context = getContext();
    	context.getNode().getPropertyValueMap().put( context.getField().getValueKey(), context.getGroupModel().getTarget());
    }
    
    //------------------------------------
    // Helpers
    //------------------------------------
    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}
