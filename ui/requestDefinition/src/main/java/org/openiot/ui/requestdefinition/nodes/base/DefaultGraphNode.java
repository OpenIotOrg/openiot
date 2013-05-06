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
package org.openiot.ui.requestdefinition.nodes.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import org.openiot.ui.requestdefinition.annotations.GraphNodeClass;
import org.openiot.ui.requestdefinition.annotations.scanners.GraphNodeScanner;
import org.openiot.ui.requestdefinition.models.ObservableMap;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNode;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeProperty;

/**
 * 
 * @author aana
 */
public class DefaultGraphNode implements GraphNode, Serializable {
	private static final long serialVersionUID = 1L;

	private String UID = "graphNode_" + System.nanoTime();
	private String type;
	private String label;
	private List<GraphNodeProperty> propertyDefinitions;
	private List<GraphNodeEndpoint> endpointDefinitions;
	private ObservableMap<String, Object> propertyMap;

	public DefaultGraphNode() {
		propertyMap = new ObservableMap<String, Object>(new HashMap<String, Object>());

		// Check if we need to scan our class for annotations
		GraphNodeClass annotationData = this.getClass().isAnnotationPresent(GraphNodeClass.class) ? this.getClass().getAnnotation(GraphNodeClass.class) : null;
		if (annotationData != null && annotationData.scanProperties()) {
			propertyDefinitions = GraphNodeScanner.detectPropertyDefinitions(this.getClass());
			endpointDefinitions = GraphNodeScanner.detectEndpointDefinitions(this.getClass());

			// Generate map fields
			for (GraphNodeProperty property : propertyDefinitions) {
				try {
					propertyMap.put(property.getName(), property.getJavaType().newInstance());
				} catch (Throwable ex) {
				}
			}

			setLabel(annotationData.label());
			setType(annotationData.type());
		} else {
			propertyDefinitions = new ArrayList<GraphNodeProperty>();
			endpointDefinitions = new ArrayList<GraphNodeEndpoint>();
		}
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String UID) {
		this.UID = UID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<GraphNodeProperty> getPropertyDefinitions() {
		return propertyDefinitions;
	}

	public void setPropertyDefinitions(List<GraphNodeProperty> propertyDefinitions) {
		this.propertyDefinitions = propertyDefinitions;
	}

	public Map<String, Object> getPropertyValueMap() {
		return propertyMap;
	}

	public void setProperty(String propertyKey, Object propertyValue) {
		propertyMap.put(propertyKey, propertyValue);
	}

	public List<GraphNodeEndpoint> getEndpointDefinitions() {
		return endpointDefinitions;
	}

	public void setEndpointDefinitions(List<GraphNodeEndpoint> endpointDefinitions) {
		this.endpointDefinitions = endpointDefinitions;
	}

	public void addPropertyChangeObserver(Observer o) {
		this.propertyMap.addObserver(o);
	}

	public void removePropertyChangeObserver(Observer o) {
		this.propertyMap.deleteObserver(o);
	}

	public GraphNode getCopy() {
		DefaultGraphNode copy = null;
		try {
			copy = this.getClass().newInstance();
			copy.setLabel(label);
			copy.setType(type);

			List<GraphNodeEndpoint> endpointDefinitionsCopy = new ArrayList<GraphNodeEndpoint>(endpointDefinitions.size());
			for (GraphNodeEndpoint ep : endpointDefinitions) {
				endpointDefinitionsCopy.add(ep.getCopy());
			}
			copy.setEndpointDefinitions(endpointDefinitionsCopy);

			List<GraphNodeProperty> propertyDefinitionsCopy = new ArrayList<GraphNodeProperty>(propertyDefinitions.size());
			for (GraphNodeProperty prop : propertyDefinitions) {
				propertyDefinitionsCopy.add(prop.getCopy());
				if (propertyMap.containsKey(prop.getName())) {
					copy.setProperty(prop.getName(), propertyMap.get(prop.getName()));
				}
			}
			copy.setPropertyDefinitions(propertyDefinitionsCopy);
		} catch (Throwable ex) {

		}
		return copy;
	}

	@Override
	public String toString() {
		return "[type: " + getType() + ", label: " + getLabel() + ", properties: " + getPropertyDefinitions() + ", endPoints: " + endpointDefinitions + "]";
	}
}
