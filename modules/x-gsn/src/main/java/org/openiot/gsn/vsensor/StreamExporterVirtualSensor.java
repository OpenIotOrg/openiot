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

package org.openiot.gsn.vsensor;

import org.openiot.gsn.Main;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.utils.GSNRuntimeException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * This virtual sensor saves its input stream to any JDBC accessible source.
 */
public class StreamExporterVirtualSensor extends AbstractVirtualSensor {

	public static final String            PARAM_USER    = "user" , PARAM_PASSWD = "password" , PARAM_URL = "url" , TABLE_NAME = "table",PARAM_DRIVER="driver";

	public static final String[] OBLIGATORY_PARAMS = new String[] {PARAM_USER,PARAM_URL,PARAM_DRIVER};

	private static final transient Logger logger        = Logger.getLogger( StreamExporterVirtualSensor.class );

	private Connection                    connection;

	private CharSequence table_name;

	private String password;

	private String user;
	
	private String url;

	public boolean initialize ( ) {
		VSensorConfig vsensor = getVirtualSensorConfiguration( );
		TreeMap < String , String > params = vsensor.getMainClassInitialParams();

		for (String param : OBLIGATORY_PARAMS)
			if ( params.get( param ) == null || params.get(param).trim().length()==0) {
				logger.warn("Initialization Failed, The "+param+ " initialization parameter is missing");
				return false;
			}
		table_name = params.get( TABLE_NAME );
		user = params.get(PARAM_USER);
		password = params.get(PARAM_PASSWD);
		url = params.get(PARAM_URL);
		try {
			Class.forName(params.get(PARAM_DRIVER));
			connection = getConnection();
			logger.debug( "jdbc connection established." );
			if (!Main.getStorage(table_name.toString()).tableExists(table_name,getVirtualSensorConfiguration().getOutputStructure() , connection))
				Main.getStorage(table_name.toString()).executeCreateTable(table_name, getVirtualSensorConfiguration().getOutputStructure(), false,connection);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(),e);
			logger.error("Initialization of the Stream Exporter VS failed !");
			return false;
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			logger.error("Initialization of the Stream Exporter VS failed !");
			return false;
		}catch (GSNRuntimeException e) {
			logger.error(e.getMessage(),e);
			logger.error("Initialization failed. There is a table called " + TABLE_NAME+ " Inside the database but the structure is not compatible with what GSN expects.");
			return false;
		}
		return true;
	}

	public void dataAvailable ( String inputStreamName , StreamElement streamElement ) {
		StringBuilder query = Main.getStorage(table_name.toString()).getStatementInsert(table_name, getVirtualSensorConfiguration().getOutputStructure());
		
		try {
			Main.getStorage(table_name.toString()).executeInsert(table_name ,getVirtualSensorConfiguration().getOutputStructure(),streamElement,getConnection() );
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			logger.error("Insertion failed! ("+ query+")");
		}finally {
			dataProduced( streamElement );
		}
		
	}


	public Connection getConnection() throws SQLException {
		if (this.connection==null || this.connection.isClosed())
			this.connection=DriverManager.getConnection(url,user,password);
		return connection;
	}


	public void dispose ( ) {
	}
}
