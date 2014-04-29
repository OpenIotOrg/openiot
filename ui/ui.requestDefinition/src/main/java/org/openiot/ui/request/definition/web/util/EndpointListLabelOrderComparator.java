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
package org.openiot.ui.request.definition.web.util;

import java.util.Comparator;
import java.util.List;

import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;

public class EndpointListLabelOrderComparator implements Comparator<GraphNodeEndpoint> {

	private List<String> indexList;

	public EndpointListLabelOrderComparator(List<String> indexList) {
		this.indexList = indexList;
	}

	@Override
	public int compare(GraphNodeEndpoint o1, GraphNodeEndpoint o2) {
		int lIndex = indexList.indexOf(o1.getLabel());
		int rIndex = indexList.indexOf(o2.getLabel());

		if (lIndex < rIndex) {
			return -1;
		} else if (lIndex > rIndex) {
			return 1;
		}
		return 0;
	}
}
