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

package org.openiot.ui.request.commons.annotations.scanners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openiot.ui.request.commons.annotations.Endpoint;
import org.openiot.ui.request.commons.annotations.Endpoints;
import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeProperty;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class GraphNodeScanner {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphNodeScanner.class);
    public static List<GraphNodeProperty> detectPropertyDefinitions(Class<?> clazz) {
        List<GraphNodeProperty> propertyDefinitions = new ArrayList<GraphNodeProperty>();
        if (!clazz.isAnnotationPresent(NodeProperties.class)) {
            return propertyDefinitions;
        }

        NodeProperties annotationList = clazz.getAnnotation(NodeProperties.class);
        if (annotationList.value() == null || annotationList.value().length == 0) {
            return propertyDefinitions;
        }

        for (NodeProperty annotationData : annotationList.value()) {
            // Generate a property object from the annotation data
            GraphNodeProperty property = new DefaultGraphNodeProperty();
            property.setType(annotationData.type());
            property.setName(annotationData.name());
            property.setJavaType(annotationData.javaType());
            property.setRequired(annotationData.required());
            property.setAllowedValues(annotationData.allowedValues() != null && annotationData.allowedValues().length > 0 ? annotationData.allowedValues() : null);
            propertyDefinitions.add(property);
        }

        return propertyDefinitions;
    }

    public static List<GraphNodeEndpoint> detectEndpointDefinitions(Class<?> clazz) {
        List<GraphNodeEndpoint> endpointDefinitions = new ArrayList<GraphNodeEndpoint>();
        if (!clazz.isAnnotationPresent(Endpoints.class)) {
            return endpointDefinitions;
        }

        Endpoints annotationList = clazz.getAnnotation(Endpoints.class);
        if (annotationList.value() == null || annotationList.value().length == 0) {
            return endpointDefinitions;
        }

        for (Endpoint annotationData : annotationList.value()) {

            // Generate a endpoint object from the annotation data
            GraphNodeEndpoint endpoint = new DefaultGraphNodeEndpoint();
            endpoint.setType(annotationData.type());
            endpoint.setAnchor(annotationData.anchorType());
            endpoint.setConnectorType(annotationData.connectorType());
            endpoint.setMaxConnections(annotationData.maxConnections());
            endpoint.setLabel(annotationData.label());
            endpoint.setScope(annotationData.scope());
            endpoint.setRequired(annotationData.required());
            endpointDefinitions.add(endpoint);
        }

        return endpointDefinitions;
    }

    public static Set<Class<?>> detectGraphNodeClasses(String rootPackage) {
        try {
        	Reflections reflections = new Reflections(rootPackage);
        	Set<Class<?>> detectedClasses =reflections.getTypesAnnotatedWith(GraphNodeClass.class);
        	Iterator<Class<?>> setIt = detectedClasses.iterator();
        	while( setIt.hasNext() ){
        		GraphNodeClass annotation = setIt.next().getAnnotation(GraphNodeClass.class);
        		if( annotation.hideFromScanner() ){
        			setIt.remove();
        		}
        	}
            return detectedClasses;
        } catch (Exception ex) {
            LOGGER.warn("Exception in detectGraphNodeClasses:", ex);
        }

        return null;
    }
}
