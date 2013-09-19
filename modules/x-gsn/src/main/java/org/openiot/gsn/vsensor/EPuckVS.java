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

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.utils.protocols.ProtocolManager;
import org.openiot.gsn.utils.protocols.EPuck.SerComProtocol;
import org.openiot.gsn.wrappers.AbstractWrapper;
import org.openiot.gsn.wrappers.general.SerialWrapper;

import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;

public class EPuckVS extends AbstractVirtualSensor {
   
   private static final transient Logger logger = Logger.getLogger( EPuckVS.class );
   
   private TreeMap < String , String >   params;
   
   private ProtocolManager               protocolManager;
   
   private AbstractWrapper                       wrapper;
   
   private VSensorConfig                 vsensor;
   
   public boolean initialize ( ) {
      params = getVirtualSensorConfiguration( ).getMainClassInitialParams( );
      wrapper = getVirtualSensorConfiguration( ).getInputStream( "input1" ).getSource( "source1" ).getWrapper( );
      protocolManager = new ProtocolManager( new SerComProtocol( ) , wrapper );
      if ( logger.isDebugEnabled( ) ) logger.debug( "Created protocolManager" );
      try {
         wrapper.sendToWrapper( "h\n",null,null );
      } catch ( OperationNotSupportedException e ) {
         e.printStackTrace( );
      }
      // protocolManager.sendQuery( SerComProtocol.RESET , null );
      if ( logger.isDebugEnabled( ) ) logger.debug( "Initialization complete." );
      return true;
   }
   
   boolean actionA = false;
   
   public void dataAvailable ( String inputStreamName , StreamElement data ) {
      if ( logger.isDebugEnabled( ) ) logger.debug( "I just received some data from the robot" );
      System.out.println( new String( ( byte [ ] ) data.getData( SerialWrapper.RAW_PACKET ) ) );
      AbstractWrapper wrapper = vsensor.getInputStream( "input1" ).getSource( "source1" ).getWrapper( );
      if ( actionA == false ) {
         actionA = true;
         try {
            // wrapper.sendToWrapper( "h\n" );
            wrapper.sendToWrapper( "d,1000,-1000\n",null,null );
         } catch ( OperationNotSupportedException e ) {
            logger.error( e.getMessage( ) , e );
         }
      }
   }
   
   public void dispose ( ) {
      try {
         vsensor.getInputStream( "input1" ).getSource( "source1" ).getWrapper().sendToWrapper( "R\n",null,null );
      } catch ( OperationNotSupportedException e ) {
         logger.error( e.getMessage( ) , e );
      }
   }
   
}
