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
package org.openiot.ui.request.definition.web.scopes.session.context.dialogs;

import java.io.Serializable;
import java.util.Map;

import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.definition.web.model.EditablePropertyField;
import org.openiot.ui.request.definition.web.scopes.session.base.DisposableContext;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class EditVariableDialogContext extends DisposableContext implements Serializable {
	private static final long serialVersionUID = 1L;

	private GraphNode node;
	private EditablePropertyField field;

	public EditVariableDialogContext(GraphNode node, EditablePropertyField field) {
		super();
		this.register();

		this.node = node;
		this.field = field;
	}

	@Override
	public String getContextUID() {
		return "editVariableDialogContext";
	}
	
	public GraphNode getNode() {
		return node;
	}

	public EditablePropertyField getField() {
		return field;
	}

	public void setField(EditablePropertyField field) {
		this.field = field;
	}

}
