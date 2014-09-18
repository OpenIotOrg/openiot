/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */

package org.openiot.security.mgmt;

import static org.openiot.security.mgmt.Utils.EmptyPermissionList;
import static org.openiot.security.mgmt.Utils.EmptyRoleList;
import static org.openiot.security.mgmt.Utils.EmptyUserList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * @author Mehdi Riahi
 *
 */
@ManagedBean
@ViewScoped
public class RolesController extends AbstractController {
	// Are you sure you want to delete Role '#{role.name}'?&lt;br&gt;It will be revoke from the
	// users having this role.
	private static final long serialVersionUID = 6553286876974799583L;

	private List<Role> roles;

	private Role selectedRole;

	private Role newRole;

	private User selectedUser;

	private User selectedOtherUser;

	private Permission selectedOtherPermission;

	private Map<Role, List<User>> roleUsers;

	private List<User> allUsers;

	private List<Permission> allPermissions;

	private Map<Long, RegisteredService> allServices;

	@ManagedProperty(value = "#{securityManagerService}")
	private SecurityManagerService securityManagerService;

	private long selectedServiceId = -1;

	private String selectedServiceIdStr = null;

	public RolesController() {

	}

	public List<Role> getRoles() {
		if (roles == null) {
			roles = securityManagerService.getAllRoles();
			roleUsers = new HashMap<Role, List<User>>();
			for (Role role : roles)
				roleUsers.put(role, securityManagerService.getRoleUsers(role));

			allUsers = securityManagerService.getAllUsers();
			allPermissions = securityManagerService.getAllPermissions();
			final List<RegisteredService> services = securityManagerService.getAllServices();
			allServices = new HashMap<Long, RegisteredService>(services.size());
			for (RegisteredService registeredService : services) {
				String name = registeredService.getName();
				
				//Checking access
				if (Utils.acUtil.hasPermission("admin:user_mgmt:" + name))
					allServices.put(registeredService.getId(), registeredService);
			}
		}
		if (selectedServiceId > -1) {
			// TODO: cache!
			List<Role> list = new ArrayList<Role>();
			for (Role role : roles)
				if (role.getServiceId().equals(selectedServiceId))
					list.add(role);
			return list;
		}
		return EmptyRoleList;
	}

	public void setSecurityManagerService(SecurityManagerService securityManagerService) {
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
		// TODO This is extremely dangerous, replace with removeRoleCascade() method
		if (role.equals(selectedRole)) {

			roles.remove(role);
			for (User roleUser : roleUsers.get(selectedRole))
				roleUser.getRoles().remove(selectedRole);

			securityManagerService.deleteRole(role.getServiceId(), role.getName());

			setSelectedRole(null);
			setSelectedUser(null);
			setSelectedOtherUser(null);

			addInfoMessage("Role Deleted", role.getName());
		}
	}

	public void removeUser(User user) {
		addInfoMessage("User Deleted", user.getUsername());
		if (user.equals(selectedUser)) {
			// TODO This is extremely dangerous, replace with removeRoleFromUser() method
			roleUsers.get(getSelectedRole()).remove(user);
			selectedUser.getRoles().remove(selectedRole);

			securityManagerService.deleteUser(selectedUser.getUsername());
			securityManagerService.addUser(selectedUser);

			setSelectedUser(null);
			setSelectedOtherUser(null);
		}
	}

	public void removePermission(Permission perm) {
		if (perm != null) {
			selectedRole.getPermissions().remove(perm);
			securityManagerService.removePermissionFromRole(selectedRole, perm);
			addInfoMessage("Permission removed from the selected role", perm.getName());
		}
	}

	public List<User> getSelectedRoleUsers() {
		List<User> users = EmptyUserList;
		if (selectedRole != null) {
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
		return users;
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

	public List<Permission> getSelectedRolePermissions() {
		List<Permission> list = EmptyPermissionList;
		if (selectedRole != null) {
			if (selectedRole.getPermissions() == null) {
				list = new ArrayList<Permission>();
				selectedRole.setPermissions(list);
			} else {
				list = selectedRole.getPermissions();
			}
		}
		return list;
	}

	public List<Permission> getSelectedRoleOtherPermissions() {
		if (selectedRole != null && selectedServiceId != -1) {
			List<Permission> currentPerms = selectedRole.getPermissions();
			List<Permission> otherPerms = new ArrayList<Permission>();
			for (Permission perm : allPermissions)
				if (perm.getServiceId().equals(selectedServiceId) && (currentPerms == null || !currentPerms.contains(perm)))
					otherPerms.add(perm);
			return otherPerms;
		}
		return EmptyPermissionList;
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

	public Permission getSelectedOtherPermission() {
		return selectedOtherPermission;
	}

	public void setSelectedOtherPermission(Permission selectedOtherPermission) {
		this.selectedOtherPermission = selectedOtherPermission;
	}

	public Long getSelectedServiceId() {
		return selectedServiceId;
	}

	public void setSelectedServiceId(Long selectedServiceId) {
		this.selectedServiceId = selectedServiceId;
	}

	public String getSelectedServiceIdStr() {
		return selectedServiceIdStr;
	}

	public String getSelectedServiceName() {
		if (allServices != null && selectedServiceId > -1 && allServices.containsKey(selectedServiceId))
			return allServices.get(selectedServiceId).getName();
		return "SELECTED_SERVICE_NOT_FOUND";
	}

	public boolean hasRoleDeletionPermission() {
		return Utils.acUtil.hasPermission("admin:delete_role:" + getSelectedServiceName());
	}

	public void setSelectedServiceIdStr(String selectedServiceIdStr) {
		this.selectedServiceIdStr = selectedServiceIdStr;
		try {
			setSelectedServiceId(Long.parseLong(selectedServiceIdStr));
		} catch (NumberFormatException e) {
			setSelectedServiceId(-1L);
		}
		setSelectedRole(null);
		setSelectedUser(null);
		setSelectedOtherUser(null);
	}

	public List<RegisteredService> getAllServices() {
		if (roles == null)
			getRoles();
		return new ArrayList<RegisteredService>(allServices.values());
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
		if (selectedServiceId >= 0 && newRole != null && newRole.getName().trim().length() > 0) {
			newRole.setServiceId(selectedServiceId);
			// TODO: check role name for not permitted characters (add a proper validator for role
			// name)
			if (isRoleNameUnique(newRole) && isRoleNameValid(newRole)) {
				securityManagerService.addRole(newRole);

				// updating roles
				roles.add(newRole);
				addInfoMessage("New role added", newRole.getName());
				newRole = null;
				roleAdded = true;
			} else {
				addErrorMessage("Adding new role failed", "Role name is not unique or role name is not valid");
			}
		} else {
			addWarnMessage("There is no new role to add", "");
		}
		RequestContext.getCurrentInstance().addCallbackParam("roleAdded", roleAdded);
	}

	public void addUser() {
		if (selectedRole != null && selectedOtherUser != null) {
			// TODO This is extremely dangerous, replace with an addRoleToUser() method
			if (selectedOtherUser.getRoles() == null)
				selectedOtherUser.setRoles(new ArrayList<Role>());
			selectedOtherUser.getRoles().add(selectedRole);
			roleUsers.get(selectedRole).add(selectedOtherUser);

			securityManagerService.deleteUser(selectedOtherUser.getUsername());
			securityManagerService.addUser(selectedOtherUser);
			addInfoMessage("User <" + selectedOtherUser.getUsername() + "> now has role", selectedRole.getName());
		} else {
			addErrorMessage("Selected role cannot be assigned to the selected user", "");
		}
	}

	public void addPermission() {
		if (selectedRole != null && selectedOtherPermission != null && selectedRole.addPermission(selectedOtherPermission)) {
			// TODO This is dangerous, replace with addPermissionToRole() method

			// adds role's permission too
			securityManagerService.addRole(selectedRole);
			addInfoMessage("Permission <" + selectedOtherPermission.getName() + "> added to service <" + selectedServiceId + "> for role",
					selectedRole.getName());
		} else {
			addErrorMessage("Selected permission cannot be assigned to the selected role", "");
		}
	}

	public boolean isRoleNameUnique(Role role) {
		for (Role r : roles)
			if (r.getServiceId().equals(role.getServiceId()) && r.getName().equals(role.getName()))
				return false;
		return true;
	}

	public boolean isRoleNameValid(Role role) {
		return !role.getName().matches(".*(\\s|__|/).*");
	}
}
