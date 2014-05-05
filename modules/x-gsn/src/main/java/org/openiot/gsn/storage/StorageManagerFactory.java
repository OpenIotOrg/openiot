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
 * @author Timotee Maret
 * @author Sofiane Sarni
*/

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
