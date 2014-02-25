package org.openiot.security.oauth.lsm;

import java.util.Arrays;
import java.util.List;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;

public class SecurityModuleInitializer {
	private static final long ID_SERVICE_MANAGER = 1;
	private static final long ID_HTTP = 2;
	private static final long ID_TEST_SERVICE_1 = 3;
	private static final long ID_TEST_SERVICE_2 = 4;
	private static final long ID_SECURITY_MANAGEMENT = 5;

	private static final String ADMIN_USERNAME = "security.lsm.initialize.admin.username";
	private static final String ADMIN_PASSWORD = "security.lsm.initialize.admin.password";
	private static final String ADMIN_EMAIL = "security.lsm.initialize.admin.email";

	public static void initialize() {
		generateAuthorizationData();

		LSMOAuthManager oM = LSMOAuthManager.getInstance();

		for (LSMRegisteredServiceImpl rs : createDefaultServices())
			oM.addRegisteredService(rs);
	}

	private static User generateUser(String name, String email, String username, String password) {
		User user = new User();
		user.setName(name);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);

		return user;
	}

	private static void generateAuthorizationData() {
		LSMOAuthManager oM = LSMOAuthManager.getInstance();
		PropertyManagement props = new PropertyManagement();
		User adminUser = generateUser("Administrator", props.getProperty(ADMIN_EMAIL, "admin@openiot.eu"), props.getProperty(ADMIN_USERNAME, "admin"),
				props.getProperty(ADMIN_PASSWORD, "5ebe2294ecd0e0f08eab7690d2a6ee69"));

		Role adminRole = new Role("admin", "Administrator role", ID_SECURITY_MANAGEMENT);

		Permission allPerm = new Permission("*", "All permissions", ID_SECURITY_MANAGEMENT);

		adminRole.addPermission(allPerm);

		adminUser.addRole(adminRole);

		oM.addPermission(allPerm);
		oM.addRole(adminRole);

		oM.addUser(adminUser);
	}

	private static List<LSMRegisteredServiceImpl> createDefaultServices() {
		LSMRegisteredServiceImpl defaultService = new LSMRegisteredServiceImpl();
		defaultService.setId(ID_SERVICE_MANAGER);
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
		httpService.setId(ID_HTTP);
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
		oauthTestService1.setId(ID_TEST_SERVICE_1);
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
		oauthTestService2.setId(ID_TEST_SERVICE_2);
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
		userManagementService.setId(ID_SECURITY_MANAGEMENT);
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

		// generateAuthorizationData();

	}

}
