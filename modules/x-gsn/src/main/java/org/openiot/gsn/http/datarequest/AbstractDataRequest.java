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
 * @author Timotee Maret
*/

package org.openiot.gsn.http.datarequest;

import java.io.OutputStream;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * <p>
 * This class provides a generic and fine grained way to select data for a set of Virtual Sensors.
 * For each of the specified Virtual Sensors it creates a SQL query that can be directly executed to
 * access the data.
 * </p>
 * <p>
 * For each Virtual Sensor, the Fields can be selected. Moreover, The three following types of filters 
 * can be added to the queries. Notice that these filters are the same for all the generated queries.
 * </p>
 * 
 * <ul>
 * <li><strong>MAX NUMBER OF RESULTS</strong> This option limits the number of returned values to a maximal value.</li>
 * <li><strong>STANDARD CRITERIA</strong> Almost the SQL tests can be added to the SQL queries.</li>
 * <li><strong>AGGREGATION CRITERION</strong> A SQL grouping function can be added to the queries.</li>
 * </ul>
 * 
 * <h3>Examples</h3>
 * <ul>
 * <li><strong>Minimal Parameters:</strong> <code>?vsname=ss_mem_vs:heap_memory_usage</code><br /> This request return a SQL query that select all the <code>heap_memory_usage</code> values from the <code>ss_me_vs</code> Virtual Sensor</li>
 * <li>
 *      <strong>Typical Parameters:</strong> <code>?vsname=tramm_meadows_vs:toppvwc_1:toppvwc_3&vsname=ss_mem_vs:heap_memory_usage&nb=0:5&critfield=and:::timed:ge:1201600800000&critfield=and:::timed:le:1211678800000&groupby=10000000:min</code><br />
 *      This request returns two SQL queries, one for <code>tramm_meadows_vs</code> and one for <code>ss_mem_vs</code> Virtual Sensor. The number of elements returned is limited to 5, are associated to a timestamp between <code>1201600800000</code> and <code>1211678800000</code>.
 *      The elements returned are the minimals values grouped by the timed field divided by <code>10000000</code>.
 * </li>
 * </ul>
 * 
 * <ul>
 * <li>Notice that by the <code>timed</code> Field is associated to all the Virtual Sensors elements returned.</li>
 * <li>Notice that this class doesn't check if the Virtual Sensors and Fields names are corrects.</li>
 * </ul>
 */
public abstract class AbstractDataRequest {

	private static transient Logger 	logger 						= Logger.getLogger(AbstractDataRequest.class);

	protected QueriesBuilder qbuilder = null;
	
	protected Map<String, String[]> requestParameters = null;

	public AbstractDataRequest (Map<String, String[]> requestParameters) throws DataRequestException {
		this.requestParameters = requestParameters;
		qbuilder = new QueriesBuilder(requestParameters);
	}

    public QueriesBuilder getQueryBuilder() {
        return qbuilder;
    }

	public abstract void process() throws DataRequestException ;

	public abstract void outputResult (OutputStream os) ;

//	public InputStream getInputStream(int bufferSize) {
//		DataRequestReaderTarget drrt = new DataRequestReaderTarget(bufferSize);
//		(new Thread(drrt)).start();
//		return drrt.getInputStream();
//	}
	
/*	private class DataRequestReaderTarget implements Runnable {

		private OutputInputStream ois = null;

		public DataRequestReaderTarget(int bufferSize) {
			ois = new OutputInputStream (bufferSize) ;
		}
		
		public void run() {
			try {
				outputResult(ois.getOutputStream());
				ois.getOutputStream().close();
			} catch (IOException e) {
				logger.debug(e.getMessage());
			}
		}
		
		public InputStream getInputStream () {
			return ois.getInputStream();
		}
	}*/
}
