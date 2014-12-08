package org.openiot.scheduler.core.utils;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.AccessTokenExpiredException;
import org.openiot.security.client.OAuthorizationCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityUtil {
	private static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
	private static final int EXPIRY_CHECK_INTERVAL = 5 * 60 * 1000; // some sample value
	private static final String SCHEDULER_USERNAME = "security.initialize.scheduler.username";
	private static final String SCHEDULER_PASSWORD = "security.initialize.scheduler.password";
	private static String username;
	private static String password;
	private static AccessControlUtil acUtil = AccessControlUtil.getRestInstance("scheduler");
	private static long lastExpiryCheck = 0;
	private static OAuthorizationCredentials credentials;

	static {
		PropertyManagement props = new PropertyManagement();
		username = props.getProperty(SCHEDULER_USERNAME, "scheduleruser");
		password = props.getProperty(SCHEDULER_PASSWORD, "scheduleruserpass");
	}

	public static boolean hasPermission(String perm, String accessToken, String clientId) {
		return hasPermission(perm, accessToken, clientId, true);
	}

	/**
	 * @param perm
	 *            the permission string to be checked
	 * @param context
	 * @param accessToken
	 *            the access token of the requester
	 * @param clientId
	 *            the clientId of the requester
	 * @param schedulerIsTarget
	 *            if true, it is checked if <code>accessToken</code> has the permission
	 *            <code>perm</code> on Scheduler. Otherwise, it is checked if
	 *            <code>accessToken</code> has the permission <code>perm</code> on the service
	 *            specified by the <code>clientId.
	 * @return
	 */
	public static boolean hasPermission(String perm, String accessToken, String clientId, boolean schedulerIsTarget) {
		if (credentials == null) {
			login();
		}
		if (credentials != null) {
			try {
				OAuthorizationCredentials callerCredentials = new OAuthorizationCredentials(accessToken, clientId, null);
				OAuthorizationCredentials credentialsToTest = new OAuthorizationCredentials(credentials.getAccessToken(), credentials.getClientId(),
						callerCredentials);
				boolean hasPermission;
				if (schedulerIsTarget)
					hasPermission = acUtil.hasPermission(perm, credentialsToTest);
				else
					hasPermission = acUtil.hasPermission(perm, clientId, credentialsToTest);

				if (acUtil.getAuthorizationManager().isCachingEnabled()) {
					// check if the Scheduler access token has expired (this step should be done
					// only if
					// caching is enabled)
					if (System.currentTimeMillis() - lastExpiryCheck > EXPIRY_CHECK_INTERVAL) {
						logger.debug("Checking if Scheduler access token is expired");
						String expiredAT = acUtil.getExpiredAccessToken(credentialsToTest);
						lastExpiryCheck = System.currentTimeMillis();
						if (credentials.getAccessToken().equals(expiredAT)) {
							// Scheduler access token has expired
							logger.debug("Scheduler access token has expired. Attempting to log in CAS.");
							credentials = null;
							return hasPermission(perm, accessToken, clientId);
						} else if (accessToken.equals(expiredAT)) {
							// The access token of the requester is expired
							logger.debug("The access token of the requester has expired: {} ", expiredAT);
							return false;
						}
					}
				}

				return hasPermission;

			} catch (AccessTokenExpiredException e) {
				if (e.getToken().equals(credentials.getAccessToken())) {
					// Scheduler access token has expired
					logger.debug("Scheduler access token has expired. Attempting to log in CAS.");
					credentials = null;
					return hasPermission(perm, accessToken, clientId, schedulerIsTarget);
				}
			}
		}
		return false;
	}

	public static OAuthorizationCredentials getCredentials() {
		if (credentials == null)
			login();
		return credentials;
	}

	public static synchronized OAuthorizationCredentials login() {
		if (credentials != null) {
			logger.debug("Credentials found in context. Aborting login process.");
		} else {
			//log out the user if necessary
			acUtil.logout();
			logger.debug("Logging into CAS by username {}", username);
			OAuthorizationCredentials creds = acUtil.login(username, password);
			logger.debug("Credentials obtained after logging in is {}", creds);
			credentials = creds;
			lastExpiryCheck = System.currentTimeMillis();
		}
		return credentials;
	}

}
