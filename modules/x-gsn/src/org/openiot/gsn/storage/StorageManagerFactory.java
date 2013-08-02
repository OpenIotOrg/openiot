package org.openiot.gsn.storage;

import org.openiot.gsn.storage.db.*;
import org.apache.log4j.Logger;

public class StorageManagerFactory {

    private static final transient Logger logger = Logger.getLogger(StorageManagerFactory.class);

    /**
     * @param driver
     * @param username
     * @param password
     * @param databaseURL
     * @param maxDBConnections
     * @return A new instance of {@link org.openiot.gsn.storage.StorageManager} that is described by its parameters, or null
     *         if the driver can't be found.
     */
    public static StorageManager getInstance(String driver, String username, String password, String databaseURL, int maxDBConnections) {
        //
        StorageManager storageManager = null;
        // Select the correct implementation
        if ("net.sourceforge.jtds.jdbc.Driver".equalsIgnoreCase(driver)) {
			storageManager = new SQLServerStorageManager();
        }
		else if ("com.mysql.jdbc.Driver".equalsIgnoreCase(driver)) {
            storageManager = new MySQLStorageManager();
        }
        else if ("oracle.jdbc.driver.OracleDriver".equalsIgnoreCase(driver)) {
            storageManager = new OracleStorageManager();
        }
        else if ("org.h2.Driver".equalsIgnoreCase(driver)) {
            storageManager = new H2StorageManager();
        }         
        else if ("org.postgresql.Driver".equalsIgnoreCase(driver)) {
            storageManager = new PostgresStorageManager();
        }
		else {
			logger.error(new StringBuilder().append("The GSN doesn't support the database driver : ").append(driver).toString());
			logger.error(new StringBuilder().append("Please check the storage elements in the configuration files."));
		}
        // Initialise the storage manager
        if (storageManager != null) {
            storageManager.init(driver, username, password, databaseURL, maxDBConnections);    
        }
        //
        return storageManager;
    }
    
}
