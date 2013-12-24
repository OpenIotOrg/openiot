package org.openiot.security.client.tags.jsf;

import javax.faces.view.facelets.TagConfig;

import org.apache.shiro.web.faces.tags.PermissionTagHandler;
import org.openiot.security.client.AccessControlUtil;

public class LacksRoleTag extends PermissionTagHandler {

	public LacksRoleTag(TagConfig config) {
		super(config);
	}

	@Override
	protected boolean showTagBody(String roleName) {
		return getSubject() == null || !AccessControlUtil.getInstance().hasRole(roleName);
	}

}
