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
    
	public String getLabel();

	public void setLabel(String label);
	
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
