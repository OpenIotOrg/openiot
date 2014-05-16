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

import io.buji.pac4j.ClientToken;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.security.client.rest.OAuthCredentialsRest;
import org.openiot.security.client.rest.CasOAuthWrapperClientRest;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mehdi Riahi
 * 
 */
class AccessControlUtilRest extends AccessControlUtil {
	private static Logger logger = LoggerFactory.getLogger(AccessControlUtilRest.class);

	AccessControlUtilRest() {
		this(null);
	}
	
	AccessControlUtilRest(String moduleName) {
		String jbossConfigDir = System.getProperty("jboss.server.config.dir");

		IniSecurityManagerFactory factory = null;
		if (moduleName != null && jbossConfigDir != null) {
			PropertyManagement props = new PropertyManagement();
			String fileName = props.getRestClientIniName();
			if (fileName == null) {
				logger.warn("The ini configuration file name is not configured in the global properties file");
			} else {
				String key = props.getProperty("casOauthClient.key." + moduleName, null);
				String secret = props.getProperty("casOauthClient.secret." + moduleName, null);
				if (key != null && secret != null) {
					factory = new IniSecurityManagerFactory("file:" + jbossConfigDir + "/" + fileName);
					SecurityManager securityManager = factory.getInstance();
					CasOAuthWrapperClientRest bean = (CasOAuthWrapperClientRest) factory.getBeans().get("casOauthClient");
					bean.setKey(key);
					bean.setSecret(secret);
					SecurityUtils.setSecurityManager(securityManager);
				} else {
					logger.warn("casOauthClient.key.{} or/and casOauthClient.secret.{} is not set in the global properties file", moduleName, moduleName);
				}
			}
		}
		if (factory == null) {
			logger.info("Falling back to the rest-client.ini in the class path");
			String confFilePath = "classpath:rest-client.ini";
			factory = new IniSecurityManagerFactory(confFilePath);
			SecurityManager securityManager = factory.getInstance();
			SecurityUtils.setSecurityManager(securityManager);
		}
	}

	public OAuthorizationCredentials login(String username, String password) {
		BaseOAuth20Client<?> client = getClient();
		OAuthCredentialsRest credentials = new OAuthCredentialsRest(username, password, client.getName(), client.getKey(), client.getSecret());
		ClientToken token = new ClientToken(client.getName(), credentials);
		Subject subject = SecurityUtils.getSubject();
		logger.debug("Logging in by username {}", username);
		subject.login(token);
		OAuthorizationCredentials oauthCredentials = getOAuthorizationCredentials();
		logger.debug("Logged in. Credentials: {}", oauthCredentials);
		return oauthCredentials;
	}

	public void logout() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		reset();
	}

	@Override
	public void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Do nothing
	}

}
