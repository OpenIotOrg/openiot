package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;

@ManagedBean
@ViewScoped
public class UsersController extends AbstractController {
	// Are you sure you want to delete Role '#{role.name}'?&lt;br&gt;It will be revoke from the
	// users having this role.
	private static final long serialVersionUID = 5365682876974798395L;

	private Role selectedRole;

	private Role newRole;

	private User selectedUser;

	private Role selectedOtherRole;

	private Map<Role, List<User>> roleUsers;

	private List<User> allUsers;

	private List<Role> allRoles;

	private Map<Long, RegisteredService> allServices;

	@SuppressWarnings("unchecked")
	private static final List<?> emptyList = Collections.unmodifiableList(Collections.EMPTY_LIST);

	@SuppressWarnings("unchecked")
	private static final List<Role> EmptyRoleList = (List<Role>) emptyList;

	@SuppressWarnings("unchecked")
	private static final List<Map.Entry<RegisteredService, List<Permission>>> EmptyPermissionsPerServiceList = (List<Entry<RegisteredService, List<Permission>>>) emptyList;

	@ManagedProperty(value = "#{securityManagerService}")
	private SecurityManagerService securityManagerService;

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
			for (Iterator<Role> iterator = selectedUser.getRoles().iterator(); iterator.hasNext();) {
				Role r = iterator.next();
				if (r.equals(role)) {
					iterator.remove();
					break;
				}
			}

			securityManagerService.deleteUser(selectedUser.getUsername());
			securityManagerService.addUser(selectedUser);
			addInfoMessage("Role revoked from the selected user", role.getName());
		}
		if (role.equals(selectedRole)) {
			setSelectedRole(null);
		}
	}

	public void removeUser(User user) {
		addInfoMessage("User Deleted", user.getUsername());
		if (user.equals(selectedUser)) {
			allUsers.remove(user);
			securityManagerService.deleteUser(selectedUser.getUsername());

			setSelectedUser(null);
		}
	}

	public List<Role> getSelectedUserRoles() {
		List<Role> userRoles = EmptyRoleList;
		if (selectedUser != null) {
			if (selectedUser.getRoles() == null)
				selectedUser.setRoles(new ArrayList<Role>());
			else
				userRoles = selectedUser.getRoles();
		}
		return userRoles;
	}

	public List<Role> getSelectedUserOtherRoles() {
		if (selectedUser != null) {
			List<Role> currentRoles = selectedUser.getRoles();
			List<Role> roles = new ArrayList<Role>();

			for (Role role : allRoles)
				if (currentRoles == null || !currentRoles.contains(role))
					roles.add(role);
			return roles;
		}
		return EmptyRoleList;
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

	public Role getNewRole() {
		if (newRole == null)
			newRole = new Role();
		return newRole;
	}

	public void cancelAddRole() {
		newRole = null;
	}

	public void addRole() {
		// TODO: extremely dangerous! replace with grantRoleToUser(user, role)
		if (selectedOtherRole != null && selectedUser != null) {
			selectedUser.getRoles().add(selectedOtherRole);
			securityManagerService.deleteUser(selectedUser.getUsername());
			securityManagerService.addUser(selectedUser);
			addInfoMessage("Role added to the selected user <" + selectedUser.getUsername() + ">", selectedOtherRole.getName());
		} else {
			addWarnMessage("There is no new role to add", "");
		}
	}

	public List<Tuple2<RegisteredService, Permission>> flatten(Map.Entry<RegisteredService, List<Permission>> entry) {
		List<Tuple2<RegisteredService, Permission>> output = new ArrayList<Tuple2<RegisteredService, Permission>>(entry.getValue().size());
		for (Permission perm : entry.getValue())
			output.add(new Tuple2<RegisteredService, Permission>(entry.getKey(), perm));
		return output;
	}

}
