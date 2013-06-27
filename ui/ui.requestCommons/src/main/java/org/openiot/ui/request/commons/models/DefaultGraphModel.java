/*******************************************************************************
 * Copyright (c) 2011-2014, OpenIoT
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it either under the terms of the GNU Lesser General Public
 *  License version 2.1 as published by the Free Software Foundation
 *  (the "LGPL"). If you do not alter this
 *  notice, a recipient may use your version of this file under the LGPL.
 *  
 *  You should have received a copy of the LGPL along with this library
 *  in the file COPYING-LGPL-2.1; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 *  This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 *  OF ANY KIND, either express or implied. See the LGPL  for
 *  the specific language governing rights and limitations.
 *  
 *  Contact: OpenIoT mailto: info@openiot.eu
 ******************************************************************************/
package org.openiot.ui.request.commons.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeConnection;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEventListener;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class DefaultGraphModel implements GraphModel, Serializable {
	private static final long serialVersionUID = 1L;

    private GraphNode selectedNode;
    private List<GraphNode> nodes;
    private List<GraphNodeConnection> connections;
    private Map<String, GraphNodePosition> positions;

    public DefaultGraphModel() {
        this.nodes = new ArrayList<GraphNode>();
        this.connections = new ArrayList<GraphNodeConnection>();
        this.positions = new HashMap<String, GraphNodePosition>();
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public List<GraphNodeConnection> getConnections() {
        return connections;
    }

    public void setNodes(List<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public void setConnections(List<GraphNodeConnection> connections) {
        this.connections = connections;
    }

    public GraphNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(GraphNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void clear() {
        selectedNode = null;
        nodes.clear();
        connections.clear();
        positions.clear();
    }

    public void insert(GraphNode node, double x, double y) {
        nodes.add(node);

        GraphNodePosition position = new GraphNodePosition(x, y);
        positions.put(node.getUID(), position);
    }

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

                LoggerService.log(Level.FINE, "[DefaultGraphModel] Deleted connection connection [" + connection.getSourceNode().getUID() + ":" + connection.getSourceEndpoint().getUID() + "] -> [" + connection.getDestinationNode().getUID() + ":" + connection.getDestinationEndpoint().getUID() + "] [connection id " + connection.getUID() + "]");
            }
        }

        nodes.remove(node);
        positions.remove(node.getUID());

        // Inform node about deletion
        if (node instanceof GraphNodeEventListener) {
            ((GraphNodeEventListener) node).onNodeDeleted(this);
        }
        LoggerService.log(Level.FINE, "[DefaultGraphModel] Deleted node [" + node.getUID() + "]");
    }

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

        LoggerService.log(Level.FINE, "[DefaultGraphModel] Established connection [" + connection.getSourceNode().getUID() + ":" + connection.getSourceEndpoint().getUID() + "] -> [" + connection.getDestinationNode().getUID() + ":" + connection.getDestinationEndpoint().getUID() + "] [connection id " + connection.getUID() + "]");
    }

    public void disconnect(GraphNodeConnection connection) {
        connections.remove(connection);

        // Notify nodes about the disconnect
        if (connection.getSourceNode() instanceof GraphNodeEventListener) {
            ((GraphNodeEventListener) connection.getSourceNode()).onNodeDisconnected(this, connection.getDestinationNode(), connection.getDestinationEndpoint(), connection.getSourceEndpoint());
        }
        if (connection.getDestinationNode() instanceof GraphNodeEventListener) {
            ((GraphNodeEventListener) connection.getDestinationNode()).onNodeDisconnected(this, connection.getSourceNode(), connection.getSourceEndpoint(), connection.getDestinationEndpoint());
        }

        LoggerService.log(Level.FINE, "[DefaultGraphModel] Deleted connection connection [" + connection.getSourceNode().getUID() + ":" + connection.getSourceEndpoint().getUID() + "] -> [" + connection.getDestinationNode().getUID() + ":" + connection.getDestinationEndpoint().getUID() + "] [connection id " + connection.getUID() + "]");
    }

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

    public GraphNodeConnection lookupGraphNodeConnection(String connectionUID) {
        for (GraphNodeConnection connection : this.connections) {
            if (connection.getUID().equals(connectionUID)) {
                return connection;
            }
        }
        return null;
    }

    public GraphNode lookupGraphNode(String nodeUID) {
        for (GraphNode node : nodes) {
            if (node.getUID().equals(nodeUID)) {
                return node;
            }
        }
        return null;
    }

    public GraphNodePosition lookupGraphNodePosition(String nodeUID) {
        return positions.get(nodeUID);
    }

    public GraphNodeEndpoint lookupGraphEndpoint(GraphNode node, String endpointUID) {
        for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
            if (endpoint.getUID().equals(endpointUID)) {
                return endpoint;
            }
        }
        return null;
    }

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
		try{
			spec.put("exported",  (new Date()).getTime());
			
			// Encode each node
			JSONArray nodes = new JSONArray();
			for( GraphNode node : getNodes() ){
				nodes.put( node.toJSON() );
			}
			spec.put("nodes", nodes);

			// Encode node positions
			JSONObject positions = new JSONObject();
			for( Map.Entry<String, GraphNodePosition> entry : this.positions.entrySet() ){
				positions.put(entry.getKey(), entry.getValue().toJSON());
			}
			spec.put("positions", nodes);
			
			// Encode node connections
			JSONArray connections = new JSONArray();
			for( GraphNodeConnection connection : this.connections ){
				connections.put( connection.toJSON() );
			}
			spec.put("connections", nodes);
			
		}catch(JSONException ex){
			LoggerService.log(ex);
		}
		return spec;
	}

	@Override
	public void fromJSON(JSONObject spec) {
		// TODO Auto-generated method stub
		
	}
}
