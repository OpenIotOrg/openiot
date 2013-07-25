package org.openiot.gsn.wrappers.ieee1451;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * This is a dummy data generator which is highly configurable through the
 * wrappers configuration in the virtual sensor's configuration file.
 * 
 */

public class TEDSDummyDataProducer extends AbstractWrapper {
   
   private final Logger                logger        = Logger.getLogger( TEDSDummyDataProducer.class );
   
   /**
    * Initializes the DataSource object. This method should be called before any
    * real task to be done.
    */
   private static int                  threadCounter = 0;
   
   private ArrayList < TEDSDataField > tedsPredicatesList;
   
   private int                         NumOfChannels;
   
   /**
    * Configurable option through the xml file are :<br>
    * rate : the output rate in milli seconds <br>
    * size : the output stream element size in bytes, used for constructing
    * binary DATA_BIN <br>
    * range : the produced number will be from zero up to the number specified
    * in range <br>
    * burst : a float value between zero and less than 1. The higher the value,
    * the high probability of burst.<br>
    */
   boolean                             rateBased     = false;

   private DataField [ ] outputDataStructure;
   
   public boolean initialize ( ) {
      ArrayList < DataField > dataField = new ArrayList < DataField >( );
      setName( "TEDSDummyRandomDataProducer-Thread" + ( ++threadCounter ) );
      AddressBean addressBean = getActiveAddressBean( );
      /**
       * Reading from the XML Configurations provided.
       */
      
      tedsPredicatesList = new ArrayList < TEDSDataField >( );
      NumOfChannels = Integer.parseInt( ( String ) addressBean.getPredicateValue( "TotalFields" ) );
      for ( int i = 1 ; i <= NumOfChannels ; i++ ) {
         tedsPredicatesList.add( new TEDSDataField( addressBean.getPredicateValue( "field." + i ) ) );
      }
      for ( TEDSDataField field : tedsPredicatesList )
         dataField.add( new DataField( field.name , field.type , field.description ) );
      this.outputDataStructure=dataField.toArray( new DataField[] {} );
      return true;
   }
   
   public void run ( ) {
      int RATE = 3000;
      try {
         Thread.sleep( RATE );
      } catch ( InterruptedException e1 ) {
         e1.printStackTrace( );
      }
      while ( isActive( ) ) {
         
         String [ ] dataFieldNames = new String [ NumOfChannels ];
         Byte [ ] dataFieldTypes = new Byte [ NumOfChannels ];
         
         for ( int i = 0 ; i < NumOfChannels ; i++ ) {
            dataFieldNames[ i ] = tedsPredicatesList.get( i ).name;
            dataFieldTypes[ i ] = tedsPredicatesList.get( i ).dataType;
         }
         
         Serializable [ ] dataFieldValues = ( new TEDSDataField( ).RandomData( dataFieldTypes ) );// new
         StreamElement streamElement = new StreamElement( getOutputFormat( ) , dataFieldValues , System.currentTimeMillis( ) );
         try {
            postStreamElement( streamElement );
         } catch ( Exception e ) {
            logger.error( e.getMessage( ) , e );
         }
      }
      
   }
   
   public void dispose ( ) {
      threadCounter--;
   }
   
   public  DataField [] getOutputFormat ( ) {
      return outputDataStructure;
   }
   
   public class TEDSDataField {
      
      public String name;
      
      public String type;
      
      public byte    dataType;
      
      public String description;
      
      public TEDSDataField ( String xmlString ) {
         StringTokenizer tokens = new StringTokenizer( xmlString , "|" );
         this.name = tokens.nextToken( );
         this.type = tokens.nextToken( );
         this.description = tokens.nextToken( );
         this.dataType = DataTypes.convertTypeNameToGSNTypeID( type );
      }
      
      public TEDSDataField ( ) {

      }
      
      public Serializable [ ] RandomData ( Byte [ ] dataTypes ) {
         Serializable [ ] result = new Serializable [ dataTypes.length ];
         for ( int i = 0 ; i < dataTypes.length ; i++ ) {
            switch ( dataTypes[ i ] ) {
               case DataTypes.BIGINT :
               case DataTypes.INTEGER :
                  result[ i ] = ( int ) ( Math.random( ) * 255 );
                  break;
               case DataTypes.DOUBLE :
                  result[ i ] = Math.random( ) * 255;
                  break;
               case DataTypes.BINARY :
                  result[ i ] = ( byte ) Math.random( ) * 255;
                  break;
               case DataTypes.VARCHAR :
                  byte oneCharacter;
                  StringBuffer resultS = new StringBuffer( 10 );
                  for ( int ii = 0 ; ii < 10 ; ii++ ) {
                     oneCharacter = ( byte ) ( ( Math.random( ) * ( 'z' - 'a' + 1 ) ) + 'a' );
                     resultS.append( ( char ) oneCharacter );
                  }
                  result[ i ] = resultS.toString( );
                  ;
                  break;
               default :
                  break;
               
            }
         }
         return result;
      }
   }

public String getWrapperName() {
    return "TEDS ieee 1451 ieee1451 simulator";
}
}
