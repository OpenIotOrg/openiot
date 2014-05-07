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
 * @author Jerome Rousselot
 * @author Ali Salehi
*/

package org.openiot.gsn.utils.protocols.EPuck;

import org.openiot.gsn.utils.protocols.AbstractHCIQueryWithoutAnswer;

import java.util.Vector;

public class Reset extends AbstractHCIQueryWithoutAnswer {

	public static final String queryDescription = "Resets the state of the EPuck robot.";
	public static final String[] paramsDescriptions = null;
	public Reset (String name) {
		super(name, queryDescription, paramsDescriptions);
	}


	/*
	 * This query does not take any parameters.
	 * If you provide any, these will be ignored.
	 */
	public byte [ ] buildRawQuery ( Vector < Object > params ) {
		byte[] query = new byte[1];
		query[0] = 'r';
		return query;
	}
}
