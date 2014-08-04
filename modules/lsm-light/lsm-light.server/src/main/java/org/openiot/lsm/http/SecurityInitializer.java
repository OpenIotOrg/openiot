package org.openiot.lsm.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.jasig.cas.services.RegisteredService;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.PermissionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityInitializer {
	private static Logger logger = LoggerFactory.getLogger(SecurityInitializer.class);
	
	private static final long ID_SERVICE_MANAGER = 1;
	private static final long ID_HTTP = 2;
	private static final long ID_LSM_SERVER = 3;
	private static final long ID_SECURITY_MANAGEMENT = 4;

	public static final String ADMIN_USERNAME = "security.initialize.admin.username";
	public static final String ADMIN_PASSWORD = "security.initialize.admin.password";
	public static final String ADMIN_EMAIL = "security.initialize.admin.email";
	public static final String LSM_SERVER_USERNAME = "security.initialize.lsmserver.username";
	public static final String LSM_SERVER_PASSWORD = "security.initialize.lsmserver.password";
	public static final String CAS_PREFIX = "security.initialize.cas.prefix";
	public static final String MGMT_PREFIX = "security.initialize.management.prefix";
	public static final String SECURITY_MANAGEMENT_SECRET = "security.initialize.management.secret";
	public static final String SECURITY_MANAGEMENT_KEY = "security.initialize.management.key";

	private String lSMOauthGraphURL;
	private static PropertyManagement props;
	private OauthServletHelper helper;
	private AccessControlUtil acUtil;

	private SecurityInitializer(String graphURL) {
		props = new PropertyManagement();
		lSMOauthGraphURL = graphURL;
		helper = new OauthServletHelper();
		acUtil = AccessControlUtil.getRestInstance();
	}

	public static void init() {
		SensorManager sensorManager = new SensorManager();
		props = new PropertyManagement();
		String securityLsmGraphURL = props.getSecurityLsmGraphURL();
		sensorManager.setMetaGraph(securityLsmGraphURL);
		logger.debug("Retrieving all registered services");
		List<RegisteredService> allRegisteredServices = sensorManager.getAllRegisteredServices();
		if (allRegisteredServices == null || allRegisteredServices.isEmpty()) {
			logger.debug("No registered services found. Initializing ...");
			SecurityInitializer securityInitializer = new SecurityInitializer(securityLsmGraphURL);
			securityInitializer.initialize();
		}
	}

	public void initialize() {
		generateAuthorizationData();

		for (LSMRegisteredServiceImpl rs : createDefaultServices())
			addRegisteredService(rs);
	}

	private User generateUser(String name, String email, String username, String password) {
		User user = new User();
		user.setName(name);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);

		return user;
	}

	private void generateAuthorizationData() {

		User adminUser = generateUser("Administrator", props.getProperty(ADMIN_EMAIL, "admin@openiot.eu"), props.getProperty(ADMIN_USERNAME, "admin"),
				md5(props.getProperty(ADMIN_PASSWORD, "secret")));

		Role adminRole = new Role("admin", "Administrator role", ID_SECURITY_MANAGEMENT);
		Permission allPerm = new Permission("*", "All permissions", ID_SECURITY_MANAGEMENT);
		adminRole.addPermission(allPerm);
		adminUser.addRole(adminRole);
		addPermission(allPerm);
		addRole(adminRole);
		addUser(adminUser);

		User lsmServerUser = generateUser("LSM Server User", "admin@openiot.eu", props.getProperty(LSM_SERVER_USERNAME, "lsmuser"),
				md5(props.getProperty(LSM_SERVER_PASSWORD, "lsmuserpass")));
		Role lsmServerRole = new Role("LSM-Server", "LSM Server Role", ID_SECURITY_MANAGEMENT);
		Permission externalRetrievePermissions = new Permission(PermissionsUtil.EXT_RETRIEVE_PERMISSIONS,
				"The permission to authorize other clients to retrieve permission information on this service", ID_SECURITY_MANAGEMENT);
		lsmServerRole.addPermission(externalRetrievePermissions);
		lsmServerUser.addRole(lsmServerRole);
		addPermission(externalRetrievePermissions);
		addRole(lsmServerRole);
		addUser(lsmServerUser);

		// Pre-defined permissions and roles for security-management console
		String key = props.getProperty(SECURITY_MANAGEMENT_KEY, "openiot-security-manager-app");
		List<Permission> predefPermissions = new ArrayList<>();
		predefPermissions.add(new Permission(PermissionsUtil.SEC_MGMT_GENERAL, "General user management permission", ID_SECURITY_MANAGEMENT));
		predefPermissions.add(new Permission(PermissionsUtil.SEC_MGMT + key, "User management permission for " + key, ID_SECURITY_MANAGEMENT));
		predefPermissions.add(new Permission(PermissionsUtil.SEC_MGMT_DEL_USER, "Delete users", ID_SECURITY_MANAGEMENT));
		predefPermissions.add(new Permission(PermissionsUtil.SEC_MGMT_DEL_PERMISSION + key, "Delete permissions", ID_SECURITY_MANAGEMENT));
		predefPermissions.add(new Permission(PermissionsUtil.SEC_MGMT_DEL_ROLE + key, "Delete roles", ID_SECURITY_MANAGEMENT));
		predefPermissions.add(new Permission(PermissionsUtil.SEC_MGMT_CREATE_PERMISSION + key, "Create new permissions", ID_SECURITY_MANAGEMENT));
		predefPermissions.add(new Permission(PermissionsUtil.SEC_MGMT_CREATE_ROLE + key, "Create new roles", ID_SECURITY_MANAGEMENT));
		predefPermissions.add(new Permission(PermissionsUtil.SEC_MGMT_GRANT_ROLE + key, "Grant/revoke roles", ID_SECURITY_MANAGEMENT));

		// Pre-defined permissions and roles for lsm-light.server
		predefPermissions.add(new Permission(PermissionsUtil.ADD_SENSOR_GUESS, "add new sensor to server", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.ADD_TRIPLES_GUESS, "insert triples into server", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.UPDATE_SENSOR_DATA_GUESS, "add new sensor reading", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.GET_SENSOR_GUESS, "retrieve sensor", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_SENSOR_GUESS, "delete sensor", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_READING_GUESS, "delete sensor reading", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_TRIPLES_GUESS, "delete triples", ID_LSM_SERVER));
		
		predefPermissions.add(new Permission(PermissionsUtil.ADD_SENSOR_DEMO, "add new sensor to server", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.ADD_TRIPLES_DEMO, "insert triples into server", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.UPDATE_SENSOR_DATA_DEMO, "add new sensor reading", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.GET_SENSOR_DEMO, "retrieve sensor", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_SENSOR_DEMO, "delete sensor", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_READING_DEMO, "delete sensor reading", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_TRIPLES_DEMO, "delete triples", ID_LSM_SERVER));
		
		predefPermissions.add(new Permission(PermissionsUtil.ADD_SENSOR_MAIN, "add new sensor to server", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.ADD_TRIPLES_MAIN, "insert triples into server", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.UPDATE_SENSOR_DATA_MAIN, "add new sensor reading", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.GET_SENSOR_MAIN, "retrieve sensor", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_SENSOR_MAIN, "delete sensor", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_READING_MAIN, "delete sensor reading", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.DEL_TRIPLES_MAIN, "delete triples", ID_LSM_SERVER));
		predefPermissions.add(new Permission(PermissionsUtil.LSM_ALL, "all permissions", ID_LSM_SERVER));
		
		for (Permission permission : predefPermissions) {
			addPermission(permission);
		}
	}

	private List<LSMRegisteredServiceImpl> createDefaultServices() {
		LSMRegisteredServiceImpl defaultService = new LSMRegisteredServiceImpl();
		defaultService.setId(ID_SERVICE_MANAGER);
		defaultService.setAllowedToProxy(true);
		defaultService.setAnonymousAccess(false);
		defaultService.setDescription("Service Manager");
		defaultService.setEnabled(true);
		defaultService.setEvaluationOrder(0);
		defaultService.setIgnoreAttributes(true);
		defaultService.setName("Service Manager");
		String casPrefix = props.getProperty(CAS_PREFIX, "https://localhost:8443/openiot-cas");
		if (casPrefix.endsWith("/") && casPrefix.length() > 1)
			casPrefix = casPrefix.substring(0, casPrefix.length() - 1);
		defaultService.setServiceId(casPrefix + "/services/j_acegi_cas_security_check");
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
		httpService.setServiceId(casPrefix + "/oauth2.0/callbackAuthorize");
		httpService.setSsoEnabled(true);

		LSMRegisteredServiceImpl lsmServerService = new LSMRegisteredServiceImpl();
		lsmServerService.setId(ID_LSM_SERVER);
		lsmServerService.setAllowedToProxy(true);
		lsmServerService.setAnonymousAccess(false);
		lsmServerService.setDescription(acUtil.getClient().getSecret());
		lsmServerService.setEnabled(true);
		lsmServerService.setEvaluationOrder(0);
		lsmServerService.setIgnoreAttributes(false);
		lsmServerService.setName(acUtil.getClient().getKey());
		lsmServerService.setServiceId("REST://lsm-light.server");
		lsmServerService.setTheme("LSM-Server");
		lsmServerService.setSsoEnabled(true);

		LSMRegisteredServiceImpl userManagementService = new LSMRegisteredServiceImpl();
		userManagementService.setId(ID_SECURITY_MANAGEMENT);
		userManagementService.setAllowedToProxy(true);
		userManagementService.setAnonymousAccess(false);
		userManagementService.setDescription(props.getProperty(SECURITY_MANAGEMENT_SECRET, "openiot-security-manager-app-secret"));
		userManagementService.setEnabled(true);
		userManagementService.setEvaluationOrder(0);
		userManagementService.setIgnoreAttributes(false);
		userManagementService.setName(props.getProperty(SECURITY_MANAGEMENT_KEY, "openiot-security-manager-app"));
		String mgmtAppPrefix = props.getProperty(MGMT_PREFIX, "http://localhost:8080/security.management");
		if (mgmtAppPrefix.endsWith("/") && mgmtAppPrefix.length() > 1)
			mgmtAppPrefix = mgmtAppPrefix.substring(0, mgmtAppPrefix.length() - 1);
		userManagementService.setServiceId(mgmtAppPrefix + "/callback?client_name=CasOAuthWrapperClient");
		userManagementService.setTheme("Manager");
		userManagementService.setSsoEnabled(true);

		return Arrays.asList(new LSMRegisteredServiceImpl[] { defaultService, httpService, lsmServerService, userManagementService });
	}

	private void addPermission(Permission permission) {
		add(permission);
	}

	private void addRole(Role role) {
		add(role);
	}

	private void addUser(User user) {
		add(user);
	}

	private void addRegisteredService(LSMRegisteredServiceImpl rs) {
		add(rs);
	}

	private void add(Object obj) {
		helper.feedToServer(obj, null, lSMOauthGraphURL);
	}

	private String md5(String content) {
		return DigestUtils.md5Hex(content);
	}

}
