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
package org.openiot.ui.request.definition.web.sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Select extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;

	public Select() {
		super();
	}

	@Override
	public String generate() {
		String pad = generatePad(getDepth());
		String out = pad + "SELECT DISTINCT " + StringUtils.join(generateChildren(), " ").replaceAll("\\s+", " ");
		return out;
	}

}
