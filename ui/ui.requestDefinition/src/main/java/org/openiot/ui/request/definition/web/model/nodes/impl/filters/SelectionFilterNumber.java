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

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "SelectionFilterNumber", type = "FILTER", scanProperties = true)
@Endpoints({ @Endpoint(type = EndpointType.Output, connectorType = ConnectorType.Dot, anchorType = AnchorType.Right, scope = "Sensor", label = "NODE", required = true), })
public class SelectionFilterNumber extends DefaultGraphNode implements GraphNodeEventListener, Serializable {
	private static final long serialVersionUID = 1L;

	public SelectionFilterNumber() {
		super();
	}

	public void onNodeConnected(GraphModel model, GraphNode otherNode, GraphNodeEndpoint otherNodeEndpoint, GraphNodeEndpoint thisNodeEndpoint) {
		if (otherNode instanceof GenericSource) {
			// Mutate our endpoints by copying the OUTPUT endpoints of the
			// connected sensor node
			List<GraphNodeEndpoint> ourEndpoints = getEndpointDefinitions();
			GraphNodeEndpoint ourInput = ourEndpoints.get(0);
			ourEndpoints.clear();
			ourEndpoints.add(ourInput);

			// Add additional endpoint for attribute recordTime
			GraphNodeEndpoint endpoint = new DefaultGraphNodeEndpoint();
			endpoint.setAnchor(AnchorType.Right);
			endpoint.setConnectorType(ConnectorType.Rectangle);
			endpoint.setMaxConnections(-1);
			endpoint.setRequired(false);
			endpoint.setType(EndpointType.Output);
			endpoint.setLabel("Compare Value");
			endpoint.setUserData(null);
			endpoint.setScope("cmp_sensor_value");
			ourEndpoints.add(endpoint);
			
			// Copy all output endpoints
			/*
			for (GraphNodeEndpoint ep : otherNode.getEndpointDefinitions()) {
				if (ep.getType().equals(EndpointType.Output)) {
					// Copy endpoint and mutate its scope so that it can only
					// be connected to a comparator node. Also limit the number
					// of outgoing connections to 1
					GraphNodeEndpoint copy = ep.getCopy();
					copy.setScope("cmp_" + ep.getScope());
					copy.setMaxConnections(1);
					ourEndpoints.add(copy);
				}
			}*/
		}
	}

	public void onNodeDisconnected(GraphModel model, GraphNode otherNode, GraphNodeEndpoint otherNodeEndpoint, GraphNodeEndpoint thisNodeEndpoint) {
		if (otherNode instanceof GenericSource) {

			// Remove groups from original node
			((GenericSource) (otherNode)).removeAllGroups();

			// Delete all endpoint connections and mutate back to original state
			List<GraphNodeConnection> deletedConnections = new ArrayList<GraphNodeConnection>();
			for (GraphNodeEndpoint ep : this.getEndpointDefinitions()) {
				for (GraphNodeConnection connection : model.getConnections()) {
					if (connection.getSourceEndpoint().getUID().equals(ep.getUID())) {
						deletedConnections.add(connection);
					}
				}
			}

			for (GraphNodeConnection connection : deletedConnections) {
				model.disconnect(connection);
			}

			List<GraphNodeEndpoint> ourEndpoints = getEndpointDefinitions();
			GraphNodeEndpoint ourInput = ourEndpoints.get(0);
			ourEndpoints.clear();
			ourEndpoints.add(ourInput);
		}
	}

	public void onNodeDeleted(GraphModel model) {
	}
}
