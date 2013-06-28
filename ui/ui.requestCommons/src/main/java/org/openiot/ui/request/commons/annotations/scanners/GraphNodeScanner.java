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
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeProperty;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;
import org.reflections.Reflections;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
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
        	Reflections reflections = new Reflections("org.openiot.ui.request.commons.nodes.impl");
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
            LoggerService.log(ex);
        }
        
        return null;
    }
}
