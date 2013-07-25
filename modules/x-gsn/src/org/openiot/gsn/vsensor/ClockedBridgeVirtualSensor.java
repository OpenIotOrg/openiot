package org.openiot.gsn.vsensor;

import org.openiot.gsn.Main;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.storage.DataEnumerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

import javax.swing.Timer;

import org.apache.log4j.Logger;

public class ClockedBridgeVirtualSensor extends AbstractVirtualSensor implements ActionListener {

	private static final String RATE_PARAM       = "rate";
	private static final String TABLE_NAME_PARAM = "table_name";

	private Timer timer;
	private int clock_rate;
	private String table_name;
	private long last_updated;

	private static final transient Logger logger = Logger.getLogger( ClockedBridgeVirtualSensor.class );

	public boolean initialize ( ) {

		TreeMap<String, String> params = getVirtualSensorConfiguration().getMainClassInitialParams();

		String rate_value = params.get(RATE_PARAM);

		if (rate_value == null){
			logger.warn("Parameter \""+ RATE_PARAM +"\" not provider in Virtual Sensor file");
			return false;
		}

		clock_rate = Integer.parseInt(rate_value);

		String table_name_value = params.get(TABLE_NAME_PARAM);

		if (table_name_value == null){
			logger.warn("Parameter \""+ TABLE_NAME_PARAM +"\" not provider in Virtual Sensor file");
			return false;
		}

		table_name = table_name_value;

		timer = new Timer( clock_rate , this );

		timer.start( );

		last_updated = -1 ; // reading the whole table, this value can be overriden, if some tuples were already read 

		/*******************************************/
		// select latest update time of VS output table
		String output_table_name = getVirtualSensorConfiguration().getName();
		logger.warn("OUTPUT TABLE NAME: "+ output_table_name);

		StringBuilder query = new StringBuilder("select max(timed) from "+output_table_name);
		logger.warn("select max(timed) from "+output_table_name);

		Connection connection = null;

		try {
			connection = Main.getStorage(output_table_name).getConnection();
			ResultSet rs = Main.getStorage(output_table_name).executeQueryWithResultSet(query, connection);
			if (rs.next()) {
				Long i = rs.getLong(1); // get result from first column (1)
				logger.warn("LAST UPDATE: "+ Long.toString(i));
				last_updated = i;                          // override initial value -1
			} 
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		} finally {
			Main.getStorage(output_table_name).close(connection);
		}


		/*******************************************/

		return true;
	}

	public void dataAvailable ( String inputStreamName , StreamElement data ) {
		dataProduced( data );
		if ( logger.isDebugEnabled( ) ) logger.debug( "Data received under the name: " + inputStreamName );
	}

	public void dispose ( ) {
		timer.stop( );

	}

	public void actionPerformed ( ActionEvent actionEvent ) {

		// check if new data is available since last update then call dataProduced(StreamElement se)
		StringBuilder query = new StringBuilder("select * from "+table_name+" where timed > "+last_updated+" order by timed asc");

		try {
			DataEnumerator data = Main.getStorage(table_name).executeQuery(query,true);
			while (data.hasMoreElements()){
				StreamElement se = data.nextElement();
				last_updated = se.getTimeStamp();
				dataProduced(se);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
	}
}

