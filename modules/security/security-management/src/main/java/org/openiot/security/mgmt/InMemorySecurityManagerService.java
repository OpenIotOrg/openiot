package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;

@ManagedBean(name = "securityManagerServiceIM")
@ApplicationScoped
public class InMemorySecurityManagerService implements SecurityManagerService {

	private Map<String, Permission> permissions;
	private Map<String, User> users;
	private Map<String, Role> roles;
	private Map<Long, RegisteredService> registeredServices;

	public InMemorySecurityManagerService() {
		permissions = new HashMap<String, Permission>();
		users = new HashMap<String, User>();
		roles = new HashMap<String, Role>();
		init();
	}

	public void init() {
		User adminUser = generateUser("Administrator", "admin@example.com", "admin", "5ebe2294ecd0e0f08eab7690d2a6ee69");
		User darkHelmetUser = generateUser("User P2", "darkh@example.com", "darkhelmet", "d9aaefa96ffeabb3a3bac5fdeadde3fa");
		User lonestarrUser = generateUser("User P3", "lonestarr@example.com", "lonestarr", "960c8c80adfcc7eee97eb6ebad135642");
		User presidentskroobUser = generateUser("User P1", "prskroob@example.com", "presidentskroob", "827ccb0eea8a706c4c34a16891f84e7b");

		User[] users = new User[] { adminUser, darkHelmetUser, lonestarrUser, presidentskroobUser };

		Role adminRole = generateRole("admin", "Administrator role");
		Role endUserRole = generateRole("end_user", "End user role");
		Role schedulerRole = generateRole("scheduler", "Scheduler role");
		Role serviceDefinerRole = generateRole("service_definer", "Service definer role");
		Role visualizerRole = generateRole("visualizer", "Data visualizer role");

		Role[] roles = new Role[] { adminRole, endUserRole, schedulerRole, serviceDefinerRole, visualizerRole };

		Permission allPerm = generatePermission("*", "All permissions");
		Permission adminCreateUserPerm = generatePermission("admin:create_user", "Create new users");
		Permission adminDeleteSens1Perm = generatePermission("admin:delete_sensor:s1", "Delete stream s1");
		Permission adminDeleteSens2and3Perm = generatePermission("admin:delete_sensor:s2,s3", "Delete streams s2 and s3");
		Permission adminDeleteUsersPerm = generatePermission("admin:delete_user", "Delete existing users");
		Permission sensorDiscovery1Perm = generatePermission("sensor:discover:s1", "View stream s1");
		Permission sensorDiscovery2Perm = generatePermission("sensor:discover:s2", "View stream s2");
		Permission sensorQuery1Perm = generatePermission("sensor:query:s1", "Query stream s1");
		Permission sensorQuery2Perm = generatePermission("sensor:query:s2", "Query stream s2");

		Permission[] permissions = new Permission[] { allPerm, adminCreateUserPerm, adminDeleteSens1Perm, adminDeleteSens2and3Perm, adminDeleteUsersPerm,
				sensorDiscovery1Perm, sensorDiscovery2Perm, sensorQuery1Perm, sensorQuery2Perm };

		adminRole.addPermissionForService(3L, allPerm);
		serviceDefinerRole.addPermissionForService(3L, adminDeleteSens2and3Perm);
		serviceDefinerRole.addPermissionForService(3L, sensorQuery1Perm);
		serviceDefinerRole.addPermissionForService(4L, sensorQuery2Perm);
		visualizerRole.addPermissionForService(3L, adminCreateUserPerm);
		visualizerRole.addPermissionForService(3L, sensorQuery2Perm);

		adminUser.setRoles(new ArrayList<Role>(Arrays.asList(new Role[] { adminRole })));
		presidentskroobUser.setRoles(new ArrayList<Role>(Arrays.asList(new Role[] { serviceDefinerRole })));
		darkHelmetUser.setRoles(new ArrayList<Role>(Arrays.asList(new Role[] { schedulerRole, endUserRole })));
		lonestarrUser.setRoles(new ArrayList<Role>(Arrays.asList(new Role[] { visualizerRole, endUserRole, serviceDefinerRole })));

		for (Permission perm : permissions)
			addPermission(perm);

		for (Role role : roles)
			addRole(role);

		for (User user : users)
			addUser(user);

		registeredServices = new HashMap<Long, RegisteredService>();
		RegisteredService[] services = new RegisteredService[] { createDefaultCASService(), createDefaultHTTPService(), createRegisteredService3(),
				createRegisteredService4() };

		for (RegisteredService service : services) {
			registeredServices.put(service.getId(), service);
		}

	}

	private Permission generatePermission(String name, String des) {
		Permission per = new Permission();
		per.setDescription(des);
		per.setName(name);
		return per;
	}

	private Role generateRole(String name, String des) {
		Role role = new Role();
		role.setDescription(des);
		role.setName(name);
		return role;
	}

	private User generateUser(String name, String email, String username, String password) {
		User user = new User();
		user.setName(name);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);

		return user;
	}

	private LSMRegisteredServiceImpl createDefaultHTTPService() {
		LSMRegisteredServiceImpl reg_ser = new LSMRegisteredServiceImpl();
		reg_ser.setId((long) 2);
		reg_ser.setAllowedToProxy(true);
		reg_ser.setAnonymousAccess(false);
		reg_ser.setDescription("OAuth wrapper callback url");
		reg_ser.setEnabled(true);
		reg_ser.setEvaluationOrder(0);
		reg_ser.setIgnoreAttributes(false);
		reg_ser.setName("HTTP");
		reg_ser.setServiceId("https://localhost:8443/openiot-cas/oauth2.0/callbackAuthorize");
		reg_ser.setSsoEnabled(true);
		return reg_ser;
	}

	private LSMRegisteredServiceImpl createDefaultCASService() {
		LSMRegisteredServiceImpl reg_ser = new LSMRegisteredServiceImpl();
		reg_ser.setId((long) 1);
		reg_ser.setAllowedToProxy(true);
		reg_ser.setAnonymousAccess(false);
		reg_ser.setDescription("Service Manager");
		reg_ser.setEnabled(true);
		reg_ser.setEvaluationOrder(0);
		reg_ser.setIgnoreAttributes(false);
		reg_ser.setName("Service Manager");
		reg_ser.setServiceId("https://localhost:8443/openiot-cas/services/j_acegi_cas_security_check");
		reg_ser.setSsoEnabled(true);
		return reg_ser;
	}

	private LSMRegisteredServiceImpl createRegisteredService3() {
		LSMRegisteredServiceImpl reg_ser = new LSMRegisteredServiceImpl();
		reg_ser.setId((long) 3);
		reg_ser.setAllowedToProxy(true);
		reg_ser.setAnonymousAccess(false);
		reg_ser.setDescription("Service 3");
		reg_ser.setEnabled(true);
		reg_ser.setEvaluationOrder(0);
		reg_ser.setIgnoreAttributes(false);
		reg_ser.setName("Service 3");
		reg_ser.setServiceId("https://localhost:8443/service3");
		reg_ser.setSsoEnabled(true);
		return reg_ser;
	}

	private LSMRegisteredServiceImpl createRegisteredService4() {
		LSMRegisteredServiceImpl reg_ser = new LSMRegisteredServiceImpl();
		reg_ser.setId((long) 4);
		reg_ser.setAllowedToProxy(true);
		reg_ser.setAnonymousAccess(false);
		reg_ser.setDescription("Service 4");
		reg_ser.setEnabled(true);
		reg_ser.setEvaluationOrder(0);
		reg_ser.setIgnoreAttributes(false);
		reg_ser.setName("Service 4");
		reg_ser.setServiceId("https://localhost:8443/service4");
		reg_ser.setSsoEnabled(true);
		return reg_ser;
	}

	@Override
	public Permission getPermission(String perId) {
		return permissions.get(perId);
	}

	@Override
	public void deletePermission(String perId) {
		permissions.remove(perId);
	}

	@Override
	public void addPermission(Permission permission) {
		permissions.put(permission.getName(), permission);
	}

	@Override
	public Role getRole(String roleId) {
		return roles.get(roleId);
	}

	@Override
	public void deleteRole(String roleId) {
		roles.remove(roleId);
	}

	@Override
	public void addRole(Role role) {
		roles.put(role.getName(), role);
	}

	@Override
	public User getUser(String userId) {
		return users.get(userId);
	}

	@Override
	public void deleteUser(String userId) {
		users.remove(userId);
	}

	@Override
	public void addUser(User user) {
		if(user.getRoles() == null)
			user.setRoles(new ArrayList<Role>());
		users.put(user.getUsername(), user);
	}

	@Override
	public User getUserByUsername(String username) {
		return getUser(username);
	}

	@Override
	public User getUserByEmail(String email) {
		for (User user : users.values())
			if (user.getEmail().equals(email))
				return user;
		return null;
	}

	@Override
	public List<Role> getAllRoles() {
		return new ArrayList<Role>(roles.values());
	}

	@Override
	public List<User> getRoleUsers(Role role) {
		List<User> roleUsers = new ArrayList<User>();
		for (User user : users.values())
			if (user.getRoles().contains(role))
				roleUsers.add(user);
		return roleUsers;
	}

	@Override
	public List<User> getAllUsers() {
		return new ArrayList<User>(users.values());
	}

	@Override
	public List<Permission> getAllPermissions() {
		return new ArrayList<Permission>(permissions.values());
	}

	@Override
	public List<RegisteredService> getAllServices() {
		return new ArrayList<RegisteredService>(registeredServices.values());
	}

}
