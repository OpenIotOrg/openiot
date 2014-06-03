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

/**
 * @author Mehdi Riahi
 *
 */
@ManagedBean
@ViewScoped
public class UsersController extends AbstractController {
	// Are you sure you want to delete Role '#{role.name}'?&lt;br&gt;It will be revoke from the
	// users having this role.
	private static final long serialVersionUID = 5365682876974798395L;

	private Role selectedRole;

	private User selectedUser;

	private Role selectedOtherRole;

	private Map<Role, List<User>> roleUsers;

	private List<User> allUsers;

	private List<Role> allRoles;

	private Map<Long, RegisteredService> allServices;

	@ManagedProperty(value = "#{securityManagerService}")
	private SecurityManagerService securityManagerService;

	private long selectedServiceId = -1;

	private String selectedServiceIdStr = null;

	public UsersController() {
	}

	public List<User> getUsers() {
		if (allUsers == null) {
			allRoles = securityManagerService.getAllRoles();
			roleUsers = new HashMap<Role, List<User>>();
			for (Role role : allRoles)
				roleUsers.put(role, securityManagerService.getRoleUsers(role));

			allUsers = securityManagerService.getAllUsers();

			final List<RegisteredService> services = securityManagerService.getAllServices();
			allServices = new HashMap<Long, RegisteredService>(services.size());
			for (RegisteredService registeredService : services) {
				String name = registeredService.getName();
				if (Utils.acUtil.hasPermission("admin:user_mgmt:" + name))
					allServices.put(registeredService.getId(), registeredService);
			}
		}
		return allUsers;
	}

	public void setSecurityManagerService(SecurityManagerService securityManagerService) {
		this.securityManagerService = securityManagerService;
	}

	public void removeRole(Role role) {
		// TODO: extremely dangerous! replace with revokeRoleFromUser(user, role)
		if (selectedUser != null) {
			selectedUser.getRoles().remove(selectedRole);

			securityManagerService.deleteUser(selectedUser.getUsername());
			securityManagerService.addUser(selectedUser);
			addInfoMessage("Role revoked from the selected user", role.getName());
		}
		if (role.equals(selectedRole)) {
			setSelectedRole(null);
		}
	}

	public void removeUser(User user) {
		if (user.equals(selectedUser)) {
			allUsers.remove(user);
			securityManagerService.deleteUser(selectedUser.getUsername());
			addInfoMessage("User Deleted", user.getUsername());
			setSelectedUser(null);
		}
	}

	public List<Role> getSelectedUserRoles() {
		List<Role> userRoles = EmptyRoleList;
		if (selectedUser != null && selectedServiceId > -1) {
			userRoles = new ArrayList<Role>();
			if (selectedUser.getRoles() == null)
				selectedUser.setRoles(userRoles);
			else {
				for (Role role : selectedUser.getRoles())
					if (role.getServiceId().equals(selectedServiceId))
						userRoles.add(role);
			}
		}
		return userRoles;
	}

	public List<Role> getSelectedUserOtherRoles() {
		if (selectedUser != null) {
			List<Role> currentRoles = selectedUser.getRoles();
			List<Role> roles = new ArrayList<Role>();

			for (Role role : allRoles)
				if (role.getServiceId().equals(selectedServiceId) && (currentRoles == null || !currentRoles.contains(role)))
					roles.add(role);
			return roles;
		}
		return EmptyRoleList;
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

	public Role getSelectedRole() {
		return selectedRole;
	}

	public void setSelectedRole(Role selectedRole) {
		this.selectedRole = selectedRole;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
		// setSelectedServiceIdStr(null);
		setSelectedRole(null);
		setSelectedOtherRole(null);
	}

	public Role getSelectedOtherRole() {
		return selectedOtherRole;
	}

	public void setSelectedOtherRole(Role selectedOtherRole) {
		this.selectedOtherRole = selectedOtherRole;
	}

	public List<RegisteredService> getAllServices() {
		return new ArrayList<RegisteredService>(allServices.values());
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

	public void setSelectedServiceIdStr(String selectedServiceIdStr) {
		this.selectedServiceIdStr = selectedServiceIdStr;
		try {
			setSelectedServiceId(Long.parseLong(selectedServiceIdStr));
		} catch (NumberFormatException e) {
			setSelectedServiceId(-1L);
		}
		setSelectedRole(null);
		setSelectedOtherRole(null);
	}

	public String getSelectedServiceName() {
		if(allServices != null && selectedServiceId > -1 && allServices.containsKey(selectedServiceId))
			return allServices.get(selectedServiceId).getName();
		return "SELECTED_SERVICE_NOT_FOUND";
	}

	public void addRole() {
		// TODO: extremely dangerous! replace with grantRoleToUser(user, role)
		if (selectedOtherRole != null && selectedUser != null) {
			selectedUser.getRoles().add(selectedOtherRole);
			securityManagerService.deleteUser(selectedUser.getUsername());
			securityManagerService.addUser(selectedUser);
			addInfoMessage("Role added to the selected user <" + selectedUser.getUsername() + ">", selectedOtherRole.getName());
			setSelectedOtherRole(null);
		} else {
			addWarnMessage("There is no new role to add", "");
		}
	}

}
