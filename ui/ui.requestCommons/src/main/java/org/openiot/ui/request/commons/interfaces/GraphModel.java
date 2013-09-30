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

package org.openiot.ui.request.commons.interfaces;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.models.GraphNodePosition;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public interface GraphModel {

    public String getUID();

    public void setUID(String UID);
    
	public List<GraphNode> getNodes();

    public List<GraphNodeConnection> getConnections();

    public void setNodes(List<GraphNode> nodes);

    public void setConnections(List<GraphNodeConnection> connections);

    public GraphNode getSelectedNode();

    public void setSelectedNode(GraphNode selectedNode);

    public void clear();

    public void insert(GraphNode node, double x, double y);

    public void remove(GraphNode node);

    public void connect(String connectionId, GraphNode sourceNode, GraphNodeEndpoint sourceEndpoint, GraphNode destinationNode, GraphNodeEndpoint destinationEndpoint);

    public void disconnect(GraphNodeConnection connection);

    public List<GraphNodeConnection> findGraphEndpointConnections(GraphNodeEndpoint endpoint);
    
    public GraphNodeConnection lookupGraphNodeConnection(String connectionUID);

    public GraphNode lookupGraphNode(String nodeUID);

    public GraphNodeEndpoint lookupGraphEndpoint(GraphNode node, String endpointUID);

    public GraphNodePosition lookupGraphNodePosition(String nodeUID);

    public void updatePosition(GraphNode graphNode, double newX, double newY);
    
    public JSONObject toJSON();
    
    public void importJSON(JSONObject spec) throws JSONException;
}
