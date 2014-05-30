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

import java.util.Collections;
import java.util.List;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.openiot.security.client.AccessControlUtil;

/**
 * @author Mehdi Riahi
 * 
 */
public class Utils {
	public static final String USE_CAPTCHA_PROP = "security.signup.useCaptcha";
	public static final String AUTOMATIC_SERVICE_SETUP = "security.automaticServiceSetup";
	public static final String DEMO_SERVICES = "security.demoServices";

	@SuppressWarnings("unchecked")
	private static final List<?> emptyList = Collections.unmodifiableList(Collections.EMPTY_LIST);

	@SuppressWarnings("unchecked")
	public static final List<User> EmptyUserList = (List<User>) emptyList;

	@SuppressWarnings("unchecked")
	public static final List<Role> EmptyRoleList = (List<Role>) emptyList;

	@SuppressWarnings("unchecked")
	public static final List<Permission> EmptyPermissionList = (List<Permission>) emptyList;

	
	private static PropertyManagement props = new PropertyManagement();

	public static PropertyManagement getPropertyManagement() {
		return props;
	}

	public static boolean isDemoEnabled() {
		PropertyManagement props = Utils.getPropertyManagement();
		String propValue = props.getProperty(AUTOMATIC_SERVICE_SETUP, "false");
		return propValue.equalsIgnoreCase("true") ? true : false;
	}
	
	public static final AccessControlUtil acUtil = AccessControlUtil.getInstance();

}
