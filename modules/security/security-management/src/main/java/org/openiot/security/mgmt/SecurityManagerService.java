package org.openiot.security.mgmt;

import java.util.List;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;

public interface SecurityManagerService {

	public abstract void addPermission(Permission permission);

	public abstract void addRole(Role role);

	public abstract Permission getPermission(Long serviceId, String permName);

	public abstract void deletePermission(Long serviceId, String permName);

	public abstract Role getRole(Long serviceId, String roleName);

	public abstract void deleteRole(Long serviceId, String roleName);

	public abstract User getUser(String userId);

	public abstract void deleteUser(String userId);

	public abstract void addUser(User user);

	/**
	 * Retrieves a user by the username
	 * 
	 * @param username
	 * @return
	 */
	public abstract User getUserByUsername(String username);

	/**
	 * Retrieves a user by the email
	 * 
	 * @param email
	 * @return
	 */
	public abstract User getUserByEmail(String email);

	public abstract List<Role> getAllRoles();

	public abstract List<User> getRoleUsers(Role role);

	public abstract List<User> getAllUsers();

	public abstract List<Permission> getAllPermissions();

	public abstract List<RegisteredService> getAllServices();

	public abstract void removePermissionFromRole(Role role, Permission permission);

}