package org.openiot.security.mgmt;

import org.openiot.commons.util.Tuple2;

public class PermissionKey extends Tuple2<Long, String> {

	public PermissionKey(Long serviceId, String permissionName) {
		super(serviceId, permissionName);
	}

	public Long getServiceId() {
		return getItem1();
	}

	public String getPermissionName() {
		return getItem2();
	}

}
