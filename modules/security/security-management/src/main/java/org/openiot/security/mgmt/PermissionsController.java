package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

	@SuppressWarnings("unchecked")
	private static final List<?> emptyList = Collections.unmodifiableList(Collections.EMPTY_LIST);

	@SuppressWarnings("unchecked")
	private static final List<User> EmptyUserList = (List<User>) emptyList;

	@SuppressWarnings("unchecked")
	private static final List<Map.Entry<RegisteredService, List<Role>>> EmptyRolesPerServiceList = (List<Entry<RegisteredService, List<Role>>>) emptyList;

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
				allServices.put(registeredService.getId(), registeredService);
			}
		}
		return allPermissions;
	}

	public void setSecurityManagerService(SecurityManagerService securityManagerService) {
		this.securityManagerService = securityManagerService;
	}

	public void removePermission(Permission permission) {
		// TODO: extremely dangerous! replace with removePermission(permission)
		if (permission.equals(selectedPermission)) {
			List<Entry<RegisteredService, List<Role>>> selectedPermissionRoles = getSelectedPermissionRoles();
			Set<Role> permRoles = new HashSet<Role>();

			for (Entry<RegisteredService, List<Role>> entry : selectedPermissionRoles)
				permRoles.addAll(entry.getValue());

			for (Role role : permRoles) {
				for (Iterator<Set<Permission>> iterator = role.getPermissionsPerService().values().iterator(); iterator.hasNext();)
					iterator.next().remove(selectedPermission);
				securityManagerService.deleteRole(role.getName());
				securityManagerService.addRole(role);
			}

			securityManagerService.deletePermission(selectedPermission.getName());
			allPermissions.remove(selectedPermission);
			addInfoMessage("Permission deleted", permission.getName());
			setSelectedPermission(null);
		}
	}

	public List<User> getSelectedPermissionUsers() {
		Set<User> permUsers = new HashSet<User>();
		if (selectedPermission != null) {
			for (User user : allUsers) {
				if(user.getRoles() == null)
					user.setRoles(new ArrayList<Role>());
				for (Role role : user.getRoles()) {
					for (Set<Permission> permissionSet : role.getPermissionsPerService().values())
						if (permissionSet.contains(selectedPermission))
							permUsers.add(user);
				}
			}
			return new ArrayList<User>(permUsers);
		}
		return EmptyUserList;
	}

	public List<Map.Entry<RegisteredService, List<Role>>> getSelectedPermissionRoles() {
		// TODO: cache
		List<Entry<RegisteredService, List<Role>>> list = EmptyRolesPerServiceList;
		if (selectedPermission != null) {
			Map<RegisteredService, List<Role>> map = new HashMap<RegisteredService, List<Role>>();
			for (Role role : allRoles) {
				for (Entry<Long, Set<Permission>> entry : role.getPermissionsPerService().entrySet()) {
					RegisteredService key = allServices.get(entry.getKey());
					if (entry.getValue().contains(selectedPermission)) {
						List<Role> roleList;
						if (map.containsKey(key))
							roleList = map.get(key);
						else {
							roleList = new ArrayList<Role>();
							map.put(key, roleList);
						}
						roleList.add(role);
					}
				}
			}

			list = new ArrayList<Map.Entry<RegisteredService, List<Role>>>(map.entrySet());
		}
		return list;
	}

	public Permission getSelectedPermission() {
		return selectedPermission;
	}

	public void setSelectedPermission(Permission selectedPermission) {
		this.selectedPermission = selectedPermission;
	}

	public List<RegisteredService> getAllServices() {
		return new ArrayList<RegisteredService>(allServices.values());
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
		if (newPermission != null && newPermission.getName().trim().length() > 0) {
			if (isPermissionNameUnique(newPermission)) {
				securityManagerService.addPermission(newPermission);

				// updating permissions
				allPermissions.add(newPermission);

				addInfoMessage("New permission added", newPermission.getName());
				newPermission = null;
				permissionAdded = true;
			} else {
				addErrorMessage("Adding new permission failed", "Permission name is not unique");
			}
		} else {
			addWarnMessage("There is no new permission to add", "");
		}
		RequestContext.getCurrentInstance().addCallbackParam("permissionAdded", permissionAdded);
	}

	public boolean isPermissionNameUnique(Permission role) {
		for (Permission perm : allPermissions)
			if (perm.getName().equals(role.getName()))
				return false;
		return true;
	}

	public List<Tuple2<RegisteredService, Role>> flatten(Map.Entry<RegisteredService, List<Role>> entry) {
		List<Tuple2<RegisteredService, Role>> output = new ArrayList<Tuple2<RegisteredService, Role>>(entry.getValue().size());
		for (Role role : entry.getValue())
			output.add(new Tuple2<RegisteredService, Role>(entry.getKey(), role));
		return output;
	}

}
