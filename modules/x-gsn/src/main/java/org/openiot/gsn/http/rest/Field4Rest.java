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

package org.openiot.gsn.http.rest;

import java.io.Serializable;

public class Field4Rest {
	private String name;
	private Serializable value;
	private Byte type;

	public Field4Rest(String name, Byte type, Serializable value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Serializable getValue() {
		return value;
	}

	public byte getType() {
		return type;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Field(name:").append(name).append(",").append("type:").append(type).append(",value:").append(value).append(")");
		return sb.toString();
	}
	
}
