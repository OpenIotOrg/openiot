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
package org.openiot.ui.requestdefinition.web.jsf.components.graph;

import java.io.IOException;
import java.util.ResourceBundle;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.openiot.ui.requestdefinition.nodes.enums.EndpointType;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNode;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.requestdefinition.interfaces.GraphModel;
import org.openiot.ui.requestdefinition.models.GraphNodePosition;
import org.openiot.ui.requestdefinition.web.util.FaceletLocalization;
import org.primefaces.renderkit.CoreRenderer;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class NodeGraphRenderer extends CoreRenderer {

    public static final int MIN_NODE_WIDTH = 150;
    public static final int MIN_NODE_HEIGHT = 100;
    public static final int PIXELS_PER_HORIZONTAL_ENDPOINT = 20;
    public static final int PIXELS_PER_VERTICAL_ENDPOINT = 40;

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(final FacesContext fc, final UIComponent component) throws IOException {
        encodeMarkup(fc, component);
        encodeScript(fc, component);
    }

    protected void encodeMarkup(final FacesContext fc, final UIComponent component) throws IOException {
        ResponseWriter writer = fc.getResponseWriter();
        NodeGraph nodeGraph = (NodeGraph) component;
        String clientId = nodeGraph.getClientId(fc);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", "graph-node-wrapper " + (nodeGraph.getStyleClass() != null ? nodeGraph.getStyleClass() : ""), null);
        {
            // Render model markup
            encodeGraphNodeMarkup(fc, component);
        }
        writer.endElement("div");
    }

    protected void encodeScript(final FacesContext fc, final UIComponent component) throws IOException {
        encodeGraphNodeScript(fc, component);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(final FacesContext fc, final UIComponent component) throws IOException {
        // nothing to do
    }

    //--------------------------------------------------------------------------
    // Implementation
    //--------------------------------------------------------------------------
    private void encodeGraphNodeMarkup(final FacesContext fc, final UIComponent component) throws IOException {
        NodeGraph nodeGraph = (NodeGraph) component;
        GraphModel model = nodeGraph.getModel();
        if (nodeGraph.getModel() == null) {
            return;
        }
        ResponseWriter writer = fc.getResponseWriter();
        ResourceBundle messages = nodeGraph.getTranslations();

        // Render a node for each graphNode
        for (GraphNode node : model.getNodes()) {
            GraphNodePosition position = model.lookupGraphNodePosition(node.getUID());

            // Count endpoints on each side
            int topEndpoints = 0;
            int rightEndpoints = 0;
            int bottomEndpoints = 0;
            int leftEndpoints = 0;
            for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {

                switch (endpoint.getAnchor()) {
                    case Top:
                        topEndpoints++;
                        break;
                    case Right:
                        rightEndpoints++;
                        break;
                    case Bottom:
                        bottomEndpoints++;
                        break;
                    case Left:
                        leftEndpoints++;
                        break;
                }
            }

            // Calculate node width and height and a starting position for each node type so they are aligned centered
            int nodeWidth = Math.max(MIN_NODE_WIDTH, Math.max(topEndpoints, bottomEndpoints) * PIXELS_PER_HORIZONTAL_ENDPOINT);
            int nodeHeight = Math.max(MIN_NODE_HEIGHT, Math.max(leftEndpoints, rightEndpoints) * PIXELS_PER_VERTICAL_ENDPOINT);

            writer.startElement("div", null);
            writer.writeAttribute("id", node.getUID(), null);
            writer.writeAttribute("style", "position:absolute;left:" + position.getX() + "px;top:" + position.getY() + "px;width:" + nodeWidth + "px;height:" + nodeHeight + "px;", null);
            writer.writeAttribute("class", "graph-node " + node.getType() + (model.getSelectedNode() != null && model.getSelectedNode().getUID().equals(node.getUID()) ? " graph-node-selected" : ""), null);
            {
                // Render label
                String label = node.getLabel();
                if (messages != null) {
                    label = FaceletLocalization.lookupLabelTranslation(messages, node.getLabel(), "UI_NODE_" + node.getLabel());
                }
                writer.startElement("div", null);
                writer.writeAttribute("class", "graph-node-label " + node.getType(), null);
                writer.write(label == null ? "-" : label);
                writer.endElement("div");
            }
            writer.endElement("div");
        }
    }

    private void encodeGraphNodeScript(final FacesContext fc, final UIComponent component) throws IOException {
        NodeGraph nodeGraph = (NodeGraph) component;
        GraphModel model = nodeGraph.getModel();
        if (nodeGraph.getModel() == null) {
            return;
        }
        String clientId = nodeGraph.getClientId(fc);
        ResponseWriter writer = fc.getResponseWriter();
        ResourceBundle messages = nodeGraph.getTranslations();

        startScript(writer, clientId);
        writer.write("$(function() {");
        {
            writer.write("Sensap.cw('NodeGraph','" + nodeGraph.resolveWidgetVar() + "',{");
            writer.write("id:'" + clientId + "',");
            if (model.getSelectedNode() != null) {
                writer.write("selectedNodeId: '" + model.getSelectedNode().getUID() + "',");
            }

            // Encode all endpoints
            writer.write("endpoints:[");
            for (GraphNode node : model.getNodes()) {
                // Count endpoints on each side
                int topEndpoints = 0;
                int rightEndpoints = 0;
                int bottomEndpoints = 0;
                int leftEndpoints = 0;
                for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {

                    switch (endpoint.getAnchor()) {
                        case Top:
                            topEndpoints++;
                            break;
                        case Right:
                            rightEndpoints++;
                            break;
                        case Bottom:
                            bottomEndpoints++;
                            break;
                        case Left:
                            leftEndpoints++;
                            break;
                    }
                }

                // Calculate node width and height and a starting position for each node type so they are aligned centered
                int nodeWidth = Math.max(MIN_NODE_WIDTH, Math.max(topEndpoints, bottomEndpoints) * PIXELS_PER_HORIZONTAL_ENDPOINT);
                int nodeHeight = Math.max(MIN_NODE_HEIGHT, Math.max(leftEndpoints, rightEndpoints) * PIXELS_PER_VERTICAL_ENDPOINT);
                double stepX = (double) PIXELS_PER_HORIZONTAL_ENDPOINT / nodeWidth;
                double stepY = (double) PIXELS_PER_VERTICAL_ENDPOINT / nodeHeight;
                double topPosition = 1.0 / (2.0 * (double) topEndpoints);
                double rightPosition = 1.0 / (2.0 * (double) rightEndpoints);
                double bottomPosition = 1.0 / (2.0 * (double) bottomEndpoints);
                double leftPosition = 1.0 / (2.0 * (double) leftEndpoints);

                for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
                    double labelX = 0;
                    double labelY = 0;
                    double anchorXPos = 0;
                    double anchorYPos = 0;
                    double anchorDX = 0;
                    double anchorDY = 0;
                    String label = endpoint.getLabel();
                    if (messages != null) {
                        label = FaceletLocalization.lookupLabelTranslation(messages, endpoint.getLabel(), "UI_NODE_ENDPOINT_" + node.getClass().getSimpleName() + "_" + endpoint.getLabel(), "UI_NODE_ENDPOINT_" + endpoint.getClass().getSimpleName());
                    }
                    switch (endpoint.getAnchor()) {
                        case Bottom:
                            anchorXPos = bottomPosition;
                            anchorYPos = 1.0;
                            bottomPosition += stepX;
                            anchorDX = 0;
                            anchorDY = 1;
                            labelX = 0.5;
                            labelY = -0.5;
                            break;
                        case Left:
                            anchorXPos = 0;
                            anchorYPos = leftPosition;
                            leftPosition += stepY;
                            anchorDX = -1;
                            anchorDY = 0;
                            //labelX = -0.18 * (double) label.length();
                            labelX = 0;
                            labelY = -0.5;
                            break;
                        case Right:
                            anchorXPos = 1.0;
                            anchorYPos = rightPosition;
                            rightPosition += stepY;
                            anchorDX = 1;
                            anchorDY = 0;
                            //labelX = 0.21 * (double) label.length();
                            labelX = 1.0;
                            labelY = -0.5;
                            break;
                        case Top:
                            anchorXPos = topPosition;
                            anchorYPos = 0;
                            topPosition += stepX;
                            anchorDX = 0;
                            anchorDY = -1;
                            labelX = 0.5;
                            labelY = 1.2;
                            break;
                    }
                    writer.write("{");
                    {
                        writer.write("nodeId : '" + node.getUID() + "',");
                        writer.write("id : '" + endpoint.getUID() + "',");
                        writer.write("endpoint : '" + endpoint.getConnectorType().toString() + "',");
                        writer.write("anchor : [" + anchorXPos + ", " + anchorYPos + ", " + anchorDX + ", " + anchorDY + "],");
                        writer.write("scope : '" + endpoint.getScope() + "',");
                        writer.write("isSource : " + (EndpointType.Input.equals(endpoint.getType()) ? "false" : "true") + ",");
                        writer.write("isTarget : " + (EndpointType.Output.equals(endpoint.getType()) ? "false" : "true") + ",");
                        if (EndpointType.Input.equals(endpoint.getType())) {
                            writer.write("dropOptions:{ hoverClass:'hover', activeClass:'active' },");
                        }
                        writer.write("maxConnections : " + endpoint.getMaxConnections() + ",");
                        writer.write("overlays: [");
                        {
                            writer.write("[ 'Label', { location:[" + labelX + ", " + labelY + "], cssClass : 'graph-endpoint-label', label : '" + label + "' } ]");
                        }
                        writer.write("]");
                    }
                    writer.write("},");
                }
            }
            writer.write("],");

            // Encode all connections
            writer.write("connections : [");
            for (GraphNodeConnection connection : model.getConnections()) {
                writer.write("{");
                writer.write("source : '" + connection.getSourceEndpoint().getUID() + "',");
                writer.write("target : '" + connection.getDestinationEndpoint().getUID() + "',");
                writer.write("parameters : {connectionId : '" + connection.getUID() + "'}");
                writer.write("},");
            }
            writer.write("]");

            // Encode client behaviors
            encodeClientBehaviors(fc, nodeGraph);
            writer.write("},true);");
        }

        writer.write("});");
        endScript(writer);
    }
}