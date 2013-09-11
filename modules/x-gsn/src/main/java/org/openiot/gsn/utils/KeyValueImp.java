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

package org.openiot.gsn.utils;

import java.io.Serializable;

import org.apache.commons.collections.KeyValue;
import org.apache.log4j.Logger;

/**
 * The <I> Predicate </I> class represents mapping between a key and a value.
 * Two predicates are the same if they have the same name and value. This class
 * is used for Abstract Addressing and Options.
 */
public class KeyValueImp implements KeyValue , Serializable {
   
	private static final long serialVersionUID = 5739537343169906104L;

	private transient final Logger logger = Logger.getLogger( KeyValueImp.class );
   
   private String                 key;
   
   private String                 value;
   
   public KeyValueImp ( ) {

   }
   
   public KeyValueImp ( String key , String value ) {
      this.key = key;
      this.value = value;
   }
   
   public void setKey ( String key ) {
      this.key = key;
   }
   
   public void setValue ( String value ) {
      this.value = value;
   }
   
   public boolean equals ( Object obj ) {
      if ( obj == null || !( obj instanceof KeyValueImp ) ) { return false; }
      KeyValueImp input = ( KeyValueImp ) obj;
      return input.getKey( ).equalsIgnoreCase( getKey( ) ) && input.getValue( ).equalsIgnoreCase( getValue( ) );
   }
   
   /**
    * Converts the value inside the predicate object to boolean or returns
    * false.
    * 
    * @return The boolean representation of the value or false if the conversion
    * fails.
    */
   public boolean valueInBoolean ( ) {
      boolean result = false;
      try {
         result = Boolean.parseBoolean( getValue( ) );
      } catch ( Exception e ) {
         logger.error( e.getMessage( ) , e );
      }
      return result;
   }
   
   /**
    * Converts the value inside the predicate object to integer or returns 0.
    * 
    * @return The integer representation of the value or 0 plus contents of
    * stack trace if the conversion fails.
    */
   public int valueInInteger ( ) {
      int result = 0;
      try {
         result = Integer.parseInt( getValue( ) );
      } catch ( Exception e ) {
         logger.error( e.getMessage( ) , e );
      }
      return result;
   }
   
   public int hashCode ( ) {
      return toString( ).hashCode( );
   }
   
   public String toString ( ) {
      StringBuffer result = new StringBuffer( );
      result.append( "Predicate ( Key = " ).append( key ).append( ", Value = " ).append( value ).append( " )\n" );
      return result.toString( );
   }
   
   public String getKey ( ) {
      return this.key;
   }
   
   public String getValue ( ) {
      return this.value;
   }
   
}
