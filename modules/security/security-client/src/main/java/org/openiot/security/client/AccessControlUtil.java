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
import io.buji.pac4j.ShiroWebContext;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mehdi Riahi
 * 
 */
public abstract class AccessControlUtil {

	private static Logger logger = LoggerFactory.getLogger(AccessControlUtil.class);

	private static AccessControlUtilWeb instanceWeb;
	private static AccessControlUtilRest instanceRest;
	
	private ClientRealm clientRealm;

	private BaseOAuth20Client<?> client;

	private AuthorizationManager authorizationManager;

	public static AccessControlUtil getInstance() {
		if (instanceWeb == null)
			instanceWeb = new AccessControlUtilWeb();
		return instanceWeb;
	}
	
	public static AccessControlUtil getRestInstance() {
		if (instanceRest == null)
			instanceRest = new AccessControlUtilRest();
		return instanceRest;
	}

	public abstract OAuthorizationCredentials login(String username, String password);

	public abstract void logout();
	
	public abstract void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException;

	public boolean hasPermission(String perm) {
		return hasPermission(perm, getOAuthorizationCredentials());
	}

	public boolean hasPermission(String permStr, OAuthorizationCredentials credentials) {
		return getAuthorizationManager().hasPermission(permStr, credentials);
	}

	public boolean hasRole(String role) {
		return hasRole(role, getOAuthorizationCredentials());
	}

	public boolean hasRole(String role, OAuthorizationCredentials credentials) {
		return getAuthorizationManager().hasRole(role, credentials);
	}

	public AuthorizationManager getAuthorizationManager() {
		if (authorizationManager == null) {
			ACRealm acRealm = (ACRealm) getCasOAuthClientRealm();
			authorizationManager = new AuthorizationManager();
			authorizationManager.setClient(getClient());
			authorizationManager.setPermissionsURL(acRealm.getPermissionsURL());
			authorizationManager.setCacheManager(((CachingSecurityManager) SecurityUtils.getSecurityManager()).getCacheManager());
			acRealm.addClearCacheListener(authorizationManager);
		}
		return authorizationManager;
	}

	public OAuthorizationCredentials getOAuthorizationCredentials() {
		final Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated())
			return null;
		final CasOAuthWrapperProfile profile = subject.getPrincipals().oneByType(CasOAuthWrapperProfile.class);
		String accessToken = profile.getAccessToken();
		String clientId = getClient().getKey();
		return new OAuthorizationCredentials(accessToken, clientId, null);
	}

	protected ClientRealm getCasOAuthClientRealm() {
		if (clientRealm == null) {
			SecurityManager securityManager = SecurityUtils.getSecurityManager();
			RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securityManager;
			Collection<Realm> realms = realmSecurityManager.getRealms();
			for (Realm realm : realms)
				if (realm instanceof ClientRealm) {
					logger.debug("A realm of type {} is found", realm.getClass().getName());
					clientRealm = (ClientRealm) realm;
					break;
				}

		}
		return clientRealm;
	}

	protected BaseOAuth20Client<?> getClient() {
		if (client == null){
			client = (BaseOAuth20Client<?>) getCasOAuthClientRealm().getClients().findAllClients().get(0);
			logger.debug("Client is of type {}", client.getClass().getName());
		}

		return client;
	}
	
	private static class AccessControlUtilWeb extends AccessControlUtil {

		@Override
		public OAuthorizationCredentials login(String username, String password) {
			return null;
		}

		@Override
		public void logout() {
			
		}
		
		public void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			try {
				String url = getLoginUrl(req, resp);
				logger.debug("redirecting to loginUrl: {} ", url);

				WebUtils.saveRequest(req);
				WebUtils.issueRedirect(req, resp, url);
			} catch (RequiresHttpAction e) {
				logger.debug("redurecting to loginUrl failed", e);
			}
		}
		
		public String getLoginUrl(HttpServletRequest req, HttpServletResponse resp) throws RequiresHttpAction {
			return getClient().getRedirectionUrl(new ShiroWebContext(req, resp), true);
		}
		
	}
}
