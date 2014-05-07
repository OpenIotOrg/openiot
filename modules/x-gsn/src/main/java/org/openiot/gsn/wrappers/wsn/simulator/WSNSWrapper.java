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

package org.openiot.gsn.wrappers.wsn.simulator;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.utils.ParamParser;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class WSNSWrapper extends AbstractWrapper implements DataListener {
   
   private final transient Logger   logger                     = Logger.getLogger( WSNSWrapper.class );
   
   private static int               threadCounter              = 0;
   
   /**
    * The rate, set the rate in which the network is re-revaluated. If the rate
    * is 1000, then the network is reevaluated every seconds. If the rate is
    * negative, the network will stop evaluating it self after specified number
    * of cycles.
    */
   private static String            RATE_KEY                   = "rate";
   
   /**
    * The Rate is specified in msec.
    */
   private static int               RATE_DEFAULT_VALUE         = 2000;
   
   private int                      rate                       = RATE_DEFAULT_VALUE;
   
   private static int               NODE_COUNT_DEFAULT_VALUE   = 10;
   
   private static String            NODE_COUNT_KEY             = "node_count";
   
   private int                      node_count                 = NODE_COUNT_DEFAULT_VALUE;
   
   private WirelessNode [ ]         nodes;
   
   private ArrayList < DataPacket > dataBuffer                 = new ArrayList < DataPacket >( );
   
   private String                   STEP_COUNTER               = "steps";
   
   private static final int         STEP_COUNTER_DEFAULT_VALUE = -1;
   
   private int                      step_counter               = STEP_COUNTER_DEFAULT_VALUE;
   
   public boolean initialize (  ) {
      setName( "WirelessSensorNetworkSimulatorWrapper-Thread" + ( ++threadCounter ) );
      AddressBean addressBean = getActiveAddressBean( );
      /**
       * Reading the initialization paramteters from the XML Configurations
       * provided.
       */
      if ( addressBean.getPredicateValue( NODE_COUNT_KEY ) != null ) {
         node_count = ParamParser.getInteger( ( String ) addressBean.getPredicateValue( NODE_COUNT_KEY ) , NODE_COUNT_DEFAULT_VALUE );
         if ( node_count <= 0 ) {
            logger.warn( "The specified >node_count< parameter for the >WSNWrapper< shouldn't be a negative number.\nGSN uses the default node_count (" + NODE_COUNT_DEFAULT_VALUE + ")." );
            node_count = NODE_COUNT_DEFAULT_VALUE;
         }
      }
      
      if ( addressBean.getPredicateValue( STEP_COUNTER ) != null ) {
         step_counter = ParamParser.getInteger( ( String ) addressBean.getPredicateValue( STEP_COUNTER ) , STEP_COUNTER_DEFAULT_VALUE );
         if ( step_counter <= 0 ) {
            logger.warn( "The specified >step_counter< parameter for the >WSNWrapper< shouldn't be a negative number.\nGSN disables the step_counter (-1)." );
            step_counter = -1;
         }
      }
      
      if ( addressBean.getPredicateValue( NODE_COUNT_KEY ) != null ) node_count = ParamParser.getInteger( ( String ) addressBean.getPredicateValue( NODE_COUNT_KEY ) , NODE_COUNT_DEFAULT_VALUE );
      
      if ( addressBean.getPredicateValue( RATE_KEY ) != null ) {
         rate = ParamParser.getInteger( ( String ) addressBean.getPredicateValue( RATE_KEY ) , RATE_DEFAULT_VALUE );
         if ( rate <= 0 ) {
            logger.warn( "The specified rate parameter for the >WSNWrapper< shouldn't be a negative number.\nGSN uses the default rate (" + RATE_DEFAULT_VALUE + ")." );
            rate = RATE_DEFAULT_VALUE;
         }
      }
      return true;
   }
   
   public void run ( ) {
      nodes = initializeNodes( node_count );
      for ( int i = 0 ; i < node_count ; i++ )
         nodes[ i ].addDataListener( this );
      
      long tempStepCounter = 0;
      while ( isActive( ) ) {
         if ( tempStepCounter <= step_counter || step_counter == -1 ) {
            tempStepCounter++;
            if ( !listeners.isEmpty( ) && dataBuffer.size( ) > 0 ) {
               DataPacket dataPacket;
               synchronized ( dataBuffer ) {
                  dataPacket = dataBuffer.remove( 0 );
               }
               StreamElement streamElement = new StreamElement( new String [ ] { "NODE_ID" , "PARENT_ID" , "TEMPREATURE" } , new Byte [ ] { DataTypes.INTEGER , DataTypes.INTEGER ,
                     DataTypes.INTEGER } , new Serializable [ ] { dataPacket.getIdentifier( ) , dataPacket.getParent( ) , dataPacket.getValue( ) } , System.currentTimeMillis( ) );
               postStreamElement( streamElement );
               if ( dataBuffer.size( ) > 0 ) continue;
            }
         }
         try {
            Thread.sleep( rate );
            
         } catch ( InterruptedException e ) {
            logger.error( e.getMessage( ) , e );
         }
         
      }
      for ( WirelessNode node : nodes )
         node.stopNode( );
   }
   
   private static  final DataField[] dataField  = new DataField[] {new DataField( "NODE_ID" , DataTypes.INTEGER_NAME , "Node's identification." ) ,
      new DataField( "PARENT_ID" , DataTypes.INTEGER_NAME , "Parent Node's identification." ) ,
      new DataField( "TEMPREATURE" , DataTypes.INTEGER_NAME , "incremental int" )};
     
      public DataField [] getOutputFormat ( ) {
      return dataField;
   }
   
   public static int randomNumber ( int fromNo , int toNo ) {
      return ( int ) ( ( Math.random( ) * ( toNo - fromNo + 1 ) ) + fromNo );
   }
   
   public WirelessNode [ ] initializeNodes ( int nodeCount ) throws RuntimeException {
      if ( nodeCount <= 0 ) throw new RuntimeException( "Wireless Sensor Network Simulator (WSNS) can't create a network with zero or negative number of nodes : " + nodeCount );
      WirelessNode [ ] nodes = new WirelessNode [ nodeCount ];
      for ( int i = 0 ; i < nodeCount ; i++ ) {
         nodes[ i ] = new WirelessNode( i );
         nodes[ i ].setName( "WSNS-Node-" + i );
      }
      for ( int i = 1 ; i < nodeCount ; i++ )
         nodes[ i ].setParent( nodes[ randomNumber( i - 1 , 0 ) ] );
      for ( int i = 1 ; i < nodeCount ; i++ )
         nodes[ i ].start( );
      return nodes;
   }
   
   public void newDataAvailable ( DataPacket dataPacket ) {
      synchronized ( dataBuffer ) {
         dataBuffer.add( dataPacket );
      }
   }
   
   public void dispose ( ) {
      
   }

public String getWrapperName() {
    return "Wireless Sensor Network Simulator";
}
}
