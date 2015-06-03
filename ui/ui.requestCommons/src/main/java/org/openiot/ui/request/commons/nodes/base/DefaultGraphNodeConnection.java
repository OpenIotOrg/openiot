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
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class DefaultGraphNodeConnection implements GraphNodeConnection, Serializable {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGraphNodeConnection.class);
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

	@Override
    public String getUID() {
        return UID;
    }

	@Override
    public void setUID(String UID) {
        this.UID = UID;
    }

	@Override
    public GraphNode getSourceNode() {
        return sourceNode;
    }

	@Override
    public GraphNodeEndpoint getSourceEndpoint() {
        return sourceEndpoint;
    }

	@Override
    public GraphNode getDestinationNode() {
        return destinationNode;
    }

	@Override
    public GraphNodeEndpoint getDestinationEndpoint() {
        return destinationEndpoint;
    }

	@Override
    public void setSourceNode(GraphNode sourceNode) {
        this.sourceNode = sourceNode;
    }

	@Override
    public void setDestinationNode(GraphNode destinationNode) {
        this.destinationNode = destinationNode;
    }

	@Override
    public void setSourceEndpoint(GraphNodeEndpoint sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }

	@Override
    public void setDestinationEndpoint(GraphNodeEndpoint destinationEndpoint) {
        this.destinationEndpoint = destinationEndpoint;
    }

	@Override
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
    		LOGGER.error("", ex);
    	}

    	return spec;
    }

	@Override
    public void importJSON(JSONObject spec) throws JSONException{
    	setUID(spec.getString("uid"));
    }

}
