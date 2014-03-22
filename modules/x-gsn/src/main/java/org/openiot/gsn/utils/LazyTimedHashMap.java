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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LazyTimedHashMap {
   
   private int                     lifeTimeOfEachElement;
   
   private Map < Object , Long >   keyToTimeMapping  = new ConcurrentHashMap( );
   
   private Map < Object , Object > keyToValueMapping = new ConcurrentHashMap( );
   
   private List < ChangeListener > changeListeners   = new ArrayList < ChangeListener >( );
   
   /**
    * This class is thread-safe .
    * 
    * @param lifeTimeOfEachElementInMilliSeconds This value is a positive
    * integer representing the maximum time an element is valid in the hashmap.
    * The value is in milliseconds.
    */
   public LazyTimedHashMap ( int lifeTimeOfEachElementInMilliSeconds ) {
      this.lifeTimeOfEachElement = lifeTimeOfEachElementInMilliSeconds;
   }
   
   public void put ( Object key , Object value ) {
      keyToTimeMapping.put( key , System.currentTimeMillis( ) );
      if ( !keyToValueMapping.containsKey( key ) ) {
         keyToValueMapping.put( key , value );
         fireChange( ITEM_ADDED , key , value );
      }
      
   }
   
   /**
    * If the element doesn't Exist returns null.
    * 
    * @param key
    * @return The element or Null if it doesn't exist or outdated.
    */
   
   public Object get ( Object key ) {
      Long insertionTime = keyToTimeMapping.get( key );
      if ( insertionTime == null ) return null;
      if ( System.currentTimeMillis( ) - insertionTime > lifeTimeOfEachElement ) {
         remove( key );
         return null;
      }
      return keyToValueMapping.get( key );
   }
   
   public Object remove ( Object key ) {
      keyToTimeMapping.remove( key );
      Object value = keyToValueMapping.remove( key );
      fireChange( ITEM_REMOVED , key , value );
      return value;
   }
   
   public ArrayList getKeys ( ) {
      ArrayList arrayList = new ArrayList( );
      Iterator it = keyToValueMapping.keySet( ).iterator( );
      while ( it.hasNext( ) ) {
         Object key = it.next( );
         Object value = keyToValueMapping.get( key );
         if ( value != null ) arrayList.add( key );
      }
      return arrayList;
   }
   
   public ArrayList getValues ( ) {
      ArrayList arrayList = new ArrayList( );
      Iterator it = keyToValueMapping.keySet( ).iterator( );
      while ( it.hasNext( ) ) {
         Object key = it.next( );
         Object value = keyToValueMapping.get( key );
         if ( value != null ) arrayList.add( value );
      }
      return arrayList;
   }
   
   public void addChangeListener ( ChangeListener cl ) {
      changeListeners.add( cl );
   }
   
   public void removeChangeListener ( ChangeListener cl ) {
      changeListeners.remove( cl );
   }
   
   public void fireChange ( String changeAction , Object changedKey , Object changedValue ) {
      for ( ChangeListener cl : changeListeners )
         cl.changeHappended( changeAction , changedKey , changedValue );
   }
   
   public static final String ITEM_REMOVED = "REMOVED";
   
   public static final String ITEM_ADDED   = "ADDED";
   
   public void update ( ) {
      Iterator it = keyToValueMapping.keySet( ).iterator( );
      while ( it.hasNext( ) )
         get( it.next( ) );
   }
}
