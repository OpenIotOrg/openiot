package org.openiot.security.client.tags.jsf;

import javax.faces.view.facelets.TagConfig;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.faces.tags.PermissionTagHandler;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;

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
