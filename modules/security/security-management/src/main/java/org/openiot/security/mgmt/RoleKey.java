package org.openiot.security.mgmt;

import org.openiot.commons.util.Tuple2;

public class RoleKey extends Tuple2<Long, String> {

	public RoleKey(Long serviceId, String roleName) {
		super(serviceId, roleName);
	}

	public Long getServiceId() {
		return getItem1();
	}

	public String getRoleName() {
		return getItem2();
	}

}
