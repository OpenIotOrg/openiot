package org.openiot.gsn.beans;

public class StorageConfig {

    private String jdbcDriver;

    private String jdbcUsername;

    private String jdbcPassword;

    private String jdbcURL;

    private String identifier;

    private String storageSize;

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getStorageSize() {
        return storageSize;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setStorageSize(String storageSize) {
        this.storageSize = storageSize;
    }

    public boolean isStorageSize() {
        return storageSize != null;
    }

    public boolean isJdbcDefined() {
        return jdbcDriver != null
                && jdbcPassword != null
                && jdbcURL != null
                && jdbcUsername != null;
    }

    public boolean isIdentifierDefined() {
        return identifier != null;
    }

    public boolean isDefined() {
        return isJdbcDefined() || isIdentifierDefined();
    }

    

}
