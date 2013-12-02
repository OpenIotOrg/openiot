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
