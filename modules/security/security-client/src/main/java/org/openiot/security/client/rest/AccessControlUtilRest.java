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

package org.openiot.security.client.rest;

import io.buji.pac4j.ClientToken;

import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.openiot.security.client.AuthorizationManager;
import org.openiot.security.client.OAuthorizationCredentials;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mehdi Riahi
 * 
 */
public class AccessControlUtilRest {
	private static Logger logger = LoggerFactory.getLogger(AccessControlUtilRest.class);

	private static AccessControlUtilRest instance;

	private CasOAuthClientRealmRest casOAuthClientRealm;

	private CasOAuthWrapperClientRest client;

	private AuthorizationManager authorizationManager;

	public static AccessControlUtilRest getInstance() {
		if (instance == null)
			instance = new AccessControlUtilRest();
		return instance;
	}

	private AccessControlUtilRest() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
	}

	public OAuthorizationCredentials login(String username, String password) {
		OAuthCredentialsRest credentials = new OAuthCredentialsRest(username, password, getClient().getName());
		ClientToken token = new ClientToken(getClient().getName(), credentials);
		Subject subject = SecurityUtils.getSubject();
		logger.debug("Logging in by username {}", username);
		subject.login(token);
		OAuthorizationCredentials oauthCredentials = getOAuthorizationCredentials();
		logger.debug("Logged in. Credentials: {}", oauthCredentials);
		return oauthCredentials;
	}

	public void logout() {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
	}

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
			authorizationManager = new AuthorizationManager();
			authorizationManager.setClient(getClient());
			authorizationManager.setPermissionsURL(getCasOAuthClientRealm().getPermissionsURL());
			getCasOAuthClientRealm().addClearCacheListener(authorizationManager);
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

	private CasOAuthClientRealmRest getCasOAuthClientRealm() {
		if (casOAuthClientRealm == null) {
			SecurityManager securityManager = SecurityUtils.getSecurityManager();
			RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securityManager;
			Collection<Realm> realms = realmSecurityManager.getRealms();
			for (Realm realm : realms)
				if (realm instanceof CasOAuthClientRealmRest) {
					casOAuthClientRealm = (CasOAuthClientRealmRest) realm;
					break;
				}

		}
		return casOAuthClientRealm;
	}

	private CasOAuthWrapperClientRest getClient() {
		if (client == null)
			client = (CasOAuthWrapperClientRest) getCasOAuthClientRealm().getClients().findClient(CasOAuthWrapperClientRest.class.getSimpleName());

		return client;
	}
}
