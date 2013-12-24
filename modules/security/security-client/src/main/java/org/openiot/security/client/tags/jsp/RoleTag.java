package org.openiot.security.client.tags.jsp;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.shiro.web.tags.SecureTag;
import org.openiot.security.client.AccessControlUtil;

@SuppressWarnings("serial")
public abstract class RoleTag extends SecureTag {

	private String name = null;

	public RoleTag() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected void verifyAttributes() throws JspException {
		String role = getName();

		if (role == null || role.length() == 0) {
			String msg = "The 'name' tag attribute must be set.";
			throw new JspException(msg);
		}
	}

	protected boolean hasRole(String role) {
		return getSubject() != null && AccessControlUtil.getInstance().hasRole(role);
	}

	public int onDoStartTag() throws JspException {
		boolean show = showTagBody(getName());
		if (show) {
			return TagSupport.EVAL_BODY_INCLUDE;
		} else {
			return TagSupport.SKIP_BODY;
		}
	}

	protected abstract boolean showTagBody(String roleName);

}
