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

public class Permission implements java.io.Serializable {

	private static final long serialVersionUID = 2822756601832497898L;
	private String name;
	private String description;
	private Long serviceId;

	public Permission() {

	}

	public Permission(String name, String description, Long serviceId) {
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		if (serviceId == null || serviceId < 0)
			throw new IllegalArgumentException("Invalide serviceId: " + serviceId);
		this.name = name;
		this.description = description;
		this.serviceId = serviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		if (serviceId == null || serviceId < 0)
			throw new IllegalArgumentException("Invalide serviceId: " + serviceId);
		this.serviceId = serviceId;
	}

	public boolean isValid() {
		return name != null && serviceId != null && serviceId >= 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
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
		Permission other = (Permission) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Permission [name=" + name + ", description=" + description + ", serviceId=" + serviceId + "]";
	}

	/**
	 * @param permissionIdStr
	 *            in the format "serviceId"+<code>serviceIdRoleIdSeparator</code>+"name"
	 * @return
	 */
	public static Permission fromPermissionIdStr(String permissionIdStr) {
		String[] split = permissionIdStr.split(Role.ServiceIdRoleIdSeparator);
		if (split.length == 2) {
			try {
				long serviceId = Long.parseLong(split[0]);
				Permission permission = new Permission();
				permission.setServiceId(serviceId);
				permission.setName(split[1]);
				return permission;
			} catch (NumberFormatException e) {

			}
		}
		return null;
	}

	public static String toPermissionIdStr(Permission permission) {
		return toPermissionIdStr(permission.getName(), permission.getServiceId());
	}

	public static String toPermissionIdStr(String name, Long serviceId) {
		return serviceId + Role.ServiceIdRoleIdSeparator + name;
	}

}
