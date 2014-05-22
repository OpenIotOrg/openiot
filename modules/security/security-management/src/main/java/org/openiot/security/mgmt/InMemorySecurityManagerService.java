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

/**
 * @author Mehdi Riahi
 *
 */
@ManagedBean(name = "securityManagerServiceIM")
@ApplicationScoped
public class InMemorySecurityManagerService implements SecurityManagerService {

	private Map<PermissionKey, Permission> permissions;
	private Map<String, User> users;
	private Map<RoleKey, Role> roles;
	private Map<Long, RegisteredService> registeredServices;

	public InMemorySecurityManagerService() {
		permissions = new HashMap<PermissionKey, Permission>();
		users = new HashMap<String, User>();
		roles = new HashMap<RoleKey, Role>();
		init();
	}

	public void init() {
		User adminUser = generateUser("Administrator", "admin@example.com", "admin", "5ebe2294ecd0e0f08eab7690d2a6ee69");
		User darkHelmetUser = generateUser("User P2", "darkh@example.com", "darkhelmet", "d9aaefa96ffeabb3a3bac5fdeadde3fa");
		User lonestarrUser = generateUser("User P3", "lonestarr@example.com", "lonestarr", "960c8c80adfcc7eee97eb6ebad135642");
		User presidentskroobUser = generateUser("User P1", "prskroob@example.com", "presidentskroob", "827ccb0eea8a706c4c34a16891f84e7b");

		User[] users = new User[] { adminUser, darkHelmetUser, lonestarrUser, presidentskroobUser };

		Role adminRole = new Role("admin", "Administrator role", 1L);
		Role adminRole3 = new Role("admin", "Administrator role", 3L);
		Role endUserRole = new Role("end_user", "End user role", 3L);
		Role schedulerRole = new Role("scheduler", "Scheduler role", 3L);
		Role serviceDefinerRole3 = new Role("service_definer", "Service definer role", 3L);
		Role serviceDefinerRole4 = new Role("service_definer", "Service definer role", 4L);
		Role visualizerRole = new Role("visualizer", "Data visualizer role", 3L);

		Role[] roles = new Role[] { adminRole, adminRole3, endUserRole, schedulerRole, serviceDefinerRole3, serviceDefinerRole4, visualizerRole };

		Permission allPerm = new Permission("*", "All permissions", 1L);
		Permission allPerm3 = new Permission("*", "All permissions", 3L);
		Permission adminDeleteSens2and3Perm3 = new Permission("admin:delete_sensor:s2,s3", "Delete streams s2 and s3", 3L);
		Permission sensorQuery1Perm3 = new Permission("sensor:query:s1", "Query stream s1", 3L);
		Permission sensorQuery2Perm4 = new Permission("sensor:query:s2", "Query stream s2", 4L);

		Permission[] permissions = new Permission[] { allPerm, allPerm3, adminDeleteSens2and3Perm3, sensorQuery1Perm3, sensorQuery2Perm4 };

		adminRole.addPermission(allPerm);
		adminRole3.addPermission(allPerm3);
		serviceDefinerRole3.addPermission(adminDeleteSens2and3Perm3);
		serviceDefinerRole3.addPermission(sensorQuery1Perm3);
		serviceDefinerRole4.addPermission(sensorQuery2Perm4);

		adminUser.addRole(adminRole);
		presidentskroobUser.addRole(serviceDefinerRole3);
		darkHelmetUser.setRoles(Arrays.asList(new Role[] { schedulerRole, endUserRole }));
		lonestarrUser.setRoles(Arrays.asList(new Role[] { visualizerRole, endUserRole, serviceDefinerRole4 }));

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
	public Permission getPermission(Long serviceId, String perId) {
		return permissions.get(new PermissionKey(serviceId, perId));
	}

	@Override
	public void deletePermission(Long serviceId, String perId) {
		permissions.remove(new PermissionKey(serviceId, perId));
	}

	@Override
	public void addPermission(Permission permission) {
		permissions.put(new PermissionKey(permission.getServiceId(), permission.getName()), permission);
	}

	@Override
	public Role getRole(Long serviceId, String roleId) {
		return roles.get(new RoleKey(serviceId, roleId));
	}

	@Override
	public void deleteRole(Long serviceId, String roleId) {
		roles.remove(new RoleKey(serviceId, roleId));
	}

	@Override
	public void addRole(Role role) {
		roles.put(new RoleKey(role.getServiceId(), role.getName()), role);
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
		if (user.getRoles() == null)
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

	public void removePermissionFromRole(Role role, Permission permission) {
		// TODO
	}

	@Override
	public void deleteRegisteredService(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LSMRegisteredServiceImpl addRegisteredService(LSMRegisteredServiceImpl service) {
		// TODO Auto-generated method stub
		return null;	
	}

}
