package org.openiot.security.client.tags.jsp;

public class HasRoleTag extends RoleTag {

	private static final long serialVersionUID = -6745783867843309574L;

	@Override
	protected boolean showTagBody(String roleName) {
		return hasRole(roleName);
	}

}
