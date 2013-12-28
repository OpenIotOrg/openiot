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

import io.buji.pac4j.ShiroWebContext;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessControlUtil {

	private static Logger logger = LoggerFactory.getLogger(AccessControlUtil.class);

	private static AccessControlUtil instance;

	private CasOAuthClientRealm casOAuthClientRealm;

	private CasOAuthWrapperClient client;

	private AuthorizationManager authorizationManager;

	public static AccessControlUtil getInstance() {
		if (instance == null)
			instance = new AccessControlUtil();
		return instance;
	}

	private AccessControlUtil() {

	}

//	public boolean hasPermission(String perm) {
//		return SecurityUtils.getSubject().isPermitted(perm);
//	}
	
	public boolean hasPermission(String perm) {
		return hasPermission(perm, getOAuthorizationCredentials());
	}
	
	public boolean hasPermission(String permStr, OAuthorizationCredentials credentials) {
		return getAuthorizationManager().hasPermission(permStr, credentials);
	}

//	public boolean hasRole(String role) {
//		return SecurityUtils.getSubject().hasRole(role);
//	}
	
	public boolean hasRole(String role) {
		return hasRole(role, getOAuthorizationCredentials());
	}
	
	public boolean hasRole(String role, OAuthorizationCredentials credentials) {
		return getAuthorizationManager().hasRole(role, credentials);
	}

	public String getLoginUrl(HttpServletRequest req, HttpServletResponse resp) {
		return getClient().getRedirectionUrl(new ShiroWebContext(req, resp), true);
	}

	public AuthorizationManager getAuthorizationManager() {
		if (authorizationManager == null) {
			authorizationManager = new AuthorizationManager();
			authorizationManager.setClient((CasOAuthWrapperClient) getClient());
			authorizationManager.setPermissionsURL(getCasOAuthClientRealm().getPermissionsURL());
			getCasOAuthClientRealm().addClearCacheListener(authorizationManager);
		}
		return authorizationManager;
	}

	public OAuthorizationCredentials getOAuthorizationCredentials() {
		final Subject subject = SecurityUtils.getSubject();
		if(!subject.isAuthenticated())
			return null;
		final CasOAuthWrapperProfile profile = subject.getPrincipals().oneByType(CasOAuthWrapperProfile.class);
		String accessToken = profile.getAccessToken();
		String clientId = getClient().getKey();
		return new OAuthorizationCredentials(accessToken, clientId, null);
	}

	public void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String url = getLoginUrl(req, resp);
		logger.debug("redirecting to loginUrl: {} ", url);

		WebUtils.saveRequest(req);
		WebUtils.issueRedirect(req, resp, url);
	}

	private CasOAuthClientRealm getCasOAuthClientRealm() {
		if (casOAuthClientRealm == null) {
			SecurityManager securityManager = SecurityUtils.getSecurityManager();
			RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securityManager;
			Collection<Realm> realms = realmSecurityManager.getRealms();
			for (Realm realm : realms)
				if (realm instanceof CasOAuthClientRealm) {
					casOAuthClientRealm = (CasOAuthClientRealm) realm;
					break;
				}

		}
		return casOAuthClientRealm;
	}

	private CasOAuthWrapperClient getClient() {
		if (client == null)
			client = (CasOAuthWrapperClient) getCasOAuthClientRealm().getClients().findClient("CasOAuthWrapperClient");

		return client;
	}
}
