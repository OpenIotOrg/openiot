package org.openiot.security.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.shiro.config.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigFileReader {
	private static Logger logger = LoggerFactory.getLogger(ConfigFileReader.class);

	private final static String CONFIG_FILE_NAME = "security-config.ini";
	private final static String CONFIG_BEGIN_DELIMITER = "@BEGIN-";
	private final static String CONFIG_END_DELIMITER = "@END-";

	public static Ini getIniConfigByFile(String configDir, String fileName) {
		if (configDir == null || fileName == null)
			return null;
		Ini ini = null;
		String iniFilePath = configDir + "/" + fileName;
		Path path = Paths.get(iniFilePath);
		if (!Files.exists(path) || Files.isDirectory(path)) {
			logger.warn("The configuration file {} was not found.", iniFilePath);
		} else {
			ini = new Ini();
			logger.info("Loading ini configuration from {} ", iniFilePath);
			ini.loadFromPath(iniFilePath);
		}

		return ini;
	}

	public static Ini getIniConfig(String configDir, String moduleName) {
		if (configDir == null || moduleName == null)
			return null;

		Ini ini = null;
		String iniFilePath = configDir + "/" + CONFIG_FILE_NAME;
		Path path = Paths.get(iniFilePath);
		if (!Files.exists(path) || Files.isDirectory(path)) {
			logger.warn("The configuration file {} was not found.", iniFilePath);
		} else {
			logger.info("Loading ini configuration from {} ", iniFilePath);
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(iniFilePath));
				String line = reader.readLine();
				boolean sectionFound = false;
				String beginDelim = CONFIG_BEGIN_DELIMITER + moduleName;
				String endDelim = CONFIG_END_DELIMITER + moduleName;
				StringBuilder sb = new StringBuilder();
				while (line != null) {
					if (!sectionFound && line.startsWith(beginDelim)) {
						sectionFound = true;
					} else if (sectionFound) {
						if (line.startsWith(endDelim)) {
							break;
						} else {
							sb.append(line).append("\n");
						}
					}
					line = reader.readLine();
				}
				if (!sectionFound || sb.length() == 0) {
					logger.error("Could not find the configuration section for module {}", moduleName);
				} else {
					ini = new Ini();
					ini.load(sb.toString());
				}

			} catch (IOException e) {
				logger.error("Error reading configuration file", e);
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						logger.error("IO Error", e);
					}
			}
		}

		return ini;
	}
}
