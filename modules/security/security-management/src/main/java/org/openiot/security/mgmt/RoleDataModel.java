package org.openiot.security.mgmt;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.openiot.lsm.security.oauth.mgmt.Role;
import org.primefaces.model.SelectableDataModel;

public class RoleDataModel extends ListDataModel<Role> implements SelectableDataModel<Role> {

	public RoleDataModel() {
	}

	public RoleDataModel(List<Role> list) {
		super(list);
	}

	@Override
	public Role getRowData(String rowKey) {
		@SuppressWarnings("unchecked")
		List<Role> roles = (List<Role>) getWrappedData();

		for (Role role : roles) {
			if (role.getName().equals(rowKey))
				return role;
		}

		return null;
	}

	@Override
	public Object getRowKey(Role role) {
		return role.getName();
	}

}
