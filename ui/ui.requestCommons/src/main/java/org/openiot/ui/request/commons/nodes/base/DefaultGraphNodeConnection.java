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

package org.openiot.ui.request.commons.nodes.base;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class DefaultGraphNodeConnection implements GraphNodeConnection, Serializable {
	private static final long serialVersionUID = 1L;

    private String UID = "graphNodeConnection_" + System.nanoTime();
    private GraphNode sourceNode;
    private GraphNode destinationNode;
    private GraphNodeEndpoint sourceEndpoint;
    private GraphNodeEndpoint destinationEndpoint;

    public DefaultGraphNodeConnection() {
    }

    public DefaultGraphNodeConnection(GraphNode sourceNode, GraphNodeEndpoint sourceEndpoint, GraphNode destinationNode, GraphNodeEndpoint destinationEndpoint) {
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.sourceEndpoint = sourceEndpoint;
        this.destinationEndpoint = destinationEndpoint;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public GraphNode getSourceNode() {
        return sourceNode;
    }

    public GraphNodeEndpoint getSourceEndpoint() {
        return sourceEndpoint;
    }

    public GraphNode getDestinationNode() {
        return destinationNode;
    }

    public GraphNodeEndpoint getDestinationEndpoint() {
        return destinationEndpoint;
    }

    public void setSourceNode(GraphNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public void setDestinationNode(GraphNode destinationNode) {
        this.destinationNode = destinationNode;
    }

    public void setSourceEndpoint(GraphNodeEndpoint sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }

    public void setDestinationEndpoint(GraphNodeEndpoint destinationEndpoint) {
        this.destinationEndpoint = destinationEndpoint;
    }

    public JSONObject toJSON(){
    	JSONObject spec = new JSONObject();
    	try{
    		spec.put("class", this.getClass().getCanonicalName());
    		spec.put("uid", this.getUID());
    		spec.put("srcNode", sourceNode != null ? sourceNode.getUID() : null);
    		spec.put("srcEndpoint", sourceEndpoint != null ? sourceEndpoint.getUID() : null);
    		spec.put("dstNode", destinationNode != null ? destinationNode.getUID() : null);
    		spec.put("dstEndpoint", destinationEndpoint != null ? destinationEndpoint.getUID() : null);
    	
    	}catch(JSONException ex){
    		LoggerService.log(ex);
    	}
    	
    	return spec;
    }

    public void importJSON(JSONObject spec) throws JSONException{
    	setUID(spec.getString("uid"));
    }
    
}
