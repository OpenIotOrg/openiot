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

import org.openiot.gsn.utils.Helpers;

import java.util.Hashtable;

import javax.servlet.ServletException;

public class AggregationCriterion extends AbstractCriterion {

	private static Hashtable<String, String> allowedGroupOperator = null;

	static {
		allowedGroupOperator = new Hashtable<String, String> () ;
		allowedGroupOperator.put("max", "max");
		allowedGroupOperator.put("min","min");
		allowedGroupOperator.put("avg", "avg");
	}

	private String critTimeRange 		= null;
	private String critGroupOperator 	= null;

	/**
	 * <p>
	 * Create a new Aggregation Criteria from a serialized Aggregation description.
	 * The description must follow the syntax:<br />
	 * <code><timerange>:<groupoperator></code>
	 * </p>
	 * @param inlinecrits
	 * @throws ServletException
	 */
	public AggregationCriterion (String inlinecrits) throws DataRequestException {

		String[] crits = inlinecrits.split(":");

		if (crits.length != 2) throw new DataRequestException (GENERAL_ERROR_MSG + " >" + inlinecrits + "<.") ;

		critTimeRange		= crits[0];
		critGroupOperator	= getCriterion(crits[1], allowedGroupOperator);
	}
	
	public String toString () {
		return "Select: " + critGroupOperator.toUpperCase() + ", group by: timed/" + critTimeRange + " (" + Helpers.formatTimePeriod(Long.parseLong(critTimeRange)) + ")";
	}

	public String getTimeRange()     { return critTimeRange; }
	public String getGroupOperator() { return critGroupOperator; }
}