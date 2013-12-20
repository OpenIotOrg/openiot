package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

@ManagedBean
@ViewScoped
public class RolesController extends AbstractController {
	// Are you sure you want to delete Role '#{role.name}'?&lt;br&gt;It will be revoke from the
	// users having this role.
	private static final long serialVersionUID = 6553286876974799583L;

	private List<Role> roles;

	private Role selectedRole;

	private Role newRole;

	private RoleDataModel roleDataModel;

	private User selectedUser;

	private User selectedOtherUser;

	private Map<Role, List<User>> roleUsers;

	private List<User> allUsers;

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
			roleUsers = new HashMap<Role, List<User>>();
			for (Role role : roles)
				roleUsers.put(role, securityManagerService.getRoleUsers(role));

			allUsers = securityManagerService.getAllUsers();
		}
		return roles;
	}

	public RoleDataModel getRoleDataModel() {
		if (roleDataModel == null)
			roleDataModel = new RoleDataModel(getRoles());
		return roleDataModel;
	}

	public void setSecurityManagerService(LSMSecurityManagerService securityManagerService) {
		this.securityManagerService = securityManagerService;
	}

	public void onEditRole(RowEditEvent event) {
		// TODO: update role
		addInfoMessage("Role Edited", ((Role) event.getObject()).getName());
	}

	public void onCancelEditRole(RowEditEvent event) {
		addInfoMessage("Role editing cancelled", ((Role) event.getObject()).getName());
	}

	public void removeRole(Role role) {
		// TODO: delete role permanently
		addInfoMessage("Role Deleted", role.getName());
		for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
			Role r = iterator.next();
			if (r.equals(role)) {
				iterator.remove();
				break;
			}
		}
		if (role.equals(selectedRole)) {
			setSelectedRole(null);
			setSelectedUser(null);
			setSelectedOtherUser(null);
		}
	}

	public void removeUser(User user) {
		// TODO: delete user permanently
		addInfoMessage("User Deleted", user.getUsername());
		for (Iterator<User> iterator = roleUsers.get(getSelectedRole()).iterator(); iterator.hasNext();) {
			User u = iterator.next();
			if (u.equals(user)) {
				iterator.remove();
				break;
			}
		}
		if (user.equals(selectedUser)) {
			// TODO This is extremely dangerous, replace with removeRoleFromUser() method
			selectedUser.getRoles().remove(selectedRole);

			securityManagerService.deleteUser(selectedUser.getUsername());
			securityManagerService.addUser(selectedUser);

			setSelectedUser(null);
			setSelectedOtherUser(null);
		}
	}

	public List<User> getSelectedRoleUsers() {
		if (selectedRole != null) {
			List<User> users;
			if (roleUsers.containsKey(selectedRole))
				users = roleUsers.get(selectedRole);
			else {
				users = securityManagerService.getRoleUsers(selectedRole);
				roleUsers.put(selectedRole, users);
			}
			return users;
		}
		setSelectedUser(null);
		setSelectedOtherUser(null);
		return EmptyUserList;
	}

	public List<User> getSelectedRoleOtherUsers() {
		if (selectedRole != null) {
			List<User> currentUsers = getSelectedRoleUsers();
			List<User> users = new ArrayList<User>();

			for (User user : allUsers) {
				if (!currentUsers.contains(user))
					users.add(user);
			}
			return users;
		}
		return EmptyUserList;
	}

	public List<Map.Entry<Long, List<Permission>>> getSelectedRolePermissions() {
		if (selectedRole != null) {
			Map<Long, List<Permission>> map = new HashMap<Long, List<Permission>>();
			for (Long key : selectedRole.getPermissionsPerService().keySet())
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
		// clearing the selected user
		setSelectedUser(null);
		setSelectedOtherUser(null);
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public User getSelectedOtherUser() {
		return selectedOtherUser;
	}

	public void setSelectedOtherUser(User selectedOtherUser) {
		this.selectedOtherUser = selectedOtherUser;
	}

	public Role getNewRole() {
		if (newRole == null)
			newRole = new Role();
		return newRole;
	}

	public void cancelAddRole() {
		newRole = null;
	}

	public void addRole() {
		boolean roleAdded = false;
		if (newRole != null && newRole.getName().trim().length() > 0) {
			if (isRoleNameUnique(newRole)) {
				securityManagerService.addRole(newRole);

				// updating roles
				roles.add(newRole);
				roleDataModel.setWrappedData(roles);

				addInfoMessage("New role added", newRole.getName());
				roleAdded = true;
			} else {
				addErrorMessage("Adding new role failed", "Role name is not unique");
			}
		} else {
			addWarnMessage("There is no new role to add", "");
		}
		RequestContext.getCurrentInstance().addCallbackParam("roleAdded", roleAdded);
	}

	public void addUser() {
		if (selectedRole != null && selectedOtherUser != null) {
			// TODO This is extremely dangerous, replace with addRoleToUser() method
			selectedOtherUser.getRoles().add(selectedRole);
			roleUsers.get(selectedRole).add(selectedOtherUser);

			securityManagerService.deleteUser(selectedOtherUser.getUsername());
			securityManagerService.addUser(selectedOtherUser);
			addInfoMessage("User <" + selectedOtherUser.getUsername() + "> now has role", selectedRole.getName());
		} else {
			addErrorMessage("Selected role cannot be assigned to the selected user", "");
		}
	}

	public boolean isRoleNameUnique(Role role) {
		for (Role r : roles)
			if (r.getName().equals(role.getName()))
				return false;
		return true;
	}

}
