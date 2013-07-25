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
