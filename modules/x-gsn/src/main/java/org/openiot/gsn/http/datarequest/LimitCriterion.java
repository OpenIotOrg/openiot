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

public class LimitCriterion extends AbstractCriterion{
	
	private Integer offset,size	;
	
	/**
	 * <p>
	 * Create a new Limit Criterion from a serialized Criterion description.
	 * The description must follow the syntax:<br />
	 * <code><offset>:<size></code>.
	 * </p>
	 * @param inlinecrits
	 * @return
	 */
	public LimitCriterion (String inlinecrits) throws DataRequestException {
		
		String[] crits = inlinecrits.split(":");

		if (crits.length != 2) throw new DataRequestException (GENERAL_ERROR_MSG + " >" + inlinecrits + "<.") ;

		offset	= Integer.parseInt(crits[0]);
		size	= Integer.parseInt(crits[1]);
	}

    public LimitCriterion() {}
	
	public Integer getOffset() {
		return offset;
	}

    public void setOffset(int offset) {
        this.offset = offset;
    }
	
	public Integer getSize() {
		return size;
	}

    public void setSize(int size) {
        this.size = size;
    }
	
	public String toString () {
		return "size: " + size + " offset: " + offset;
	}
}
