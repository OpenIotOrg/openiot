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
package org.openiot.ui.requestdefinition.nodes.impl.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.openiot.ui.requestdefinition.annotations.Endpoint;
import org.openiot.ui.requestdefinition.annotations.Endpoints;
import org.openiot.ui.requestdefinition.annotations.GraphNodeClass;
import org.openiot.ui.requestdefinition.interfaces.GraphModel;
import org.openiot.ui.requestdefinition.nodes.base.DefaultGraphNode;
import org.openiot.ui.requestdefinition.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.requestdefinition.nodes.enums.AnchorType;
import org.openiot.ui.requestdefinition.nodes.enums.ConnectorType;
import org.openiot.ui.requestdefinition.nodes.enums.EndpointType;
import org.openiot.ui.requestdefinition.nodes.impl.sensors.GenericSensor;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNode;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEventListener;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "SelectionFilter", type = "FILTER", scanProperties = true)
@Endpoints({
    @Endpoint(type = EndpointType.Output, anchorType = AnchorType.Left, scope = "Sensor", label = "NODE", required = true),})
public class SelectionFilter extends DefaultGraphNode implements GraphNodeEventListener, Serializable {
	private static final long serialVersionUID = 1L;

    public SelectionFilter() {
        super();
    }

    public void onNodeConnected(GraphModel model, GraphNode otherNode, GraphNodeEndpoint otherNodeEndpoint, GraphNodeEndpoint thisNodeEndpoint) {
    	if (otherNode instanceof GenericSensor) {
            // Mutate our endpoints by copying the OUTPUT endpoints of the connected sensor node
            List<GraphNodeEndpoint> ourEndpoints = getEndpointDefinitions();
            GraphNodeEndpoint ourInput = ourEndpoints.get(0);
            ourEndpoints.clear();
            ourEndpoints.add(ourInput);

            // Copy only output endpoints
            for (GraphNodeEndpoint ep : otherNode.getEndpointDefinitions()) {
                if (ep.getType().equals(EndpointType.Output)) {
                    // Copy endpoint and mutate its scope so that it can only
                    // be connected to a comparator node. Also limit the number of outgoing connections to 1
                    GraphNodeEndpoint copy = ep.getCopy();
                    copy.setScope("Compare." + ep.getScope());
                    copy.setMaxConnections(1);
                    ourEndpoints.add(copy);
                }
            }
            
			// Add an additional endpoint for the record timestamp filtering
			GraphNodeEndpoint endpoint = new DefaultGraphNodeEndpoint();
			endpoint.setAnchor(AnchorType.Bottom);
			endpoint.setConnectorType(ConnectorType.Rectangle);
			endpoint.setMaxConnections(1);
			endpoint.setRequired(false);
			endpoint.setType(EndpointType.Output);
			endpoint.setLabel("REC_TIMESTAMP");
			endpoint.setScope("Compare.Date");
			endpoint.setUserData("observationResultTime");
			ourEndpoints.add(endpoint);
        }
    }

    public void onNodeDisconnected(GraphModel model, GraphNode otherNode, GraphNodeEndpoint otherNodeEndpoint, GraphNodeEndpoint thisNodeEndpoint) {
        if (otherNode instanceof GenericSensor) {

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
