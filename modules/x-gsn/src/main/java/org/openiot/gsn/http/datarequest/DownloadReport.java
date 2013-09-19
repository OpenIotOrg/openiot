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

package org.openiot.gsn.http.datarequest;

import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.reports.ReportManager;
import org.openiot.gsn.reports.beans.Data;
import org.openiot.gsn.reports.beans.Report;
import org.openiot.gsn.reports.beans.Stream;
import org.openiot.gsn.reports.beans.VirtualSensor;

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class DownloadReport extends AbstractDataRequest {

	private static final String PARAM_REPORTCLASS 	= "reportclass";

	private static final int[] ALLOWED_REPORT_FIELDS_TYPES = new int[]{ Types.BIGINT, Types.DOUBLE, Types.FLOAT, Types.INTEGER, Types.NUMERIC, Types.REAL, Types.SMALLINT, Types.TINYINT};

	private static transient Logger logger = Logger.getLogger(DownloadReport.class);
	
	private Collection<Report> reports;
	
	private String reportPath;
	
	public DownloadReport(Map<String, String[]> requestParameters) throws DataRequestException {
		super(requestParameters);
	}
	
	@Override
	public void process () throws DataRequestException {
		if (QueriesBuilder.getParameter(requestParameters, PARAM_REPORTCLASS) == null) throw new DataRequestException ("The following >" + PARAM_REPORTCLASS + "< parameter is missing in your query.") ;
		String reportClass = QueriesBuilder.getParameter(requestParameters, PARAM_REPORTCLASS);
		reportPath = "gsn-reports/" + reportClass + ".jasper";
		File f = new File (reportPath) ;
		if (f == null || ! f.exists() || ! f.isFile()) throw new DataRequestException ("The path to compiled jasper file >" + reportPath + "< is not valid.") ;
		//
		reports = new ArrayList<Report> ();
		reports.add(createReport ());
	}
	
	@Override
	public void outputResult (OutputStream os) {
		ReportManager.generatePdfReport(reports, reportPath, new HashMap<String, String> (), os);
	}
	
	public byte[] outputResult () {
		return ReportManager.generatePdfReport(reports, reportPath, new HashMap<String, String> ());
	}
	
	private Report createReport () {
		Collection<VirtualSensor> virtualSensors = new ArrayList<VirtualSensor> () ;
		// create all the virtual sensors for the report
		Iterator<Entry<String, FieldsCollection>> iter = qbuilder.getVsnamesAndStreams().entrySet().iterator();
		Entry<String, FieldsCollection> vsNameAndStream;
		VirtualSensor virtualSensor;
		while (iter.hasNext()) {
			vsNameAndStream = iter.next();
			virtualSensor = createVirtualSensor(vsNameAndStream.getKey(), vsNameAndStream.getValue().getFields());
			if (virtualSensor != null) virtualSensors.add(virtualSensor);
		}
		//		
		String aggregationCrierion 	= qbuilder.getAggregationCriterion() 	== null		? "None" 		: qbuilder.getAggregationCriterion().toString();
		String standardCriteria 	= qbuilder.getStandardCriteria() 		== null		? "None" 		: qbuilder.getStandardCriteria().toString();
		String maxNumber			= qbuilder.getLimitCriterion()			== null		? "All"			: qbuilder.getLimitCriterion().getSize().toString();
		
		return new Report (reportPath, (qbuilder.getSdf() == null ? "UNIX: " + new Date().getTime() : qbuilder.getSdf().format(new Date())), aggregationCrierion, standardCriteria,maxNumber, virtualSensors);
	}

	private VirtualSensor createVirtualSensor (String vsname, String[] vsstream) {
		Collection<Stream> streams = null;
		// create all the streams for this Virtual Sensor
		Connection connection = null;
		
		try {
			// Get the last update for this Virtual Sensor (In GSN, all the Virtual Sensor streams are inserted in the same record)
			String configFileName = Mappings.getVSensorConfig(vsname).getFileName();
			long last = Mappings.getLastModifiedTime(configFileName);
			String lastModified = (qbuilder.getSdf() == null ? "UNIX: " + last : qbuilder.getSdf().format(new Date(last)));

			// Create the streams
			connection = Main.getStorage(vsname).getConnection();
			ResultSet rs = Main.getStorage(vsname).executeQueryWithResultSet(qbuilder.getSqlQueries().get(vsname), connection);
			ResultSetMetaData rsmd = rs.getMetaData();

			Hashtable<String, Stream> dataStreams = new Hashtable<String, Stream> () ;
			FieldsCollection streamNames = qbuilder.getVsnamesAndStreams().get(vsname);
			for (int i = 0 ; i < streamNames.getFields().length ; i++) {
				if (streamNames.getFields()[i].compareToIgnoreCase("timed") != 0 || streamNames.isWantTimed()) {
					dataStreams.put(streamNames.getFields()[i], new Stream (vsstream[i], lastModified, new ArrayList<Data> ()));
				}
			}

			while (rs.next()) {
				Stream astream;
				Integer columnInResultSet;
				for (int i = 0 ; i < vsstream.length ; i++) {
					columnInResultSet = getColumnId (rsmd, vsstream[i]) ;
					if (columnInResultSet != null) {
						if (isAllowedReportType(rsmd.getColumnType(columnInResultSet))) {
							astream = dataStreams.get(vsstream[i]);
							if (astream != null) {
								if (rs.getObject(vsstream[i]) != null) {
									astream.getDatas().add(new Data ("only",rs.getLong("timed"), rs.getDouble(vsstream[i]), "label"));
								}
								else {
									astream.getDatas().add(new Data ("only",rs.getLong("timed"), null, "label"));
								}
							}
						}
						else logger.debug("Column type >" + rsmd.getColumnType(columnInResultSet) + "< is not allowed for report.");
					}
					else logger.debug("Column >" + vsstream[i] + "< not found in the ResultSetMetaData");
				}
			}
			streams = dataStreams.values();
		}
		catch (SQLException e) {
			logger.error("Error while executing the SQL request. Check your query.");
			logger.debug("The query: ",e);
			return null;
		}finally{
			Main.getStorage(vsname).close(connection);
		}
		//
		boolean mappedVirtualSensor = (Mappings.getVSensorConfig(vsname) != null);

		String latitude = "NA";
		if (mappedVirtualSensor && Mappings.getVSensorConfig(vsname).getLatitude() != null) latitude =  Mappings.getVSensorConfig(vsname).getLatitude().toString();
		String longitude = "NA";
		if (mappedVirtualSensor && Mappings.getVSensorConfig(vsname).getLongitude() != null) longitude = Mappings.getVSensorConfig(vsname).getLongitude().toString(); 		
		return new VirtualSensor (
				vsname,
				latitude,
				longitude,
				streams) ;
	}

	private static boolean isAllowedReportType (int type) {
		for (int i = 0 ; i < ALLOWED_REPORT_FIELDS_TYPES.length ; i++) {
			if (type == ALLOWED_REPORT_FIELDS_TYPES[i]) return true;
		}
		return false;
	}

	private static Integer getColumnId (ResultSetMetaData rsmd, String columnname) {
		try {
			for (int i = 1 ; i <= rsmd.getColumnCount() ; i++) {
				if (rsmd.getColumnLabel(i).compareToIgnoreCase(columnname) == 0) return i;
			}
		}
		catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} 
		return null;
	}

}
