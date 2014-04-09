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
*/

package org.openiot.gsn.http.datarequest;

import java.util.Hashtable;

public class AbstractCriterion {
	
	protected static final String GENERAL_ERROR_MSG 	= "Failed to create the Criteria";
	protected static final String CRITERION_ERROR_MSG 	= "Invalid Criterion";
	
	public String getCriterion (String criterion, Hashtable<String, String> allowedValues) throws DataRequestException {
		if (allowedValues.containsKey(criterion.toLowerCase())) {
			return allowedValues.get(criterion.toLowerCase());
		}
		else throw new DataRequestException (CRITERION_ERROR_MSG + " >" + criterion + "<. Valid values are >" + allowedValues.keySet().toString() + "<") ;
	}
}
