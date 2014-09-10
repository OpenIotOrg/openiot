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

package org.openiot.cupus.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A class that writes to a file if the logging flag is set and/or to the stdout
 * if the testing flag is set.
 * 
 * @author Eugen
 * 
 */
public class LogWriter {

	private BufferedWriter log;
	public static final String DATE_FORMAT_NOW = "dd.MM.yyyy HH:mm:ss";

	private boolean logging;
	private boolean testing;

	/**
	 * Constructor
	 * 
	 * @param filename
	 *            name of file where to write log.
	 */
	public LogWriter(String filename, boolean logging, boolean testing) {
		this.logging = logging;
		this.testing = testing;
		if (logging) {
			try {
				File folder = new File("log");
				folder.mkdir();
				log = new BufferedWriter(new FileWriter("log/" + filename));
				writeToLog("Log file: " + folder.getCanonicalPath()
						+ File.separator + filename);
			} catch (Exception e) {
				throw new RuntimeException("Faild to make the log file...", e);
			}
		}
	}

	/**
	 * Writes string to log.
	 * 
	 * @param text
	 *            Input text for writing
	 */
	public void writeToLog(String text, boolean forceToScreen) {
		if (logging) {
			try {
				log.write(now() + " " + text + "\n");
				log.flush();
			} catch (IOException e) {
				// logging error... so what xD
			}
		}
		if (testing || forceToScreen) {
			System.out.println(text);
		}
	}

	public void writeToLog(String text) {
		writeToLog(text, false);
	}

	/**
	 * Adds "ERROR: " in front of the given string and forces the output to
	 * screen [calls writeToLog("ERROR: "+error, true)]
	 * 
	 * @param error
	 *            Msn to print/log
	 */
	public void error(String error) {
		writeToLog("ERROR: " + error, true);
	}

	/**
	 * 
	 * This method returns current time in format set by DATE_FORMAT_NOW
	 */
	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	/**
	 * Closes log
	 */
	public void close() {
		try {
			log.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
