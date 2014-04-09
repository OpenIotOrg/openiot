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

package org.openiot.gsn.wrappers.general;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.wrappers.AbstractWrapper;

import org.apache.log4j.Logger;

public class EmptyWrapper extends AbstractWrapper {
   
   private final transient Logger               logger        = Logger.getLogger( EmptyWrapper.class );
   
   private int                                  threadCounter = 0;
   
   private static   DataField [] dataField  ;
   
   public boolean initialize (  ) {
      setName( "EmptyWrapper-Thread" + ( ++threadCounter ) );
      AddressBean addressBean = getActiveAddressBean( );
      dataField = new DataField[] { new DataField( "DATA" , "int" , "incremental int" ) };
      return true;
   }
   
   public void run ( ) {
      while ( isActive( ) ) {
    	  // do something
      }
   }
   
   public  DataField[] getOutputFormat ( ) {
      return dataField;
   }
   public String getWrapperName() {
    return "empty template";
}
   public void dispose ( ) {
      threadCounter--;
   }
   
}
