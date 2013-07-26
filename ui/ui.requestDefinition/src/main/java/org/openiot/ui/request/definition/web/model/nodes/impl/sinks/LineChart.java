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
package org.openiot.ui.request.definition.web.model.nodes.impl.sinks;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.openiot.ui.request.commons.annotations.Endpoint;
import org.openiot.ui.request.commons.annotations.Endpoints;
import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.models.ObservableMap;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeProperty;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "LineChart", type = "SINK", scanProperties = true)
@NodeProperties({ @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "TITLE", required = true), @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "SERIES", required = true, allowedValues = { "1", "2", "3", "4", "5" }), @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "X_AXIS_TYPE", required = true, allowedValues = { "Number", "Date (result set)", "Date (observation)" }), @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "X_AXIS_LABEL", required = true), @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "Y_AXIS_LABEL", required = true) })
public class LineChart extends DefaultGraphNode implements Serializable, Observer {
	private static final long serialVersionUID = 1L;

	public LineChart() {
		super();
		
		// Setup some defaults
		setProperty("TITLE", LineChart.class.getSimpleName());
		setProperty("SERIES", "1");
		setProperty("X_AXIS_TYPE", "Number");
		setProperty("X_AXIS_LABEL", "x axis");
		setProperty("Y_AXIS_LABEL", "y axis");

		addPropertyChangeObserver(this);
		validateSeries();
	}

	private void removeAllXAxisEndpoints() {
		Iterator<GraphNodeEndpoint> endpointIt = this.getEndpointDefinitions().iterator();
		while (endpointIt.hasNext()) {
			GraphNodeEndpoint endpoint = endpointIt.next();
			if (endpoint.getLabel().startsWith("x")) {
				endpointIt.remove();
				disconnectEndpoint(endpoint);
			}
		}
	}

	private void addMissingXAxii() {
		String xAxisType = (String) getPropertyValueMap().get("X_AXIS_TYPE");
		if ("Date (result set)".equals(xAxisType)) {
			return;
		}

		int maxSeries = Integer.valueOf((String) getPropertyByName("SERIES").getAllowedValues()[getPropertyByName("SERIES").getAllowedValues().length - 1]);
		for (int i = 0; i < maxSeries; i++) {
			GraphNodeEndpoint yep = getEndpointByLabel("y" + (i + 1));
			if( yep == null ){
				return;
			}
			int insIndex = Math.max(0, getEndpointDefinitions().indexOf(yep) - 1);
			GraphNodeEndpoint ep = getEndpointByLabel("x" + (i + 1));
			if (ep == null) {
				ep = new DefaultGraphNodeEndpoint();
				ep.setType(EndpointType.Input);
				ep.setAnchor(AnchorType.Left);
				ep.setConnectorType(ConnectorType.Rectangle);
				ep.setLabel("x" + (i + 1));
				ep.setRequired(true);

				// Setup scope and connection count depending on axis type
				if ("Number".equals(xAxisType)) {
					ep.setScope("agr_Number agr_Integer agr_Long, agr_Float agr_Double");
					ep.setMaxConnections(1);
				} else {
					ep.setScope("grp_Date");
					ep.setMaxConnections(-1); // allow connection of multiple
												// attributes
				}

				getEndpointDefinitions().add(insIndex, ep);
			} else {
				// Check that scope is consistent with axis type
				if ("Number".equals(xAxisType) && ep.getScope().contains("grp_")) {
					disconnectEndpoint(ep);
					ep.setScope("agr_Number agr_Integer agr_Long, agr_Float agr_Double");
				} else if ("Date (observation)".equals(xAxisType) && ep.getScope().contains("agr_")) {
					disconnectEndpoint(ep);
					ep.setScope("grp_Date");
				}
			}
		}
	}

	public void validateSeries() {
		int seriesCount = Integer.valueOf((String) getPropertyValueMap().get("SERIES"));
		int i = 0;
		for (; i < seriesCount; i++) {
			// If we are missing the required endpoints and properties create
			// them now
			String epLabel = "y" + (i + 1);
			GraphNodeEndpoint ep = getEndpointByLabel(epLabel);
			if (ep == null) {

				// Generate Y axis entry
				ep = new DefaultGraphNodeEndpoint();
				ep.setType(EndpointType.Input);
				ep.setAnchor(AnchorType.Left);
				ep.setConnectorType(ConnectorType.Rectangle);
				ep.setScope("agr_Number agr_Integer agr_Long, agr_Float agr_Double");
				ep.setLabel(epLabel);
				ep.setRequired(true);
				getEndpointDefinitions().add(ep);

				// Generate series properties
				GraphNodeProperty prop = new DefaultGraphNodeProperty();
				String propKey = "SERIES_" + i + "_LABEL";
				prop.setType(PropertyType.Writable);
				prop.setName(propKey);
				prop.setJavaType(java.lang.String.class);
				prop.setRequired(true);
				getPropertyDefinitions().add(prop);

				((ObservableMap<String, Object>) getPropertyValueMap()).getWrappedMap().put(propKey, "Series " + (i + 1));
			}
		}

		// If we reduced the number of series, get rid of the old series
		int maxSeries = Integer.valueOf((String) getPropertyByName("SERIES").getAllowedValues()[getPropertyByName("SERIES").getAllowedValues().length - 1]);
		for (; i < maxSeries; i++) {
			String epLabel = "y" + (i + 1);
			GraphNodeEndpoint ep = getEndpointByLabel(epLabel);
			if (ep != null) {

				// Check for an xAxis point
				GraphNodeEndpoint xep = getEndpointByLabel("x" + (i + 1));
				if (xep != null) {
					disconnectEndpoint(xep);
					getEndpointDefinitions().remove(xep);
				}

				// If we have a connection to this node, kill it
				disconnectEndpoint(ep);
				getEndpointDefinitions().remove(ep);

				String propKey = "SERIES_" + i + "_LABEL";
				GraphNodeProperty prop = getPropertyByName(propKey);
				if (prop != null) {
					getPropertyDefinitions().remove(prop);
					((ObservableMap<String, Object>) getPropertyValueMap()).getWrappedMap().remove(propKey);
				}
			}
		}

		addMissingXAxii();
	}

	public void update(Observable o, Object modifiedKey) {
		Map<String, Object> propertyMap = getPropertyValueMap();

		// Check for X_AXIS_TYPE modifications
		if ((modifiedKey != null) && ("X_AXIS_TYPE".equals((String) modifiedKey)) && (propertyMap.get("X_AXIS_TYPE") != null)) {
			String newXAxisType = (String) propertyMap.get("X_AXIS_TYPE");
			if ("Date (result set)".equals(newXAxisType)) {
				removeAllXAxisEndpoints();
			}
		}

		validateSeries();
	}

}
