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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.openiot.security.client.SecurityConstants.*;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class provides authorization information to the clients. TODO: We assume
 * that the utilizing entity is trusted! To be more secure we must also send the
 * calling entitiy's credentials to the OAuth server.
 * 
 * @author Mehdi Riahi
 * 
 */
public class AuthorizationManager {

	private static Logger logger = LoggerFactory.getLogger(AuthorizationManager.class);

	private Cache<OAuthorizationCredentials, Map<String, Set<Permission>>> cacheManager;

	private CasOAuthWrapperClient client;

	private boolean cachingEnabled = false;

	private String permissionsURL;

	private PermissionResolver permissionResolver = new WildcardPermissionResolver();

	public AuthorizationManager() {
		setCacheManager(new MemoryConstrainedCacheManager());
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager.<OAuthorizationCredentials, Map<String, Set<Permission>>> getCache("AuthorizationManager-Cache");
		if (cacheManager != null)
			cachingEnabled = true;
	}

	public void setPermissionsURL(String permissionsURL) {
		this.permissionsURL = permissionsURL;
	}

	public boolean isCachingEnabled() {
		return cachingEnabled;
	}

	public void setClient(CasOAuthWrapperClient client) {
		this.client = client;
	}

	public boolean hasPermission(String permStr, OAuthorizationCredentials credentials) {
		Permission perm = permissionResolver.resolvePermission(permStr);
		Map<String, Set<Permission>> authorizationInfo = getAuthorizationInfo(credentials);
		boolean hasPerm = false;
		for (Set<Permission> permSet : authorizationInfo.values()) {
			if (hasPerm)
				break;
			for (Permission permission : permSet)
				if (permission.implies(perm)) {
					hasPerm = true;
					break;
				}
		}
		return hasPerm;
	}

	public boolean hasRole(String role, OAuthorizationCredentials credentials) {
		Map<String, Set<Permission>> authorizationInfo = getAuthorizationInfo(credentials);
		return authorizationInfo.containsKey(role);
	}

	protected Map<String, Set<Permission>> getAuthorizationInfo(final OAuthorizationCredentials credentials) {
		Map<String, Set<Permission>> authorizationInfo = null;
		if (cachingEnabled)
			authorizationInfo = cacheManager.get(credentials);

		if (authorizationInfo == null) {
			authorizationInfo = getAuthorizationInfoInternal(credentials);

			if (cachingEnabled)
				cacheManager.put(credentials, authorizationInfo);
		}

		return authorizationInfo;
	}

	protected Map<String, Set<Permission>> getAuthorizationInfoInternal(final OAuthorizationCredentials credentials) {
		Map<String, Set<Permission>> map = new HashMap<String, Set<Permission>>();

		final String body = sendRequestForPermissions(credentials);
		JsonNode json = JsonHelper.getFirstNode(body);
		if (json != null) {
			json = json.get(ROLE_PERMISSIONS);
			if (json != null) {
				final Iterator<JsonNode> nodes = json.iterator();
				while (nodes.hasNext()) {
					for (Iterator<Entry<String, JsonNode>> fields = nodes.next().fields(); fields.hasNext();) {
						Entry<String, JsonNode> next = fields.next();

						logger.debug("next role: {}", next.getKey());

						final HashSet<Permission> permissionsSet = new HashSet<Permission>();
						map.put(next.getKey(), permissionsSet);
						Iterator<JsonNode> permIter = next.getValue().iterator();
						while (permIter.hasNext()) {
							json = permIter.next();
							String permission = json.asText();
							permissionsSet.add(permissionResolver.resolvePermission(permission));
							logger.debug("next permission: {}", permission);
						}
					}
				}
			}
		}

		return map;
	}

	protected String sendRequestForPermissions(final OAuthorizationCredentials credentials) {
		logger.debug("accessToken : {} / permissionsUrl : {}", credentials.getAccessToken(), permissionsURL);
		final long t0 = System.currentTimeMillis();
		final ProxyOAuthRequest request = new ProxyOAuthRequest(Verb.GET, permissionsURL, client.getConnectTimeout(), client.getReadTimeout(),
				client.getProxyHost(), client.getProxyPort());

		request.addQuerystringParameter(CLIENT_ID, credentials.getClientId());
		request.addQuerystringParameter(ACCESS_TOKEN, credentials.getAccessToken());

		final OAuthorizationCredentials callerCredentials = credentials.getCallerCredentials();
		if (callerCredentials != null) {
			request.addQuerystringParameter(CALLER_CLIENT_ID, callerCredentials.getClientId());
			request.addQuerystringParameter(CALLER_ACCESS_TOKEN, callerCredentials.getAccessToken());
		} else {
			// TODO: ?
		}

		final Response response = request.send();
		final int code = response.getCode();
		final String body = response.getBody();
		final long t1 = System.currentTimeMillis();
		logger.debug("Request took : " + (t1 - t0) + " ms for : " + permissionsURL);
		logger.debug("response code : {} / response body : {}", code, body);
		if (code != 200) {
			logger.error("Failed to get permissions, code : " + code + " / body : " + body);
			throw new HttpCommunicationException(code, body);
		}
		return body;
	}

}
