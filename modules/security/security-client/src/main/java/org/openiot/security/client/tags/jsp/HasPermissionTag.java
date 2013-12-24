package org.openiot.security.client.tags.jsp;

public class HasPermissionTag extends PermissionTag {

	private static final long serialVersionUID = 4571963903452958138L;

	@Override
	protected boolean showTagBody(String p) {
		return isPermitted(p);
	}

}
