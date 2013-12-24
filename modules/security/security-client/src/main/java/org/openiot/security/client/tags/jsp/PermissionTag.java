package org.openiot.security.client.tags.jsp;

import org.apache.shiro.web.tags.SecureTag;
import org.openiot.security.client.AccessControlUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

@SuppressWarnings("serial")
public abstract class PermissionTag extends SecureTag {

	private String name = null;

    public PermissionTag() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void verifyAttributes() throws JspException {
        String permission = getName();

        if (permission == null || permission.length() == 0) {
            String msg = "The 'name' tag attribute must be set.";
            throw new JspException(msg);
        }
    }

    public int onDoStartTag() throws JspException {

        String p = getName();

        boolean show = showTagBody(p);
        if (show) {
            return TagSupport.EVAL_BODY_INCLUDE;
        } else {
            return TagSupport.SKIP_BODY;
        }
    }

    protected boolean isPermitted(String p) {
        return getSubject() != null && AccessControlUtil.getInstance().hasPermission(p);
    }

    protected abstract boolean showTagBody(String p);

}
