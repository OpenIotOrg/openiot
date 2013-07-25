package org.openiot.gsn.beans;

import org.openiot.gsn.utils.GSNRuntimeException;

import java.sql.Types;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class DataTypes {
   
   public final static String            OPTIONAL_NUMBER_PARAMETER = "\\s*(\\(\\s*\\d+\\s*\\))?";
   
   public final static String            REQUIRED_NUMBER_PARAMETER = "\\s*\\(\\s*\\d+\\s*\\)";
   
   private final static transient Logger logger                    = Logger.getLogger( DataTypes.class );
   
   // NEXT FIELD
   public final static String            VAR_CHAR_PATTERN_STRING   = "\\s*varchar" + REQUIRED_NUMBER_PARAMETER + "\\s*";
   
   public final static byte               VARCHAR                   = 0;
   
   public final static String            VARCHAR_NAME              = "Varchar";
   
   // NEXT FIELD
   public final static String            CHAR_PATTERN_STRING       = "\\s*char" + REQUIRED_NUMBER_PARAMETER + "\\s*";
   
   public final static byte               CHAR                      = 1;
   
   public final static String            CHAR_NAME                 = "Char";
   
   // NEXT FIELD
   public final static String            INTEGER_PATTERN_STRING    = "\\s*((INTEGER)|(INT))\\s*";
   
   public final static byte               INTEGER                   = 2;
   
   public final static String            INTEGER_NAME              = "Integer";
   
   // NEXT FIELD
   public final static String            BIGINT_PATTERN_STRING     = "\\s*BIGINT\\s*";
   
   public final static byte               BIGINT                    = 3;
   
   public final static String            BIGINT_NAME               = "BigInt";
   
   // NEXT FIELD
   public final static String            BINARY_PATTERN_STRING     = "\\s*(BINARY|BLOB)" + OPTIONAL_NUMBER_PARAMETER + "(\\s*:.*)?";
   
   public final static byte               BINARY                    = 4;
   
   public final static String            BINARY_NAME               = "Binary";
   
   // NEXT FIELD
   public final static String            DOUBLE_PATTERN_STRING     = "\\s*DOUBLE\\s*";
   
   public final static byte               DOUBLE                    = 5;
   
   public final static String            DOUBLE_NAME               = "Double";
   
   // NEXT FIELD
   /**
    * Type Time is not supported at the moment. If you want to present time, please use
    * longint. For more information consult the GSN mailing list on the same subject. 
    */
   public final static String            TIME_PATTERN_STRING       = "\\s*TIME\\s*";
   
   public final static byte               TIME                      = 6;
  
   public final static String            TIME_NAME                 = "Time";
   
   // NEXT FIELD
   public final static String            TINYINT_PATTERN_STRING    = "\\s*TINYINT\\s*";
   
   public final static byte               TINYINT                   = 7;
   
   public final static String            TINYINT_NAME              = "TinyInt";
   
   // NEXT FIELD
   public final static String            SMALLINT_PATTERN_STRING   = "\\s*SMALLINT\\s*";
   
   public final static byte               SMALLINT                  = 8;
   
   public final static String            SMALLINT_NAME             = "SmallInt";
   
   // FINISH
   public final static Pattern [ ]       ALL_PATTERNS              = new Pattern [ ] { Pattern.compile( VAR_CHAR_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) ,
         Pattern.compile( CHAR_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) , Pattern.compile( INTEGER_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) ,
         Pattern.compile( BIGINT_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) , Pattern.compile( BINARY_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) ,
         Pattern.compile( DOUBLE_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) ,
         Pattern.compile( TIME_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) ,
         Pattern.compile( TINYINT_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) , Pattern.compile( SMALLINT_PATTERN_STRING , Pattern.CASE_INSENSITIVE ) };
   
   public final static StringBuilder     ERROR_MESSAGE             = new StringBuilder( "Acceptable types are (TINYINT, SMALLINT, INTEGER,BIGINT,CHAR(#),BINARY[(#)],VARCHAR(#),DOUBLE,TIME)." );
   
   public final static String [ ]        TYPE_NAMES                = new String [ ] { VARCHAR_NAME , CHAR_NAME , INTEGER_NAME , BIGINT_NAME , BINARY_NAME , DOUBLE_NAME , 
     TIME_NAME ,
     TINYINT_NAME ,
         SMALLINT_NAME                                            };
   
   public final static Object [ ]        TYPE_SAMPLE_VALUES        = { "A chain of chars" , 'c' , new Integer( 32 ) , new Integer( 66000 ) , new Byte( ( byte ) 12 ) , new Double( 3.141592 ) ,
         new Date( ).getTime( ) , new Integer( 1 ) , new Integer( 9 ) };
   
   public static byte convertTypeNameToGSNTypeID ( final String type ) {
       if ( type == null ) throw new GSNRuntimeException( new StringBuilder( "The type *null* is not recoginzed by GSN." ).append( DataTypes.ERROR_MESSAGE ).toString( ) );
       if(type.trim().equalsIgnoreCase("string")) return DataTypes.VARCHAR;
       for ( byte i = 0 ; i < DataTypes.ALL_PATTERNS.length ; i++ )
         if ( DataTypes.ALL_PATTERNS[ i ].matcher( type ).matches( ) ){
             return i;
         }
      if(type.trim().equalsIgnoreCase("numeric")) return DataTypes.DOUBLE;
      
      
      throw new GSNRuntimeException( new StringBuilder( "The type *" ).append( type ).append( "* is not recognized." ).append( DataTypes.ERROR_MESSAGE ).toString( ) );
   }
   
   /**
    * throws runtime exception if the type conversion fails.
    * @param sqlType
    * @return
    */
   public static byte SQLTypeToGSNTypeSimplified(int sqlType) {
	   if (sqlType == Types.BIGINT || sqlType == Types.SMALLINT || sqlType == Types.DOUBLE || sqlType==Types.INTEGER || sqlType == Types.DECIMAL||sqlType == Types.REAL || sqlType == Types.FLOAT|| sqlType == Types.NUMERIC )
			return  DataTypes.DOUBLE;
		else if (sqlType == Types.VARCHAR || sqlType == Types.CHAR|| sqlType == Types.LONGNVARCHAR || sqlType == Types.LONGVARCHAR || sqlType== Types.NCHAR )
			return  DataTypes.VARCHAR;
		else if (sqlType == Types.BINARY || sqlType == Types.BLOB|| sqlType == Types.VARBINARY )
			return  DataTypes.BINARY;
	   throw new RuntimeException("Can't convert SQL type id of: "+sqlType+ " to GSN type id.");
   }
}
