package org.openiot.security.client.tags.jsp;

public class LacksPermissionTag extends PermissionTag {

	private static final long serialVersionUID = -6394532998234499857L;

	@Override
	protected boolean showTagBody(String p) {
		return !isPermitted(p);
	}

}
