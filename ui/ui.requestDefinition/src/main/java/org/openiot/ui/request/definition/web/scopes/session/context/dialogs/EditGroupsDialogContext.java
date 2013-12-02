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
import java.util.ArrayList;
import java.util.List;

import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.definition.web.model.EditablePropertyField;
import org.openiot.ui.request.definition.web.scopes.session.base.DisposableContext;
import org.primefaces.model.DualListModel;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class EditGroupsDialogContext extends DisposableContext implements Serializable {
	private static final long serialVersionUID = 1L;

	private DualListModel<String> groupModel;
	private GraphNode node;
	private EditablePropertyField field;

	public EditGroupsDialogContext(GraphNode node, EditablePropertyField field) {
		super();
		this.register();

		this.field = field;
		this.node = node;
		initialize();
	}

	@Override
	public String getContextUID() {
		return "editGroupsDialogContext";
	}

	public GraphNode getNode() {
		return node;
	}

	public EditablePropertyField getField() {
		return field;
	}

	public DualListModel<String> getGroupModel() {
		return groupModel;
	}

	public void setGroupModel(DualListModel<String> groupModel) {
		this.groupModel = groupModel;
	}

	private void initialize() {
		List<String> availableFields = new ArrayList<String>();
		availableFields.add("recordTime_year");
		availableFields.add("recordTime_month");
		availableFields.add("recordTime_day");
		availableFields.add("recordTime_hours");
		availableFields.add("recordTime_minutes");
		availableFields.add("recordTime_seconds");

		// Get list of current groups
		@SuppressWarnings("unchecked")
		List<String> groupFields = (List<String>) node.getPropertyValueMap().get(field.getValueKey());

		// Remove existing groups from available groups
		availableFields.removeAll(groupFields);
		
		// Initialize picklist
		groupModel = new DualListModel<String>(availableFields, groupFields);
	}

}
