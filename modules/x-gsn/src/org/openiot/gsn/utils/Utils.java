package org.openiot.gsn.utils;

import org.openiot.gsn.beans.VSensorConfig;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utils {

    private static final transient Logger logger = Logger.getLogger(Utils.class);

    public static Properties loadProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            logger.warn("Unable to load the property file: " + path);
            return null;
        }
        return properties;
    }

    public static String identify(VSensorConfig vsConfig) {
        return new StringBuilder(vsConfig.getName()).append(" [").append(vsConfig.getFileName()).append("]").toString();
    }

}
