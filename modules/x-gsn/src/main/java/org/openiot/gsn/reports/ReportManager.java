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
 * @author Timotee Maret
 * @author Ali Salehi
*/

package org.openiot.gsn.reports;

import org.openiot.gsn.reports.beans.Data;
import org.openiot.gsn.reports.beans.Report;
import org.openiot.gsn.reports.beans.Stream;
import org.openiot.gsn.reports.beans.VirtualSensor;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;

public class ReportManager {	
	
	public static transient Logger logger= Logger.getLogger ( ReportManager.class );
	
	public static void generatePdfReport (Collection<Report> reports, String jasperFile, HashMap<String, String> params, OutputStream os) {
		try {
			JasperPrint print = generate (reports, jasperFile, params) ;
			JasperExportManager.exportReportToPdfStream(print, os);				
		} catch (JRException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static byte[] generatePdfReport (Collection<Report> reports, String jasperFile, HashMap<String, String> params) {
		byte[] report = null;
		try {
			JasperPrint print = generate (reports, jasperFile, params) ;
			report = JasperExportManager.exportReportToPdf(print);				
		} catch (JRException e) {
			logger.error(e.getMessage(), e);
		}
		return report;
	}
	
	public static void generatePdfReport (Collection<Report> reports, String jasperFile, HashMap<String, String> params, String PDFoutputPath) {
		try {
			JasperPrint print = generate (reports, jasperFile, params) ;
			JasperExportManager.exportReportToPdfFile(print, PDFoutputPath);				
		} catch (JRException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private static JasperPrint generate (Collection<Report> reports, String jasperFile, HashMap<String, String> params) {
		JasperPrint print = null;
		JRBeanCollectionDataSource source = new JRBeanCollectionDataSource (reports) ;
		try {
			JasperReport report = (JasperReport) JRLoader.loadObjectFromLocation(jasperFile);
			print = JasperFillManager.fillReport(report, params, source);
		} catch (JRException e) {
			logger.error(e.getMessage(), e);
		}
		return print;
	}

	public static byte[] generateSampleReport () {
		
		// Datas
		long now = System.currentTimeMillis();			
		long twohours = 1000 * 60 * 60 * 2;

		Collection<Data> dat1 = new ArrayList<Data> ();
		dat1.add(new Data(1, (now + twohours),100.0,"label 1"));
		dat1.add(new Data(1, (now + 2 * twohours),90.0,"label 1"));
		dat1.add(new Data(1, (now + 9 * twohours),95.0,"label 1"));
		
		Collection<Data> dat2 = new ArrayList<Data> ();
		dat2.add(new Data(1, (now + twohours),110.0,"label 1"));
		
		Collection<Data> dat3 = new ArrayList<Data> ();
		
		Date lastUpdate = new Date ();
		
		// Streams
		Collection<Stream> fields_v1 = new ArrayList<Stream> ();
		fields_v1.add(new Stream("Temperature [°C]", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Humidity [%]", lastUpdate.toString(), dat2));
		fields_v1.add(new Stream("Temp", lastUpdate.toString(), dat3));
		fields_v1.add(new Stream("Solar", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Altitude", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Length 1", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Length 2", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Length 3", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Length 4", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Length 5", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Length 6", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Length 7", lastUpdate.toString(), dat1));
		fields_v1.add(new Stream("Length 8", lastUpdate.toString(), dat1));
		
		Collection<Stream> fields_v2 = new ArrayList<Stream> ();
		fields_v2.add(new Stream("Stream 1", lastUpdate.toString(), dat2));
		fields_v2.add(new Stream("Stream 2", lastUpdate.toString(), dat1));
		
		// Virtual Sensors
		Collection<VirtualSensor> virtualSensors = new ArrayList<VirtualSensor> ();
		virtualSensors.add(new VirtualSensor("Virtual Sensor One","0.0","0.0", fields_v1));
		virtualSensors.add(new VirtualSensor("Virtual Sensor Two","0.0","0.0", fields_v2));
		
		// Report
		Collection<Report> reports = new ArrayList<Report> ();
		reports.add(new Report("gsn-reports/report-default.jasper", new Date().toString(), null, null, null, virtualSensors));

		// Build the source
		
		ReportManager.generatePdfReport(reports, "gsn-reports/report-default.jasper", new HashMap<String, String>(), "sample-report.pdf");
		byte[] rep = ReportManager.generatePdfReport(reports, "gsn-reports/report-default.jasper", new HashMap<String, String>());
		return rep;
	}
	
	public static void main (String[] args) {
		generateSampleReport();
		System.out.println("done");
	}
}
