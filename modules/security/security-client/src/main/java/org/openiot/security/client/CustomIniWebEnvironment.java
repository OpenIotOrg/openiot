package org.openiot.security.client;

import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomIniWebEnvironment extends IniWebEnvironment {
	private static final Logger logger = LoggerFactory.getLogger(CustomIniWebEnvironment.class);
	private static final String MODULE_NAME_PARAM = "module-name";

	@Override
	public void init() {
		String jbossConfigDir = System.getProperty("jboss.server.config.dir");
		Ini ini = null;

		String moduleName = getServletContext().getInitParameter(MODULE_NAME_PARAM);

		if (StringUtils.hasText(moduleName) && jbossConfigDir != null) {
			ini = ConfigFileReader.getIniConfig(jbossConfigDir, moduleName);
		}
		if (CollectionUtils.isEmpty(ini)) {
			logger.info("Falling back to the web-client.ini in the class path");
			String confFilePath = "classpath:web-client.ini";
			ini = getSpecifiedIni(new String[] { confFilePath });
		}

		setIni(ini);
		configure();

	}

}
