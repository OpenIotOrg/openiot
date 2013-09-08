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

package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.utils.protocols.ProtocolManager;
import org.openiot.gsn.utils.protocols.EPuck.SerComProtocol;
import org.openiot.gsn.wrappers.AbstractWrapper;
import org.openiot.gsn.wrappers.general.SerialWrapper;

import java.io.Serializable;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;


/**
 * Virtual sensor to support GPS coord given by NMEA specification over serial
 * Only the $GPRMC values are required.
 * (works as well on bluetooth GPS mapped to serial)
 */
public class GPSNMEAVS extends AbstractVirtualSensor {
   
   private static final transient Logger logger = Logger.getLogger( GPSNMEAVS.class );
   
   private TreeMap < String , String >   params;
   
   private ProtocolManager               protocolManager;
   
   private AbstractWrapper                       wrapper;
   
   private VSensorConfig                 vsensor;
   
   private static final String [ ] fieldNames = new String [ ] { "latitude" , "longitude" };
   
   private static final Byte [ ] fieldTypes = new Byte [ ] { DataTypes.DOUBLE, DataTypes.DOUBLE};
   
   private Serializable [ ] outputData = new Serializable [ fieldNames.length ];
   
   public boolean initialize ( ) {
      vsensor = getVirtualSensorConfiguration( );
      params = vsensor.getMainClassInitialParams( );
      wrapper = vsensor.getInputStream( "input1" ).getSource( "source1" ).getWrapper( );
      protocolManager = new ProtocolManager( new SerComProtocol( ) , wrapper );
      if ( logger.isDebugEnabled( ) ) logger.debug( "Created protocolManager" );
      try {
         wrapper.sendToWrapper( "h\n" ,null,null);
      } catch ( OperationNotSupportedException e ) {
         e.printStackTrace( );
      }      
      
      // protocolManager.sendQuery( SerComProtocol.RESET , null );
      if ( logger.isDebugEnabled( ) ) logger.debug( "Initialization complete." );
      return true;
   }
   
   public void dataAvailable ( String inputStreamName , StreamElement data ) {
      if ( logger.isDebugEnabled( ) ) logger.debug( "SERIAL RAW DATA :"+new String((byte[])data.getData(SerialWrapper.RAW_PACKET)));
      
      //needed? ######
      AbstractWrapper wrapper = vsensor.getInputStream( "input1" ).getSource( "source1" ).getWrapper( );
      
      //raw data from serial
      String s = new String( ( byte [ ] ) data.getData( SerialWrapper.RAW_PACKET ) );
      String [ ] line = s.split( "\n" );
      //iterate on every line
      for ( int i = 0 ; i < line.length ; i++ ) {
         String [ ] part = line[ i ].split( "," );
         //Only the $GPRMC line are analyed for GPS coord
         //Using $GPGGA might be better but wouldn't give any result when no sat are tracked
         if ( part[ 0 ].equals( "$GPRMC" ) ) {
            //converting latitude from DDMM.MMMM to decimal notation
        	Double d = Double.valueOf( part[ 3 ] );
        	Double lat = d / 100.0;
            lat = Math.floor( lat );
            lat += Double.valueOf( d % 100.0 ) / 60.0;
            if ( part[ 4 ].equals( "S" ) )
               lat = -lat; // south coord
            else if ( !part[ 4 ].equals( "N" ) ) 
            	continue; // neither south or north: invalid format -> skip 
            
            //converting longitude
            d = Double.valueOf( part[ 5 ] );
            Double lon = Math.floor( d / 100.0 );
            lon += Double.valueOf( d % 100.0 ) / 60.0;
            if ( part[ 6 ].equals( "W" ) ) 
            	lon = -lon; // west coord
            
            logger.debug( "latitude:" + lat + " longitude:" + lon );
            
            //send back the data
            outputData[ 0 ] = lat;
            outputData[ 1 ] = lon;
            StreamElement output = new StreamElement( fieldNames , fieldTypes , outputData , System.currentTimeMillis( ) );
            dataProduced( output );
            break;//one $GPRMC line is enough
         }
      }
      
   }
   
   public void dispose ( ) {
   }
   
}
