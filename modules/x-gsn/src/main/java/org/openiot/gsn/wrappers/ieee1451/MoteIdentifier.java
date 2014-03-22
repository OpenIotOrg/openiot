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
 * @author Ali Salehi
 * @author Mehdi Riahi
*/

package org.openiot.gsn.wrappers.ieee1451;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.InputStream;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.utils.ChangeListener;
import org.openiot.gsn.utils.LazyTimedHashMap;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import net.tinyos1x.message.Message;
import net.tinyos1x.message.MessageListener;
import net.tinyos1x.message.MoteIF;

import org.apache.log4j.Logger;


public class MoteIdentifier extends AbstractWrapper implements MessageListener , ChangeListener {
   
   private ArrayList < String >     micaTEDS            = new ArrayList < String >( );
   
   private LazyTimedHashMap         lazyActiveMicas;
   
   /**
    * The pooling interval in Milliseconds. How often pool for the TEDS. Used by motes and cameras.
    */
   private int                      pooling_interval                ;
   
   /**
    * The TimeOut in milliseconds. When to remove the TEDS. Used by motes and cameras.
    */
   private int                      TIMEOUT             = 30 * 1000;
   
   private TedsToVSResult           tedsResult;
   
   private final Logger             logger              = Logger.getLogger( MoteIdentifier.class );
   
   private int                      threadCounter       = 0;
   
   private String                   host;
   
   private int                      port;
   
   private MoteIF                   mote;
   
   private String                   status;
   
   private TedsToVirtualSensor      tedsToVirtualSensor;
   
   private static final String      ADD_ACTION          = "added";
   
   private static final String      REMOVE_ACTION       = "removed";
   
   private static final String      ID_OUTPUT_FIELD     = "ID";
   
   private static final String      TEDS_OUTPUT_FIELD   = "TEDS";
   
   private static final String      STATUS_OUTPUT_FIELD = "STATUS";
   
   private static final String      VSFILE_OUTPUT_FIELD = "VSFILE";
   
   private static final String [ ]  OUTPUT_FIELD_NAMES  = new String [ ] { ID_OUTPUT_FIELD , TEDS_OUTPUT_FIELD , STATUS_OUTPUT_FIELD , VSFILE_OUTPUT_FIELD };
   
   private static final Byte [ ] OUTPUT_FIELD_TYPES  = new Byte [ ] { DataTypes.VARCHAR , DataTypes.VARCHAR , DataTypes.VARCHAR , DataTypes.VARCHAR };
   
   private boolean                  isConsumed          = true;
   
   private File                     templateFolder;
   
   public boolean initialize ( ) {
      // mica related
      micaTEDS.add( 0 , "MicaONE.xml" );
      micaTEDS.add( 1 , "MicaTWO.xml" );
      micaTEDS.add( 2 , "MicaTHREE.xml" );
      
      AddressBean addressBean = getActiveAddressBean( );
      if ( addressBean.getPredicateValue( "TIMEOUT" ) != null ) {
         TIMEOUT = Integer.parseInt( ( String ) addressBean.getPredicateValue( "TIMEOUT" ) );
      }
      /*
       * pooling interval is five times less than the timeout value. 
       */
      pooling_interval = TIMEOUT/5;
      host =addressBean.getPredicateValue( "host" ); 
      if (host  == null ){
         logger.warn( "The MoteIdentifier couldn't initialize. The >host< parameter is missing from the set of the wrapper configuration parameters." );
         return false;
      }  
      
      port = addressBean.getPredicateValueAsInt("port", 9001);
      // ------INITIALIZING THE TEMPLATE DIRECTORY ---------
      String templateDirPath = addressBean.getPredicateValue( "templates-directory" );
      if ( templateDirPath == null ) {
         logger.warn( "The MoteIdentifier couldn't initialize. The >templates-directory< parameter is missing from the set of the wrapper configuration parameters." );
         return false;
      }
      String templateFile = addressBean.getPredicateValue( "template-file" );
      if ( templateFile == null ) {
         logger.warn( "The MoteIdentifier couldn't initialize. The >template-file< parameter is missing from the set of the wrapper configuration parameters." );
         return false;
      }
      
      templateFolder = new File( templateDirPath );
      if ( !templateFolder.exists( ) || !templateFolder.isDirectory( ) || !templateFolder.canRead( ) ) {
         logger.warn( "The MoteIdentifier couldn't initialize. Can't read >" + templateFolder.getAbsolutePath( ) + "<." );
         return false;
      }
      
      File templateF = new File( templateFolder.getAbsolutePath( ) + "/" + templateFile + ".st" );
      if ( !templateF.exists( ) || !templateF.isFile( ) || !templateF.canRead( ) ) {
         logger.warn( "The MoteIdentifier couldn't initialize. Can't read >" + templateF.getAbsolutePath( ) + "<." );
         return false;
      }
      tedsToVirtualSensor = new TedsToVirtualSensor( templateDirPath , templateFile );
      // ------INITIALIZING THE TEMPLATE DIRECTORY ---------DONE
      //
      lazyActiveMicas = new LazyTimedHashMap( TIMEOUT );
      lazyActiveMicas.addChangeListener( this );
      // Serial Forwarder Related
      if ( logger.isDebugEnabled( ) ) logger.debug( "The MoteIdentifier connects to the Serial Forwarder interface at *" + host + ":" + port + "*" );
      setName( "MoteIdentifier-Thread" + ( ++threadCounter ) );
      logger.info("Initializing the serial forwarder connection to:  "+host+":"+port);
      mote = new MoteIF( host , port );
      mote.registerListener( new TedsMessage( ) , this );
      outputStructure = new DataField [ ] { new DataField( ID_OUTPUT_FIELD , "varchar(20)" , "Id of the detected transducer" ) , new DataField( TEDS_OUTPUT_FIELD , "VARCHAR(8000)" , "TEDS-data" ) ,
            new DataField( STATUS_OUTPUT_FIELD , "VARCHAR(20)" , "status:added or removed" ) , new DataField( VSFILE_OUTPUT_FIELD , "VARCHAR(40)" , "Virtual Sensor Filename" ) };
      
      return true;
   }
   
   /**
    * 1. GSN Data Containing SensorReadings 2. TEDS ID Packet sent by the mote
    * containing TEDS_ID. its simple.. In this file.. we don't care about the
    * GSNMessages..... all we care about is TEDSMessages.. Once we get the
    * TEDSMessages.... We create corrsponding VS xmls..
    * 
    * @param to Receiver ID
    * @param m The actual message Received by us.
    */
   public synchronized void messageReceived ( int to , Message m ) {
      if ( isConsumed == false ) {
         if ( logger.isInfoEnabled( ) ) logger.info( "A Message is dropped because buffer is full." );
         return;
      }
      if ( m instanceof TedsMessage ) {
         if ( ( ( TedsMessage ) m ).dataLength( ) == 1 ) {
            if ( logger.isDebugEnabled( ) ) {
               logger.debug( "TedsMessage Received." );
               logger.debug( m );
            }
            int tedsID = ( ( TedsMessage ) m ).get_TEDS_ID( );
            if ( lazyActiveMicas.get( tedsID ) != null ) {
               if ( logger.isDebugEnabled( ) ) logger.debug( "The sensor is alive and the virtual sensor file exists." );
            } else {
               status = ADD_ACTION;
               isConsumed = false;
               generateStreamElement( TedsReader.readTedsFromXMLFile( new File( templateFolder.getAbsolutePath( ) + "/" + micaTEDS.get( tedsID ) ) ) , status );
               if ( logger.isInfoEnabled( ) ) logger.info( "TEDS received and virtual sensor is generated with ID " + tedsID );
            }
            lazyActiveMicas.put( tedsID , Integer.toString( tedsID ) );
         }
      } else {
         logger.warn( "DROPED : UNKOWN MESSAGE RECEVED" );
      }
      
   }
   
   private void generateStreamElement ( TEDS teds , String status ) {
      try {
         Thread.sleep( 3000 );
      } catch ( InterruptedException e ) {
         e.printStackTrace( );
      }
      try {
         if ( status == ADD_ACTION ) tedsResult = tedsToVirtualSensor.GenerateVS( teds );
         if ( status == REMOVE_ACTION ) tedsResult = tedsToVirtualSensor.getTedsToVSResult( teds );
         StreamElement streamElement = new StreamElement( OUTPUT_FIELD_NAMES , OUTPUT_FIELD_TYPES ,
            new Serializable [ ] { tedsResult.tedsID , tedsResult.tedsHtmlString , status , tedsResult.fileName } , System.currentTimeMillis( ) );
         postStreamElement( streamElement );
         isConsumed = true;
      } catch ( RuntimeException e1 ) {
         logger.warn( "*TEDS ERROR" + e1.getMessage( ) , e1 );
      }
   }
   
   private static transient DataField [ ] outputStructure;
   
   public DataField [ ] getOutputFormat ( ) {
      return outputStructure;
   }
   
   public void dispose ( ) {
      threadCounter--;
   }
   
   public void run ( ) {
      try {
         Thread.sleep( InputStream.INITIAL_DELAY_5000MSC * 2 );
      } catch ( InterruptedException e ) {
         e.printStackTrace( );
      }
      mote.start( );
      while ( isActive( ) ) {
         try {
            Thread.sleep( pooling_interval );
         } catch ( InterruptedException e ) {
            logger.error( e.getMessage( ) , e );
         }
         if ( listeners.isEmpty( ) ) continue;
         TedsRequest tedsReq = new TedsRequest( );
         tedsReq.set_Teds_Request( ( short ) 1 );
         try {
            mote.send( MoteIF.TOS_BCAST_ADDR , tedsReq );
         } catch ( IOException e1 ) {
            e1.printStackTrace( );
         }
         lazyActiveMicas.update( );
      }
   }
   
   public void changeHappended ( String changeType , Object changedKey , Object changeValue ) {
      if ( changeType == LazyTimedHashMap.ITEM_REMOVED ) {
         status = REMOVE_ACTION;
         generateStreamElement( TedsReader.readTedsFromXMLFile( new File( templateFolder.getAbsolutePath( ) + "/" + micaTEDS.get( ( Integer ) changedKey ) ) ) , status );
         boolean success = ( new File( TedsToVirtualSensor.TARGET_VS_DIR + tedsResult.fileName ) ).delete( );
      }
   }
   
   public String getWrapperName ( ) {
      return "IEEE1451 IEEE 1451 mica mote";
   }
}
