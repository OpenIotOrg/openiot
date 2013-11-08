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

package org.openiot.security.oauth.lsm.mgmt;

import java.util.List;

/**
 * This class is responsible for retrieving and persisting OpenIoT
 * authentication and access control management objects.
 * 
 * @author Mehdi Riahi
 * 
 */
public abstract class LSMAbstractSecurityManager {

	/********************************
	 * To be retrieved from LSM *
	 ********************************/
	public abstract User getUserById(Long userId);

	/********************************
	 * To be retrieved from LSM *
	 ********************************/
	public abstract List<Role> getAllRoles();

	/********************************
	 * To be retrieved from LSM *
	 ********************************/
	public abstract List<Permission> getAllPermissions();

	/********************************
	 * To be retrieved from LSM *
	 ********************************/
	public abstract boolean addRoleToUser(User user, Role role);

	/********************************
	 * To be retrieved from LSM *
	 ********************************/
	public abstract boolean addPermissionToRole(Role role, Permission permission, Long serviceId);

	/********************************
	 * To be retrieved from LSM *
	 ********************************/
	public abstract boolean saveUser(User user, char[] passwd);

	/********************************
	 * To be retrieved from LSM *
	 ********************************/
	public abstract boolean savePermission(Permission permission);

	/********************************
	 * To be retrieved from LSM *
	 ********************************/
	public abstract boolean saveRole(Role role);
}
