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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.openiot.security.client.AccessControlUtil;
import org.primefaces.context.RequestContext;

/**
 * @author Mehdi Riahi
 *
 */
@ManagedBean
@ViewScoped
public class PermissionsController extends AbstractController {

	private static final long serialVersionUID = -3531791896078368761L;

	private Permission selectedPermission;

	private Permission newPermission;

	private List<User> allUsers;

	private List<Role> allRoles;

	private List<Permission> allPermissions;

	private Map<Long, RegisteredService> allServices;

	private Map<Permission, List<Role>> permissionRoles;

	private Long selectedServiceId = -1L;

	private String selectedServiceIdStr = null;

	@ManagedProperty(value = "#{securityManagerService}")
	private SecurityManagerService securityManagerService;

	public PermissionsController() {

	}

	public List<Permission> getPermissions() {
		if (allPermissions == null) {
			allRoles = securityManagerService.getAllRoles();

			allUsers = securityManagerService.getAllUsers();
			allPermissions = securityManagerService.getAllPermissions();

			final List<RegisteredService> services = securityManagerService.getAllServices();
			allServices = new HashMap<Long, RegisteredService>(services.size());
			for (RegisteredService registeredService : services) {
				String name = registeredService.getName();
				if (AccessControlUtil.getInstance().hasPermission("admin:user_mgmt:" + name))
					allServices.put(registeredService.getId(), registeredService);
			}
			permissionRoles = new HashMap<Permission, List<Role>>();
			for (Role role : allRoles) {
				for (Permission permission : role.getPermissions()) {
					if (!permissionRoles.containsKey(permission))
						permissionRoles.put(permission, new ArrayList<Role>());
					permissionRoles.get(permission).add(role);
				}
			}
		}
		if (selectedServiceId > -1) {
			// TODO: cache!
			List<Permission> list = new ArrayList<Permission>();
			for (Permission permission : allPermissions)
				if (permission.getServiceId().equals(selectedServiceId))
					list.add(permission);
			return list;
		}
		return EmptyPermissionList;
	}

	public void setSecurityManagerService(SecurityManagerService securityManagerService) {
		this.securityManagerService = securityManagerService;
	}

	public void removePermission(Permission permission) {

		if (permission.equals(selectedPermission)) {
			List<Role> selectedPermissionRoles = getSelectedPermissionRoles();

			for (Role role : selectedPermissionRoles)
				role.getPermissions().remove(permission);

			securityManagerService.deletePermission(permission.getServiceId(), permission.getName());
			allPermissions.remove(selectedPermission);
			permissionRoles.remove(permission);
			addInfoMessage("Permission deleted", permission.getName());
			setSelectedPermission(null);
		}
	}

	public List<User> getSelectedPermissionUsers() {
		Set<User> permUsers = new HashSet<User>();
		if (selectedPermission != null) {
			for (User user : allUsers) {
				if (user.getRoles() == null)
					user.setRoles(new ArrayList<Role>());
				for (Role role : user.getRoles()) {
					if (role.getPermissions().contains(selectedPermission))
						permUsers.add(user);
				}
			}
			return new ArrayList<User>(permUsers);
		}
		return EmptyUserList;
	}

	public List<Role> getSelectedPermissionRoles() {
		List<Role> list = EmptyRoleList;
		if (selectedPermission != null)
			list = permissionRoles.get(selectedPermission);
		return list;
	}

	public Permission getSelectedPermission() {
		return selectedPermission;
	}

	public void setSelectedPermission(Permission selectedPermission) {
		this.selectedPermission = selectedPermission;
	}

	public List<RegisteredService> getAllServicesAsList() {
		if (allPermissions == null)
			getPermissions();
		return new ArrayList<RegisteredService>(allServices.values());
	}

	public RegisteredService getServiceById(Long serviceId) {
		return allServices.get(serviceId);
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
	}

	public String getSelectedServiceName() {
		if(allServices != null && selectedServiceId > -1 && allServices.containsKey(selectedServiceId))
			return allServices.get(selectedServiceId).getName();
		return "SELECTED_SERVICE_NOT_FOUND";
	}
	
	public boolean hasPermissionDeletionPermission() {
		return AccessControlUtil.getInstance().hasPermission("admin:delete_permission:" + getSelectedServiceName());
	}
	
	public Permission getNewPermission() {
		if (newPermission == null)
			newPermission = new Permission();
		return newPermission;
	}

	public void cancelAddPermission() {
		newPermission = null;
	}

	public void addPermission() {
		boolean permissionAdded = false;
		if (selectedServiceId > -1 && newPermission != null && newPermission.getName().trim().length() > 0) {
			newPermission.setServiceId(selectedServiceId);
			if (isPermissionNameUnique(newPermission) & isPermissionNameValid(newPermission)) {
				securityManagerService.addPermission(newPermission);

				// updating permissions
				allPermissions.add(newPermission);
				permissionRoles.put(newPermission, new ArrayList<Role>());

				addInfoMessage("New permission added", newPermission.getName());
				newPermission = null;
				permissionAdded = true;
			} else {
				addErrorMessage("Adding new permission failed", "Permission name is not unique or permission name is not valid");
			}
		} else {
			addWarnMessage("There is no new permission to add", "");
		}
		RequestContext.getCurrentInstance().addCallbackParam("permissionAdded", permissionAdded);
	}

	public boolean isPermissionNameUnique(Permission permission) {
		for (Permission perm : allPermissions)
			if (permission.getServiceId().equals(perm.getServiceId()) && perm.getName().equals(permission.getName()))
				return false;
		return true;
	}

	public boolean isPermissionNameValid(Permission permission) {
		return !permission.getName().matches(".*(\\s|__|/).*");
	}

}
