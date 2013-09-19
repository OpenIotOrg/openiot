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

package org.openiot.gsn;

public class VirtualSensorInitializationFailedException extends Exception {
   
   public VirtualSensorInitializationFailedException ( ) {
      super( );
   }
   
   public VirtualSensorInitializationFailedException ( String message ) {
      super( message );
   }
   
   public VirtualSensorInitializationFailedException ( String message , Throwable cause ) {
      super( message , cause );
   }
   
   public VirtualSensorInitializationFailedException ( Throwable cause ) {
      super( cause );
   }
   
}
