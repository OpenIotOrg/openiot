package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.primefaces.event.RowEditEvent;

@ManagedBean
@ViewScoped
public class RolesController extends AbstractController {
//Are you sure you want to delete Role '#{role.name}'?&lt;br&gt;It will be revoke from the users having this role.
	private static final long serialVersionUID = 6553286876974799583L;

	private List<Role> roles;

	private Role selectedRole;
	
	private RoleDataModel roleDataModel;
	
	@SuppressWarnings("unchecked")
	private List<?> emptyList = Collections.unmodifiableList(Collections.EMPTY_LIST);
	
	@SuppressWarnings("unchecked")
	private List<User> EmptyUserList = (List<User>) emptyList;
	
	@SuppressWarnings("unchecked")
	private List<Map.Entry<Long, List<Permission>>> EmptyPermissionsList = (List<Entry<Long, List<Permission>>>) emptyList;
	
	@ManagedProperty(value = "#{securityManagerService}")
	private LSMSecurityManagerService securityManagerService;

	public RolesController() {
	}

	public List<Role> getRoles() {
		if (roles == null) {
			roles = securityManagerService.getAllRoles();
		}
		return roles;
	}
	
	public RoleDataModel getRoleDataModel(){
		if(roleDataModel == null)
			roleDataModel = new RoleDataModel(getRoles());
		return roleDataModel;
	}

	public void setSecurityManagerService(LSMSecurityManagerService securityManagerService) {
		this.securityManagerService = securityManagerService;
	}

	public void onEditRole(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Role Edited", ((Role) event.getObject()).getName());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onCancelEditRole(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Role editing cancelled", ((Role) event.getObject()).getName());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void removeRole(Role role) {
		FacesMessage msg = new FacesMessage("Role Deleted", role.getName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
		for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
			Role r = iterator.next();
			if (r.equals(role)) {
				iterator.remove();
				break;
			}
		}
		if(role.equals(selectedRole))
			setSelectedRole(null);
	}
	
	public List<User> getSelectedRoleUsers(){
		if(selectedRole != null)
			return securityManagerService.getRoleUsers(selectedRole);
		return EmptyUserList;
	}
	
	public List<Map.Entry<Long, List<Permission>>> getSelectedRolePermissions(){
		if(selectedRole != null){
			Map<Long, List<Permission>> map = new HashMap<Long, List<Permission>>();
			for(Long key : selectedRole.getPermissionsPerService().keySet())
				map.put(key, new ArrayList<Permission>(selectedRole.getPermissionsPerService().get(key)));
			return new ArrayList<Map.Entry<Long, List<Permission>>>(map.entrySet());
		}
		return EmptyPermissionsList;
	}

	public Role getSelectedRole() {
		return selectedRole;
	}

	public void setSelectedRole(Role selectedRole) {
		this.selectedRole = selectedRole;
	}

}
