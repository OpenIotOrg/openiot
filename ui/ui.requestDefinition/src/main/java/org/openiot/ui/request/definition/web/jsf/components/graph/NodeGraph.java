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

package org.openiot.ui.request.definition.web.jsf.components.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;

import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.definition.web.jsf.components.events.NodeInsertedEvent;
import org.primefaces.component.api.Widget;
import org.primefaces.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "primefaces.js"),
    @ResourceDependency(library = "node-graph", name = "0-jquery.jsPlumb-1.4.0-all-min.js"),
    @ResourceDependency(library = "node-graph", name = "1-node-graph.js"),
    @ResourceDependency(library = "node-graph", name = "node-graph.css"),})
public class NodeGraph extends UIComponentBase implements Widget, ClientBehaviorHolder {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NodeGraph.class);
    public static final String COMPONENT_TYPE = "org.openiot.ui.request.definition.web.jsf.components.graph.NodeGraph";
    public static final String COMPONENT_FAMILY = "org.openiot.ui.request.definition.web.jsf.components.graph";
    private static final String DEFAULT_RENDERER = "org.openiot.ui.request.definition.web.jsf.components.graph.NodeGraphRenderer";
    private static final String OPTIMIZED_PACKAGE = "org.openiot.ui.request.definition.web.jsf.components.graph.";
    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(
            Arrays.asList("create", "select", "deselect", "move", "delete", "disconnect", "connect"));

    /**
     * Properties that are tracked by state saving.
     */
    protected enum PropertyKeys {

        translations,
        widgetVar,
        styleClass,
        model;
        private String toString;

        PropertyKeys(final String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public NodeGraph() {
        setRendererType(DEFAULT_RENDERER);
    }

    //-------------------------------------------------------------------------
    // Internal (JSF)
    //-------------------------------------------------------------------------
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public String resolveWidgetVar() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final String userWidgetVar = (String) getAttributes().get(PropertyKeys.widgetVar.toString());

        if (userWidgetVar != null) {
            return userWidgetVar;
        }

        return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
    }

    public void setAttribute(final PropertyKeys property, final Object value) {
        getStateHelper().put(property, value);

        @SuppressWarnings("unchecked")
        List<String> setAttributes =
                (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
        if (setAttributes == null) {
            final String cname = this.getClass().getName();
            if (cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
                setAttributes = new ArrayList<String>(6);
                this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
            }
        }

        if (setAttributes != null && value == null) {
            final String attributeName = property.toString();
            final ValueExpression ve = getValueExpression(attributeName);
            if (ve == null) {
                setAttributes.remove(attributeName);
            } else if (!setAttributes.contains(attributeName)) {
                setAttributes.add(attributeName);
            }
        }
    }

    //-------------------------------------------------------------------------
    // Attribute getter/setters
    //-------------------------------------------------------------------------
    public void setWidgetVar(final String widgetVar) {
        setAttribute(PropertyKeys.widgetVar, widgetVar);
    }

    public GraphModel getModel() {
        return (GraphModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(final GraphModel model) {
        setAttribute(PropertyKeys.model, model);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        setAttribute(PropertyKeys.styleClass, styleClass);
    }

    public ResourceBundle getTranslations() {
        return (ResourceBundle) getStateHelper().eval(PropertyKeys.translations, null);
    }

    public void setTranslations(ResourceBundle translations) {
        setAttribute(PropertyKeys.translations, translations);
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = this.getClientId(context);

        if (isSelfRequest(context)) {
            //AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            GraphModel model = getModel();

            if ("select".equals(eventName)) {
                GraphNode node = model.lookupGraphNode(params.get(clientId + "_sourceNodeId"));
                if (node != null) {
                    model.setSelectedNode(node);
                } else {
                    return;
                }

            } else if ("deselect".equals(eventName)) {
                model.setSelectedNode(null);
            } else if ("move".equals(eventName)) {
                GraphNode node = model.lookupGraphNode(params.get(clientId + "_sourceNodeId"));
                if (node != null) {
                    double x = Double.valueOf(params.get(clientId + "_x"));
                    double y = Double.valueOf(params.get(clientId + "_y"));
                    model.updatePosition(node, x, y);
                } else {
                    return;
                }

            } else if ("connect".equals(eventName)) {
                String connectionId = params.get(clientId + "_connectionId");
                GraphNode sourceNode = model.lookupGraphNode(params.get(clientId + "_sourceNodeId"));
                GraphNodeEndpoint sourceEndpoint = model.lookupGraphEndpoint(sourceNode, params.get(clientId + "_sourceEndpointId"));
                GraphNode destinationNode = model.lookupGraphNode(params.get(clientId + "_destinationNodeId"));
                GraphNodeEndpoint destinationEndpoint = model.lookupGraphEndpoint(destinationNode, params.get(clientId + "_destinationEndpointId"));
                if (connectionId != null && sourceEndpoint != null && destinationNode != null && destinationEndpoint != null) {
                    model.connect(connectionId, sourceNode, sourceEndpoint, destinationNode, destinationEndpoint);
                } else {
                    return;
                }
            } else if ("disconnect".equals(eventName)) {
                String connectionId = params.get(clientId + "_connectionId");
                GraphNodeConnection connection = model.lookupGraphNodeConnection(connectionId);
                if (connection != null) {
                    model.disconnect(connection);
                } else {
                    return;
                }
            } else if ("create".equals(eventName)) {
                String type = params.get(clientId + "_type");
                double x = Double.valueOf(params.get(clientId + "_x"));
                double y = Double.valueOf(params.get(clientId + "_y"));

                try {
                    // Try to instanciate sensor class
                	String[] tokens = type.split("-");
                	if( tokens.length != 2 ){
                		throw new Exception("Invalid type token: " + type);
                	}

                    AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                    NodeInsertedEvent wrappedEvent = new NodeInsertedEvent(this, behaviorEvent.getBehavior(), tokens[0], tokens[1], x, y);
                    wrappedEvent.setPhaseId(behaviorEvent.getPhaseId());
                    super.queueEvent(wrappedEvent);
                    return;
                } catch (Throwable ex) {
                    LOGGER.error("Error instanciating node of type: " + type, ex);
                    return;
                }
            } else if ("delete".equals(eventName)) {
                GraphNode node = model.lookupGraphNode(params.get(clientId + "_sourceNodeId"));
                if (node != null) {
                    model.remove(node);
                } else {
                    return;
                }
            }
        }

        super.queueEvent(event);
    }

    private boolean isSelfRequest(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }
}
