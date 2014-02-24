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

import static org.openiot.security.client.SecurityConstants.CALLER_ACCESS_TOKEN;
import static org.openiot.security.client.SecurityConstants.CALLER_CLIENT_ID;
import static org.openiot.security.client.SecurityConstants.ERROR;
import static org.openiot.security.client.SecurityConstants.ROLE_PERMISSIONS;
import static org.openiot.security.client.SecurityConstants.TARGET_CLIENT_ID;
import static org.openiot.security.client.SecurityConstants.USER_ACCESS_TOKEN;
import static org.openiot.security.client.SecurityConstants.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.model.OAuthConstants;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class provides authorization information to the clients. The permissions and roles are
 * obtained for the tokens on services as follows:
 * <ul>
 * <li>If OAuthorizationCredentials does not have caller credentials, then the authorization
 * information is retrieved and consulted for its token on its service (specified by clientId)</li>
 * <li>If OAuthorizationCredentials has caller credentials, but the caller does not have a caller
 * (i.e., chain of 1 credentials), then the authorization information is retrieved and consulted for
 * the caller's token on the current service (specified by clientId) unless the target service is
 * explicitly specified</li>
 * <li>If OAuthorizationCredentials has a caller credentials, which also has a caller (i.e., chain
 * of 3 credentials), then the authorization information is retrieved and consulted for the the last
 * caller's token on the current service (specified by clientId) unless the target service is
 * explicitly specified</li>
 * </ul>
 * If the target service is different than the current service, the user must have the required
 * permission for retrieving authorization information on the target service.
 * 
 * @author Mehdi Riahi
 * 
 */
public class AuthorizationManager implements ClearCacheListener {

	private static Logger logger = LoggerFactory.getLogger(AuthorizationManager.class);

	private Cache<CacheKey, Map<String, Set<Permission>>> cacheManager;

	private BaseOAuth20Client<?> client;

	private boolean cachingEnabled = false;

	private String permissionsURL;

	private PermissionResolver permissionResolver = new WildcardPermissionResolver();

	private boolean accessTokenExpired = false;

	public AuthorizationManager() {

	}

	public void setCacheManager(CacheManager cacheManager) {
		if (cacheManager != null) {
			logger.debug("Setting the cache manager to {}", cacheManager.getClass().getCanonicalName());
			this.cacheManager = cacheManager.<CacheKey, Map<String, Set<Permission>>> getCache("AuthorizationManager-Cache");
			cachingEnabled = true;
		}
	}

	public void setPermissionsURL(String permissionsURL) {
		this.permissionsURL = permissionsURL;
	}

	public boolean isCachingEnabled() {
		return cachingEnabled;
	}

	public void setClient(BaseOAuth20Client<?> client) {
		this.client = client;
	}

	/**
	 * Note that this might not be a real indication of expiry as we can get the information from
	 * the cache without contacting the server.
	 * 
	 * @return true if the access token has expired
	 */
	public boolean isAccessTokenExpired() {
		return accessTokenExpired;
	}

	/**
	 * Sends a request to the server to check if the token is expired.
	 * 
	 * @param credentials
	 * @return
	 */
	public boolean checkAccessTokenExpiry(OAuthorizationCredentials credentials) {
		if (cachingEnabled)
			cacheManager.remove(new CacheKey(credentials.getClientId(), credentials));
		getAuthorizationInfo(credentials, credentials.getClientId());
		return isAccessTokenExpired();
	}

	void reset() {
		accessTokenExpired = false;
		clearCache(null);
	}

	public boolean hasPermission(String permStr, OAuthorizationCredentials credentials) {
		if (credentials == null)
			return false;
		return hasPermission(permStr, credentials.getClientId(), credentials);
	}

	public boolean hasPermission(String permStr, String targetClientId, OAuthorizationCredentials credentials) {
		if (credentials == null)
			return false;
		Permission perm = permissionResolver.resolvePermission(permStr);

		Map<String, Set<Permission>> authorizationInfo = getAuthorizationInfo(credentials, targetClientId == null ? credentials.getClientId() : targetClientId);
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
		if (credentials == null)
			return false;
		return hasRole(role, credentials.getClientId(), credentials);
	}

	public boolean hasRole(String role, String targetClientId, OAuthorizationCredentials credentials) {
		if (credentials == null)
			return false;
		Map<String, Set<Permission>> authorizationInfo = getAuthorizationInfo(credentials, targetClientId == null ? credentials.getClientId() : targetClientId);
		return authorizationInfo.containsKey(role);
	}

	protected Map<String, Set<Permission>> getAuthorizationInfo(final OAuthorizationCredentials credentials, final String targetClientId) {
		Map<String, Set<Permission>> authorizationInfo = null;
		try {
			CacheKey key = new CacheKey(targetClientId, credentials);
			if (cachingEnabled)
				authorizationInfo = cacheManager.get(key);

			if (authorizationInfo == null) {
				authorizationInfo = getAuthorizationInfoInternal(credentials, targetClientId);

				if (cachingEnabled)
					cacheManager.put(key, authorizationInfo);
			}
		} catch (AccessTokenExpiredException e) {
			accessTokenExpired = true;
			clearCache(null);
		}

		return authorizationInfo;
	}

	protected Map<String, Set<Permission>> getAuthorizationInfoInternal(final OAuthorizationCredentials credentials, final String targetClientId)
			throws AccessTokenExpiredException {
		Map<String, Set<Permission>> map = new HashMap<String, Set<Permission>>();

		final String body = sendRequestForPermissions(credentials, targetClientId);
		JsonNode json = JsonHelper.getFirstNode(body);
		if (json != null) {
			JsonNode errorNode = json.get(ERROR);
			if (errorNode != null) {
				logger.info("Error returned: {}", errorNode.asText());
				if (EXPIRED_ACCESS_TOKEN.equals(errorNode.asText()))
					accessTokenExpired = true;
			} else {
				accessTokenExpired = false;
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
		}

		return map;
	}

	protected String sendRequestForPermissions(final OAuthorizationCredentials credentials, final String targetClientId) {
		logger.debug("accessToken : {} / permissionsUrl : {}", credentials.getAccessToken(), permissionsURL);
		final long t0 = System.currentTimeMillis();
		final ProxyOAuthRequest request = new ProxyOAuthRequest(Verb.GET, permissionsURL, client.getConnectTimeout(), client.getReadTimeout(),
				client.getProxyHost(), client.getProxyPort());

		request.addQuerystringParameter(OAuthConstants.CLIENT_ID, credentials.getClientId());
		request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, credentials.getAccessToken());

		String userToken = credentials.getAccessToken();
		final OAuthorizationCredentials callerCredentials = credentials.getCallerCredentials();

		if (callerCredentials != null) {
			final OAuthorizationCredentials userCredentials = callerCredentials.getCallerCredentials();
			if (userCredentials != null) {
				userToken = userCredentials.getAccessToken();
				request.addQuerystringParameter(USER_CLIENT_ID, userCredentials.getClientId());

				request.addQuerystringParameter(CALLER_CLIENT_ID, callerCredentials.getClientId());
				request.addQuerystringParameter(CALLER_ACCESS_TOKEN, callerCredentials.getAccessToken());
			} else {
				userToken = callerCredentials.getAccessToken();
				request.addQuerystringParameter(USER_CLIENT_ID, callerCredentials.getClientId());
			}
		}

		request.addQuerystringParameter(USER_ACCESS_TOKEN, userToken);
		request.addQuerystringParameter(TARGET_CLIENT_ID, targetClientId);

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

	@Override
	public void clearCache(PrincipalCollection principals) {
		if (cachingEnabled) {
			cacheManager.clear();
		}
	}

	private static class CacheKey {
		String targetClientId;
		OAuthorizationCredentials credentials;

		public CacheKey(String targetClientId, OAuthorizationCredentials credentials) {
			super();
			this.targetClientId = targetClientId;
			this.credentials = credentials;
		}

		@Override
		public int hashCode() {
			HashCodeBuilder builder = new HashCodeBuilder();
			builder.append(targetClientId);
			builder.append(credentials);
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

	}

}
