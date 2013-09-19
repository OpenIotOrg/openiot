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
