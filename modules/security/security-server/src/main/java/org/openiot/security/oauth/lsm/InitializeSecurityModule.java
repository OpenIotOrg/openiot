package org.openiot.security.oauth.lsm;

import java.util.Arrays;
import java.util.List;

import org.openiot.lsm.security.oauth.LSMOAuthHttpManager;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.openiot.security.oauth.lsm.LSMOAuthManager;


public class InitializeSecurityModule {
	static String OAuthGraphURL = "http://lsm.deri.ie/OpenIoT/OAuth#";

	public static User generateUser(String name, String email, String username, String password) {
		User user = new User();
		user.setName(name);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);

		return user;
	}

	public static void generateAuthorizationData() {
		LSMOAuthHttpManager oM = new LSMOAuthHttpManager(OAuthGraphURL);
		User adminUser = generateUser("Administrator", "admin@openiot.eu", "admin", "5ebe2294ecd0e0f08eab7690d2a6ee69");

		Role adminRole5 = new Role("admin", "Administrator role", 5L);

		Permission allPerm5 = new Permission("*", "All permissions", 5L);

		adminRole5.addPermission(allPerm5);

		adminUser.addRole(adminRole5);

		oM.addPermission(allPerm5);
		oM.addRole(adminRole5);
		
		oM.addUser(adminUser);
	}

	public static List<LSMRegisteredServiceImpl> createDefaultServices() {
		LSMRegisteredServiceImpl defaultService = new LSMRegisteredServiceImpl();
		defaultService.setId(1L);
		defaultService.setAllowedToProxy(true);
		defaultService.setAnonymousAccess(false);
		defaultService.setDescription("Service Manager");
		defaultService.setEnabled(true);
		defaultService.setEvaluationOrder(0);
		defaultService.setIgnoreAttributes(true);
		defaultService.setName("Service Manager");
		defaultService.setServiceId("https://localhost:8443/openiot-cas/services/j_acegi_cas_security_check");
		defaultService.setSsoEnabled(true);

		LSMRegisteredServiceImpl httpService = new LSMRegisteredServiceImpl();
		httpService.setId(2L);
		httpService.setAllowedToProxy(true);
		httpService.setAnonymousAccess(false);
		httpService.setDescription("OAuth wrapper callback url");
		httpService.setEnabled(true);
		httpService.setEvaluationOrder(0);
		httpService.setIgnoreAttributes(true);
		httpService.setName("HTTP");
		httpService.setServiceId("https://localhost:8443/openiot-cas/oauth2.0/callbackAuthorize");
		httpService.setSsoEnabled(true);

		LSMRegisteredServiceImpl oauthTestService1 = new LSMRegisteredServiceImpl();
		oauthTestService1.setId(3L);
		oauthTestService1.setAllowedToProxy(true);
		oauthTestService1.setAnonymousAccess(false);
		oauthTestService1.setDescription("testsecret1");
		oauthTestService1.setEnabled(true);
		oauthTestService1.setEvaluationOrder(0);
		oauthTestService1.setIgnoreAttributes(false);
		oauthTestService1.setName("testservice1");
		oauthTestService1.setServiceId("http://localhost:9080/callback?client_name=CasOAuthWrapperClient");
		oauthTestService1.setTheme("Service1");
		oauthTestService1.setSsoEnabled(true);

		LSMRegisteredServiceImpl oauthTestService2 = new LSMRegisteredServiceImpl();
		oauthTestService2.setId(4L);
		oauthTestService2.setAllowedToProxy(true);
		oauthTestService2.setAnonymousAccess(false);
		oauthTestService2.setDescription("testsecret2");
		oauthTestService2.setEnabled(true);
		oauthTestService2.setEvaluationOrder(0);
		oauthTestService2.setIgnoreAttributes(false);
		oauthTestService2.setName("testservice2");
		oauthTestService2.setServiceId("http://localhost:7080/callback?client_name=CasOAuthWrapperClient");
		oauthTestService2.setTheme("Service2");
		oauthTestService2.setSsoEnabled(true);

		LSMRegisteredServiceImpl userManagementService = new LSMRegisteredServiceImpl();
		userManagementService.setId(5L);
		userManagementService.setAllowedToProxy(true);
		userManagementService.setAnonymousAccess(false);
		userManagementService.setDescription("openiot-security-manager-app-secret");
		userManagementService.setEnabled(true);
		userManagementService.setEvaluationOrder(0);
		userManagementService.setIgnoreAttributes(false);
		userManagementService.setName("openiot-security-manager-app");
		userManagementService.setServiceId("http://localhost:8080/security.management/callback?client_name=CasOAuthWrapperClient");
		userManagementService.setTheme("Manager");
		userManagementService.setSsoEnabled(true);

		return Arrays.asList(new LSMRegisteredServiceImpl[] { defaultService, httpService, oauthTestService1, oauthTestService2, userManagementService });
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LSMOAuthManager oM = LSMOAuthManager.getInstance();

		for (LSMRegisteredServiceImpl rs : createDefaultServices())
			oM.addRegisteredService(rs);

//		generateAuthorizationData();

	}

}
