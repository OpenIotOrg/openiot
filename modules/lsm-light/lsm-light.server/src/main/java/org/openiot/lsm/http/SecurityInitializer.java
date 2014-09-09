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
	private static final long ID_SCHEDULER = 5;
	private static final long ID_SDUM = 6;
	private static final long ID_REQ_DEF = 7;
	private static final long ID_REQ_PRES = 8;

	public static final String ADMIN_USERNAME = "security.initialize.admin.username";
	public static final String ADMIN_PASSWORD = "security.initialize.admin.password";
	public static final String ADMIN_EMAIL = "security.initialize.admin.email";
	public static final String LSM_SERVER_USERNAME = "security.initialize.lsmserver.username";
	public static final String LSM_SERVER_PASSWORD = "security.initialize.lsmserver.password";
	public static final String SCHEDULER_USERNAME = "security.initialize.scheduler.username";
	public static final String SCHEDULER_PASSWORD = "security.initialize.scheduler.password";
	public static final String SDUM_USERNAME = "security.initialize.sdum.username";
	public static final String SDUM_PASSWORD = "security.initialize.sdum.password";
	public static final String CAS_PREFIX = "security.initialize.cas.prefix";
	public static final String MGMT_PREFIX = "security.initialize.management.prefix";
	public static final String REQ_DEF_PREFIX = "security.initialize.reqDef.prefix";
	public static final String REQ_PRES_PREFIX = "security.initialize.reqPres.prefix";
	public static final String SECURITY_MANAGEMENT_SECRET = "security.initialize.management.secret";
	public static final String SECURITY_MANAGEMENT_KEY = "security.initialize.management.key";
	public static final String REQ_DEF_SECRET = "security.initialize.reqDef.secret";
	public static final String REQ_DEF_KEY = "security.initialize.reqDef.key";
	public static final String REQ_PRES_SECRET = "security.initialize.reqPres.secret";
	public static final String REQ_PRES_KEY = "security.initialize.reqPres.key";

	public static final String SERVICE_KEY_PREFIX = "casOauthClient.key.";
	public static final String SERVICE_SECRET_PREFIX = "casOauthClient.key.";

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

		// Pre-defined permissions and roles for scheduler
		Permission allPermScheduler = new Permission(PermissionsUtil.SCHEDULER_ALL, "all permissions", ID_SCHEDULER);
		predefPermissions.add(allPermScheduler);

		// Pre-defined permissions and roles for SDUM
		Permission allPermSdum = new Permission(PermissionsUtil.SDUM_ALL, "all permissions", ID_SDUM);
		predefPermissions.add(allPermSdum);

		for (Permission permission : predefPermissions) {
			addPermission(permission);
		}

		Role allPermRoleScheduler = new Role("AllPermRole", "This role has the permission *", ID_SCHEDULER);
		allPermRoleScheduler.addPermission(allPermScheduler);
		addRole(allPermRoleScheduler);

		User schedulerUser = generateUser("Scheduler", "scheduler@openiot.eu", props.getProperty(SCHEDULER_USERNAME, "scheduleruser"),
				md5(props.getProperty(SCHEDULER_PASSWORD, "scheduleruserpass")));
		addUser(schedulerUser);

		Role allPermRoleSDUM = new Role("AllPermRole", "This role has the permission *", ID_SDUM);
		allPermRoleSDUM.addPermission(allPermSdum);
		addRole(allPermRoleSDUM);

		User sdumUser = generateUser("SDUM", "sdum@openiot.eu", props.getProperty(SDUM_USERNAME, "sdumuser"),
				md5(props.getProperty(SDUM_PASSWORD, "sdumuserpass")));
		addUser(sdumUser);

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

		// Scheduler REST service
		LSMRegisteredServiceImpl schedulerService = new LSMRegisteredServiceImpl();
		schedulerService.setId(ID_SCHEDULER);
		schedulerService.setAllowedToProxy(true);
		schedulerService.setAnonymousAccess(false);
		schedulerService.setDescription(props.getProperty(SERVICE_SECRET_PREFIX + "scheduler", "scheduler.secret"));
		schedulerService.setEnabled(true);
		schedulerService.setEvaluationOrder(0);
		schedulerService.setIgnoreAttributes(false);
		schedulerService.setName(props.getProperty(SERVICE_KEY_PREFIX + "scheduler", "scheduler"));
		schedulerService.setServiceId("REST://scheduler");
		schedulerService.setTheme("Scheduler");
		schedulerService.setSsoEnabled(true);

		// SDUM REST service
		LSMRegisteredServiceImpl sdumService = new LSMRegisteredServiceImpl();
		sdumService.setId(ID_SDUM);
		sdumService.setAllowedToProxy(true);
		sdumService.setAnonymousAccess(false);
		sdumService.setDescription(props.getProperty(SERVICE_SECRET_PREFIX + "sdum", "sdum.secret"));
		sdumService.setEnabled(true);
		sdumService.setEvaluationOrder(0);
		sdumService.setIgnoreAttributes(false);
		sdumService.setName(props.getProperty(SERVICE_KEY_PREFIX + "sdum", "sdum"));
		sdumService.setServiceId("REST://sdum");
		sdumService.setTheme("SDUM");
		sdumService.setSsoEnabled(true);

		// Request Definition service
		LSMRegisteredServiceImpl reqDefService = new LSMRegisteredServiceImpl();
		reqDefService.setId(ID_REQ_DEF);
		reqDefService.setAllowedToProxy(true);
		reqDefService.setAnonymousAccess(false);
		reqDefService.setDescription(props.getProperty(REQ_DEF_SECRET, "requestDefinitionUI-secret"));
		reqDefService.setEnabled(true);
		reqDefService.setEvaluationOrder(0);
		reqDefService.setIgnoreAttributes(false);
		reqDefService.setName(props.getProperty(REQ_DEF_KEY, "requestDefinitionUI"));
		String reqDefPrefix = props.getProperty(REQ_DEF_PREFIX, "http://localhost:8080/ui.requestDefinition");
		if (reqDefPrefix.endsWith("/") && reqDefPrefix.length() > 1)
			reqDefPrefix = reqDefPrefix.substring(0, reqDefPrefix.length() - 1);
		reqDefService.setServiceId(reqDefPrefix + "/callback?client_name=CasOAuthWrapperClient");
		reqDefService.setTheme("RequestDefinition");
		reqDefService.setSsoEnabled(true);

		// Request Presentation service
		LSMRegisteredServiceImpl reqPresService = new LSMRegisteredServiceImpl();
		reqPresService.setId(ID_REQ_PRES);
		reqPresService.setAllowedToProxy(true);
		reqPresService.setAnonymousAccess(false);
		reqPresService.setDescription(props.getProperty(REQ_PRES_SECRET, "requestPresentationUI-secret"));
		reqPresService.setEnabled(true);
		reqPresService.setEvaluationOrder(0);
		reqPresService.setIgnoreAttributes(false);
		reqPresService.setName(props.getProperty(REQ_PRES_KEY, "requestPresentationUI"));
		String reqPresPrefix = props.getProperty(REQ_PRES_PREFIX, "http://localhost:8080/ui.requestPresentation");
		if (reqPresPrefix.endsWith("/") && reqPresPrefix.length() > 1)
			reqPresPrefix = reqPresPrefix.substring(0, reqPresPrefix.length() - 1);
		reqPresService.setServiceId(reqPresPrefix + "/callback?client_name=CasOAuthWrapperClient");
		reqPresService.setTheme("RequestPresentation");
		reqPresService.setSsoEnabled(true);

		return Arrays.asList(new LSMRegisteredServiceImpl[] { defaultService, httpService, lsmServerService, userManagementService, schedulerService,
				sdumService, reqDefService, reqPresService });
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
