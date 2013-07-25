package org.openiot.gsn.acquisition2;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SafeStorageDB {

  public static transient Logger logger= Logger.getLogger ( SafeStorageDB.class );

  private Connection connection;

  private String dbUrl = null;

  private static final String SAFESTORAGE_PROPERTIES_FILE = "conf/safestorage.properties";
  private static final String DEFAULT_SAFESTORAGE_PATH = "."; //by default db files are saved in GSN's default directory

  public SafeStorageDB(int safeStoragePort) throws ClassNotFoundException, SQLException {
    Class.forName("org.h2.Driver");
    dbUrl = getDBUrl(SAFESTORAGE_PROPERTIES_FILE, "storage" + safeStoragePort);
    logger.warn("Connecting to : " + dbUrl);
    connection = getConnection();
  }

  private void close(Connection c) {
    try {
      if (c!=null && !c.isClosed())
        c.close();
    }catch (Exception e) {
    }
  }

  public Connection getConnection() throws SQLException {
    if (connection ==null || connection.isClosed())
     connection = DriverManager.getConnection(dbUrl, "sa", "");
    return connection;
  }



  public void executeSQL(String string) throws SQLException {
    Statement stmt = connection.createStatement();
    stmt.execute(string);
    stmt.close();
  }

  public String prepareTableIfNeeded(String requester) throws SQLException {
	// requester like: ss_mem_vs/data/mem2
    final Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("select table_name from SETUP where requester = '"+requester+"'");
    String toReturn = null;
    if (rs.next()) { //exists
      toReturn =rs.getString(1);
    }
    else { //create
      toReturn  = "_"+Integer.toString((int)(Math.random()*10000000));
      //create the table to store the data
      stmt.execute("create table "+toReturn+" (pk bigint not null identity primary key, processed boolean not null default false, stream_element ARRAY not null, created_at timestamp not null default CURRENT_TIMESTAMP())");
      PreparedStatement ps = connection.prepareStatement("insert into setup(table_name,requester) values (?,?) ");
      ps.setString(1,toReturn);
      ps.setString(2, requester);
      ps.execute();
      ps.close();
    }
    stmt.close();
    return toReturn;
  }


  public void dropAllTables () {
	  try {
		// Works only with H2 DB
		PreparedStatement psTableList = createPreparedStatement("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where SQL is not null");
		Statement sDrop = getConnection().createStatement();
		ResultSet tableList = psTableList.executeQuery();
		String tableName;
		while (tableList.next()) {
			tableName = tableList.getString(1);
			logger.warn("Drop table >" + tableName + "<");
			sDrop.execute("drop table " + tableName);
		}
		sDrop.close();
		psTableList.close();
	} catch (SQLException e) {
		logger.error(e.getMessage());
	}
  }



  public PreparedStatement createPreparedStatement(String sqlCommand) throws SQLException {
    PreparedStatement ps = getConnection().prepareStatement(sqlCommand);
    return ps;
  }

    /*
    * Creates a well-formed h2 jdb url
    * using the path given in properties file 
    * and the database name
    * */
    public static String getDBUrl(String safe_storage_properties_file, String databaseName) {

        Properties props = new Properties();
        String dbPath = null;

        try { //try to retrieve path to db files from properties file

            props.load(new FileInputStream(safe_storage_properties_file));
            dbPath = props.getProperty("path", DEFAULT_SAFESTORAGE_PATH);
            if (dbPath.isEmpty())
                dbPath = DEFAULT_SAFESTORAGE_PATH;
            logger.warn("Path for safestorage db files: " + dbPath);
        }
        catch (IOException e) { //catch exception in case properties file does not exist
            dbPath = DEFAULT_SAFESTORAGE_PATH;
            logger.warn("Couldn't find safe storage properties file: " + safe_storage_properties_file + " , using defaults ");
            logger.warn("Path for safestorage db files: " + dbPath);
        }

        return "jdbc:h2:" + dbPath + "/"+ databaseName + ".h2";

    }

    /*
    * Creates a well-formed db url using the path given the database name
    * and default properties files
    * */
    public static String getDBUrl(String databaseName) {
          return getDBUrl(SAFESTORAGE_PROPERTIES_FILE, databaseName);
    }
}
