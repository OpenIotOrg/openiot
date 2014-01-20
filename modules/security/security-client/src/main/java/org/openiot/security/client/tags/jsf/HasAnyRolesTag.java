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

package org.openiot.security.client.tags.jsf;

import javax.faces.view.facelets.TagConfig;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.faces.tags.PermissionTagHandler;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;

/**
 * @author Mehdi Riahi
 *
 */
public class HasAnyRolesTag extends PermissionTagHandler {

	public HasAnyRolesTag(TagConfig config) {
		super(config);
	}

	// Delimiter that separates role names in tag attribute
	private static final String ROLE_NAMES_DELIMETER = ",";


	protected boolean showTagBody(String roleNames) {
		boolean hasAnyRole = false;
		AccessControlUtil instance = AccessControlUtil.getInstance();
		OAuthorizationCredentials oAuthorizationCredentials = instance.getOAuthorizationCredentials();

		Subject subject = getSubject();

		if (subject != null) {

			// Iterate through roles and check to see if the user has one of the roles
			for (String role : roleNames.split(ROLE_NAMES_DELIMETER)) {

				if (instance.hasRole(role.trim(), oAuthorizationCredentials)) {
					hasAnyRole = true;
					break;
				}

			}

		}

		return hasAnyRole;
	}

}
