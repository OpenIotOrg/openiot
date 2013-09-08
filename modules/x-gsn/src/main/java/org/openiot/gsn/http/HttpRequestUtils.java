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

package org.openiot.gsn.http;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

/**
 * A class to simplify parameter handling. It can return parameters of any
 * primitive type and throws an exception when a parameter is not found or some
 * specified default value.
 */
public class HttpRequestUtils {
   
   /**
    * Extracts the string parameter from the request
    * 
    * @param name the parameter name
    * @return the parameter value as a String
    * @exception ParameterMissingException if the parameter was not found or was
    * the empty string
    */
   public static String getStringParameter ( String name , HttpServletRequest req ) throws ParameterMissingException {
      String [ ] values = req.getParameterValues( name );
      if ( values == null ) {
         throw new ParameterMissingException( name + " not found" );
      } else if ( values[ 0 ].length( ) == 0 ) { throw new ParameterMissingException( name + " was empty" ); }
      return values[ 0 ];
      
   }
   
   /**
    * Gets the named parameter value as a String, with a default. Returns the
    * default value if the parameter is not found or is the empty string.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a String, or the default
    */
   public static String getStringParameter ( String name , String def , HttpServletRequest req ) {
      try {
         return getStringParameter( name , req );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Gets the named parameter value as a boolean, with true indicated by
    * "true", "on", or "yes" in any letter case, false indicated by "false",
    * "off", or "no" in any letter case.
    * 
    * @param name the parameter name
    * @return the parameter value as a boolean
    * @exception ParameterMissingException if the parameter was not found
    * @exception NumberFormatException if the parameter could not be converted
    * to a boolean
    */
   public static Boolean getBooleanParameter ( String name , HttpServletRequest req ) throws ParameterMissingException , NumberFormatException {
      String value = getStringParameter( name , req ).toLowerCase( );
      if ( ( value.equalsIgnoreCase( "true" ) ) || ( value.equalsIgnoreCase( "on" ) ) || ( value.equalsIgnoreCase( "yes" ) ) ) {
         return true;
      } else if ( ( value.equalsIgnoreCase( "false" ) ) || ( value.equalsIgnoreCase( "off" ) ) || ( value.equalsIgnoreCase( "no" ) ) ) {
         return false;
      } else {
         throw new NumberFormatException( "Parameter " + name + " value " + value + " is not a boolean" );
      }
   }
   
   /**
    * Gets the named parameter value as a boolean, with a default. Returns the
    * default value if the parameter is not found.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a boolean, or the default
    */
   public static Boolean getBooleanParameter ( String name , boolean def , HttpServletRequest request ) {
      try {
         return getBooleanParameter( name , request );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Gets the named parameter value as a byte
    * 
    * @param name the parameter name
    * @return the parameter value as a byte
    * @exception ParameterMissingException if the parameter was not found
    * @exception NumberFormatException if the parameter value could not be
    * converted to a byte
    */
   public static Byte getByteParameter ( String name , HttpServletRequest request ) throws ParameterMissingException , NumberFormatException {
      return Byte.parseByte( getStringParameter( name , request ) );
   }
   
   /**
    * Gets the named parameter value as a byte, with a default. Returns the
    * default value if the parameter is not found or cannot be converted to a
    * byte.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a byte, or the default
    */
   public static Byte getByteParameter ( String name , byte def , HttpServletRequest request ) {
      try {
         return getByteParameter( name , request );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Gets the named parameter value as a char
    * 
    * @param name the parameter name
    * @return the parameter value as a char
    * @exception ParameterMissingException if the parameter was not found or was
    * the empty string
    */
   public static Character getCharParameter ( String name , HttpServletRequest request ) throws ParameterMissingException {
      String param = getStringParameter( name , request );
      if ( param.length( ) == 0 ) throw new ParameterMissingException( name + " is empty string" );
      else
         return ( param.charAt( 0 ) );
   }
   
   /**
    * Gets the named parameter value as a char, with a default. Returns the
    * default value if the parameter is not found.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a char, or the default
    */
   public static Character getCharParameter ( String name , char def , HttpServletRequest httpServletRequest ) {
      try {
         return getCharParameter( name , httpServletRequest );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Gets the named parameter value as a double
    * 
    * @param name the parameter name
    * @return the parameter value as a double
    * @exception ParameterMissingException if the parameter was not found
    * @exception NumberFormatException if the parameter could not be converted
    * to a double
    */
   public static Double getDoubleParameter ( String name , HttpServletRequest request ) throws ParameterMissingException , NumberFormatException {
      return new Double( getStringParameter( name , request ) ).doubleValue( );
   }
   
   /**
    * Gets the named parameter value as a double, with a default. Returns the
    * default value if the parameter is not found.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a double, or the default
    */
   public static Double getDoubleParameter ( String name , double def , HttpServletRequest request ) {
      try {
         return getDoubleParameter( name , request );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Gets the named parameter value as a float
    * 
    * @param name the parameter name
    * @return the parameter value as a float
    * @exception ParameterMissingException if the parameter was not found
    * @exception NumberFormatException if the parameter could not be converted
    * to a float
    */
   public static Float getFloatParameter ( String name , HttpServletRequest httpServletRequest ) throws ParameterMissingException , NumberFormatException {
      return new Float( getStringParameter( name , httpServletRequest ) ).floatValue( );
   }
   
   /**
    * Gets the named parameter value as a float, with a default. Returns the
    * default value if the parameter is not found.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a float, or the default
    */
   public static Float getFloatParameter ( String name , float def , HttpServletRequest request ) {
      try {
         return getFloatParameter( name , request );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Gets the named parameter value as a int
    * 
    * @param name the parameter name
    * @return the parameter value as a int
    * @exception ParameterMissingException if the parameter was not found
    * @exception NumberFormatException if the parameter could not be converted
    * to a int
    */
   public static Integer getIntParameter ( String name , HttpServletRequest request ) throws ParameterMissingException , NumberFormatException {
      return Integer.parseInt( getStringParameter( name , request ) );
   }
   
   /**
    * Gets the named parameter value as a int, with a default. Returns the
    * default value if the parameter is not found.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a int, or the default
    */
   public static Integer getIntParameter ( String name , int def , HttpServletRequest request ) {
      try {
         return getIntParameter( name , request );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Gets the named parameter value as a long
    * 
    * @param name the parameter name
    * @return the parameter value as a long
    * @exception ParameterMissingException if the parameter was not found
    * @exception NumberFormatException if the parameter could not be converted
    * to a long
    */
   public static Long getLongParameter ( String name , HttpServletRequest request ) throws ParameterMissingException , NumberFormatException {
      return Long.parseLong( getStringParameter( name , request ) );
   }
   
   /**
    * Gets the named parameter value as a long, with a default. Returns the
    * default value if the parameter is not found.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a long, or the default
    */
   public static Long getLongParameter ( String name , long def , HttpServletRequest request ) {
      try {
         return getLongParameter( name , request );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Gets the named parameter value as a short
    * 
    * @param name the parameter name
    * @return the parameter value as a short
    * @exception ParameterMissingException if the parameter was not found
    * @exception NumberFormatException if the parameter could not be converted
    * to a short
    */
   public static Short getShortParameter ( String name , HttpServletRequest request ) throws ParameterMissingException , NumberFormatException {
      return Short.parseShort( getStringParameter( name , request ) );
   }
   
   /**
    * Gets the named parameter value as a short, with a default. Returns the
    * default value if the parameter is not found.
    * 
    * @param name the parameter name
    * @param def the default parameter value
    * @return the parameter value as a short, or the default
    */
   public static Short getShortParameter ( String name , short def , HttpServletRequest request ) {
      try {
         return getShortParameter( name , request );
      } catch ( Exception e ) {
         return def;
      }
   }
   
   /**
    * Checks which of the required parameters are missing from the request
    * 
    * @param required
    * @return
    */
   public static ArrayList < String > getMissingParameters ( String [ ] required , HttpServletRequest request ) {
      ArrayList < String > missing = new ArrayList < String >( );
      for ( int i = 0 ; i < required.length ; i++ ) {
         String val = getStringParameter( required[ i ] , null , request );
         if ( val == null ) missing.add( required[ i ] );
      }
      return missing;
   }
}
