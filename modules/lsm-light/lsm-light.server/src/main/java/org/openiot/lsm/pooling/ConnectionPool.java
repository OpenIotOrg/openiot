package org.openiot.lsm.pooling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.openiot.lsm.http.ObjectServlet;
import org.openiot.lsm.utils.ConstantsUtil;



import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class ConnectionPool {
	private static BoneCP SQLPooled; 
	
	public static BoneCP getConnectionPool(){
		return SQLPooled;
	}
	
	static{
		init();
	}
	
	public static void init(){
		  Properties prop = new Properties();
		  
//		  String driver="virtuoso.jdbc4.Driver";	
	      try {	    	  	       
	    	  
	    	  //load a LSM Database Connector properties file
	    	System.out.println(ObjectServlet.realPath);  
	    	prop.load(new FileInputStream(ObjectServlet.realPath+"/classes/lsm_DBConnector_config.properties"));
	    	String driver = prop.getProperty("connection.driver_class");
	    	  
	        Class.forName(driver);		
	        BoneCPConfig config = new BoneCPConfig();
//	          config.setJdbcUrl("jdbc:virtuoso://140.203.155.176:1111/DERI.DBA/log_enable=2");
	        config.setJdbcUrl(prop.getProperty("connection.url"));
			config.setUsername(prop.getProperty("connection.username")); 
			config.setPassword(prop.getProperty("connection.password"));
			
			int minConn = Integer.parseInt(prop.getProperty("minConnection"));
			int maxConn = Integer.parseInt(prop.getProperty("maxConnection"));
			int acq_attemps = Integer.parseInt(prop.getProperty("maxConnection"));
			
			config.setMinConnectionsPerPartition(minConn);
			config.setMaxConnectionsPerPartition(maxConn);
			config.setPartitionCount(1);
			config.setTransactionRecoveryEnabled(true);
			config.setAcquireRetryAttempts(acq_attemps);//default 5
			SQLPooled = new BoneCP(config); // setup the connection pool	

	      } catch(SQLException sqle) {
	        System.err.println("Error making pool: " + sqle);	        
	      } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	 public static void attemptClose(ResultSet o)
	    {
		try
		    { if (o != null) o.close();}
		catch (Exception e)
		    { e.printStackTrace();}
	    }

	 public  static void attemptClose(Statement o)
	    {
		try
		    { if (o != null) o.close();}
		catch (Exception e)
		    { e.printStackTrace();}
	    }

	 public  static void attemptClose(Connection o)
	    {
		try
		    { if (o != null) o.close();}
		catch (Exception e)
		    { e.printStackTrace();}
	    }
	 
	 public static void main(String[] agrs){
		 
	 }
}
