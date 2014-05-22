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

import java.util.List;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;

/**
 * @author Mehdi Riahi
 *
 */
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

	public abstract void deleteRegisteredService(long id);

	public abstract LSMRegisteredServiceImpl addRegisteredService(LSMRegisteredServiceImpl service);

}