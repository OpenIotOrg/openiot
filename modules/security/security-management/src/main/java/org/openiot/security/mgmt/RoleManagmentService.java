package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.openiot.lsm.security.oauth.mgmt.Role;


@SessionScoped
@ManagedBean(name =  "roleTable")
public class RoleManagmentService {

	private List<Role> roles;
	private Role selectedRole;
	private Role[] selectedRoles;
	
	public RoleManagmentService() {
		roles = new ArrayList<Role>();
//		roles.add(new Role("role1", "myRole"));
//		roles.add(new Role("role2", "myRole"));
	}
	
	public RoleManagmentService(List<Role> roles) {
		this.roles = roles;
	}
	
	public void setSelectedRole(Role selectedRole) {
		this.selectedRole = selectedRole;
	}
	
	public Role getSelectedRole() {
		return selectedRole;
	}
	
	
	public List<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public Role[] getSelectedRoles() {
		return selectedRoles;
	}
	
	public void addRoles(Role role) {
		if (!roles.contains(role))
			roles.add(role);
	}
	
	public void addRoles(List<Role> UserRoles) {
//		if (!roles.contains(role))
//			roles.add(role);
//		TODO check if roles contain the role
//		do not add it again
		roles.addAll(UserRoles);
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
