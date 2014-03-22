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

public class ParamParser {
   
   public static int getInteger ( String input , int defaultValue ) {
      if ( input == null ) return defaultValue;
      try {
         return Integer.parseInt( input );
      } catch ( Exception e ) {
         return defaultValue;
      }
   }
   
   public static int getInteger ( Object input , int defaultValue ) {
      if ( input == null ) return defaultValue;
      try {
         if ( input instanceof String ) return getInteger( ( String ) input , defaultValue );
         if ( input instanceof Number ) return ( ( Number ) input ).intValue( );
         else
            return Integer.parseInt( input.toString( ) );
      } catch ( Exception e ) {
         return defaultValue;
      }
   }
}
