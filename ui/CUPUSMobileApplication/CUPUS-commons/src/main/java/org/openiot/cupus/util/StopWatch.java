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

/**
 * Class for timing stuff...
 * 
 * Uses System.nanoTime for precision, but returnes values in miliseconds.
 * 
 * @author Eugen
 */
public class StopWatch {

	protected long ini, end;

	/**
	 * Constrcucts and sets the stopwatch to current time.
	 */
	public StopWatch() {
		reset();
	}

	/**
	 * Sets the stopwatch to current time
	 */
	public void reset() {
		ini = end = System.nanoTime();
	}

	/**
	 * Marks the end of measuring with the current time
	 */
	public void stop() {
		end = System.nanoTime();
	}

	/**
	 * Gets the difference between last reset and last stop, in miliseconds.
	 */
	public long getMeasure() {
		return (end - ini) / 1000000;
	}

	/**
	 * Calls stop() then getMeasure()
	 */
	public long stopAndGet() {
		stop();
		return getMeasure();
	}

	/**
	 * Call stop and then returnes the measure as a nice string
	 */
	public String stopAndShow() {
		stop();
		return toString();
	}

	@Override
	public String toString() {
		return usToString(getMeasure());
	}

	private String usToString(long us) {
		long totalSecs = us / 1000000;
		int hours = (int) (totalSecs / 3600);
		int mins = (int) (totalSecs / 60) % 60;
		int secs = (int) (totalSecs % 60);
		int ms = (int) (us % 1000000) / 1000;
		us = us % 1000;

		StringBuilder out = new StringBuilder();
		if (hours > 0) {
			out.append(hours).append(" hour ");
		}
		if (mins > 0) {
			out.append(mins).append(" min ");
		}
		if (secs > 0) {
			out.append(secs).append(" sec ");
		}
		if (ms > 0) {
			out.append(ms).append(" ms ");
		}
		if (us > 0) {
			out.append(us).append(" us ");
		}
		return out.toString().trim();
	}

}
