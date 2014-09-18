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

package org.openiot.security.client;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openiot.commons.util.PropertyManagement;

/**
 * This class represents a chain of OAuth credentials.
 * 
 * @author Mehdi Riahi
 * 
 */
public class OAuthorizationCredentials implements Serializable {

	private static final String LSM_RESOURCE_USER;
	private static final PropertyManagement props;
	static {
		props = new PropertyManagement();
		String namespace = props.getOpeniotResourceNamespace();
		LSM_RESOURCE_USER = namespace + (namespace.endsWith("/") ? "" : "/") + "user/";
	}

	private static final long serialVersionUID = 4880915177543108283L;

	private String accessToken;
	private String clientId;
	private String userId;
	private OAuthorizationCredentials callerCredentials;

	public OAuthorizationCredentials(String userId, String token, String clientId, OAuthorizationCredentials callerCredentials) {
		this.userId = userId;
		this.accessToken = token;
		this.clientId = clientId;
		this.callerCredentials = callerCredentials;
	}

	public OAuthorizationCredentials(String token, String clientId, OAuthorizationCredentials callerCredentials) {
		this(null, token, clientId, callerCredentials);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getClientId() {
		return clientId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserIdURI() {
		return LSM_RESOURCE_USER + userId;
	}

	public OAuthorizationCredentials getCallerCredentials() {
		return callerCredentials;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(userId);
		builder.append(accessToken);
		builder.append(clientId);
		builder.append(callerCredentials);
		return builder.build();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean containsToken(String token) {
		if (token == null)
			return false;
		return token.equals(getAccessToken()) || (callerCredentials != null && callerCredentials.containsToken(token));
	}

}
