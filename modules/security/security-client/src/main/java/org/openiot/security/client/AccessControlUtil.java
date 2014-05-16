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
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
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

	/**
	 * Returns a singleton instance of this class for web applications.
	 * 
	 * @return
	 */
	public static AccessControlUtil getInstance() {
		if (instanceWeb == null)
			instanceWeb = new AccessControlUtilWeb();
		return instanceWeb;
	}

	/**
	 * Returns a singleton instance of this class for RESTful applications.
	 * 
	 * @return
	 */
	public static AccessControlUtil getRestInstance() {
		if (instanceRest == null)
			instanceRest = new AccessControlUtilRest();
		return instanceRest;
	}
	
	/**
	 * Returns a singleton instance of this class for RESTful applications.
	 * 
	 * @return
	 */
	public static AccessControlUtil getRestInstance(String moduleName) {
		if (instanceRest == null)
			instanceRest = new AccessControlUtilRest(moduleName);
		return instanceRest;
	}

	/**
	 * Authenticated the user and abtains a token. Should be used only for the REST client.
	 * 
	 * @param username
	 * @param password
	 * @return an OAuthorizationCredentials object containing the token or null in case of failure
	 */
	public abstract OAuthorizationCredentials login(String username, String password);

	/**
	 * Logs the user out and sends a request to delete the token. Should be used only for the REST
	 * client.
	 */
	public abstract void logout();

	/**
	 * This method can be called by a servlet container to redirect the user to OpenIoT CAS login
	 * page.
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	public abstract void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException;

	/**
	 * @param perm
	 * @return true if the user has the specified permission
	 */
	public boolean hasPermission(String perm) {
		return hasPermission(perm, getOAuthorizationCredentials());
	}

	/**
	 * @param permStr
	 * @param credentials
	 * @return true if the user with the provided <code>credentials</code> has the specified
	 *         permission.
	 */
	public boolean hasPermission(String permStr, OAuthorizationCredentials credentials) {
		return getAuthorizationManager().hasPermission(permStr, credentials);
	}

	/**
	 * @param permStr
	 * @param targetClientId
	 * @param credentials
	 * @return true if the user with the provided <code>credentials</code> has the specified
	 *         permission on service <code>targetClientId</code>
	 */
	public boolean hasPermission(String permStr, String targetClientId, OAuthorizationCredentials credentials) {
		return getAuthorizationManager().hasPermission(permStr, targetClientId, credentials);
	}

	/**
	 * @param role
	 * @return true if the user has the specified role
	 */
	public boolean hasRole(String role) {
		return hasRole(role, getOAuthorizationCredentials());
	}

	/**
	 * @param role
	 * @param credentials
	 * @return true if the user with the provided <code>credentials</code> has the specified role.
	 */
	public boolean hasRole(String role, OAuthorizationCredentials credentials) {
		return getAuthorizationManager().hasRole(role, credentials);
	}

	/**
	 * @param role
	 * @param targetClientId
	 * @param credentials
	 * @return true if the user with the provided <code>credentials</code> has the specified role on
	 *         service <code>targetClientId</code>
	 */
	public boolean hasRole(String role, String targetClientId, OAuthorizationCredentials credentials) {
		return getAuthorizationManager().hasRole(role, targetClientId, credentials);
	}

	/**
	 * Sends a request to the server to check if the token is expired.
	 * 
	 * @param credentials
	 * @return the expired token or <code>null</code> if non of the tokens in
	 *         <code>credentials</code> are expired
	 */
	public String getExpiredAccessToken(OAuthorizationCredentials credentials) {
		return getAuthorizationManager().getExpiredAccessToken(credentials);
	}

	/**
	 * Clears the state information.
	 */
	public void reset() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated())
			getAuthorizationManager().clearCache(subject.getPrincipals());
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

	/**
	 * @return an OAuthorizationCredentials object containing the user's token and the clientId if
	 *         the user has logged in, otherwise returns <code>null</code>
	 */
	public OAuthorizationCredentials getOAuthorizationCredentials() {
		final Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated())
			return null;
		final CasOAuthWrapperProfile profile = subject.getPrincipals().oneByType(CasOAuthWrapperProfile.class);
		String accessToken = profile.getAccessToken();
		String clientId = getClient().getKey();
		String userId = profile.getId();
		return new OAuthorizationCredentials(userId, accessToken, clientId, null);
	}

	protected ClientRealm getCasOAuthClientRealm() {
		if (clientRealm == null) {
			SecurityManager securityManager = SecurityUtils.getSecurityManager();
			RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securityManager;
			Collection<Realm> realms = realmSecurityManager.getRealms();
			for (Realm realm : realms)
				if (realm instanceof ClientRealm) {
					logger.debug("A realm of type {} was found", realm.getClass().getName());
					clientRealm = (ClientRealm) realm;
					break;
				}

		}
		return clientRealm;
	}

	public BaseOAuth20Client<?> getClient() {
		if (client == null) {
			client = (BaseOAuth20Client<?>) getCasOAuthClientRealm().getClients().findAllClients().get(0);
			logger.debug("Client is of type {}", client.getClass().getName());
		}

		return client;
	}

	private static class AccessControlUtilWeb extends AccessControlUtil implements SessionListener {

		public AccessControlUtilWeb() {
			DefaultSessionManager sessionManager = (DefaultSessionManager) ((SessionsSecurityManager) SecurityUtils.getSecurityManager()).getSessionManager();
			sessionManager.getSessionListeners().add(this);
		}

		@Override
		public OAuthorizationCredentials login(String username, String password) {
			return null;
		}

		@Override
		public void logout() {
			// Do nothing
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

		@Override
		public void onStart(Session session) {
			resetCache(session);
		}

		@Override
		public void onStop(Session session) {
			// Do Nothing
		}

		@Override
		public void onExpiration(Session session) {
			resetCache(session);
		}

		private void resetCache(Session session) {
			Session subjectSession = SecurityUtils.getSubject().getSession(false);
			if (subjectSession != null && session.getId().equals(subjectSession.getId()))
				reset();
		}

	}
}
