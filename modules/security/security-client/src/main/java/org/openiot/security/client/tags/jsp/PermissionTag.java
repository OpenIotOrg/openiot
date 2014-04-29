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

package org.openiot.security.client.tags.jsp;

import org.apache.shiro.web.tags.SecureTag;
import org.openiot.security.client.AccessControlUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author Mehdi Riahi
 *
 */
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
