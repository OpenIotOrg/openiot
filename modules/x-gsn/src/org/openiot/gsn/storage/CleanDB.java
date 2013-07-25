package org.openiot.gsn.storage;

import org.openiot.gsn.Main;
import org.openiot.gsn.beans.ContainerConfig;

import java.util.ArrayList;

import org.openiot.gsn.beans.StorageConfig;
import org.apache.log4j.PropertyConfigurator;
/**
 * Removes the temporary tables, tables starting with underscore.
 */
public class CleanDB {
  
  public static void main(String[] args) throws Exception {
    PropertyConfigurator.configure ( Main.DEFAULT_GSN_LOG4J_PROPERTIES );
    ContainerConfig cc =Main.getContainerConfig();
    StorageConfig sc = cc.getSliding() != null ? cc.getSliding().getStorage() : cc.getStorage() ;
    Class.forName(sc.getJdbcDriver());
    StorageManager sm = StorageManagerFactory.getInstance(sc.getJdbcDriver ( ) , sc.getJdbcUsername ( ) , sc.getJdbcPassword ( ) , sc.getJdbcURL ( ), Main.DEFAULT_MAX_DB_CONNECTIONS);
    ArrayList<String> tables = sm.getInternalTables();
    for (String t : tables) 
      sm.executeDropTable(t);
    tables = sm.getInternalTables();
    for (String t : tables) 
      sm.executeDropView(new StringBuilder(t));
      
  }
}
