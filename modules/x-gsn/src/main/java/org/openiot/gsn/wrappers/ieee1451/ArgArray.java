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

/*
 * 
 * $RCSfile: ArgArray.java $	
 *
 * Copyright (c) 2003, 2004, 2005, Agilent Technologies, Inc. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 *    -	Redistributions of source code must retain the above 
 *      copyright notice, this list of conditions and the following 
 *      disclaimer. 
 *    -	Redistributions in binary form must reproduce the above 
 *      copyright notice, this list of conditions and the following 
 *      disclaimer in the documentation and/or other materials provided 
 *      with the distribution. 
 *    -	Neither the name of Agilent Technologies, Inc. nor the names 
 *      of its contributors may be used to endorse or promote products 
 *      derived from this software without specific prior written 
 *      permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.openiot.gsn.wrappers.ieee1451;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The <code>ArgArray</code> class is the fundamental building block in JDDAC
 * for passing information between objects. It is designed to be a simple
 * container class compatible with both J2ME and J2SE. In one sense it can be
 * viewed as simply a <code>Hashtable>/code>
 * with nameed objects for contents.  However, a number of get/set members are provided to 
 * simplify access to the internals of the <code>ArgArray</code> contents.  Some of the 
 * features of the <code>ArgArray</code> class are:
 * <ul>
 * <li>Typesafe get methods.
 * <li>Fundamental type put methods (int, long, etc).
 * <li>Ability to specify default values if a get would otherwise return null.
 * <li>Ability to do basic units conversion on get methods.
 * <li>Ability to lock contents from being changed accidently.
 * <li>Ability to determine if one <code>ArgArray</code> is a subset of another ArgArray.
 * <li>Ability to merge one <code>ArgArray</code> into another.
 * <li>Ability to provide a 'backing store' of default <code>ArgArray</code> contents.
 * <li>Ability to convert easily to <code>String</code> for debugging.
 * </ul>
 *  
 * To successfully store and retrieve objects from an <code>ArgArray</code>, the objects used as keys must implement the hashCode method and the equals method.
 */
public class ArgArray {
   
   /**
    * Insert Vector contents to existing vectors when adding.
    */
   public static final int  VECTOR_MODE_INSERT = 0;
   
   /**
    * Append Vector contents to existing vectors when adding.
    */
   public static final int  VECTOR_MODE_APPEND = 1;
   
   /**
    * Append Vector contents to existing vectors when adding.
    */
   public static final int  MODE_REPLACE       = 2;
   
   /**
    * Append Vector contents to existing vectors when adding.
    */
   public static final int  VECTOR_MODE_ERROR  = 3;
   
   protected Hashtable      contents;
   
   protected ArgArray       defaults           = null;
   
   protected boolean        locked             = false;
   
   private static String    lockMessage        = "Container locked for change";
   
   private static Hashtable argConverters      = new Hashtable( );
   
   /**
    * An interface to allow conversion of complex types to and from an ArgArray.
    * This interface is most often used when serializing as it allows complex
    * objects to be converted into a 'generic' form. ArgArrays are composed of
    * other ArgArrays, Vectors, and individual types called collectively Args.
    * Classes that are not part of the core JDDAC classes that wish to be
    * serialized need to implement this interface and register with the ArgArray
    * class before any deserialization is needed. Implementations can choose to
    * encode either as another fundamental type or they can choose to encode as
    * an ArgArray composed of fundamental types. The <code>toArgValue</code>
    * and <code>fromArgValue</code> method is used to convert to and from
    * fundamental Arg types such as long, int, long[], String etc. When
    * serializing, an attempt is made to cast unrecognized types to ArgConverter
    * or to obtain a converter using the ArgArray
    * {@link ArgArray#findArgConverter(Object)} method. before throwing an
    * Exception.
    */
   public abstract interface ArgConverter {
      
      /**
       * Convert a complex object into an ArgArray of more fundamental types.
       * 
       * @param obj The object to convert.
       * @return an <code>ArgArray</code> representing the object.
       */
      ArgArray toArgArray ( Object obj );
      
      /**
       * Convert a complex object into a byte array. If the object is not easily
       * converted into a binary form then the function should return null.
       * Binary encoders such as WBXML use this method for unrecognized types.
       * 
       * @param obj The object to convert.
       * @return an <code>Object</code> representing the object.
       */
      byte [ ] toByteArray ( Object obj );
      
      /**
       * Convert an <code>ArgArray</code> into an object.
       * 
       * @param argArray the <code>ArgArray</code> to convert.
       * @return a newly created object.
       */
      Object fromArgArray ( ArgArray argArray );
      
      /**
       * Convert an object encoded as a byte array into a more complex object.
       * 
       * @param obj the object to convert.
       * @return a newly created object.
       */
      Object fromByteArray ( byte [ ] obj );
      
      /**
       * Return the preferred type name for this converter. This is usually used
       * when serializing a type as well as looking up a converter when reading
       * a type.
       * 
       * @return preferred type name for this converter
       */
      String getArgTypeName ( );
   }
   
   /**
    * The <code>UnitsConverter</code> interface provides a mechanism to attach
    * advanced unit conversion capabilities to certain get methods. The
    * conversion primarily assists with scaled integer representations commonly
    * used with J2ME implementations without floating point capability. For
    * example, a J2ME implementation may want to keep track of a distance as
    * scaled integer with a resolution of thousands of a meter (mm) but it may
    * be specified by the user or another source as Km or m. This class
    * facilitates the conversion between scaled integer representations. One
    * form of the <code>convertUnits</code> member function allows the current
    * value to be specified as a floating point string.
    */
   public interface UnitsConverter {
      
      /**
       * Convert a long value and convert it to a new unit.
       * 
       * @param value - the value to convert to new units.
       * @param fromUnits - the unit representing the current value.
       * @param toUnits - the desired result unit of the returned value.
       * @return - the new value embodied as <code>toUnits</code>.
       */
      long convertUnits ( long value , Object fromUnits , Object toUnits );
      
      /**
       * Given a string form of a number (possibly in floating point), convert
       * it to be a long value in the destired units. This is useful in a J2ME
       * CLDC1.0 environment to convert something like "3.141" KVolts into a
       * long mVolts (3141000) scaled integer representation.
       * 
       * @param value - A string representing the value. Can include floating
       * point.
       * @param fromUnits - the unit representing the current string value.
       * @param toUnits - the desired result unit of the return value.
       */
      long convertUnits ( String value , Object fromUnits , Object toUnits );
   }
   
   static UnitsConverter convertHelper;
   
   Hashtable             combined = null;
   
   /**
    * The DefaultUnitsConverter class provides a simple minded implementation of
    * units conversion suitable for casual use of units in a J2ME environment.
    * It supports basic engineering prefixes and expects the unit to be
    * convertable to a string form where the prefixes can be readily examined.
    */
   private class DefaultUnitsConverter implements UnitsConverter {
      
      final long scaleFactor[] = { 1 , 10 , 100 , 1000 , 10000 , 100000 , 1000000 , 10000000 , 100000000 , 1000000000 , 10000000000L , 100000000000L , 1000000000000L , 10000000000000L ,
                                     100000000000000L , 1000000000000000L , 10000000000000000L , 100000000000000000L , 1000000000000000000L , // 10^18
                               };
      
      private int getSIUnitExponent ( Object unitsRep ) {
         int exponent = 0;
         String units;
         if ( unitsRep == null ) return 0;
         if ( unitsRep instanceof String ) units = ( String ) unitsRep;
         else
            units = unitsRep.toString( );
         if ( units.length( ) > 1 ) {
            // Assume simple units that use engineering prefixes
            char prefix = units.charAt( 0 );
            if ( prefix == 'm' ) exponent = -3;
            else if ( prefix == 'u' ) exponent = -6;
            else if ( prefix == 'n' ) exponent = -9;
            else if ( prefix == 'u' ) exponent = -12;
            else if ( prefix == 'k' ) exponent = 3;
            else if ( prefix == 'K' ) exponent = 3;
            else if ( prefix == 'M' ) exponent = 6;
            else if ( prefix == 'G' ) exponent = 9;
            else if ( prefix == 'T' ) exponent = 12;
            // less frequently used
            else if ( prefix == 'f' ) exponent = -15;
            else if ( prefix == 'a' ) exponent = -18;
            else if ( prefix == 'z' ) exponent = -21;
            else if ( prefix == 'y' ) exponent = -24;
            else if ( prefix == 'P' ) exponent = 15;
            else if ( prefix == 'E' ) exponent = 18;
            else if ( prefix == 'Z' ) exponent = 21;
            else if ( prefix == 'Y' ) exponent = 24;
         }
         return exponent;
         
      }
      
      public long convertUnits ( long value , Object fromUnits , Object toUnits ) {
         
         int fromExponent = getSIUnitExponent( fromUnits );
         int toExponent = getSIUnitExponent( toUnits );
         int adjustExponent = toExponent - fromExponent;
         if ( adjustExponent == 0 ) return value;
         
         if ( Math.abs( adjustExponent ) < 18 ) {
            long scale = scaleFactor[ Math.abs( adjustExponent ) ];
            if ( adjustExponent < 0 ) value = value * scale;
            else
               value = value / scale;
         } else {
            if ( adjustExponent > 0 ) value = 0;
            else if ( value < 0 ) value = -Long.MAX_VALUE;
            else
               value = Long.MAX_VALUE;
         }
         return value;
      }
      
      public long convertUnits ( String s , Object fromUnits , Object toUnits ) {
         long lvalue = 0;
         // check for units
         for ( int i = 0 ; i < s.length( ) ; i++ ) {
            char ch = s.charAt( i );
            if ( ch == '.' || ch == ',' || ch == '-' || ch == '+' ) continue;
            if ( ch >= '0' && ch <= '9' ) continue;
            if ( fromUnits == null ) fromUnits = s.substring( i ).trim( );
            s = s.substring( 0 , i );
         }
         
         int fromExponent = getSIUnitExponent( fromUnits );
         int toExponent = getSIUnitExponent( toUnits );
         int adjustExponent = toExponent - fromExponent;
         if ( adjustExponent == 0 ) return Long.parseLong( s );
         
         // check for a digit scaling
         if ( adjustExponent != 0 ) {
            int decimalPt = s.indexOf( "." );
            if ( decimalPt < 0 ) decimalPt = s.indexOf( "," );
            if ( decimalPt < 0 ) decimalPt = s.length( );
            
            // create a string buffer and delete the decimal point if
            // present
            StringBuffer sbuf = new StringBuffer( s );
            if ( decimalPt < sbuf.length( ) ) sbuf.deleteCharAt( decimalPt );
            
            if ( adjustExponent < 0 ) {
               while ( sbuf.length( ) < ( decimalPt + ( -adjustExponent ) ) )
                  sbuf.append( '0' );
               sbuf.setLength( decimalPt + ( -adjustExponent ) );
            }
            sbuf.setLength( decimalPt + ( -adjustExponent ) );
            s = sbuf.toString( );
         }
         lvalue = Long.parseLong( s );
         return lvalue;
      }
   }
   
   /**
    * Initialize the internals of an ArgArray. This is common code for all the
    * constructors.
    */
   private void initArgArrayInternals ( ) {
      synchronized ( lockMessage ) {
         if ( convertHelper == null ) convertHelper = new DefaultUnitsConverter( );
      }
      contents = new Hashtable( );
   }
   
   /**
    * Create an empty instance of <code>ArgArray</code>.
    */
   public ArgArray ( ) {
      initArgArrayInternals( );
   }
   
   /**
    * Create a new instance of <code>ArgArray</code> and clone the contents of
    * the specified argument instance into the newly created
    * <code>ArgArray</code>.
    * 
    * @param source - the source <code>ArgArray</code> to clone from.
    */
   public ArgArray ( ArgArray source ) {
      initArgArrayInternals( );
      source.cloneContentsTo( this );
   }
   
   /**
    * Create an <code>ArgArray</code> from an array of key/value pairs.
    * Example code:
    * <code>ArgArray args=createFromKeyValuePairs(new String[][]{"1","abc"}, {"2","efg"}});</code>
    * 
    * @param strings The array containing the key/value pairs.
    */
   public ArgArray ( Object [ ][ ] strings ) {
      initArgArrayInternals( );
      for ( int i = 0 ; i < strings.length ; i++ ) {
         put( strings[ i ][ 0 ] , strings[ i ][ 1 ] );
      }
   }
   
   /**
    * Specify a default units conversion engine to assist with extracting values
    * from ArgArrays. The converter must implement the
    * <code>ArgArray.UnitsConverter</code> interface. A single global
    * converter is used for all ArgArray instances so setting this value will
    * globally affect conversions. By default a basic converter is provided
    * internally to the <code>ArgArray</code> implementation.
    * 
    * @param converter - the converter.
    */
   public void setUnitsConverter ( UnitsConverter converter ) {
      if ( converter != null ) convertHelper = converter;
   }
   
   /**
    * Clone the contents of the <code>dest</code> argument into the existing
    * <code>ArgArray</code>. The existing contents are not removed before the
    * clone is performed so this essentially acts as an add and replace
    * operation. Note that the first level object references are copied so this
    * is not a deep clone operation. To do a deep clone you can use the
    * {@link  ArgArray#deepAdd(ArgArray) method} on an empty
    * <code>ArgArray</code>.
    * 
    * @param dest - the <code>ArgArray</code> to fill with new contents.
    */
   public void cloneContentsTo ( ArgArray dest ) {
      dest.defaults = this.defaults;
      // J2ME does not have clone() on Hasttable so do it ourselves
      for ( Enumeration e = this.contents.keys( ) ; e.hasMoreElements( ) ; ) {
         Object key = e.nextElement( );
         dest.put( key , this.contents.get( key ) );
      }
   }
   
   /**
    * Return a <code>Hashtable</code> instance representing the internal
    * contents. This value is useful for iterating over the contents. Note that
    * the <code>Hashtable</code> returned is not the one used internally by
    * the <code>ArgArray</code> internals.
    * 
    * @return - the internal contents as a <code>Hashtable</code>
    */
   public Hashtable getHashtable ( ) {
      return getCombined( );
   }
   
   /**
    * Obtain a reference to the internal default <code>ArgArray</code>
    * settings.
    * 
    * @return - the internal default <code>ArgArray</code>.
    */
   public ArgArray getDefault ( ) {
      return defaults;
   }
   
   /**
    * Set the default 'backing store' for the <code>ArgArray</code>. Requests
    * for values not found by user set operations are returned from the default
    * set. null is returned if the request cannot be satisfied by either
    * mechanism. Note that since the default settings is itself an
    * <code>ArgArray</code> the retrieval of the value is can be recursive.
    * That is, the default <code>ArgArray</code> can itself have it's own
    * default <code>ArgArray</code>.
    * 
    * @param def - the <code>ArgArray</code> to use for the default settings.
    */
   public void setDefault ( ArgArray def ) {
      if ( locked ) throw new ArrayStoreException( lockMessage );
      if ( def == this ) throw new IllegalArgumentException( "ArgArray default set to self" );
      this.defaults = def;
      combined = null;
   }
   
   /**
    * Clears this <code>ArgArray</code> so it contains no values or defaults.
    */
   public synchronized void clear ( ) {
      if ( locked ) throw new ArrayStoreException( lockMessage );
      contents.clear( );
      defaults = null;
      combined = null;
   }
   
   /**
    * Prevent updates to the contents. Attempts to modify a locked
    * <code>ArgArray</code> will result in an <code>ArrayStoreException</code>
    * being thrown.
    */
   public synchronized void lock ( ) {
      locked = true;
   }
   
   /**
    * Allow updates to the contents if previously locked.
    */
   public synchronized void unlock ( ) {
      locked = false;
   }
   
   /**
    * Returns the current lock state of the <code>ArgArray</code>.
    * 
    * @return - the current lock state.
    */
   public boolean locked ( ) {
      return this.locked;
   }
   
   /**
    * Tests if some key maps into the specified value in this hashtable.
    * 
    * @param key - possible key.
    * @return true if some key maps to the value argument in this instance;
    * false otherwise.
    */
   public synchronized boolean contains ( Object key ) {
      if ( combined != null ) return combined.contains( key );
      
      boolean res = contents.contains( key );
      if ( ( !res ) && ( defaults != null ) ) res = defaults.contains( key );
      return res;
   }
   
   /**
    * Tests if the <code>key</code> exists in the <code>ArgArray</code>.
    * 
    * @param key - The key to check.
    * @return true if the key is contained.
    */
   public synchronized boolean containsKey ( String key ) {
      if ( combined != null ) return combined.containsKey( key );
      
      boolean res = contents.containsKey( key );
      if ( ( !res ) && ( defaults != null ) ) res = defaults.containsKey( key );
      return res;
   }
   
   /**
    * Compare the argument array to self and return true if the argument has
    * zero elements that are not already in the <code>ArgArray</code>. Stated
    * another way, this method returns true if <code>arg0</code> is a subset
    * of this.
    * 
    * @param arg0 - the <code>ArgArray</code> to compare to self.
    * @return - true if they argument is a subset.
    */
   public boolean isSubsetOf ( ArgArray arg0 ) {
      if ( arg0 == null ) return false;
      if ( this == arg0 ) return true;
      
      Enumeration e = keys( );
      while ( e.hasMoreElements( ) ) {
         String key = ( String ) e.nextElement( );
         Object val = get( key );
         Object arg0Val = arg0.get( key );
         
         if ( val instanceof ArgArray ) {
            if ( !( arg0Val instanceof ArgArray ) ) return false;
            ArgArray aVal = ( ArgArray ) val;
            ArgArray aArg0Val = ( ArgArray ) arg0Val;
            if ( !aVal.isSubsetOf( aArg0Val ) ) return false;
         } else {
            if ( val == null ) {
               if ( arg0Val != null ) return false;
            } else {
               if ( !val.equals( arg0Val ) ) return false;
            }
         }
      }
      return true;
   }
   
   private void copy ( Hashtable from , Hashtable to ) {
      Enumeration ke = from.keys( );
      while ( ke.hasMoreElements( ) ) {
         Object key = ke.nextElement( );
         to.put( key , from.get( key ) );
      }
   }
   
   private synchronized Hashtable getCombined ( ) {
      if ( combined != null ) return combined;
      if ( defaults == null ) return contents;
      
      combined = new Hashtable( );
      copy( defaults.getCombined( ) , combined );
      copy( contents , combined );
      return combined;
   }
   
   /**
    * Returns an enumeration of the values in this object. Use the Enumeration
    * methods on the returned object to fetch the elements sequentially.
    * 
    * @return an enumeration of the values in this hashtable.
    */
   public synchronized Enumeration elements ( ) {
      return getCombined( ).elements( );
   }
   
   /**
    * Implements the functionality described by the
    * {@link  java.lang.Object#equals(Object) equal} method in Object.
    * 
    * @param obj - the reference object with which to compare.
    * @return true if this object is the same as the obj argument; false
    * otherwise.
    */
   public boolean equals ( Object obj ) {
      if ( !( obj instanceof ArgArray ) ) return false;
      
      Hashtable thisCombined = combined;
      if ( ( combined == null ) && ( defaults != null ) ) thisCombined = getCombined( );
      if ( thisCombined == null ) thisCombined = contents;
      
      Hashtable objCombined = ( ( ArgArray ) obj ).getCombined( );
      
      if ( thisCombined.size( ) != objCombined.size( ) ) return false;
      
      boolean ret = true;
      for ( Enumeration it = thisCombined.keys( ) ; it.hasMoreElements( ) ; ) {
         Object key = it.nextElement( );
         Object thisVal = thisCombined.get( key );
         Object objVal = objCombined.get( key );
         
         if ( thisVal instanceof Object [ ] ) {
            Object [ ] thisArr = ( Object [ ] ) thisVal;
            Object [ ] objArr = ( Object [ ] ) objVal;
            for ( int i = 0 ; i < thisArr.length ; i++ ) {
               ret = thisArr[ i ].equals( objArr[ i ] );
               if ( !ret ) break;
            }
         } else if ( thisVal instanceof long [ ] ) {
            long [ ] thisLArr = ( long [ ] ) thisVal;
            long [ ] objLArr = ( long [ ] ) objVal;
            for ( int i = 0 ; i < thisLArr.length ; i++ ) {
               ret = ( thisLArr[ i ] == objLArr[ i ] );
               if ( !ret ) break;
            }
         } else if ( thisVal instanceof int [ ] ) {
            int [ ] thisIArr = ( int [ ] ) thisVal;
            int [ ] objIArr = ( int [ ] ) objVal;
            for ( int i = 0 ; i < thisIArr.length ; i++ ) {
               ret = ( thisIArr[ i ] == objIArr[ i ] );
               if ( !ret ) break;
            }
         } else if ( thisVal instanceof byte [ ] ) {
            byte [ ] thisBArr = ( byte [ ] ) thisVal;
            byte [ ] objBArr = ( byte [ ] ) objVal;
            for ( int i = 0 ; i < thisBArr.length ; i++ ) {
               ret = ( thisBArr[ i ] == objBArr[ i ] );
               if ( !ret ) break;
            }
         } else if ( thisVal instanceof boolean [ ] ) {
            boolean [ ] thisBArr = ( boolean [ ] ) thisVal;
            boolean [ ] objBArr = ( boolean [ ] ) objVal;
            for ( int i = 0 ; i < thisBArr.length ; i++ ) {
               ret = ( thisBArr[ i ] == objBArr[ i ] );
               if ( !ret ) break;
            }
         } else
            ret = thisVal.equals( objVal );
         
         if ( !ret ) {
            break;
         }
      }
      return ret;
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @return the value to which the key is mapped in this object; null if the
    * key is not mapped to any value in this object.
    */
   public synchronized Object get ( String key ) {
      if ( combined != null ) {
         Object val = combined.get( key );
         if ( val != null ) return val;
      }
      
      Object val = contents.get( key );
      if ( ( val == null ) && ( defaults != null ) ) val = defaults.get( key );
      // if (val!=null) return val;
      //        
      // // look for hierarchical name
      // // Unfortunately we sometimes look up urls and jddac ids which have
      // slashes
      // int start=0;
      // val = this;
      // int sep = key.indexOf('/');
      // if (sep < 0) return null;
      //        
      // do {
      // if ((val==null) || !(val instanceof ArgArray)) return null; // cannot
      // traverse
      // if (sep < 0)
      // sep = key.length();
      // if (sep > start) {
      // String name = key.substring(start, sep);
      // start = sep + 1;
      // val = ((ArgArray)val).get(name);
      // }
      // sep = key.indexOf('/', start);
      // } while (start < key.length());
      
      return val;
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object. If
    * the key is not found, <code>defaultValue</code> is returned.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @param defaultValue
    * @return the value to which the key is mapped in this object; defaultValue
    * if the key is not mapped to any value in this object.
    */
   public synchronized Object get ( String key , Object defaultValue ) {
      Object rtn = get( key );
      if ( rtn == null ) return defaultValue;
      return rtn;
   }
   
   /**
    * Implements the functionality described by the
    * {@link  java.lang.Object#hashCode() hashCode} method in Object.
    * 
    * @return a hash code value for this object.
    */
   public int hashCode ( ) {
      return getCombined( ).hashCode( );
   }
   
   /**
    * Tests if this hashtable maps no keys to values.
    * 
    * @return true if this hashtable maps no keys to values; false otherwise.
    */
   public boolean isEmpty ( ) {
      boolean res = contents.isEmpty( );
      if ( defaults != null ) res = res && defaults.isEmpty( );
      return res;
   }
   
   /**
    * Returns an enumeration of the keys in this object.
    * 
    * @return an enumeration of the keys in this object.
    */
   public synchronized Enumeration keys ( ) {
      return getCombined( ).keys( );
   }
   
   /**
    * Maps the specified key to the specified value in this object. Neither the
    * key nor the value can be null. The value can be retrieved by calling the
    * get method with a key that is equal to the original key.
    * 
    * @param key - the key
    * @param value - the value
    * @return the previous value of the specified key in this object, or null if
    * it did not have one.
    */
   public synchronized Object put ( Object key , Object value ) {
      if ( locked ) throw new ArrayStoreException( lockMessage );
      
      if ( combined != null ) combined.put( key , value );
      return contents.put( key , value );
   }
   
   /**
    * Add an integer to the ArgArray.
    * 
    * @param key - the ArgArray key, usually a String.
    * @param value - the value to insert.
    * @return the previous value of the specified key in this object, or null if
    * it did not have one.
    */
   public synchronized Object put ( Object key , int value ) {
      return put( key , new Integer( value ) );
   }
   
   /**
    * Add an integer to the ArgArray.
    * 
    * @param key - the ArgArray key, usually a String.
    * @param value - the value to insert.
    * @return the previous value of the specified key in this object, or null if
    * it did not have one.
    */
   public synchronized Object put ( Object key , long value ) {
      return put( key , new Long( value ) );
   }
   
   /**
    * Add a boolean to the ArgArray.
    * 
    * @param key - the ArgArray key, usually a String.
    * @param value - the value to insert.
    * @return the previous value of the specified key in this object, or null if
    * it did not have one.
    */
   public synchronized Object put ( Object key , boolean value ) {
      return put( key , new Boolean( value ) );
   }
   
   /**
    * Removes the key (and its corresponding value) from this hashtable. This
    * method does nothing if the key is not in the hashtable.
    * 
    * @param key - the key that needs to be removed.
    * @return the value to which the key had been mapped in this object, or null
    * if the key did not have a mapping.
    */
   public synchronized Object remove ( Object key ) {
      if ( locked ) throw new ArrayStoreException( lockMessage );
      
      combined = null;
      return contents.remove( key );
   }
   
   /**
    * Returns the number of keys in this <code>ArgArray</code>.
    * 
    * @return the number of keys in this <code>ArgArray</code>.
    */
   public int size ( ) {
      return getCombined( ).size( );
   }
   
   /**
    * Returns a String object representing this ArgArray's value.
    * 
    * @return a string representation of the value of this object.
    */
   public synchronized String toString ( ) {
      StringBuffer buf = new StringBuffer( "\nArgArray{" );
      
      Hashtable c = getCombined( );
      Enumeration e = keys( );
      while ( e.hasMoreElements( ) ) {
         Object blah = e.nextElement( );
         String k = ( String ) blah;
         Object o = c.get( k );
         
         buf.append( "\n " );
         buf.append( k );
         buf.append( " : " );
         if ( o.getClass( ).isArray( ) ) {
            if ( o instanceof int [ ] ) {
               int [ ] ia = ( int [ ] ) o;
               buf.append( "int[] : {" );
               for ( int i = 0 ; i < ia.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( ia[ i ] );
               }
            } else if ( o instanceof long [ ] ) {
               long [ ] la = ( long [ ] ) o;
               buf.append( "long[] : {" );
               for ( int i = 0 ; i < la.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( la[ i ] );
               }
            } else if ( o instanceof byte [ ] ) {
               byte [ ] bya = ( byte [ ] ) o;
               buf.append( "byte[] : {" );
               for ( int i = 0 ; i < bya.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( bya[ i ] );
               }
            } else if ( o instanceof boolean [ ] ) {
               boolean [ ] ba = ( boolean [ ] ) o;
               buf.append( "boolean[] : {" );
               for ( int i = 0 ; i < ba.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( ba[ i ] ? "true" : "false" );
               }
            } else if ( o instanceof Object [ ] ) {
               Object [ ] oa = ( Object [ ] ) o;
               buf.append( o.getClass( ).getName( ) );
               buf.append( "[] : {" );
               for ( int i = 0 ; i < oa.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( oa[ i ].toString( ) );
               }
            } else
               buf.append( o.toString( ) );
            buf.append( "}" );
         } else {
            buf.append( o.getClass( ).getName( ) );
            buf.append( " : " );
            buf.append( o.toString( ) );
         }
      }
      buf.append( "\n}" );
      return buf.toString( );
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @return the value to which the key is mapped in this object; null if the
    * key is not mapped to any value in this object.
    */
   public synchronized String getString ( String key ) {
      return getString( key , null );
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object. If
    * the key is not found, <code>defaultValue</code> is returned.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @param defaultValue
    * @return the value to which the key is mapped in this object; defaultValue
    * if the key is not mapped to any value in this object.
    */
   public synchronized String getString ( String key , String defaultValue ) {
      Object obj = get( key );
      if ( obj == null ) return defaultValue;
      if ( obj instanceof ArgArray ) {
         ArgArray args = ( ArgArray ) obj;
         obj = args.get( MeasAttr.VALUE );
      }
      
      if ( obj != null ) return obj.toString( );
      else
         return null;
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @return the value to which the key is mapped in this object; null if the
    * key is not mapped to any value in this object.
    */
   public synchronized boolean getBool ( String key ) {
      return getBool( key , false );
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object. If
    * the key is not found, <code>defaultValue</code> is returned.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @param defaultValue
    * @return the value to which the key is mapped in this object; defaultValue
    * if the key is not mapped to any value in this object.
    */
   public synchronized boolean getBool ( String key , boolean defaultValue ) {
      boolean bval = defaultValue;
      Object obj = get( key );
      if ( obj == null ) return bval;
      if ( obj instanceof ArgArray ) {
         ArgArray args = ( ArgArray ) obj;
         obj = args.get( MeasAttr.VALUE );
      }

      else if ( obj instanceof Boolean ) {
         bval = ( ( Boolean ) obj ).booleanValue( );
      } else if ( obj instanceof Integer ) {
         bval = ( ( Integer ) obj ).intValue( ) != 0;
      } else {
         if ( !( obj instanceof String ) ) {
            obj = obj.toString( );
         }
         
         // obj is now a string
         String s = ( String ) obj;
         s = s.toLowerCase( );
         if ( s.equals( "false" ) ) bval = false;
         else if ( s.equals( "true" ) ) bval = true;
         else
            bval = ( Integer.parseInt( s ) != 0 );
      }
      return bval;
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @return the value to which the key is mapped in this object; null if the
    * key is not mapped to any value in this object.
    */
   public synchronized int getInt ( String key ) {
      return getInt( key , 0 );
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object. If
    * the key is not found, <code>defaultValue</code> is returned.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @param defaultValue
    * @return the value to which the key is mapped in this object; defaultValue
    * if the key is not mapped to any value in this object.
    */
   public synchronized int getInt ( String key , int defaultValue ) {
      return getInt( key , defaultValue , null );
   }
   
   /**
    * Extract an int value with optional application of internal unit scaling.
    * For platforms where the internal representation of a floating point number
    * is not convenient (J2ME/CLDC1.0) our where the basic internal usage is
    * something like milliseconds, the caller can specified the desired integer
    * internal units. For example, if an internal delay is stored in
    * milliseconds to be compatible with many java delay/wait/timeout
    * parameters, one can specify "ms" as the unit. This will cause values such
    * as 1.123 to return as 1123 (ms). Or, 4578us would return as 4578 (ms). If
    * a unit is not specified (either null or empty) then the internal units are
    * presumed to be the fundamental SI unit without any scaling.
    * 
    * @param key - the name of the value to extract
    * @param defaultValue - the value to use if an error occurs.
    * @param units - The desired internal units, can be null.
    * @return - An integer scaled to the specified internal units.
    */
   public synchronized int getInt ( String key , int defaultValue , String units ) {
      long lval = getLong( key , defaultValue , units );
      return ( int ) lval;
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @return the value to which the key is mapped in this object; null if the
    * key is not mapped to any value in this object.
    */
   public synchronized long getLong ( String key ) {
      return getLong( key , 0 );
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object. If
    * the key is not found, <code>defaultValue</code> is returned.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @param defaultValue
    * @return the value to which the key is mapped in this object; defaultValue
    * if the key is not mapped to any value in this object.
    */
   public synchronized long getLong ( String key , long defaultValue ) {
      return getLong( key , defaultValue , null );
   }
   
   /**
    * Extract a long value with optional application of internal unit scaling.
    * For platforms where the internal representation of a floating point number
    * is not convenient (J2ME/CLDC1.0) our where the basic internal usage is
    * something like milliseconds, the caller can specified the desired integer
    * internal units. For example, if an internal delay is stored in
    * milliseconds to be compatible with many java delay/wait/timeout
    * parameters, one can specify "ms" as the unit. This will cause values such
    * as 1.123 to return as 1123 (ms). Or, 4578us would return as 4578 (ms). If
    * a unit is not specified (either null or empty) then the internal units are
    * presumed to be the fundamental SI unit without any scaling.
    * 
    * @param key - the name of the value to extract
    * @param defaultValue - the value to use if an error occurs.
    * @param toUnits - The desired internal units, can be null.
    * @return - A long scaled to the specified internal units.
    */
   public synchronized long getLong ( String key , long defaultValue , String toUnits ) {
      long lval = defaultValue;
      Object fromUnits = null;
      Object obj = get( key );
      
      if ( obj == null ) return lval;
      if ( obj instanceof ArgArray ) {
         ArgArray args = ( ArgArray ) obj;
         fromUnits = args.get( MeasAttr.UNITS );
         obj = args.get( MeasAttr.VALUE );
      }
      
      if ( obj instanceof Boolean ) {
         lval = ( ( Boolean ) obj ).booleanValue( ) ? 1 : 0;
      } else if ( obj instanceof Long ) {
         lval = ( ( Long ) obj ).longValue( );
         lval = convertHelper.convertUnits( lval , fromUnits , toUnits );
      } else if ( obj instanceof Integer ) {
         lval = ( ( Integer ) obj ).longValue( );
         lval = convertHelper.convertUnits( lval , fromUnits , toUnits );
      } else {
         if ( !( obj instanceof String ) ) {
            obj = obj.toString( );
         }
         // obj is now a string
         String s = ( String ) obj;
         lval = convertHelper.convertUnits( s , fromUnits , toUnits );
      }
      return lval;
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @return the value to which the key is mapped in this object; null if the
    * key is not mapped to any value in this object.
    */
   public synchronized ArgArray getArgArray ( String key ) {
      return getArgArray( key , null );
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object. If
    * the key is not found, <code>defaultValue</code> is returned.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @param defaultValue
    * @return the value to which the key is mapped in this object; defaultValue
    * if the key is not mapped to any value in this object.
    */
   public synchronized ArgArray getArgArray ( String key , ArgArray defaultValue ) {
      Object obj = get( key );
      if ( obj == null ) return defaultValue;
      if ( obj instanceof ArgArray ) return ( ArgArray ) obj;
      return null;
      
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @return the value to which the key is mapped in this object; null if the
    * key is not mapped to any value in this object.
    */
   public synchronized Vector getVector ( String key ) {
      return getVector( key , null );
   }
   
   /**
    * Returns the value to which the specified key is mapped in this object. If
    * the key is not found, <code>defaultValue</code> is returned.
    * 
    * @param key - a key in the <code>ArgArray</code>.
    * @param defaultValue
    * @return the value to which the key is mapped in this object; defaultValue
    * if the key is not mapped to any value in this object.
    */
   public synchronized Vector getVector ( String key , Vector defaultValue ) {
      Object obj = get( key );
      if ( obj == null ) return defaultValue;
      if ( obj instanceof Vector ) return ( Vector ) obj;
      return null;
      
   }
   
   /**
    * Add the top-level contents of the specified <code>ArgArray</code>.
    * Existing entries with the same key name are replaced with object
    * references from the source ArgArray.
    * 
    * @param newStuff - the <code>ArgArray</code> contents to add.
    */
   public void add ( ArgArray newStuff ) {
      for ( Enumeration e = newStuff.contents.keys( ) ; e.hasMoreElements( ) ; ) {
         Object key = e.nextElement( );
         put( key , newStuff.contents.get( key ) );
      }
   }
   
   /**
    * Performs a deep add of the contents of the specified <code>ArgArray</code>.
    * Existing ArgArrays are not replaced but are instead 'augmented' with
    * values from <code>newStuff</code>. Scalar values are replaced. When
    * adding a <code>Vector</code> at places where a <code>Vector</code>
    * already exists, the behaviour is controlled by the <code>vectorMode</code>
    * argument to allow one of error, replace, insert, or append.
    * 
    * @param newStuff - the <code>ArgArray</code> contents to add.
    * @param vectorMode - One of MODE_REPLACE, VECTOR_MODE_INSERT,
    * VECTOR_MODE_APPEND, VECTOR_MODE_ERROR to control how vectors are added to
    * existing vectors.
    */
   public void deepAdd ( ArgArray newStuff , int vectorMode ) {
      for ( Enumeration e = newStuff.contents.keys( ) ; e.hasMoreElements( ) ; ) {
         String key = ( String ) e.nextElement( );
         Object val = newStuff.contents.get( key );
         
         if ( val instanceof ArgArray ) {
            if ( !containsKey( key ) ) {
               put( key , new ArgArray( ) );
            }
            ArgArray inner = getArgArray( key );
            inner.deepAdd( ( ArgArray ) val );
         } else if ( val instanceof Vector ) {
            Vector newVal = deepCloneVector( ( Vector ) val );
            if ( !containsKey( key ) || vectorMode == MODE_REPLACE ) {
               put( key , newVal );
            } else {
               Vector oldVector = getVector( key );
               switch ( vectorMode ) {
                  case VECTOR_MODE_INSERT :
                     for ( int i = newVal.size( ) ; i > 0 ; i-- ) {
                        oldVector.insertElementAt( newVal.elementAt( i - 1 ) , 0 );
                     }
                     break;
                  case VECTOR_MODE_APPEND :
                     for ( int i = 0 ; i < newVal.size( ) ; i++ ) {
                        oldVector.addElement( newVal.elementAt( i ) );
                     }
                     break;
                  case VECTOR_MODE_ERROR :
                     throw ( new RuntimeException( "Vector element already exists for " + key ) );
                     
                  default :
                     throw ( new RuntimeException( "Unrecognized mode in ArgArray.deepAdd for key=" + key ) );
               }
            }
         } else {
            put( key , val );
         }
      }
   }
   
   /**
    * Performs a deep add of the contents of the specified <code>ArgArray</code>.
    * This is equivalent to calling <code>deepAdd(newStuff,MODE_REPLACE).
    * 
    * @param newStuff
    */
   public void deepAdd ( ArgArray newStuff ) {
      deepAdd( newStuff , MODE_REPLACE );
   }
   
   /**
    * Create a new <code>Vector</code> copy where all of the container objects (<code>ArgArray</code>
    * and <code>Vector</code>) in the result are newly created. Contents
    * which represent scalar types (as opposed to code>ArgArray</code> and
    * <code>Vector</code>) are copied by refernce so new objects are not
    * created.
    * 
    * @param v
    * @return
    */
   public static Vector deepCloneVector ( Vector v ) {
      Vector inner = new Vector( );
      
      for ( Enumeration e = v.elements( ) ; e.hasMoreElements( ) ; ) {
         Object val = e.nextElement( );
         if ( val instanceof ArgArray ) {
            ArgArray newVal = new ArgArray( );
            newVal.deepAdd( ( ArgArray ) val );
            inner.addElement( newVal );
         } else if ( val instanceof Vector ) {
            Vector newVal = deepCloneVector( ( Vector ) val );
            inner.addElement( newVal );
         } else {
            inner.addElement( val );
         }
      }
      return inner;
   }
   
   /**
    * Register an <code>ArgConverter</code> to be used with
    * <code>findArgConverter</code>.
    * 
    * @param converter an instance of <code>ArgConverter</code> to add to the
    * list.
    * @return always true. Can be useful for initialization checks.
    */
   public static boolean registerArgConverter ( Object key , ArgConverter converter ) {
      argConverters.put( key , converter );
      argConverters.put( converter.getClass( ) , converter );
      // Currently don't use reverse lookup feature to find name from
      // converter
      // if (key instanceof String)
      // argConverters.put(converter,key); // allow reverse lookup of name
      return true;
   }
   
   /**
    * Register an <code>ArgConverter</code> to be used with
    * <code>findArgConverter</code>.
    * 
    * @param converterClass a class that implements the
    * <code>ArgConverter></code> interface.
    * @return always true. Can be useful for initialization checks.
    */
   public static boolean registerArgConverter ( Object key , Class converterClass ) {
      ArgConverter argConverter;
      try {
         argConverter = ( ArgConverter ) converterClass.newInstance( );
         registerArgConverter( key , argConverter );
      } catch ( Exception e ) {
         e.printStackTrace( );
      }
      
      return true;
   }
   
   /**
    * Given a type key, look up the associated <code>ArgConverter</code>.
    * 
    * @param name Either a class or name to look up.
    * @return the associated <code>ArgConverter</code>, null if none found.
    */
   public static ArgConverter findArgConverter ( Object name ) {
      return ( ArgConverter ) ( argConverters.get( name ) );
   }
   
   /*
    * public synchronized double getDouble(Object key) { double dval; Object obj =
    * contents.get(key); if (obj == null) return 0; if (obj instanceof Integer) {
    * dval = ((Double)obj).doubleValue(); } else { if (!(obj instanceof String)) {
    * obj = obj.toString(); } // obj is now a string String s = (String)obj;
    * dval = Double.parseDouble(s); } return dval; }
    */

   /**
    * Returns a String object representing this ArgArray's value.
    * 
    * @return a string representation of the value of this object.
    */
   public synchronized String toHtmlString ( ) {
      StringBuffer buf = new StringBuffer( "" );
      
      Hashtable c = getCombined( );
      Enumeration e = keys( );
      while ( e.hasMoreElements( ) ) {
         Object blah = e.nextElement( );
         String k = ( String ) blah;
         Object o = c.get( k );
         buf.append( k ).append(" : " );
         if ( o.getClass( ).isArray( ) ) {
            if ( o instanceof int [ ] ) {
               int [ ] ia = ( int [ ] ) o;
               buf.append( "int[] : {" );
               for ( int i = 0 ; i < ia.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( ia[ i ] );
               }
            } else if ( o instanceof long [ ] ) {
               long [ ] la = ( long [ ] ) o;
               buf.append( "long[] : {" );
               for ( int i = 0 ; i < la.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( la[ i ] );
               }
            } else if ( o instanceof byte [ ] ) {
               byte [ ] bya = ( byte [ ] ) o;
               buf.append( "byte[] : {" );
               for ( int i = 0 ; i < bya.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( bya[ i ] );
               }
            } else if ( o instanceof boolean [ ] ) {
               boolean [ ] ba = ( boolean [ ] ) o;
               buf.append( "boolean[] : {" );
               for ( int i = 0 ; i < ba.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( ba[ i ] ? "true" : "false" );
               }
            } else if ( o instanceof Object [ ] ) {
               Object [ ] oa = ( Object [ ] ) o;
               buf.append( o.getClass( ).getName( ) );
               buf.append( "[] : {" );
               for ( int i = 0 ; i < oa.length ; i++ ) {
                  if ( i > 0 ) buf.append( ", " );
                  buf.append( oa[ i ].toString( ) );
               }
            } else
               buf.append( o.toString( ) );
            buf.append( "}" );
         } else {
            buf.append( o.getClass( ).getName( ) );
            if ( o.getClass( ) == Measurement.class ) {
               buf.append( ( ( Measurement ) o ).toHtmlString( ) );
            } else {
               buf.append( " : " );
               buf.append( o.toString( ) );
            }
         }
      }
      return buf.toString( );
   }
   
}
