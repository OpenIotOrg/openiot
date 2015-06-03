/**
 *    Copyright (c) 2011-2014, OpenIoT
 *
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

package org.openiot.ui.request.definition.web.scopes.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.openiot.ui.request.definition.web.scopes.session.base.DisposableContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "sessionBean")
@SessionScoped
public class SessionBean implements Serializable {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionBean.class);
	private static final long serialVersionUID = 1L;

	private Map<String, DisposableContext> contextMap;
	private String userId;

	/**
	 * Creates a new instance of SessionBean
	 */
	public SessionBean() {
		contextMap = new HashMap<String, DisposableContext>();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	// ------------------------------------
	// Context API
	// ------------------------------------
	public DisposableContext getContext(String contextName) {
		if (contextMap.containsKey(contextName)) {
			return contextMap.get(contextName);
		}

		return null;
	}

	public void purgeContext(DisposableContext context) {
		if (contextMap.containsValue(context)) {
			for (Map.Entry<String, DisposableContext> entry : contextMap.entrySet()) {
				if (entry.getValue().equals(context)) {
					contextMap.remove(entry.getKey());
					LOGGER.trace("SessionBean: purged context '" + entry.getKey() + "' of type " + entry.getValue().getClass().getSimpleName());
					return;
				}
			}
		}
	}

	public void registerContext(DisposableContext context) {
		if (contextMap.containsValue(context)) {
			LOGGER.trace("SessionBean: overwriting old context '" + context.getContextUID() + "' of type " + context.getClass().getSimpleName());
		}
		contextMap.put(context.getContextUID(), context);
		LOGGER.trace("SessionBean: registered context '" + context.getContextUID() + "' of type " + context.getClass().getSimpleName());
	}

	public String addToFlashScopeAndRedirect(String key, String value, String redirectTo) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put(key, value);
		return redirectTo;
	}

	public Map<String, DisposableContext> getContextMap() {
		return contextMap;
	}

}
