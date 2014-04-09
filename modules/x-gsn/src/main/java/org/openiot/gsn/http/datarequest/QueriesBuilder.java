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

import org.openiot.gsn.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class QueriesBuilder {
	
	private static transient Logger 	logger 						= Logger.getLogger(QueriesBuilder.class);
	
	/* Mandatory Parameters */
	public static final String 			PARAM_VSNAMES_AND_FIELDS 	= "vsname";

	/* Optional Parameters */
	public static final String 			PARAM_AGGREGATE_CRITERIA	= "groupby";
	public static final String 			PARAM_STANDARD_CRITERIA		= "critfield";
	public static final String 			PARAM_MAX_NB				= "nb";
	public static final String			PARAM_TIME_FORMAT			= "timeformat";

	/* Parsed Parameters */
	private HashMap<String, FieldsCollection> 	vsnamesAndStreams 			= null;
	private AggregationCriterion 				aggregationCriterion 		= null;
	private ArrayList<StandardCriterion> 		standardCriteria 			= null;
	private LimitCriterion						limitCriterion				= null;

	private Hashtable<String, AbstractQuery> sqlQueries ;

	protected Map<String, String[]> requestParameters;

	protected SimpleDateFormat sdf = new SimpleDateFormat (Main.getContainerConfig().getTimeFormat());
	
	private static Hashtable<String, String> allowedTimeFormats = null;
	
	static {
		allowedTimeFormats = new Hashtable<String, String> () ;
		allowedTimeFormats.put("iso","yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		allowedTimeFormats.put("unix", "unix");
	}
	
	public QueriesBuilder (Map<String, String[]> requestParameters) throws DataRequestException {
		this.requestParameters = requestParameters ;
		parseParameters();
		buildSQLQueries () ;
	}
	
	private void parseParameters () throws DataRequestException {

		String[] vsnamesParameters = requestParameters.get(PARAM_VSNAMES_AND_FIELDS);

		if (vsnamesParameters == null) throw new DataRequestException ("You must specify at least one >" + PARAM_VSNAMES_AND_FIELDS + "< parameter.") ; 

		vsnamesAndStreams = new HashMap<String, FieldsCollection> () ;
		String name;
		String[] streams;
		for (int i = 0 ; i < vsnamesParameters.length ; i++) {
			int firstColumnIndex = vsnamesParameters[i].indexOf(':');
			if (firstColumnIndex == -1) {
				name = vsnamesParameters[i];
				streams = new String[0];
			}
			else {
				name = vsnamesParameters[i].substring(0, firstColumnIndex);
				streams = vsnamesParameters[i].substring(firstColumnIndex + 1).split(":");
			}

			vsnamesAndStreams.put(name, new FieldsCollection (streams));
		}

		String ac = getParameter(requestParameters, PARAM_AGGREGATE_CRITERIA);
		if (ac != null) { aggregationCriterion = new AggregationCriterion (ac) ; }

		String[] cc = requestParameters.get(PARAM_STANDARD_CRITERIA);
		if (cc != null) {
			standardCriteria = new ArrayList<StandardCriterion> ();
			for (int i = 0 ; i < cc.length ; i++) {
				standardCriteria.add(new StandardCriterion (cc[i]));
			}
		}

		String lm = getParameter(requestParameters, PARAM_MAX_NB);
		if (lm != null) limitCriterion = new LimitCriterion (lm);
		
		String timeformat = getParameter(requestParameters, PARAM_TIME_FORMAT);
		if (timeformat != null && allowedTimeFormats.containsKey(timeformat)) {
			String format = allowedTimeFormats.get(timeformat);
			sdf = format.compareToIgnoreCase("unix") == 0 ? null : new SimpleDateFormat(format) ;
		}
	}

	public Hashtable<String, AbstractQuery> getSqlQueries() {
		return sqlQueries;
	}

	private void buildSQLQueries () {

		this.sqlQueries = new Hashtable<String, AbstractQuery> () ;

		// Fields and Virtual Sensors
		Iterator<Entry<String, FieldsCollection>> iter 	= vsnamesAndStreams.entrySet().iterator();
		Entry<String, FieldsCollection> next;
		String[] fields;
		String vsname;
		while (iter.hasNext()) {
			next = iter.next();
			fields = next.getValue().getFields();
			vsname = next.getKey();
			this.sqlQueries.put(vsname, new AbstractQuery(limitCriterion, aggregationCriterion, vsname, fields, standardCriteria));
		}
	}

	public AggregationCriterion getAggregationCriterion() {
		return aggregationCriterion;
	}

	public ArrayList<StandardCriterion> getStandardCriteria() {
		return standardCriteria;
	}

	public HashMap<String, FieldsCollection> getVsnamesAndStreams() {
		return vsnamesAndStreams;
	}

	public LimitCriterion getLimitCriterion() {
		return limitCriterion;
	}



	/**
	 * Returns the value of a request parameter as a <code>String</code>, or <code>null</code> if the parameter does not exist.
	 */
	protected static String getParameter (Map<String, String[]> parameters, String requestedParameter) {
		String[] rpv = parameters.get(requestedParameter);
		return (rpv == null || rpv.length == 0) ? null : rpv[0];
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}


}
