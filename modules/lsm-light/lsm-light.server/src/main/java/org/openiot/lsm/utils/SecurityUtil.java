package org.openiot.lsm.utils;

import javax.servlet.ServletContext;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.http.SecurityInitializer;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.AccessTokenExpiredException;
import org.openiot.security.client.OAuthorizationCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note that LSM server wants to verify a permission on a service other than LSM itself, it must
 * have the "ext:retrieve_permissions" permission on that service. For example, if LSM wants to
 * check if the provided token has permissions for adding Roles, LSM must have the
 * "ext:retrieve_permissions" on "openiot-security-manager-app" service.
 */
public class SecurityUtil {
	private static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
	private static final String CREDENTIALS = "OAuthCredentials";
	private static final int EXPIRY_CHECK_INTERVAL = 5 * 60 * 1000; // some sample value
	private static String username;
	private static String password;
	private static AccessControlUtil acUtil = AccessControlUtil.getRestInstance("lsm-server");
	private static long lastExpiryCheck = 0;

	static {
		PropertyManagement props = new PropertyManagement();
		username = props.getProperty(SecurityInitializer.LSM_SERVER_USERNAME, "lsmuser");
		password = props.getProperty(SecurityInitializer.LSM_SERVER_PASSWORD, "lsmuserpass");
	}

	public static boolean hasPermission(String perm, ServletContext context, String accessToken, String clientId) {
		return hasPermission(perm, context, accessToken, clientId, true);
	}

	/**
	 * @param perm
	 *            the permission string to be checked
	 * @param context
	 * @param accessToken
	 *            the access token of the requester
	 * @param clientId
	 *            the clientId of the requester
	 * @param lsmIsTarget
	 *            if true, it is checked if <code>accessToken</code> has the permission
	 *            <code>perm</code> on LSM. Otherwise, it is checked if <code>accessToken</code> has
	 *            the permission <code>perm</code> on the service specified by the <code>clientId.
	 * @return
	 */
	public static boolean hasPermission(String perm, ServletContext context, String accessToken, String clientId, boolean lsmIsTarget) {
		OAuthorizationCredentials credentials = (OAuthorizationCredentials) context.getAttribute(CREDENTIALS);
		if (credentials == null) {
			credentials = login(context);
		}
		if (credentials != null) {
			try {
				OAuthorizationCredentials callerCredentials = new OAuthorizationCredentials(accessToken, clientId, null);
				OAuthorizationCredentials credentialsToTest = new OAuthorizationCredentials(credentials.getAccessToken(), credentials.getClientId(),
						callerCredentials);
				boolean hasPermission;
				if (lsmIsTarget)
					hasPermission = acUtil.hasPermission(perm, credentialsToTest);
				else
					hasPermission = acUtil.hasPermission(perm, clientId, credentialsToTest);

				if (acUtil.getAuthorizationManager().isCachingEnabled()) {
					// check if the LSM access token has expired (this step should be done only if
					// caching is enabled)
					if (System.currentTimeMillis() - lastExpiryCheck > EXPIRY_CHECK_INTERVAL) {
						logger.debug("Checking if LSM access token is expired");
						String expiredAT = acUtil.getExpiredAccessToken(credentialsToTest);
						lastExpiryCheck = System.currentTimeMillis();
						if (credentials.getAccessToken().equals(expiredAT)) {
							// LSM access token has expired
							logger.debug("LSM access token has expired. Attempting to log in CAS.");
							context.setAttribute(CREDENTIALS, null);
							return hasPermission(perm, context, accessToken, clientId);
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
					// LSM access token has expired
					logger.debug("LSM access token has expired. Attempting to log in CAS.");
					context.setAttribute(CREDENTIALS, null);
					return hasPermission(perm, context, accessToken, clientId, lsmIsTarget);
				}
			}
		}
		return false;
	}

	public static OAuthorizationCredentials login(ServletContext context) {
		logger.debug("Logging into CAS by username {}", username);
		OAuthorizationCredentials credentials = acUtil.login(username, password);
		logger.debug("Credentials obtained after logging in is {}", credentials);
		context.setAttribute(CREDENTIALS, credentials);
		lastExpiryCheck = System.currentTimeMillis();
		return credentials;
	}

}
