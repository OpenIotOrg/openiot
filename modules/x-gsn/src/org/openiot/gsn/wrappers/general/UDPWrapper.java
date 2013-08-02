package org.openiot.gsn.wrappers.general;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

/**
 * Links GSN to a Wisenet sensors network. The computer running this wrapper
 * should be connected to an IP network. One of the WSN nodes should forward
 * received packets through UDP to the host running this wrapper.
 */
public class UDPWrapper extends AbstractWrapper {
   
   private static final String    RAW_PACKET    = "RAW_PACKET";
   
   private final transient Logger logger        = Logger.getLogger( UDPWrapper.class );
   
   private int                    threadCounter = 0;
   
   public InputStream             is;
   
   private AddressBean            addressBean;
   
   private int                    port;
   
   private DatagramSocket         socket;
   
   /*
    * Needs the following information from XML file : port : the udp port it
    * should be listening to rate : time to sleep between each packet
    */
   public boolean initialize (  ) {
      addressBean = getActiveAddressBean( );
      try {
         port = Integer.parseInt( addressBean.getPredicateValue( "port" ) );
         socket = new DatagramSocket( port );
      } catch ( Exception e ) {
         logger.warn( e.getMessage( ) , e );
         return false;
      }
      setName( "UDPWrapper-Thread" + ( ++threadCounter ) );
      return true;
   }
   
   public void run ( ) {
      byte [ ] receivedData = new byte [ 50 ];
      DatagramPacket receivedPacket = null;
      while ( isActive( ) ) {
         try {
            receivedPacket = new DatagramPacket( receivedData , receivedData.length );
            socket.receive( receivedPacket );
            String dataRead = new String( receivedPacket.getData( ) );
            if ( logger.isDebugEnabled( ) ) logger.debug( "UDPWrapper received a packet : " + dataRead );
            StreamElement streamElement = new StreamElement( new String [ ] { RAW_PACKET } , new Byte [ ] { DataTypes.BINARY } , new Serializable [ ] { receivedPacket.getData( ) } , System
                  .currentTimeMillis( ) );
            postStreamElement( streamElement );
         } catch ( IOException e ) {
            logger.warn( "Error while receiving data on UDP socket : " + e.getMessage( ) );
         }
      }
   }
   
   public  DataField [] getOutputFormat ( ) {
      return new DataField[] {new DataField( RAW_PACKET , "BINARY" , "The packet contains raw data received as a UDP packet." ) };
     
   }
   
   public void dispose (  ) {
      threadCounter--;
   }
   public String getWrapperName() {
    return "network udp";
}
   public static void main ( String [ ] args ) {
   // To check if the wrapper works properly.
   // this method is not going to be used by the system.
   }
}
