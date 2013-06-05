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
package org.openiot.ui.requestdefinition.nodes.impl.vizualizers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.openiot.ui.requestdefinition.annotations.Endpoint;
import org.openiot.ui.requestdefinition.annotations.Endpoints;
import org.openiot.ui.requestdefinition.annotations.GraphNodeClass;
import org.openiot.ui.requestdefinition.annotations.NodeProperties;
import org.openiot.ui.requestdefinition.annotations.NodeProperty;
import org.openiot.ui.requestdefinition.interfaces.GraphModel;
import org.openiot.ui.requestdefinition.nodes.base.DefaultGraphNode;
import org.openiot.ui.requestdefinition.nodes.enums.AnchorType;
import org.openiot.ui.requestdefinition.nodes.enums.EndpointType;
import org.openiot.ui.requestdefinition.nodes.enums.PropertyType;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNode;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEventListener;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "LineChart1", type = "VISUALIZER", scanProperties = true)
@Endpoints({
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "Number", label = "x", required = true),
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "Number", label = "y", required = true),})
@NodeProperties({
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "X_AXIS_TYPE", required = true, allowedValues = {"Date", "Number"}),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "X_AXIS_LABEL", required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "Y_AXIS_LABEL", required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "SERIES1_LABEL", required = true),})
public class LineChart1 extends DefaultGraphNode implements Serializable, Observer, GraphNodeEventListener {
	private static final long serialVersionUID = 1L;

    private GraphModel model;

    public LineChart1() {
        super();

        // Setup some defaults
        setProperty("X_AXIS_TYPE", "Number");
        setProperty("X_AXIS_LABEL", "x axis");
        setProperty("Y_AXIS_LABEL", "y axis");
        setProperty("SERIES1_LABEL", "series1");
        
        addPropertyChangeObserver(this);
    }

    public void update(Observable o, Object arg) {
        // Mutate our label
        Map<String, Object> propertyMap = getPropertyValueMap();
        if (propertyMap.get("X_AXIS_TYPE") != null) {
            // Update 'x' endpoint scope
            for (GraphNodeEndpoint endpoint : getEndpointDefinitions()) {
                if (!endpoint.getLabel().equals("x")) {
                    continue;
                }

                endpoint.setScope((String) propertyMap.get("X_AXIS_TYPE"));

                // If we have a connection of different scope, disconnect it
                if( model != null ){
                    List<GraphNodeConnection> connections = model.findGraphEndpointConnections(endpoint);
                    if (!connections.isEmpty()) {
                        GraphNodeConnection connection = connections.get(0);
                        if (!connection.getSourceEndpoint().getScope().equals(endpoint.getScope())) {
                            model.disconnect(connection);
                        }
                    }
                }
                break;
            }
        }
    }

    public void onNodeConnected(GraphModel model, GraphNode otherNode, GraphNodeEndpoint otherNodeEndpoint, GraphNodeEndpoint thisNodeEndpoint) {
        this.model = model;
    }

    public void onNodeDisconnected(GraphModel model, GraphNode otherNode, GraphNodeEndpoint otherNodeEndpoint, GraphNodeEndpoint thisNodeEndpoint) {
    }

    public void onNodeDeleted(GraphModel model) {
    }
}
