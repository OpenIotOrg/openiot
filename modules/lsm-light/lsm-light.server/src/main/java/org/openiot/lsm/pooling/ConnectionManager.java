package org.openiot.lsm.pooling;
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
*/
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.openiot.commons.util.PropertyManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
public class ConnectionManager {
	private static BoneCP SQLPooled; 
	final static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	static private PropertyManagement propertyManagement = null;
	
	public static BoneCP getConnectionPool(){
		return SQLPooled;
	}
	
	public static void setConnectionPool(BoneCP pools){
		ConnectionManager.SQLPooled = pools;
	}
	
	public static void init(){
//		  Properties prop = new Properties();
		  propertyManagement = new PropertyManagement();
	
	      try {	    	  	       	    	  
	    	  //load a LSM Database Connector properties file
	    	logger.info("Loading property file"); 
	    	String driver = propertyManagement.getLsmServerConnectionDriver();
	    	logger.info("loading database server driver "+driver);  
	        Class.forName(driver);		
	        BoneCPConfig config = new BoneCPConfig();
	        config.setJdbcUrl(propertyManagement.getLsmServerConnectionURL());
			config.setUsername(propertyManagement.getLsmServerUserName()); 
			config.setPassword(propertyManagement.getLsmServerPass());
			
			int minConn = propertyManagement.getLsmMinConnection();
			int maxConn = propertyManagement.getLsmMaxConnection();
			int acq_attemps = propertyManagement.getLsmRetryAttempts();
			
			config.setMinConnectionsPerPartition(minConn);
			config.setMaxConnectionsPerPartition(maxConn);
			config.setPartitionCount(2);
			config.setAcquireIncrement(5);
			config.setTransactionRecoveryEnabled(true);
//			config.setAcquireRetryAttempts(acq_attemps);//default 5
			SQLPooled = new BoneCP(config); // setup the connection pool	
			logger.info("contextInitialized.....Connection Pooling is configured");
			logger.info("Total connections ==> " + SQLPooled.getTotalCreatedConnections());
			ConnectionManager.setConnectionPool(SQLPooled);
	      } catch(SQLException sqle) {
	        logger.error("Error making pool: " , sqle);	        
	      } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public static void shutdownConnPool() {		 
		try {
			BoneCP connectionPool = ConnectionManager.getConnectionPool();
			logger.info("contextDestroyed....");
			if (connectionPool != null) {
				 connectionPool.shutdown(); //this method must be called only once when the application stops.
				//you don't need to call it every time when you get a connection from the Connection Pool
				logger.info("contextDestroyed.....Connection Pooling shut downed!");
			}		 
		} catch (Exception e) {
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
		    {
			if (o != null) 
		    	o.close();
		    }
		catch (Exception e)
		    { e.printStackTrace();}
	    }
	 
	 public static Connection getConnection() {		 
		 Connection conn = null;
		 try {
			  conn = getConnectionPool().getConnection();
			 //will get a thread-safe connection from the BoneCP connection pool.
			 //synchronization of the method will be done inside BoneCP source		  
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return conn;		  
	}
	 
	 public static void main(String[] agrs){
		 init();
	 }
	 
}
