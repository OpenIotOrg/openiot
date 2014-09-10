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

package org.openiot.cupus.common;

import java.util.List;

/**
 * A class representing a string attribute. It is consisted of a list of
 * possible values and a name of the attribute. The step is 1 by definition.
 * 
 * All strings should be lowercase!!!
 * 
 * @author Eugen
 * 
 */
public class StringAttribute implements Attribute {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<String> values;

	public StringAttribute(String name, List<String> values) {
		this.name = name;
		this.values = values;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getSize() {
		return values.size();
	}

	@Override
	public double getStep() {
		return 1;
	}

	/**
	 * Returns the list of possible values. SHOULD NOT BE CHANGED!!! All
	 * elements should be lowercase!
	 */
	public List<String> getValues() {
		return values;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name + ": ");
		for (String s : values) {
			sb.append(s + ",");
		}
		return sb.toString();
	}

}
