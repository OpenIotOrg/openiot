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

import io.buji.pac4j.ClientRealm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.client.BaseOAuthClient;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.scribe.model.OAuthConstants;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Mehdi Riahi
 *
 */
public class CasOAuthClientRealm extends ClientRealm {

	private static Logger log = LoggerFactory.getLogger(CasOAuthClientRealm.class);

	private String permissionsURL;

	private List<ClearCacheListener> clearCacheListeners = new ArrayList<>();

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {

		log.debug("clients : {}", getClients());
		final BaseOAuthClient<?> client = (BaseOAuthClient<?>) getClients().findClient("CasOAuthWrapperClient");
		log.debug("client : {}", client);

		final CasOAuthWrapperProfile profile = principals.oneByType(CasOAuthWrapperProfile.class);

		log.debug("profile: {} ", profile);

		final String accessToken = profile.getAccessToken();
		final ArrayNode roleNames = (ArrayNode) profile.getAttribute("role_name");

		log.debug("accessToken: {} , key : {} ", accessToken, client.getKey());

		// create simple authorization info
		final SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

		// add roles
		for (JsonNode element : roleNames) {
			String role = element.asText();
			log.debug("adding role: {}", role);
			simpleAuthorizationInfo.addRole(role);
		}

		// add default role
		simpleAuthorizationInfo.addRoles(split(getDefaultRoles()));

		// get permissions
		final String body = sendRequestForPermissions(accessToken, client);
		JsonNode json = JsonHelper.getFirstNode(body);
		if (json != null) {
			json = json.get("role_permissions");
			if (json != null) {
				final Iterator<JsonNode> nodes = json.iterator();
				while (nodes.hasNext()) {
					for (Iterator<Entry<String, JsonNode>> fields = nodes.next().fields(); fields.hasNext();) {
						Iterator<JsonNode> permIter = fields.next().getValue().iterator();
						while (permIter.hasNext()) {
							json = permIter.next();
							String permission = json.asText();
							simpleAuthorizationInfo.addStringPermission(permission);
							log.debug("next permission: {}", permission);
						}
					}
				}
			}
		}

		// add default permissions
		simpleAuthorizationInfo.addStringPermissions(split(getDefaultPermissions()));
		return simpleAuthorizationInfo;
	}

	protected String sendRequestForPermissions(final String accessToken, final BaseOAuthClient<?> client) {
		log.debug("accessToken : {} / permissionsUrl : {}", accessToken, permissionsURL);
		final long t0 = System.currentTimeMillis();
		final ProxyOAuthRequest request = new ProxyOAuthRequest(Verb.GET, permissionsURL, client.getConnectTimeout(), client.getReadTimeout(),
				client.getProxyHost(), client.getProxyPort());

		request.addQuerystringParameter(OAuthConstants.CLIENT_ID, client.getKey());
		request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken);

		// TODO: fix this
		request.addQuerystringParameter(SecurityConstants.CALLER_CLIENT_ID, client.getKey());
		request.addQuerystringParameter(SecurityConstants.CALLER_ACCESS_TOKEN, accessToken);

		final Response response = request.send();
		final int code = response.getCode();
		final String body = response.getBody();
		final long t1 = System.currentTimeMillis();
		log.debug("Request took : " + (t1 - t0) + " ms for : " + permissionsURL);
		log.debug("response code : {} / response body : {}", code, body);
		if (code != 200) {
			log.error("Failed to get permissions, code : " + code + " / body : " + body);
			throw new HttpCommunicationException(code, body);
		}
		return body;
	}

	public String getPermissionsURL() {
		return permissionsURL;
	}

	public void setPermissionsURL(String permissionsURL) {
		this.permissionsURL = permissionsURL;
	}

	public void addClearCacheListener(ClearCacheListener listener) {
		clearCacheListeners.add(listener);
	}

	@Override
	protected void doClearCache(PrincipalCollection principals) {
		for (ClearCacheListener listener : clearCacheListeners)
			listener.clearCache(principals);
	}

}
