package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.utils.KeyValueImp;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.commons.collections.KeyValue;

public class TestStreamExporterVirtualSensor extends TestCase {
   
   private final String  user = "sa" , passwd = "" , db = "." , url = "jdbc:hsqldb:mem:." , streamName = "aJUnitTestStream";
   
   private VSensorConfig config;
   
   /*
    * To run some of these tests, a mysql server must be running on localhost
    * with the following configuration: a database 'gsntest' must exist. a user
    * 'gsntest' with password 'gsntest' must exist and have all privileges on
    * the 'gsntest' database. You can do it with the following: mysql -u -p root
    * create user 'gsntest' IDENTIFIED BY 'gsntest'; create database gsntest ;
    * grant ALL ON gsntest.* TO gsntest ; (non-Javadoc)
    * 
    * @see junit.framework.TestCase#setUp()
    */
   public void setUp ( ) {
      config = new VSensorConfig( );
      config.setName( "JUnitTestStreamExporterVS" );
      config.setFileName( "PlaceholderfileNameForJUNitTesting" );
      
   }
   
   public void tearDown ( ) {
      config = null;
      try {
         DriverManager.registerDriver( new org.h2.Driver() );
         Connection connection = DriverManager.getConnection( url , user , passwd );
         connection.createStatement( ).execute( "DROP TABLE IF EXISTS " + streamName );
      } catch ( SQLException e ) {
          e.printStackTrace( );
      }
      
   }
   
   /*
    * Tries to instantiate a VS without the required arguments. Should always
    * fail.
    */
   public void testMissingAllEssentialParameters ( ) {
      StreamExporterVirtualSensor vs = new StreamExporterVirtualSensor( );
      assertFalse( vs.initialize( ) );
   }
   
   /*
    * Tries to connect to a (supposedly) existing mysql db on local host. See
    * class comments for more info. Should succeed.
    */
   public void testConnectToExistingMySQLDB ( ) {
      StreamExporterVirtualSensor vs = new StreamExporterVirtualSensor( );
      ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
      params.add( new KeyValueImp( StreamExporterVirtualSensor.PARAM_URL , url ) );
      params.add( new KeyValueImp( StreamExporterVirtualSensor.PARAM_USER , user ) );
      params.add( new KeyValueImp( StreamExporterVirtualSensor.PARAM_PASSWD , passwd ) );
      config.setMainClassInitialParams( params );
      vs.setVirtualSensorConfiguration( config );
      assertTrue( vs.initialize( ) );
   }
   
   /*
    * Tries to log a line into a Mysql table. The test stream generates data for
    * each possible data type.
    */
   public void testLogStatementIntoMySQLDB ( ) {
      StreamExporterVirtualSensor vs = new StreamExporterVirtualSensor( );
      // configure parameters
      ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
      params.add( new KeyValueImp( StreamExporterVirtualSensor.PARAM_URL , url ) );
      params.add( new KeyValueImp( StreamExporterVirtualSensor.PARAM_USER , user ) );
      params.add( new KeyValueImp( StreamExporterVirtualSensor.PARAM_PASSWD , passwd ) );
      config.setMainClassInitialParams( params );
      vs.setVirtualSensorConfiguration( config );
      vs.initialize( );
      
      // configure datastream
      Vector < DataField > fieldTypes = new Vector < DataField >( );
      Object [ ] data = null;
      
      for ( String type : DataTypes.TYPE_NAMES )
         fieldTypes.add( new DataField( type , type , type ) );
      int i = 0;
      for ( Object value : DataTypes.TYPE_SAMPLE_VALUES )
         data[ i++ ] = value;
      
      long timeStamp = new Date( ).getTime( );
      StreamElement streamElement = new StreamElement( fieldTypes.toArray( new DataField[] {} ) , ( Serializable [ ] ) data , timeStamp );
      
      // give datastream to vs
      vs.dataAvailable( streamName , streamElement );
      
      // clean up and control
      boolean result = true;
      try {
         DriverManager.registerDriver( new com.mysql.jdbc.Driver( ) );
         Connection connection = DriverManager.getConnection( url , user , passwd );
         Statement statement = connection.createStatement( );
         statement.execute( "SELECT * FROM " + streamName );
         System.out.println( "result" + result );
         result = statement.getResultSet( ).last( );
         System.out.println( "result" + result );
      } catch ( SQLException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace( );
         result = false;
      }
      assertTrue( result );
   }
   
}
