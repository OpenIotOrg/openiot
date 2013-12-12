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

package org.openiot.lsm.security.oauth.mgmt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Role implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3752268111741124575L;
	private String name;
	private String description;
	private Map<Long, Set<Permission>> permissionsPerService;

	public Role() {
		permissionsPerService = new HashMap<Long, Set<Permission>>();
	}

	public boolean addPermissionForService(Long serviceId, Permission permission) {
		Set<Permission> permissions = permissionsPerService.get(serviceId);
		if (permissions == null) {
			permissions = new HashSet<Permission>();
			permissionsPerService.put(serviceId, permissions);
		}
		if (!permissions.contains(permission)) {
			permissions.add(permission);
			return true;
		}

		return false;
	}

	public Map<Long, Set<Permission>> getPermissionsPerService() {
		return permissionsPerService;
	}

	
	public void setPermissionsPerService(
			Map<Long, Set<Permission>> permissionsPerService) {
		this.permissionsPerService = permissionsPerService;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
