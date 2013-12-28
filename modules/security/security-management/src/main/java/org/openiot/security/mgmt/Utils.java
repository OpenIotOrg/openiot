package org.openiot.security.mgmt;

import java.util.Collections;
import java.util.List;

import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;

public class Utils {
	@SuppressWarnings("unchecked")
	private static final List<?> emptyList = Collections.unmodifiableList(Collections.EMPTY_LIST);

	@SuppressWarnings("unchecked")
	public static final List<User> EmptyUserList = (List<User>) emptyList;

	@SuppressWarnings("unchecked")
	public static final List<Role> EmptyRoleList = (List<Role>) emptyList;

	@SuppressWarnings("unchecked")
	public static final List<Permission> EmptyPermissionList = (List<Permission>) emptyList;
}
