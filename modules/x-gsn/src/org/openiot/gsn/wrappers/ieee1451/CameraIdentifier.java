package org.openiot.gsn.wrappers.ieee1451;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.InputStream;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class CameraIdentifier extends AbstractWrapper {
   
   /**
    * 
    */
   private static final Object [ ][ ] Channel1 = { { MeasAttr.NAME , "PICTURE" } , { MeasAttr.DESCRIPTION , "Camera Picture" } , { MeasAttr.DATA_TYPE , "binary:image/jpeg" } , { MeasAttr.METADATA_ID , "Channel 1" } };

   private ArrayList < String >                            camIPs                = new ArrayList < String >( );
   
   private ArrayList < Object [ ][ ][ ] >                  camTEDS               = new ArrayList < Object [ ][ ][ ] >( );
   
   private ArrayList < String >                            activeCams            = new ArrayList < String >( );
   
   private int                                             RATE                  = 15000;
   
   private TedsToVSResult                                  tedsResult;
   
   private final Logger                                    logger                = Logger.getLogger( CameraIdentifier.class );
   
   private int                                             threadCounter         = 0;
   
   private String                                          status                = NONE_ACTION;
   
   private TedsToVirtualSensor                             tedsToVirtualSensor;
   
   private static final String                             NONE_ACTION           = "none";
   
   private static final String                             ADD_ACTION            = "added";
   
   private static final String                             REMOVE_ACTION         = "removed";
   
   private static final String                             ID_OUTPUT_FIELD       = "ID";
   
   private static final String                             TEDS_OUTPUT_FIELD     = "TEDS";
   
   private static final String                             STATUS_OUTPUT_FIELD   = "STATUS";
   
   private static final String                             VSFILE_OUTPUT_FIELD   = "VSFILE";
   
   private static final String [ ]                         OUTPUT_FIELD_NAMES    = new String [ ] { ID_OUTPUT_FIELD , TEDS_OUTPUT_FIELD , STATUS_OUTPUT_FIELD , VSFILE_OUTPUT_FIELD };
   
   private static final Byte [ ]                        OUTPUT_FIELD_TYPES    = new Byte [ ] { DataTypes.VARCHAR , DataTypes.VARCHAR , DataTypes.VARCHAR , DataTypes.VARCHAR };
   
   private static final transient  DataField [] cachedOutputStructure = new DataField[] {new DataField( ID_OUTPUT_FIELD , "VARCHAR(20)" , "Id of the detected transducer" ) 
   , new DataField( TEDS_OUTPUT_FIELD , "VARCHAR(8000)" , "TEDS-data" ) 
   , new DataField( STATUS_OUTPUT_FIELD , "VARCHAR(20)" , "status:added or removed" ) 
   , new DataField( VSFILE_OUTPUT_FIELD , "VARCHAR(40)" , "Virtual Sensor Filename" ) };
   
   public boolean initialize ( ) {
       
      String pingCommand = "ping ";
      String pingCommandParams = null;
      if ( System.getProperty( "os.name" ).equals( "Mac OS X" ) )
         pingCommandParams = " -c1 -t1 ";
      else if ( System.getProperty( "os.name" ).toLowerCase( ).indexOf( "linux" ) >= 0 )
         pingCommandParams = " -c1 -w1 ";
      else if (System.getProperty("os.name").toLowerCase().indexOf("windows")>=0)
    	  pingCommandParams="-n 1 -w 1000 ";
      else
         logger.error( "Not defined for your OS : "+System.getProperty("os.name") );
      camIPs.add( 0 , pingCommand + pingCommandParams + "192.168.51.30" );
      camIPs.add( 1 , pingCommand + pingCommandParams + "192.168.51.31" );
      camIPs.add( 2 , pingCommand + pingCommandParams + "192.168.51.32" );
      camIPs.add( 3 , pingCommand + pingCommandParams + "192.168.51.33" );
      camIPs.add( 4 , pingCommand + pingCommandParams + "192.168.51.34" );
      camIPs.add( 5 , pingCommand + pingCommandParams + "192.168.51.35" );
      
      camTEDS.add( 0 , tedsCam1 );
      camTEDS.add( 1 , tedsCam2 );
      camTEDS.add( 2 , tedsCam3 );
      camTEDS.add( 3 , tedsCam4 );
      camTEDS.add( 4 , tedsCam5 );
      camTEDS.add( 5 , tedsCam6 );
      
      AddressBean addressBean = getActiveAddressBean( );
      if ( addressBean.getPredicateValue( "RATE" ) != null ) RATE = Integer.parseInt( ( String ) addressBean.getPredicateValue( "RATE" ) );
      
      // ------INITIALIZING THE TEMPLATE DIRECTORY ---------
      String templateDirPath = addressBean.getPredicateValue( "templates-directory" );
      if ( templateDirPath == null ) {
         logger.warn( "The CameraIdentifier couldn't initialize. The >templates-directory< parameter is missing from the set of the wrapper configuration parameters." );
         return false;
      }
      String templateFile = addressBean.getPredicateValue( "template-file" );
      if ( templateFile == null ) {
         logger.warn( "The CameraIdentifier couldn't initialize. The >template-file< parameter is missing from the set of the wrapper configuration parameters." );
         return false;
      }
      
      File templateFolder = new File( templateDirPath );
      if ( !templateFolder.exists( ) || !templateFolder.isDirectory( ) || !templateFolder.canRead( ) ) {
         logger.warn( "The CameraIdentifier couldn't initialize. Can't read >" + templateFolder.getAbsolutePath( ) + "<." );
         return false;
      }
      
      File templateF = new File( templateFolder.getAbsolutePath( ) + "/" + templateFile + ".st" );
      if ( !templateF.exists( ) || !templateF.isFile( ) || !templateF.canRead( ) ) {
         logger.warn( "The CameraIdentifier couldn't initialize. Can't read >" + templateF.getAbsolutePath( ) + "<." );
         return false;
      }
      tedsToVirtualSensor = new TedsToVirtualSensor( templateDirPath , templateFile );
      // ------INITIALIZING THE TEMPLATE DIRECTORY ---------DONE
      
      setName( "CameraIdentifier-Thread" + ( ++threadCounter ) );
      try {
         Thread.sleep( 4000 );
      } catch ( InterruptedException e ) {
         e.printStackTrace( );
      }
      return true;
   }
   
   public void run ( ) {
      /**
       * Initial delay to make sure than non of packets are dropped b/c of the
       * intiaial delay.
       */
      try {
         Thread.sleep( InputStream.INITIAL_DELAY_5000MSC * 2 );
      } catch ( InterruptedException e ) {
         e.printStackTrace( );
      }
      while ( isActive( ) ) {
         try {
            Thread.sleep( RATE );
         } catch ( InterruptedException e ) {
            logger.error( e.getMessage( ) , e );
         }
         Boolean pingResult;
         if ( listeners.isEmpty( ) ) continue;
         for ( String strIP : camIPs ) {
            try {
               Process p = Runtime.getRuntime( ).exec( strIP );
               try {
                  p.waitFor( );
               } catch ( InterruptedException e ) {
                  e.printStackTrace( );
               }
               int res = p.exitValue( );
               pingResult = ( res == 0 ) ? true : false;
               
            } catch ( IOException e ) {
               pingResult = false;
            }
            if ( pingResult ) {
               if ( !activeCams.contains( strIP ) ) {
                  status = ADD_ACTION;
                  generateStreamElement( new TEDS( camTEDS.get( camIPs.indexOf( strIP ) ) ) , status );
                  activeCams.add( strIP );
               }
            } else {
               if ( activeCams.contains( strIP ) ) {
                  activeCams.remove( strIP );
                  status = REMOVE_ACTION;
                  generateStreamElement( new TEDS( camTEDS.get( camIPs.indexOf( strIP ) ) ) , status );
                  boolean success = ( new File( TedsToVirtualSensor.TARGET_VS_DIR + tedsResult.fileName ) ).delete( );
                  if ( !success ) {
                     logger.warn( "Can't remove the non-live camera." );
                  }
               }
               
            }
         }
      }
   }
   
   private void generateStreamElement ( TEDS teds , String status ) {
      try {
         if ( status == ADD_ACTION ) tedsResult = tedsToVirtualSensor.GenerateVS( teds );
         if ( status == REMOVE_ACTION ) tedsResult = tedsToVirtualSensor.getTedsToVSResult( teds );
         StreamElement streamElement = new StreamElement( OUTPUT_FIELD_NAMES , OUTPUT_FIELD_TYPES ,
            new Serializable [ ] { tedsResult.tedsID , tedsResult.tedsHtmlString , status , tedsResult.fileName } , System.currentTimeMillis( ) );
         postStreamElement( streamElement );
      } catch ( RuntimeException e1 ) {
         // TODO Auto-generated catch block
         e1.printStackTrace( );
         logger.error( new StringBuilder( ).append( " ********TEDS ERROR" ).toString( ) );
      }
      try {
         Thread.sleep( 3000 );
      } catch ( InterruptedException e ) {
         e.printStackTrace( );
      }
   }
   
   public DataField [] getOutputFormat ( ) {
      return cachedOutputStructure;
   }
   public String getWrapperName() {
    return "IEEE1451 IEEE 1451 camera wireless AXIS 206W";
}
   
   public void dispose ( ) {
      threadCounter--;
   }
     
   private Object tedsCam1[][][] = {
                                 // chan0 - meta data about the TIM itself
         { { MeasAttr.NAME , "CameraF" } , { MeasAttr.DESCRIPTION , "Axis 206w Wireless Camera" } , { MeasAttr.LOCATION , "INM 035" } , { MeasAttr.IP , "192.168.51.30" } , { MeasAttr.NUMBER_OF_CHANNELS , "1" } ,
         { MeasAttr.MANUFACTURER , "GSN-LSIR-LAB" } , { MeasAttr.METADATA_ID , "Channel 0" } } ,
         // chan1
         Channel1 , };
   
   private Object tedsCam2[][][] = {
                                 // chan0 - meta data about the TIM itself
         { { MeasAttr.NAME , "CameraE" } , { MeasAttr.DESCRIPTION , "Axis 206w Wireless Camera" } , { MeasAttr.LOCATION , "INM 035" } , { MeasAttr.IP , "192.168.51.31" } , { MeasAttr.NUMBER_OF_CHANNELS , "1" } ,
         { MeasAttr.MANUFACTURER , "GSN-LSIR-LAB" } , { MeasAttr.METADATA_ID , "Channel 0" } } ,
         // chan1
         Channel1 , };
   
   private Object tedsCam3[][][] = {
                                 // chan0 - meta data about the TIM itself
         { { MeasAttr.NAME , "CameraD" } , { MeasAttr.DESCRIPTION , "Axis 206w Wireless Camera"} , { MeasAttr.IP , "192.168.51.32" } , { MeasAttr.LOCATION , "INM 035" } , { MeasAttr.NUMBER_OF_CHANNELS , "1" } ,
         { MeasAttr.MANUFACTURER , "GSN-LSIR-LAB" } , { MeasAttr.METADATA_ID , "Channel 0" } } ,
         // chan1
         Channel1 , };
   
   private Object tedsCam4[][][] = {
                                 // chan0 - meta data about the TIM itself
         { { MeasAttr.NAME , "CameraC" } , { MeasAttr.DESCRIPTION , "Axis 206w Wireless Camera" } , { MeasAttr.IP , "192.168.51.33" } , { MeasAttr.LOCATION , "INM 035" } ,
         { MeasAttr.NUMBER_OF_CHANNELS , "1" } , { MeasAttr.MANUFACTURER , "GSN-LSIR-LAB" } , { MeasAttr.METADATA_ID , "Channel 0" } } ,
         // chan1
         Channel1 , };
   
   private Object tedsCam5[][][] = {
                                 // chan0 - meta data about the TIM itself
         { { MeasAttr.NAME , "CameraB" } , { MeasAttr.DESCRIPTION , "Axis 206w Wireless Camera" } , { MeasAttr.IP , "192.168.51.34" } , { MeasAttr.LOCATION , "INM 035" } ,
         { MeasAttr.NUMBER_OF_CHANNELS , "1" } , { MeasAttr.MANUFACTURER , "GSN-LSIR-LAB" } , { MeasAttr.METADATA_ID , "Channel 0" } } ,
         // chan1
         Channel1 , };
   
   private Object tedsCam6[][][] = {
                                 // chan0 - meta data about the TIM itself
         { { MeasAttr.NAME , "CameraA" } , { MeasAttr.DESCRIPTION , "Axis 206w Wireless Camera" } , { MeasAttr.IP , "192.168.51.35" } , { MeasAttr.LOCATION , "INM 035" } ,
         { MeasAttr.NUMBER_OF_CHANNELS , "1" } , { MeasAttr.MANUFACTURER , "GSN-LSIR-LAB" } , { MeasAttr.METADATA_ID , "Channel 0" } } ,
         // chan1
         Channel1 , };
}
