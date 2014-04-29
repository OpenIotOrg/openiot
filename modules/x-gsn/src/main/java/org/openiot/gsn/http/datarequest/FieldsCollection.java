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

/**
 * This class stores a list of Fields for a Virtual Sensor. It adds by default
 * the <code>timed</code> field if missing and keep track if the
 * <code>timed</code> was needed or not.
 */
public class FieldsCollection {

	private boolean wantTimed;
	private String[] fields;

	public FieldsCollection(String[] _fields) {

		wantTimed = false;
		for (int j = 0; j < _fields.length; j++) {
			if (_fields[j].compareToIgnoreCase("timed") == 0)
				wantTimed = true;
		}
		String[] tmp = _fields;
		if (!wantTimed) {
			tmp = new String[_fields.length + 1];
			System.arraycopy(_fields, 0, tmp, 0, _fields.length);
			tmp[tmp.length - 1] = "timed";
		}
		this.fields = tmp;
	}

	public boolean isWantTimed() {
		return wantTimed;
	}

	public String[] getFields() {
		return fields;
	}
}