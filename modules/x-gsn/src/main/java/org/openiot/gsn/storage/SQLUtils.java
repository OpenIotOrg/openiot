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
 * @author gsn_devs
 * @author Timotee Maret
 * @author Ali Salehi
*/

package org.openiot.gsn.storage;

import org.openiot.gsn.utils.CaseInsensitiveComparator;

import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class SQLUtils {

	/**
	 * Table renaming, note that the renameMapping should be a tree map. This
	 * method gets a sql query and changes the table names using the mappings
	 * provided in the second argument.<br>
	 * 
	 * @param query
	 * @param renameMapping
	 * @return
	 */
	public static StringBuilder newRewrite ( CharSequence query , TreeMap < CharSequence , CharSequence > renameMapping ) {
		// Selecting strings between pair of "" : (\"[^\"]*\")
		// Selecting tableID.tableName or tableID.* : (\\w+(\\.(\w+)|\\*))
		// The combined pattern is : (\"[^\"]*\")|(\\w+\\.((\\w+)|\\*))
		Pattern pattern = Pattern.compile( "(\"[^\"]*\")|((\\w+)(\\.((\\w+)|\\*)))" , Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( query );
		StringBuffer result = new StringBuffer( );
		if ( !( renameMapping.comparator( ) instanceof CaseInsensitiveComparator ) ) throw new RuntimeException( "Query rename needs case insensitive treemap." );
		while ( matcher.find( ) ) {
			if ( matcher.group( 2 ) == null ) continue;
			String tableName = matcher.group( 3 );
			CharSequence replacement = renameMapping.get( tableName );
			// $4 means that the 4th group of the match should be appended to the
			// string (the forth group contains the field name).
			if ( replacement != null ) matcher.appendReplacement( result , new StringBuilder( replacement ).append( "$4" ).toString( ) );
		}
		String toReturn = matcher.appendTail( result ).toString( ).toLowerCase( );

		//TODO " from " has to use regular expressions because now from is separated through space which is not always the case, for instance if the user uses \t(tab) for separating "from" from the rest of the query, then we get exception. The same issue with other sql keywords in this method.

		int indexOfFrom = toReturn.indexOf( " from " )>=0?toReturn.indexOf( " from " ) + " from ".length( ):0;
		int indexOfWhere = ( toReturn.lastIndexOf( " where " ) > 0 ? ( toReturn.lastIndexOf( " where " ) ) : toReturn.length( ) );
		String selection = toReturn.substring( indexOfFrom , indexOfWhere );
		Pattern fromClausePattern = Pattern.compile( "\\s*(\\w+)\\s*" , Pattern.CASE_INSENSITIVE );
		Matcher fromClauseMather = fromClausePattern.matcher( selection );
		result = new StringBuffer( );
		while ( fromClauseMather.find( ) ) {
			if ( fromClauseMather.group( 1 ) == null ) continue;
			String tableName = fromClauseMather.group( 1 );
			CharSequence replacement = renameMapping.get( tableName );
			if ( replacement != null ) 
				fromClauseMather.appendReplacement( result , replacement.toString( ) + " ");         
		}
		String cleanFromClause = fromClauseMather.appendTail( result ).toString( );
		String finalResult = StringUtils.replace( toReturn , selection , cleanFromClause );
		return new StringBuilder(finalResult);
	}

	/**
	 * This method gets a sql query and changes the table names which are equal to 
	 * <code>tableNameToRename</code> to the <code>replacement</code> 
	 * provided in the second argument.<br>
	 * 
	 * @param query
	 * @param tableNameToRename Table name to be replaced
	 * @param replaceTo 
	 * @return
	 */

	private static Pattern pattern = Pattern.compile( "(\"[^\"]*\")|((\\w+)(\\.((\\w+)|\\*)))" , Pattern.CASE_INSENSITIVE );

	public static String getTableName ( String query ) {
		String q = SQLValidator.removeSingleQuotes(SQLValidator.removeQuotes(query)).toLowerCase();
		StringTokenizer tokens = new StringTokenizer(q," ");
		while(tokens.hasMoreElements())
			if (tokens.nextToken().equalsIgnoreCase("from") && tokens.hasMoreTokens()) 
				return tokens.nextToken();
		return null;
	}
	public static StringBuilder newRewrite ( CharSequence query , CharSequence tableNameToRename, CharSequence replaceTo ) {
		// Selecting strings between pair of "" : (\"[^\"]*\")
		// Selecting tableID.tableName or tableID.* : (\\w+(\\.(\w+)|\\*))
		// The combined pattern is : (\"[^\"]*\")|(\\w+\\.((\\w+)|\\*))
		Matcher matcher = pattern.matcher( query );
		StringBuffer result = new StringBuffer( );
		while ( matcher.find( ) ) {
			if ( matcher.group( 2 ) == null ) continue;
			String tableName = matcher.group( 3 );
			if(tableName.equals(tableNameToRename)){
				// $4 means that the 4th group of the match should be appended to the
				// string (the forth group contains the field name).
				if ( replaceTo != null ) matcher.appendReplacement( result , new StringBuilder( replaceTo ).append( "$4" ).toString( ) );
			}
		}
		String toReturn = matcher.appendTail( result ).toString( ).toLowerCase( );
		int indexOfFrom = toReturn.indexOf( " from " )>=0?toReturn.indexOf( " from " ) + " from ".length( ):0;
		int indexOfWhere = ( toReturn.lastIndexOf( " where " ) > 0 ? ( toReturn.lastIndexOf( " where " ) ) : toReturn.length( ) );
		String selection = toReturn.substring( indexOfFrom , indexOfWhere );
		Pattern fromClausePattern = Pattern.compile( "\\s*(\\w+)\\s*" , Pattern.CASE_INSENSITIVE );
		Matcher fromClauseMather = fromClausePattern.matcher( selection );
		result = new StringBuffer( );
		while ( fromClauseMather.find( ) ) {
			if ( fromClauseMather.group( 1 ) == null ) continue;
			String tableName = fromClauseMather.group( 1 );
			if (tableName.equals(tableNameToRename) && replaceTo != null)
				fromClauseMather.appendReplacement( result , replaceTo.toString( ) + " ");
		}
		String cleanFromClause = fromClauseMather.appendTail( result ).toString( );
		String finalResult = StringUtils.replace( toReturn , selection , cleanFromClause );
		return new StringBuilder(finalResult);
	}

	public static String extractProjection(String pQuery) {
		String query = pQuery.trim().toLowerCase();
		int indexOfFrom = query.indexOf( " from " ) ;
		int indexOfSelect =query.indexOf("select");
		return pQuery.substring(indexOfSelect+"select".length(), indexOfFrom);
	}

	public static String extractWhereClause(String pQuery) {
		int indexOfWhere = pQuery.toLowerCase().indexOf( " where " ) ;
		if (indexOfWhere<0)
			return " true ";
		String toReturn = pQuery.substring(indexOfWhere+" where".length(),pQuery.length());
		System.out.println(toReturn);
		return toReturn;
	}

	public static void main ( String [ ] args ) {
		TreeMap < CharSequence , CharSequence > map = new TreeMap < CharSequence , CharSequence >( new CaseInsensitiveComparator( ) );
		String query ="seLect ali.fd, x.x, fdfd.fdfd, *.r, * from x,x, bla, x whEre k";
		map.put( "x" , "done" );
		CharSequence out = newRewrite( query , map );
		System.out.println( out.toString( ) );
		System.out.println(extractProjection(query)  		  );
		out = newRewrite( extractProjection(query) , map );
		System.out.println( out.toString( ) );
	}

	public static int getWhereIndex(CharSequence c) {
		return c.toString().toLowerCase().lastIndexOf(" where ");
	}
	public static int getOrderByIndex(CharSequence c) {
		return c.toString().toLowerCase().lastIndexOf(" order by ");
	}
	public static int getGroupByIndex(CharSequence c) {
		return c.toString().toLowerCase().lastIndexOf(" group by ");
	}
}
