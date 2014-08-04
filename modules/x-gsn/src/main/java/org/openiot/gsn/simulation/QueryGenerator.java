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

package org.openiot.gsn.simulation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

public class QueryGenerator {
   
   public static StringBuffer generateQuery ( String selectingFields , ArrayList < String > tables , int maxNumberOfPredicates , int maxNumOfInvolvedTables , int max_number_in_produced_data ) {
      ArrayList < String > choosenTables = new ArrayList < String >( );
      final double andOrProability = .5;
      StringBuffer result = new StringBuffer( "select " + selectingFields + " from " );
      int actualNumOfTablesInvolved = ( int ) ( Math.random( ) * maxNumOfInvolvedTables ) + 1;
      int choosedTables = 0;
      while ( choosedTables != actualNumOfTablesInvolved ) {
         int tableIndex = ( int ) ( Math.random( ) * tables.size( ) );
         if ( !choosenTables.contains( tables.get( tableIndex ) ) ) {
            choosedTables++;
            choosenTables.add( tables.get( tableIndex ) );
         }
      }
      for ( String name : choosenTables )
         result.append( "\"" ).append( name ).append( "\"" ).append( "," );
      result.deleteCharAt( result.length( ) - 1 );
      int actualNumOfPredicatesInvolved = ( int ) ( Math.random( ) * ( maxNumberOfPredicates + 1 ) );
      if ( actualNumOfPredicatesInvolved == 0 ) return result;
      result.append( " where " );
      
      for ( int i = 0 ; i < actualNumOfPredicatesInvolved ; i++ ) {
         result.append( "(" );
         int firstTable = ( int ) ( Math.random( ) * actualNumOfTablesInvolved );
         int secondTable = ( int ) ( Math.random( ) * actualNumOfTablesInvolved );
         while ( actualNumOfTablesInvolved > 1 && secondTable == firstTable ) {
            secondTable = ( int ) ( Math.random( ) * actualNumOfTablesInvolved );
         }
         result.append( "\"" ).append( choosenTables.get( firstTable ) ).append( ".data" ).append( "\"" );
         
         int operation = ( int ) ( Math.random( ) * ( actualNumOfTablesInvolved > 1 ? 5 : 3 ) );
         boolean set = false;
         switch ( operation ) {
            case 0 :
               result.append( " + " );
               set = false;
               break;
            case 1 :
               result.append( " - " );
               set = false;
               break;
            case 2 :
               result.append( " * " );
               set = false;
               break;
            case 3 :
               result.append( " IN " );
               set = true;
               break;
            case 4 :
               result.append( " NOT IN " );
               set = true;
               break;
         }
         if ( actualNumOfTablesInvolved > 1 ) {
            if ( set ) {
               ArrayList < String > arrayList = new ArrayList < String >( );
               arrayList.add( choosenTables.get( secondTable ) );
               result.append( "(" ).append( generateQuery( choosenTables.get( secondTable ) + ".data" , arrayList , 2 , 1 , max_number_in_produced_data ) ).append( ")" );
            } else {
               result.append( "\"" ).append( choosenTables.get( secondTable ) ).append( ".data" ).append( "\"" );
            }
         } else
            result.append( ( int ) ( Math.random( ) * max_number_in_produced_data ) );
         if ( !set ) {
            int operation2 = ( int ) ( Math.random( ) * 5 );
            switch ( operation2 ) {
               case 0 :
                  result.append( " = " );
                  break;
               case 1 :
                  result.append( " < " );
                  break;
               case 2 :
                  result.append( " <= " );
                  break;
               case 3 :
                  result.append( " > " );
                  break;
               case 4 :
                  result.append( " <> " );
                  break;
            }
            
            result.append( ( int ) ( Math.random( ) * max_number_in_produced_data ) );
         }
         result.append( ")" );
         result.append( ( Math.random( ) < andOrProability ) ? " OR " : " AND " );
      }
      result.delete( result.length( ) - 4 , result.length( ) );
      return ( result );
      
   }
   
   public static void main ( String [ ] args ) throws ClassNotFoundException , Exception {
      ArrayList < String > tables = new ArrayList < String >( );
      tables.add( "sim" );
      // tables.add ( "table2" ) ;
      // tables.add ( "table3" ) ;
      // tables.add ( "table4" ) ;
      // tables.add ( "table5" ) ;
      // tables.add ( "table6" ) ;
      // tables.add ( "table7" ) ;
      // tables.add ( "table8" ) ;
      // tables.add ( "table9" ) ;
      // tables.add ( "table10" ) ;
      int count_range = 1000000000;
      int num_of_rows_in_each_table = 5;
      Class.forName( "org.hsqldb.jdbcDriver" );
      Properties properties = new Properties( );
      properties.put( "user" , "sa" );
      properties.put( "password" , "" );
      properties.put( "ignorecase" , "true" );
      properties.put( "autocommit" , "true" );
      
      Connection con = DriverManager.getConnection( "jdbc:hsqldb:file:/tmp/testdb-1" , properties );
      con.createStatement( ).execute( "SET IGNORECASE TRUE" );
      con.createStatement( ).execute( "SET AUTOCOMMIT TRUE" );
      con.createStatement( ).execute( "SET REFERENTIAL_INTEGRITY FALSE" );
      
      con.createStatement( ).execute( "CREATE ALIAS NOW_MILLIS FOR \"java.lang.System.currentTimeMillis\";" );
      con.createStatement( ).execute( "create table sim (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table2 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table3 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table4 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table5 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table6 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table7 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table8 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table9 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table10 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table11 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table12 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      con.createStatement( ).execute( "create table table13 (TIMED BIGINT NOT NULL IDENTITY PRIMARY KEY, DATA integer)" );
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into sim values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table2 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table3 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table4 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table5 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table6 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table7 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table8 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table9 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table10 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table11 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table12 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      for ( int i = 0 ; i < num_of_rows_in_each_table ; i++ ) {
         con.createStatement( ).executeUpdate( "insert into table13 values (" + i + "," + ( int ) ( Math.random( ) * count_range ) + ");" );
      }
      
      System.out.println( "Insertion finieshed." );
      long totalResultCount = 0;
      int num_of_queries = 30;
      for ( int i = 0 ; i < num_of_queries ; i++ ) {
         final StringBuffer generateQuery = generateQuery( "*" , tables , 2 , tables.size( ) , count_range );
         System.out.println( generateQuery );
         ResultSet rs = con.createStatement( ).executeQuery( generateQuery.toString( ).replace( "\"" , "" ) );
         // ResultSet rs = con.createStatement ().getBinaryFieldByQuery
         // ("select
         // * from
         // table4,table2,table1,table5" ) ;
         System.out.println( "EXECUTED-2" );
         // while ( rs.next () )
         // totalResultCount++ ;
      }
      System.out.println( "Total result :" + ( totalResultCount / ( num_of_queries * num_of_rows_in_each_table * 3 ) ) );
   }
}
