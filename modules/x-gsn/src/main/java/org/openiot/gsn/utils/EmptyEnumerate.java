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

package org.openiot.gsn.utils;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EmptyEnumerate < T > implements Enumeration {
   
   public boolean hasMoreElements ( ) {
      return false;
   }
   
   public Object nextElement ( ) throws NoSuchElementException {
      return new NoSuchElementException( "This is an Empty Enumerator" );
   }
   
}
