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
