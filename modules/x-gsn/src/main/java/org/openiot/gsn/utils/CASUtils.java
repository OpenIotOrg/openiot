package org.openiot.gsn.utils;

import java.io.File;

import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Note that LSM server wants to verify a permission on a service other than LSM itself, it must
 * have the "ext:retrieve_permissions" permission on that service. For example, if LSM wants to
 * check if the provided token has permissions for adding Roles, LSM must have the
 * "ext:retrieve_permissions" on "openiot-security-manager-app" service.
 */
public class CASUtils {
	private static Logger logger = LoggerFactory.getLogger(CASUtils.class);
	private static final int EXPIRY_CHECK_INTERVAL = 5 * 60 * 1000; // some
																	// sample
																	// value
	private static String username = "";
	private static String password = "";

	static {
		Config conf = ConfigFactory.load();
		username = conf.getString("username");
		password = conf.getString("password");
	}

	private static AccessControlUtil acUtil = AccessControlUtil.getRestInstance("xgsn", new File("conf").getAbsolutePath());
	private static long lastExpiryCheck = 0;

	static OAuthorizationCredentials credentials;

	public static OAuthorizationCredentials getTokenAndId() {
		if (credentials != null) {
			if (System.currentTimeMillis() - lastExpiryCheck > EXPIRY_CHECK_INTERVAL) {
				logger.debug("Checking if LSM access token is expired");
				String expiredAT = acUtil.getExpiredAccessToken(credentials);
				lastExpiryCheck = System.currentTimeMillis();
				if (credentials.getAccessToken().equals(expiredAT)) {
					// LSM access token has expired
					logger.debug("LSM access token has expired. Attempting to log in CAS.");
					credentials = null;
					return login();
				}
			}
			return credentials;
		} else {
			return login();
		}

	}

	public synchronized static OAuthorizationCredentials login() {
		if (credentials == null) {
			//log out the user if necessary
			acUtil.logout();
			logger.debug("Logging into CAS by username {}", username);
			OAuthorizationCredentials cred = acUtil.login(username, password);
			logger.debug("Credentials obtained after logging in is {}", cred);
			credentials = cred;
			lastExpiryCheck = System.currentTimeMillis();
		}
		return credentials;
	}

}