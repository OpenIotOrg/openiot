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

package org.openiot.gsn.wrappers.ieee1451;

/**
 * An aggregation or collection of related measurements. Measurements are stored
 * along with a name. This name is used to retrieve the Measurement instance.
 * <p>
 */
public class Record extends ArgArray {
   
   /**
    * Clone the given Record instance.
    * 
    * @param value
    */
   public Record ( Record value ) {
      super( );
      value.cloneContentsTo( this );
   }
   
   /**
    * Clone the given ArgArray instance
    * 
    * @param value
    */
   public Record ( ArgArray value ) {
      super( );
      value.cloneContentsTo( this );
   }
   
   /**
    * Creates a blank record with no comments.
    */
   public Record ( ) {

   }
}
