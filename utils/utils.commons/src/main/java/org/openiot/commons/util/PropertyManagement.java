package org.openiot.commons.util;

/**
 *    Copyright (c) 2011-2014, OpenIoT
 *
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 */
public class PropertyManagement {

	final static Logger logger = LoggerFactory.getLogger(PropertyManagement.class);

	// // reading proeprty LSM_META_GRAPH
	// String schedulerLsmMetaGraph = "";
	//
	// // reading proeprty LSM_DATA_GRAPH
	// String schedulerLsmDataGraph = "";
	//
	// // reading proeprty LSM_FUNCTIONAL_GRAPH
	// String schedulerLsmFunctionalGraph = "";
	//
	// // reading proeprty LSM_USER_NAME
	// String schedulerLsmUserName = "";
	//
	// // reading proeprty LSM_PASSWORD
	// String schedulerLsmPassword = "";
	//
	//
	// //============SD&UM=============================
	// // reading proeprty LSM_FUNCTIONAL_GRAPH
	// String sdumLsmFunctionalGraph = "";
	//
	// // reading proeprty LSM_SPARQL_END_POINT
	// String sdumLsmSparqlEndPoint = "";

	private static final String PROPERTIES_FILE = "openiot.properties";

	// ==============Scheduler====================

	private static final String SCHEDULER_LSM_META_GRAPH = "scheduler.core.lsm.openiotMetaGraph";
	private static final String SCHEDULER_LSM_DATA_GRAPH = "scheduler.core.lsm.openiotDataGraph";
	private static final String SCHEDULER_LSM_FUNCTIONAL_GRAPH = "scheduler.core.lsm.openiotFunctionalGraph";
	private static final String SCHEDULER_LSM_USER_NAME = "scheduler.core.lsm.access.username";
	private static final String SCHEDULER_LSM_PASSWORD = "scheduler.core.lsm.access.password";
	private static final String SCHEDULER_LSM_SPARQL_END_POINT = "scheduler.core.lsm.sparql.endpoint";
	private static final String SCHEDULER_LSM_REMOTE_SERVER = "scheduler.core.lsm.remote.server";

	// ==============SD&UM====================
	private static final String SDUM_LSM_FUNCTIONAL_GRAPH = "sdum.core.lsm.openiotFunctionalGraph";
	private static final String SDUM_LSM_SPARQL_END_POINT = "sdum.core.lsm.sparql.endpoint";
	private static final String SDUM_LSM_REMOTE_SERVER = "sdum.core.lsm.remote.server";

	// ==============LSM-LIGHT====================
	private static final String LSM_CONNECTION_DRIVER = "lsm-light.server.connection.driver_class";
	private static final String LSM_CONNECTION_URL = "lsm-light.server.connection.url";
	private static final String LSM_CONNECTION_USERNAME = "lsm-light.server.connection.username";
	private static final String LSM_CONNECTION_PASS = "lsm-light.server.connection.password";
	private static final String LSM_MIN_CONNECTION = "lsm-light.server.minConnection";
	private static final String LSM_MAX_CONNECTION = "lsm-light.server.maxConnection";
	private static final String LSM_RETRY_ATTEMPTS = "lsm-light.server.acquireRetryAttempts";
	private static final String LSM_LOCAL_METAGRAPH = "lsm-light.server.localMetaGraph";
	private static final String LSM_LOCAL_DATAGRAPH = "lsm-light.server.localDataGraph";
	private static final String LSM_CLIENT_CONNECTION_SERVER_HOST = "lsm-light.client.connection.server";
	private static final String OPENIOT_ONTOLOGY_NAMESPACE = "lsm-light.client.openiot.ontology.namespace";
	private static final String OPENIOT_RESOURCE_NAMESPACE = "lsm-light.client.openiot.resource.namespace";

	// ==============Security&Privacy====================
	private static final String SECURITY_LSM_SPARQL_END_POINT = "security.lsm.sparql.endpoint";
	private static final String SECURITY_LSM_GRAPH = "security.lsm.graphURL";

	// ==============REQUEST COMMONS ====================
	private static final String REQUEST_COMMONS_SCHEDULER_CORE_HOST_URL = "request.commons.scheduler.core.host.url";
	private static final String REQUEST_COMMONS_SDUM_CORE_HOST_URL = "request.commons.sdum.core.host.url";

	private static final String IDE_CORE_NAVIGATION_PREFIX = "ide.core.navigation";
	
	
	// ==============Sensor Schema Editor====================
	private static final String SCHEMA_EDITOR_TITLE = "ide.core.navigation.sensorSchemaEditor.title";
	private static final String SCHEMA_EDITOR_URL = "ide.core.navigation.sensorSchemaEditor.url";
	
	private Properties props = null;

	public PropertyManagement() {
		initializeProperties();
	}

	/**
	 * Initialize the Properties
	 */
	private void initializeProperties() {

		String jbosServerConfigDir = System.getProperty("jboss.server.config.dir");
		String openIotConfigFile = jbosServerConfigDir + File.separator + PROPERTIES_FILE;
		props = new Properties();

		logger.debug("jbosServerConfigDir:" + openIotConfigFile);

		InputStream fis = null;

		try {
			fis = new FileInputStream(openIotConfigFile);

		} catch (FileNotFoundException e) {
			// TODO Handle exception

			logger.warn("Unable to find file: " + openIotConfigFile);

		}

		// trying to find the file in the classpath
		if (fis == null) {
			fis = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			if (fis == null)
				logger.error("Unable to find file in the classpath: " + PROPERTIES_FILE);
		}

		// loading properites from properties file
		try {
			props.load(fis);
		} catch (IOException e) {
			// TODO Handle exception
			logger.error("Unable to load properties from file " + openIotConfigFile);
		}

	}

	public String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	public String getSchedulerLsmMetaGraph() {
		return props.getProperty(SCHEDULER_LSM_META_GRAPH);
	}

	public String getSchedulerLsmDataGraph() {
		return props.getProperty(SCHEDULER_LSM_DATA_GRAPH);
	}

	public String getSchedulerLsmFunctionalGraph() {
		return props.getProperty(SCHEDULER_LSM_FUNCTIONAL_GRAPH);
	}

	public String getSchedulerLsmUserName() {
		return props.getProperty(SCHEDULER_LSM_USER_NAME);
	}

	public String getSchedulerLsmPassword() {
		return props.getProperty(SCHEDULER_LSM_PASSWORD);
	}

	public String getSchedulerLsmSparqlEndPoint() {
		return props.getProperty(SCHEDULER_LSM_SPARQL_END_POINT);
	}

	public String getSchedulerLsmRemoteServer() {
		return props.getProperty(SCHEDULER_LSM_REMOTE_SERVER);
	}

	public String getSdumLsmFunctionalGraph() {
		return props.getProperty(SDUM_LSM_FUNCTIONAL_GRAPH);
	}

	public String getSdumLsmSparqlEndPoint() {
		return props.getProperty(SDUM_LSM_SPARQL_END_POINT);
	}

	public String getSdumLsmRemoteServer() {
		return props.getProperty(SDUM_LSM_REMOTE_SERVER);
	}

	public String getRequestCommonsSdumHostUrl() {
		
		return props.getProperty(REQUEST_COMMONS_SDUM_CORE_HOST_URL);

	}

	public String getRequestCommonsSchedulerHostUrl() {
		return props.getProperty(REQUEST_COMMONS_SCHEDULER_CORE_HOST_URL);
	}

	public String getLsmServerConnectionDriver() {
		return props.getProperty(LSM_CONNECTION_DRIVER);
	}

	public String getLsmServerConnectionURL() {
		return props.getProperty(LSM_CONNECTION_URL);
	}

	public String getLsmServerUserName() {
		return props.getProperty(LSM_CONNECTION_USERNAME);
	}

	public String getLsmServerPass() {
		return props.getProperty(LSM_CONNECTION_PASS);
	}

	public String getSecurityLsmSparqlEndPoint() {
		return props.getProperty(SECURITY_LSM_SPARQL_END_POINT);
	}

	public String getSecurityLsmGraphURL() {
		return props.getProperty(SECURITY_LSM_GRAPH);
	}

	public int getLsmMinConnection() {
		try {
			return Integer.parseInt(props.getProperty(LSM_MIN_CONNECTION));
		} catch (Exception e) {
			logger.error("Invalid input value", e);
		}
		return -99;
	}

	public int getLsmMaxConnection() {
		try {
			return Integer.parseInt(props.getProperty(LSM_MAX_CONNECTION));
		} catch (Exception e) {
			logger.error("Invalid input value", e);
		}
		return -99;
	}

	public int getLsmRetryAttempts() {
		try {
			return Integer.parseInt(props.getProperty(LSM_RETRY_ATTEMPTS));
		} catch (Exception e) {
			logger.error("Invalid input value", e);
		}
		return -99;
	}

	public HashMap<String, String> getIdeNavigationSettings() {
		HashMap<String, String> navigationMap = new HashMap<String, String>();

		for (String key : props.stringPropertyNames()) {
			if (key.startsWith(IDE_CORE_NAVIGATION_PREFIX)) {
				navigationMap.put(key, props.getProperty(key));
			}
		}
		return navigationMap;
	}

	public String getLSMLocalMetaGraph() {
		return props.getProperty(LSM_LOCAL_METAGRAPH);
	}

	public String getLSMLocalDataGraph() {
		return props.getProperty(LSM_LOCAL_DATAGRAPH);
	}

	public String getLSMClientConnectionServerHost() {
		return props.getProperty(LSM_CLIENT_CONNECTION_SERVER_HOST);
	}

	public String getOpeniotOntologyNamespace() {
		return props.getProperty(OPENIOT_ONTOLOGY_NAMESPACE);
	}

	public String getOpeniotResourceNamespace() {
		return props.getProperty(OPENIOT_RESOURCE_NAMESPACE);
	}

	public String getCASLogoutURL() {
		String serverName = props.getProperty("server.name");
		String serverPrefix = props.getProperty("server.prefix");
		String appName = serverPrefix.substring(serverPrefix.lastIndexOf("/") + 1);
		return serverName + "/" + appName + "/logout";
	}

	public String getSchemaEditorTitle() {
		return props.getProperty(SCHEMA_EDITOR_TITLE);
	}

	public String getSchemaEditorUrl() {
		return props.getProperty(SCHEMA_EDITOR_URL);
	}

}
