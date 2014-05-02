package org.openiot.gsn.utils;


import org.openiot.commons.util.PropertyManagement;
import org.openiot.gsn.metadata.LSM.LSMRepository;
import org.openiot.gsn.metadata.LSM.LSMUser;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note that LSM server wants to verify a permission on a service other than LSM
 * itself, it must have the "ext:retrieve_permissions" permission on that
 * service. For example, if LSM wants to check if the provided token has
 * permissions for adding Roles, LSM must have the "ext:retrieve_permissions" on
 * "openiot-security-manager-app" service.
 */
public class SecurityUtil {
	private static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
	private static final int EXPIRY_CHECK_INTERVAL = 5 * 60 * 1000; // some
																	// sample
																	// value
    private static LSMUser lsmUser=new LSMUser();
	
	private static String username="";
	private static String password="";
	
	static {
		//PropertyManagement props = new PropertyManagement();
		lsmUser.initFromConfigFile(LSMRepository.LSM_CONFIG_PROPERTIES_FILE);
		username=lsmUser.getUser();
		password=lsmUser.getPassword();
	}

	private static AccessControlUtil acUtil = AccessControlUtil
			.getRestInstance();
	private static long lastExpiryCheck = 0;

	

	
	static OAuthorizationCredentials credentials;
	
	public static OAuthorizationCredentials getTokenAndId(){
		if (credentials!=null){
			if (System.currentTimeMillis() - lastExpiryCheck > EXPIRY_CHECK_INTERVAL) {
				logger.debug("Checking if LSM access token is expired");
				String expiredAT = acUtil
						.getExpiredAccessToken(credentials);
				lastExpiryCheck = System.currentTimeMillis();
				if (credentials.getAccessToken().equals(expiredAT)) {
					// LSM access token has expired
					logger.debug("LSM access token has expired. Attempting to log in CAS.");
					//context.setAttribute(CREDENTIALS, null);
					credentials=null;
					return login();
					//return hasPermission(perm, context, accessToken,
						//	clientId);
				}
				
			}
			return credentials;
		}
		else {
			return login();
		}
		
	}

	public static OAuthorizationCredentials login() {
		logger.debug("Logging into CAS by username {}", username);
		OAuthorizationCredentials cred = acUtil
				.login(username, password);
		logger.debug("Credentials obtained after logging in is {}", credentials);
		//context.setAttribute(CREDENTIALS, credentials);
		credentials=cred;
		lastExpiryCheck = System.currentTimeMillis();
		return credentials;
	}

}