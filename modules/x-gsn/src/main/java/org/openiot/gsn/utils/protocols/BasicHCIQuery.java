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

package org.openiot.gsn.utils.protocols;

import java.util.Vector;

public class BasicHCIQuery extends AbstractHCIQueryWithoutAnswer {

	public static final String DEFAULT_NAME="CUSTOM_QUERY";
	public static final String DESCRIPTION="A custom raw query: you enter bytes as a parameters and it sends bytes to the controller.";
	public static final String[] PARAMS_DESCRIPTION= {"Bytes to send to the controller."};
	/**
	 * You can change the default texts here.
	 */
	public BasicHCIQuery(String Name, String queryDescription, String[] paramsDescriptions) {
		super(Name, queryDescription, paramsDescriptions);

	}

	public BasicHCIQuery() {
		super(DEFAULT_NAME, DESCRIPTION, PARAMS_DESCRIPTION);
	}
	/* (non-Javadoc)
	 * @see org.openiot.gsn.utils.protocols.AbstractHCIQuery#buildRawQuery(java.util.Vector)
	 */
	@Override
	public byte[] buildRawQuery(Vector<Object> params) {
		byte[] rawQuery = null;
		if(params != null && params.firstElement() != null) {
			rawQuery = params.firstElement().toString().getBytes();
		} 
		return rawQuery; 
	}

}
