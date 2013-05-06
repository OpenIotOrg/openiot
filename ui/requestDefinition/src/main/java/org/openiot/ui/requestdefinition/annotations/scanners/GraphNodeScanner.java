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
package org.openiot.ui.requestdefinition.annotations.scanners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.openiot.ui.requestdefinition.annotations.Endpoint;
import org.openiot.ui.requestdefinition.annotations.Endpoints;
import org.openiot.ui.requestdefinition.annotations.GraphNodeClass;
import org.openiot.ui.requestdefinition.annotations.NodeProperties;
import org.openiot.ui.requestdefinition.annotations.NodeProperty;
import org.openiot.ui.requestdefinition.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.requestdefinition.nodes.base.DefaultGraphNodeProperty;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeProperty;
import org.openiot.ui.requestdefinition.logging.LoggerService;
import org.reflections.Reflections;

/**
 *
 * @author aana
 */
public class GraphNodeScanner {

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

    public static Set<Class<?>> detectGraphNodeClasses() {
        try {
        	Reflections reflections = new Reflections("org.openiot.ui.requestdefinition.nodes.impl");
            return reflections.getTypesAnnotatedWith(GraphNodeClass.class);
        } catch (Exception ex) {
            LoggerService.log(ex);
        }
        
        return null;
    }
}
