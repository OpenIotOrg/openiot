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

package org.openiot.ui.request.definition.web.model.nodes.impl.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import org.openiot.ui.request.commons.annotations.Endpoint;
import org.openiot.ui.request.commons.annotations.Endpoints;
import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEventListener;
import org.openiot.ui.request.definition.web.model.nodes.impl.sources.GenericSource;
import org.openiot.ui.request.definition.web.util.EndpointListLabelOrderComparator;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "Group", type = "FILTER", scanProperties = true)
@Endpoints({ @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "sensor_Number sensor_Integer sensor_Long sensor_Double sensor_Float", label = "ATTRIBUTES", maxConnections = -1, required = true) })
@NodeProperties({ @NodeProperty(type = PropertyType.Writable, javaType = java.util.ArrayList.class, name = "GROUPS", required = true) })
public class Group extends DefaultGraphNode implements GraphNodeEventListener, Serializable, Observer {
	private static final long serialVersionUID = 1L;

	public Group() {
		super();

		addPropertyChangeObserver(this);
	}

	protected void removeAllGroups() {
		Iterator<GraphNodeEndpoint> endpointIt = this.getEndpointDefinitions().iterator();
		while (endpointIt.hasNext()) {
			GraphNodeEndpoint endpoint = endpointIt.next();

			if (endpoint.getLabel().startsWith("grp_")) {
				disconnectEndpoint(endpoint);
				endpointIt.remove();
			} else if (endpoint.getScope().startsWith("sensor_")) {
				disconnectEndpoint(endpoint);
				endpoint.setScope(endpoint.getScope().replace("sensor_", ""));
			}
		}
	}

	protected void addGroups(List<String> srcEndpointLabels) {
		// If an empty list is passed, remove all groups
		if (srcEndpointLabels == null || srcEndpointLabels.isEmpty()) {
			removeAllGroups();
			return;
		}

		// Operate on a copy of the input list
		List<String> newEndpointLabels = new ArrayList<String>(srcEndpointLabels);
		List<String> goneEndpointLabels = new ArrayList<String>();

		Iterator<GraphNodeEndpoint> endpointIt = this.getEndpointDefinitions().iterator();
		while (endpointIt.hasNext()) {
			GraphNodeEndpoint endpoint = endpointIt.next();

			// Ignore non group endpoints
			if (!endpoint.getLabel().contains("recordTime")) {
				continue;
			}

			String label = endpoint.getLabel().replace("grp_", "");
			if (newEndpointLabels.contains(label)) {
				newEndpointLabels.remove(label);
			} else {
				goneEndpointLabels.add(endpoint.getLabel());
			}
		}

		// Remove gone endpoints
		for (String goneLabel : goneEndpointLabels) {
			GraphNodeEndpoint ep = getEndpointByLabel(goneLabel);
			disconnectEndpoint(ep);
			getEndpointDefinitions().remove(ep);
		}

		// And new end point for each new group
		for (String srcEndpointLabel : newEndpointLabels) {

			GraphNodeEndpoint dst = new DefaultGraphNodeEndpoint();
			dst.setType(EndpointType.Output);
			dst.setAnchor(AnchorType.Right);
			dst.setConnectorType(ConnectorType.Dot);
			dst.setScope("grp_Date");
			dst.setLabel("grp_" + srcEndpointLabel);
			dst.setRequired(false);
			dst.setMaxConnections(-1);
			getEndpointDefinitions().add(0, dst);
		}

		// Finally sort all endpoints using the input list label indices
		Collections.sort(getEndpointDefinitions(), new EndpointListLabelOrderComparator(srcEndpointLabels));
	}

	@SuppressWarnings("unchecked")
	public void update(Observable o, Object modifiedKey) {

		if ((modifiedKey != null) && ("GROUPS".equals((String) modifiedKey))) {
			addGroups((List<String>) getPropertyValueMap().get("GROUPS"));
		}
	}

	public void onNodeConnected(GraphModel model, GraphNode otherNode, GraphNodeEndpoint otherNodeEndpoint, GraphNodeEndpoint thisNodeEndpoint) {
		if(!"ATTRIBUTES".equals(thisNodeEndpoint.getLabel())){
			return;
		}
		
		GraphNodeEndpoint dst = new DefaultGraphNodeEndpoint();
		dst.setType(EndpointType.Output);
		dst.setAnchor(AnchorType.Right);
		dst.setConnectorType(ConnectorType.Rectangle);
		dst.setScope("grp_" + otherNodeEndpoint.getScope().replace("sensor_",  ""));
		dst.setLabel("grp_" + otherNodeEndpoint.getLabel());
		dst.setRequired(true);
		getEndpointDefinitions().add(dst);
	}

	public void onNodeDisconnected(GraphModel model, GraphNode otherNode, GraphNodeEndpoint otherNodeEndpoint, GraphNodeEndpoint thisNodeEndpoint) {
		if(!"ATTRIBUTES".equals(thisNodeEndpoint.getLabel())){
			return;
		}
		
		// Remove output endpoint
		String label = "grp_" + otherNodeEndpoint.getLabel();
		GraphNodeEndpoint ep = this.getEndpointByLabel(label);
		if( ep != null ){
			getEndpointDefinitions().remove(ep);
		}
	}

	public void onNodeDeleted(GraphModel model) {
	}
}
