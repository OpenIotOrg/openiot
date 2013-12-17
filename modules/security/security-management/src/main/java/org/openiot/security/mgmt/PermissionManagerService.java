package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@SessionScoped
@ManagedBean(name = "permissionTable")
public class PermissionManagerService {
	
	private List<Permission> permissions;
	private Permission selectedPermission;
	private Permission[] selectedPermissions;
	
	public PermissionManagerService() {
		permissions = new ArrayList<Permission>();
		permissions.add(new Permission("permission1", "description1"));
		permissions.add(new Permission("permission2", "description2"));
	}
	
	public List<Permission> getPermissions() {
		return permissions;
	}
	
	
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	public Permission getSelectedPermission() {
		return selectedPermission;
	}
	
	public void setSelectedPermission(Permission selectedPermission) {
		this.selectedPermission = selectedPermission;
	}
	
	public Permission[] getSelectedPermissions() {
		return selectedPermissions;
	}
	
	public void setSelectedPermissions(Permission[] selectedPermissions) {
		this.selectedPermissions = selectedPermissions;
	}
	

	public String addColumn() {
		return "userPermission";
	}
	
	public String removeColumn() {
		return "userPermission";
	}
	
	public String editColumn() {
		return "userPermission";
	}
}
