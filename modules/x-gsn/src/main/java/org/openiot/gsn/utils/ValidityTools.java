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
 * @author Jerome Rousselot
 * @author gsn_devs
 * @author Ali Salehi
 * @author Timotee Maret
*/

package org.openiot.gsn.utils;

import java.io.File;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class ValidityTools {

	public static final int              SMTP_PORT = 25;

	static Pattern hostAndPortPattern = Pattern.compile( "(.+):(\\d+)$" );

	public static final transient Logger logger    = Logger.getLogger( ValidityTools.class );
	/**
	 * Checks to see if the specified address is accessible. 3sec is used as the default
	 * timeout period.
	 * @param host The host to connect to.
	 * @param port the port to connect to (e.g., host:port).
	 */
	public static boolean isAccessibleSocket ( String host , int port ) throws UnknownHostException {
		return isAccessibleSocket(host, port, 3000);
	}

	public static boolean isAccessibleSocket ( String host , int port, int timeOutInMSec ) throws UnknownHostException,RuntimeException{
		Socket socket=null;
		boolean toReturn = false;
		try {
			if (port <=0 || port >65535|| host==null || host.trim().length()==0){
				logger.info("Bad parameters for validator tool"+"(port = "+port+", host="+(host==null));
				throw new RuntimeException("The specified parameters are not valid") ;
			}
			socket = new Socket( );
			InetSocketAddress inetSocketAddress = new InetSocketAddress( host , port );
			socket.connect( inetSocketAddress , timeOutInMSec );
			toReturn=true;
		}catch(ConnectException e){

		}catch ( Exception e ) {
			logger.info(e.getMessage(),e);
		}finally{
			if (socket !=null )
				try{
					socket.close();
				}catch(Exception e){}
		}
		return toReturn;
	}

	public static void checkAccessibilityOfDirs ( String ... args ) {
		for ( String name : args ) {
			File f = new File( name );
			if ( f.canRead( ) && f.canWrite( ) && f.isDirectory( ) ) continue;
			else {
				System.out.println( "The required directory : " + f.getAbsolutePath( ) + " is not accessible." );
				System.exit( 1 );
			}
		}
	}

	public static void checkAccessibilityOfFiles ( String... args ) {
		for ( String name : args ) {
			File f = new File( name );
			if ( f.canRead( ) && f.canWrite( ) && f.isFile( ) ) continue;
			else {
				System.out.println( "The required file : " + f.getAbsolutePath( ) + " is not accessible." );
				System.exit( 1 );
			}
		}
	}

	public static void isDBAccessible ( String driverClass , String url , String user , String password ) throws ClassNotFoundException , SQLException {
		Class.forName( driverClass );
		Connection con = DriverManager.getConnection( url , user , password );
		con.close( );
	}


	/*
	 * Returns the hostname part of a host:port String. This method is ipv6
	 * compatible. @param hostandport A string containing a host and a port
	 * number, separated by a ":" @return host A string with the host name part
	 * (either name or ip address) of the input string.
	 */
	public static String getHostName ( String hostandport ) {
		String hostname = "";
		try {
			Matcher m = hostAndPortPattern.matcher( hostandport );
			m.matches( );
			hostname = m.group( 1 ).toLowerCase( ).trim( );
		} catch ( Exception e ) {}
		return hostname;
	}

	/*
	 * Returns the port number of a host:port String. This method is ipv6
	 * compatible.
	 */
	public static int getPortNumber ( String hostandport ) {
		int port = -1;
		try {
			Matcher m = hostAndPortPattern.matcher(hostandport);
			m.matches();
			port = Integer.parseInt( m.group( 2 ).toLowerCase( ).trim( ) );
		} catch ( Exception e ) {}
		return port;
	}

	public static boolean isLocalhost ( String host ) {
		// this allows us to be ipv6 compatible (we simply remove the port)
		try {
			InetAddress hostAddress = InetAddress.getByName( host );
			if (hostAddress==null)
				return false;
			for ( InetAddress address : NETWORK_LOCAL_INETADDRESSES )
				if ( address.equals( hostAddress ) ) return true;
			return hostAddress.isLoopbackAddress();
		} catch ( UnknownHostException e ) {
			logger.debug( e );
			return false;
		}
	}

	//public static final ArrayList < String >      NETWORK_LOCAL_ADDRESS       = new ArrayList < String >( );

	public static final ArrayList < InetAddress > NETWORK_LOCAL_INETADDRESSES = new ArrayList < InetAddress >( );
	static {
		try {
			Enumeration < NetworkInterface > nets = NetworkInterface.getNetworkInterfaces( );
			for ( NetworkInterface netint : Collections.list( nets ) ) {
				Enumeration < InetAddress > address = netint.getInetAddresses( );
				for ( InetAddress addr : Collections.list( address ) ) {
					if ( !addr.isMulticastAddress( ) ) {
						//NETWORK_LOCAL_ADDRESS.add( addr.getCanonicalHostName( ) );
						NETWORK_LOCAL_INETADDRESSES.add( addr );
					}
				}
				// NETWORK_LOCAL_ADDRESS.add("localhost");
				// NETWORK_LOCAL_ADDRESS.add("127.0.0.1");

			}
		} catch ( Exception e ) {
			e.printStackTrace( );
		}
	}
	/**
	 * Returns true if the provided string can be converted
	 * correctly to an integer. Otherwise (null or non-numerical
	 * string) returns false
	 * @param value
	 * @return
	 */
	public static boolean isInt ( String value ) {
		try {
			Integer.parseInt( value.trim( ) );
			return true;
		} catch ( Exception exception ) {
			return false;
		}
	}


	public static boolean isValidJavaVariable(CharSequence string) {
		if (string ==null)
			return false;
		StringBuilder sb = new StringBuilder(string);
		if (sb.length()==0)
			return false;
		if (!Character.isJavaIdentifierStart(sb.charAt(0)) && string.charAt(0)!='\"' )
			return false;
		for (int i=1;i<sb.length();i++)
			if (!Character.isJavaIdentifierPart(sb.charAt(i)) && (i==sb.length()-1 && string.charAt(sb.length()-1)!='\"'))
				return false;
		return true;
	}
	
}
