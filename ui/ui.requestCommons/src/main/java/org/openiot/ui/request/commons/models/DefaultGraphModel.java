/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
package org.openiot.ui.request.commons.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.factory.GraphFactory;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeConnection;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class DefaultGraphModel implements GraphModel, Serializable {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGraphModel.class);
	private static final long serialVersionUID = 1L;

	private String UID = "graph_" + System.nanoTime();
	private GraphNode selectedNode;
	private List<GraphNode> nodes;
	private List<GraphNodeConnection> connections;
	private Map<String, GraphNodePosition> positions;

	public DefaultGraphModel() {
		this.nodes = new ArrayList<GraphNode>();
		this.connections = new ArrayList<GraphNodeConnection>();
		this.positions = new HashMap<String, GraphNodePosition>();
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public void setUID(String UID) {
		this.UID = UID;
	}

	@Override
	public List<GraphNode> getNodes() {
		return nodes;
	}

	@Override
	public List<GraphNodeConnection> getConnections() {
		return connections;
	}

	@Override
	public void setNodes(List<GraphNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void setConnections(List<GraphNodeConnection> connections) {
		this.connections = connections;
	}

	@Override
	public GraphNode getSelectedNode() {
		return selectedNode;
	}

	@Override
	public void setSelectedNode(GraphNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	@Override
	public void clear() {
		selectedNode = null;
		nodes.clear();
		connections.clear();
		positions.clear();
	}

	@Override
	public void insert(GraphNode node, double x, double y) {
		node.setGraphModel(this);
		nodes.add(node);

		GraphNodePosition position = new GraphNodePosition(x, y);
		positions.put(node.getUID(), position);
	}

	@Override
	public void remove(GraphNode node) {
		// Remove all connections that point to deleted node
		Iterator<GraphNodeConnection> connectionIt = connections.iterator();
		while (connectionIt.hasNext()) {
			GraphNodeConnection connection = connectionIt.next();
			if (node.getUID().equals(connection.getSourceNode().getUID())
					|| node.getUID().equals(connection.getDestinationNode().getUID())) {
				connectionIt.remove();

				// Notify nodes about the disconnect
				if (connection.getSourceNode() instanceof GraphNodeEventListener) {
					((GraphNodeEventListener) connection.getSourceNode()).onNodeDisconnected(this, connection.getDestinationNode(), connection.getDestinationEndpoint(), connection.getSourceEndpoint());
				}
				if (connection.getDestinationNode() instanceof GraphNodeEventListener) {
					((GraphNodeEventListener) connection.getDestinationNode()).onNodeDisconnected(this, connection.getSourceNode(), connection.getSourceEndpoint(), connection.getDestinationEndpoint());
				}

				LOGGER.trace("Deleted connection connection [" + connection.getSourceNode().getUID() + ":" + connection.getSourceEndpoint().getUID() + "] -> [" + connection.getDestinationNode().getUID() + ":" + connection.getDestinationEndpoint().getUID() + "] [connection id " + connection.getUID() + "]");
			}
		}

		nodes.remove(node);
		positions.remove(node.getUID());

		// Inform node about deletion
		if (node instanceof GraphNodeEventListener) {
			((GraphNodeEventListener) node).onNodeDeleted(this);
		}
		LOGGER.trace("[DefaultGraphModel] Deleted node [{}]", node.getUID());
	}

	@Override
	public void connect(String connectionId, GraphNode sourceNode, GraphNodeEndpoint sourceEndpoint, GraphNode destinationNode, GraphNodeEndpoint destinationEndpoint) {
		GraphNodeConnection connection = new DefaultGraphNodeConnection(sourceNode, sourceEndpoint, destinationNode, destinationEndpoint);
		if (connectionId != null) {
			connection.setUID(connectionId);
		}
		connections.add(connection);

		// Notify nodes about the new connection
		if (connection.getSourceNode() instanceof GraphNodeEventListener) {
			((GraphNodeEventListener) connection.getSourceNode()).onNodeConnected(this, connection.getDestinationNode(), connection.getDestinationEndpoint(), connection.getSourceEndpoint());
		}
		if (connection.getDestinationNode() instanceof GraphNodeEventListener) {
			((GraphNodeEventListener) connection.getDestinationNode()).onNodeConnected(this, connection.getSourceNode(), connection.getSourceEndpoint(), connection.getDestinationEndpoint());
		}

		LOGGER.trace("[DefaultGraphModel] Established connection [" + connection.getSourceNode().getUID() + ":" + connection.getSourceEndpoint().getUID() + "] -> [" + connection.getDestinationNode().getUID() + ":" + connection.getDestinationEndpoint().getUID() + "] [connection id " + connection.getUID() + "]");
	}

	@Override
	public void disconnect(GraphNodeConnection connection) {
		connections.remove(connection);

		// Notify nodes about the disconnect
		if (connection.getSourceNode() instanceof GraphNodeEventListener) {
			((GraphNodeEventListener) connection.getSourceNode()).onNodeDisconnected(this, connection.getDestinationNode(), connection.getDestinationEndpoint(), connection.getSourceEndpoint());
		}
		if (connection.getDestinationNode() instanceof GraphNodeEventListener) {
			((GraphNodeEventListener) connection.getDestinationNode()).onNodeDisconnected(this, connection.getSourceNode(), connection.getSourceEndpoint(), connection.getDestinationEndpoint());
		}

		LOGGER.trace("[DefaultGraphModel] Deleted connection connection [" + connection.getSourceNode().getUID() + ":" + connection.getSourceEndpoint().getUID() + "] -> [" + connection.getDestinationNode().getUID() + ":" + connection.getDestinationEndpoint().getUID() + "] [connection id " + connection.getUID() + "]");
	}

	@Override
	public List<GraphNodeConnection> findGraphEndpointConnections(GraphNodeEndpoint endpoint) {
		List<GraphNodeConnection> matchingConnections = new ArrayList<GraphNodeConnection>();
		if (endpoint.getType().equals(EndpointType.Input)) {
			for (GraphNodeConnection connection : this.connections) {
				if (connection.getDestinationEndpoint().getUID().equals(endpoint.getUID())) {
					matchingConnections.add(connection);
				}
			}
		} else {
			for (GraphNodeConnection connection : this.connections) {
				if (connection.getSourceEndpoint().getUID().equals(endpoint.getUID())) {
					matchingConnections.add(connection);
				}
			}
		}

		return matchingConnections;
	}

	@Override
	public GraphNodeConnection lookupGraphNodeConnection(String connectionUID) {
		for (GraphNodeConnection connection : this.connections) {
			if (connection.getUID().equals(connectionUID)) {
				return connection;
			}
		}
		return null;
	}

	@Override
	public GraphNode lookupGraphNode(String nodeUID) {
		for (GraphNode node : nodes) {
			if (node.getUID().equals(nodeUID)) {
				return node;
			}
		}
		return null;
	}

	@Override
	public GraphNodePosition lookupGraphNodePosition(String nodeUID) {
		return positions.get(nodeUID);
	}

	@Override
	public GraphNodeEndpoint lookupGraphEndpoint(GraphNode node, String endpointUID) {
		for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
			if (endpoint.getUID().equals(endpointUID)) {
				return endpoint;
			}
		}
		return null;
	}

	@Override
	public void updatePosition(GraphNode graphNode, double newX, double newY) {
		GraphNodePosition position = positions.get(graphNode.getUID());
		if (position == null) {
			position = new GraphNodePosition(newX, newY);
			positions.put(graphNode.getUID(), position);
		}
		position.setX(newX);
		position.setY(newY);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject spec = new JSONObject();
		try {
			spec.put("class", this.getClass().getCanonicalName());
			spec.put("uid", getUID());

			// Encode each node
			JSONArray nodes = new JSONArray();
			for (GraphNode node : getNodes()) {
				nodes.put(node.toJSON());
			}
			spec.put("nodes", nodes);

			// Encode node positions
			JSONObject positions = new JSONObject();
			for (Map.Entry<String, GraphNodePosition> entry : this.positions.entrySet()) {
				positions.put(entry.getKey(), entry.getValue().toJSON());
			}
			spec.put("positions", positions);

			// Encode node connections
			JSONArray connections = new JSONArray();
			for (GraphNodeConnection connection : this.connections) {
				connections.put(connection.toJSON());
			}
			spec.put("connections", connections);

		} catch (JSONException ex) {
			LOGGER.error("", ex);
		}
		return spec;
	}

	@Override
	public void importJSON(JSONObject spec) throws JSONException {
		setUID(spec.getString("uid"));

		// Parse nodes
		JSONArray nodes = spec.getJSONArray("nodes");
		this.nodes.clear();
		for (int index = 0; index < nodes.length(); index++) {
			GraphNode node = GraphFactory.createGraphNode(nodes.getJSONObject(index));
			node.setGraphModel(this);
			this.nodes.add(node);
		}

		// Parse node positions
		JSONObject positions = spec.getJSONObject("positions");
		Iterator<?> keyIt = positions.keys();
		this.positions.clear();
		while (keyIt.hasNext()) {
			String nodeId = (String) keyIt.next();
			GraphNodePosition pos = new GraphNodePosition(0, 0);
			pos.importJSON(positions.getJSONObject(nodeId));
			this.positions.put(nodeId, pos);
		}

		// Parse node connections
		JSONArray connections = spec.getJSONArray("connections");
		this.connections.clear();
		for (int index = 0; index < connections.length(); index++) {
			JSONObject conSpec = connections.getJSONObject(index);
			GraphNodeConnection connection = GraphFactory.createGraphNodeConnection(conSpec);

			// Lookup connection nodes
			GraphNode srcNode = this.lookupGraphNode(conSpec.getString("srcNode"));
			GraphNodeEndpoint srcEndpoint = this.lookupGraphEndpoint(srcNode, conSpec.getString("srcEndpoint"));
			GraphNode dstNode = this.lookupGraphNode(conSpec.getString("dstNode"));
			GraphNodeEndpoint dstEndpoint = this.lookupGraphEndpoint(dstNode, conSpec.getString("dstEndpoint"));

			// Setup connection
			connection.setSourceNode(srcNode);
			connection.setSourceEndpoint(srcEndpoint);
			connection.setDestinationNode(dstNode);
			connection.setDestinationEndpoint(dstEndpoint);
			this.connections.add(connection);
		}
	}
}
