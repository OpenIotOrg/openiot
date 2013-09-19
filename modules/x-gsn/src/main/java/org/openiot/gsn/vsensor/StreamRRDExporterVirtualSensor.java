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

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class StreamRRDExporterVirtualSensor extends AbstractVirtualSensor {
	public static final String            PARAM_RRDFILE    = "rrdfile" ;

	public static final String            PARAM_FIELD    = "field" ;

	private static final transient Logger logger        = Logger.getLogger(StreamRRDExporterVirtualSensor.class );

	private String rrdfile = null;

	private Vector<String> fields = new Vector<String>();

	public boolean initialize ( ) {
		VSensorConfig vsensor = getVirtualSensorConfiguration( );
		TreeMap < String , String > params = vsensor.getMainClassInitialParams( );
		Set<Entry<String, String>> entrySet = params.entrySet();
		Iterator it = entrySet.iterator();
		while(it.hasNext()){
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if(key.equals(PARAM_RRDFILE))
				this.rrdfile=value;
			else if (key.equals(PARAM_FIELD))
				this.fields.add(value);
		}
		if ( rrdfile == null) {
			logger.debug("Initialization Parameter "+PARAM_RRDFILE+" is missing!" );
			return false;
		}
		logger.debug( "rrdfile=" + this.rrdfile);
		if (ensureFileExistence(rrdfile)) return true;
		else return createRRDFile();
	}

	private boolean createRRDFile() {
		String command = "rrdtool create "+rrdfile+" --step 300 ";
		for(int i=0;i<this.fields.size();i++){
			command = command + "DS:field"+i+":GAUGE:600:0:U ";
		}
		command = command + "RRA:AVERAGE:0.5:1:600 ";
		command = command + "RRA:AVERAGE:0.5:6:700 ";
		command = command + "RRA:AVERAGE:0.5:24:775 ";
		command = command + "RRA:AVERAGE:0.5:288:797 ";
		command = command + "RRA:MAX:0.5:1:600 ";
		command = command + "RRA:MAX:0.5:6:700 ";
		command = command + "RRA:MAX:0.5:24:775";
		command = command + "RRA:MAX:0.5:288:797";
		Runtime runtime = Runtime.getRuntime();
		try {
			if(logger.isDebugEnabled())
				logger.debug( "The used rrdtool create command is: " + command);                        
			Process process = runtime.exec(command);
			if(logger.isDebugEnabled())
				logger.debug( "The exit value of the rrdtool create command is: " +
						process.exitValue());
			return true;
		} catch (IOException e) {
			logger.debug("An IOException has occured: "+e);
			return false;
		} 
	}


	public void dataAvailable ( String inputStreamName , StreamElement streamElement ) {
		ensureFileExistence( );
		exportValues( streamElement );
	}


	/*
     returns true if the requested file exists.
	 * @param filename The file name to check for.
	 */
	private boolean ensureFileExistence ( ) {
		return ensureFileExistence(this.rrdfile);
	}

	/*
     returns true if the requested file exists.
	 * @param filename The file name to check for.
	 */
	private boolean ensureFileExistence ( String filename ) {
		File file = new File(rrdfile);
		if (file.exists()) return true;
		else {
			logger.error("rrdfile "+rrdfile+" does not exist!");
			return false;
		}
	}


	/*
	 * Export all received values from a stream to the proposed table name into
	 * the database selected by the currently open connection.
	 */

	private void exportValues ( StreamElement streamElement ) {
		if(logger.isDebugEnabled())
			logger.debug( "Trying to add new data items to the rrdfile:" + this.rrdfile );
		String command ="rrdtool update "+rrdfile+" N";
		Serializable[] stream = streamElement.getData();
		String field;
		for(int i=0;i<stream.length;i++){
			field = stream[i].toString();
			// if the field is empty we have to add an U for unknown to the rrdfile
			if(field==null || field.equals("")) field = "U";
			command = command+":"+field;
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			if(logger.isDebugEnabled())
				logger.debug( "The used rrdtool update command is: " + command);                        
			Process process = runtime.exec(command);
			if(logger.isDebugEnabled())
				logger.debug( "The processing did not generate an error!");                        
		} catch (IOException e) {
			logger.debug("An IOException has occured: "+e);
		} 
	}

	public void dispose ( ) {
	}
}
