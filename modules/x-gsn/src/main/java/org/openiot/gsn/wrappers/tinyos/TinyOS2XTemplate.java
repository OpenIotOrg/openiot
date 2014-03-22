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
*/

package org.openiot.gsn.wrappers.tinyos;

import net.tinyos.message.Message;
import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.packet.PhoenixSource;
/**
 * A simple test class to see if the tinyos connection is working fine by simply asking for some data from the serial forwarder. 

 *
 */
public class TinyOS2XTemplate {
   /**
    * A Test class to check if the wrapper works.
    * @param args
    * @throws Exception
    */
   public static void main ( String [ ] args ) throws Exception {
      PhoenixSource reader = BuildSource.makePhoenix( BuildSource.makeSF( "eflumpc24.epfl.ch" , 2020 ), null );
      reader.start( );
      MoteIF moteif = new MoteIF( reader );
      moteif.registerListener( new SensorScopeDataMsg( ) , new MessageListener( ) {
         public void messageReceived ( int dest , Message rawMsg ) {
            System.out.println( "Received." );
         }
      } );
   }
}
