package org.openiot.gsn.metadata.LSM;

import org.openiot.gsn.utils.PropertiesReader;
import org.apache.log4j.Logger;

public class LSMUser {

    private static final transient Logger logger = Logger.getLogger(LSMUser.class);

    private String user;
    private String password;

    public boolean initFromConfigFile(String fileName) {
        try {
            this.setUser(PropertiesReader.readProperty(fileName, "username"));
            this.setPassword(PropertiesReader.readProperty(fileName, "password"));
        } catch (NullPointerException e) {
            logger.warn("Error while reading properties file: " + fileName);
            logger.warn(e);
            return false;
        }


        return true;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LSMUser{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
