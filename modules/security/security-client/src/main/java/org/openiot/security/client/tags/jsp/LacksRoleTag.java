package org.openiot.security.client.tags.jsp;

public class LacksRoleTag extends RoleTag {

	private static final long serialVersionUID = 8420332042877629627L;

	@Override
	protected boolean showTagBody(String roleName) {
		return !hasRole(roleName);
	}

}
