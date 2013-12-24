package org.openiot.security.client.tags.jsp;

import org.apache.shiro.subject.Subject;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;

public class HasAnyRolesTag extends RoleTag {

	private static final long serialVersionUID = -2841686788672234133L;

	// Delimiter that separates role names in tag attribute
	private static final String ROLE_NAMES_DELIMETER = ",";

	public HasAnyRolesTag() {
	}

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
