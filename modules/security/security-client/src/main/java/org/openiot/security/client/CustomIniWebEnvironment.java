package org.openiot.security.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.security.client.rest.CasOAuthWrapperClientRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomIniWebEnvironment extends IniWebEnvironment {
	private static final Logger logger = LoggerFactory.getLogger(CustomIniWebEnvironment.class);
	private static final String MODULE_NAME_PARAM = "module-name";

	@Override
	public void init() {
		String jbossConfigDir = System.getProperty("jboss.server.config.dir");
		String key = null;
		String secret = null;
		Ini ini = null;

		String moduleName = getServletContext().getInitParameter(MODULE_NAME_PARAM);

		if (StringUtils.hasText(moduleName) && jbossConfigDir != null) {
			PropertyManagement props = new PropertyManagement();
			String iniFilePath = jbossConfigDir + "/web-client-" + moduleName + ".ini";
			Path path = Paths.get(iniFilePath);
			if (!Files.exists(path) || Files.isDirectory(path)) {
				logger.warn("The configuration file {} is not found.", iniFilePath);
			} else {
				ini = getSpecifiedIni(new String[] { "file:" + iniFilePath });
			}

			key = props.getProperty("casOauthClient.key." + moduleName, null);
			secret = props.getProperty("casOauthClient.secret." + moduleName, null);
		}
		if (CollectionUtils.isEmpty(ini)) {
			logger.info("Falling back to the web-client.ini in the class path");
			String confFilePath = "classpath:web-client.ini";
			ini = getSpecifiedIni(new String[] { confFilePath });
		}

		setIni(ini);
		configure();

		if (key != null && secret != null) {
			CasOAuthWrapperClientRest bean = getObject("casOauthClient", CasOAuthWrapperClientRest.class);
			bean.setKey(key);
			bean.setSecret(secret);
		} else {
			logger.warn("casOauthClient.key.{} or/and casOauthClient.secret.{} is not set in the global properties file", moduleName, moduleName);
		}
	}


}
