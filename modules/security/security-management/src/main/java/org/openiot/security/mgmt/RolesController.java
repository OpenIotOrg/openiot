package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.jasig.cas.services.RegisteredService;
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

	private Permission selectedPermission;

	private Permission selectedOtherPermission;

	private Map<Role, List<User>> roleUsers;

	private List<User> allUsers;

	private List<Permission> allPermissions;

	private Map<Long, RegisteredService> allServices;

	@SuppressWarnings("unchecked")
	private static final List<?> emptyList = Collections.unmodifiableList(Collections.EMPTY_LIST);

	@SuppressWarnings("unchecked")
	private static final List<User> EmptyUserList = (List<User>) emptyList;

	@SuppressWarnings("unchecked")
	private static final List<Map.Entry<RegisteredService, List<Permission>>> EmptyPermissionsPerServiceList = (List<Entry<RegisteredService, List<Permission>>>) emptyList;

	@SuppressWarnings("unchecked")
	private static final List<Permission> EmptyPermissionList = (List<Permission>) emptyList;

	@ManagedProperty(value = "#{securityManagerService}")
	private SecurityManagerService securityManagerService;

	private long selectedServiceId = -1;

	private String selectedServiceIdStr = "-1";

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
				allServices.put(registeredService.getId(), registeredService);
			}
		}
		return roles;
	}

	public RoleDataModel getRoleDataModel() {
		if (roleDataModel == null)
			roleDataModel = new RoleDataModel(getRoles());
		return roleDataModel;
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
			for (User roleUser : roleUsers.get(selectedRole)) {
				roleUser.getRoles().remove(selectedRole);
				securityManagerService.deleteUser(roleUser.getUsername());
				securityManagerService.addUser(roleUser);
			}
			securityManagerService.deleteRole(role.getName());

			setSelectedRole(null);
			setSelectedUser(null);
			setSelectedOtherUser(null);
			setSelectedServiceIdStr("-1");

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

	public void removePermission(Tuple2<RegisteredService, Permission> perm) {
		if (perm != null) {
			selectedRole.getPermissionsPerService().get(perm.getItem1().getId()).remove(perm.getItem2());
			// TODO This is extremely dangerous, replace with removePermissionFromRole() method
			securityManagerService.deleteRole(selectedRole.getName());
			securityManagerService.addRole(selectedRole);
			setSelectedServiceIdStr(null);
			addInfoMessage("Permission removed from the selected role", perm.getItem2().getName());
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

	public List<Map.Entry<RegisteredService, List<Permission>>> getSelectedRolePermissions() {
		List<Entry<RegisteredService, List<Permission>>> list = EmptyPermissionsPerServiceList;
		if (selectedRole != null) {
			Map<RegisteredService, List<Permission>> map = new HashMap<RegisteredService, List<Permission>>();
			for (Long key : selectedRole.getPermissionsPerService().keySet())
				map.put(allServices.get(key), new ArrayList<Permission>(selectedRole.getPermissionsPerService().get(key)));
			list = new ArrayList<Map.Entry<RegisteredService, List<Permission>>>(map.entrySet());
		}
		return list;
	}

	public List<Permission> getSelectedRoleOtherPermissions(long serviceId) {
		if (selectedRole != null && serviceId != -1) {
			selectedServiceId = serviceId;
			Set<Permission> currentPerms = selectedRole.getPermissionsPerService().get(serviceId);
			if (currentPerms == null)
				currentPerms = new HashSet<Permission>();
			List<Permission> otherPerms = new ArrayList<Permission>();
			for (Permission perm : allPermissions)
				if (!currentPerms.contains(perm))
					otherPerms.add(perm);
			return otherPerms;
		}
		return EmptyPermissionList;
	}

	public List<Permission> getSelectedRoleOtherPermissions() {
		return getSelectedRoleOtherPermissions(selectedServiceId);
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

	public Permission getSelectedPermission() {
		return selectedPermission;
	}

	public void setSelectedPermission(Permission selectedPermission) {
		this.selectedPermission = selectedPermission;
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

	public void setSelectedServiceIdStr(String selectedServiceIdStr) {
		this.selectedServiceIdStr = selectedServiceIdStr;
		try {
			setSelectedServiceId(Long.parseLong(selectedServiceIdStr));
		} catch (NumberFormatException e) {
			setSelectedServiceId(-1L);
		}
	}

	public List<RegisteredService> getAllServices() {
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
		if (newRole != null && newRole.getName().trim().length() > 0) {
			if (isRoleNameUnique(newRole)) {
				securityManagerService.addRole(newRole);

				// updating roles
				roles.add(newRole);
				roleDataModel.setWrappedData(roles);

				addInfoMessage("New role added", newRole.getName());
				newRole = null;
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
		if (selectedRole != null && selectedOtherPermission != null) {
			// TODO This is extremely dangerous, replace with addPermissionToRole() method
			selectedRole.addPermissionForService(selectedServiceId, selectedOtherPermission);

			securityManagerService.deleteRole(selectedRole.getName());
			securityManagerService.addRole(selectedRole);
			addInfoMessage("Permission <" + selectedOtherPermission.getName() + "> added to service <" + selectedServiceId + "> for role",
					selectedRole.getName());
		} else {
			addErrorMessage("Selected permission cannot be assigned to the selected role", "");
		}
	}

	public boolean isRoleNameUnique(Role role) {
		for (Role r : roles)
			if (r.getName().equals(role.getName()))
				return false;
		return true;
	}

	public void clearServiceId() {
		setSelectedServiceIdStr("-1");
	}

	public List<Tuple2<RegisteredService, Permission>> flatten(Map.Entry<RegisteredService, List<Permission>> entry) {
		List<Tuple2<RegisteredService, Permission>> output = new ArrayList<Tuple2<RegisteredService, Permission>>(entry.getValue().size());
		for (Permission perm : entry.getValue())
			output.add(new Tuple2<RegisteredService, Permission>(entry.getKey(), perm));
		return output;
	}

}
