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
package org.openiot.ui.requestdefinition.nodes.generator.generators;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import org.openiot.ui.requestdefinition.interfaces.GraphModel;
import org.openiot.ui.requestdefinition.nodes.base.AbstractGraphNodeVisitor;
import org.openiot.ui.requestdefinition.nodes.enums.EndpointType;
import org.openiot.ui.requestdefinition.nodes.impl.generators.TimestampGenerator;
import org.openiot.ui.requestdefinition.nodes.impl.sensors.GenericSensor;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNode;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.requestdefinition.logging.LoggerService;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class SparqlGenerator extends AbstractGraphNodeVisitor {

    private GraphModel model;
    // Generated code
    private String generatedCode;
    // Visitor service construction state
    private String generatedCommentCode;
    private String generatedSelectModifierCode;
    private String generatedSelectCode;
    private String generatedValueSelectCode;
    private String generatedFromCode;
    private Set<String> generatedWhereCode;
    private Set<String> visitedSensorTypes;
    private Stack<GraphNodeConnection> visitedConnectionGraphStack;

    public SparqlGenerator(GraphModel model) {
        this.model = model;
        this.generatedCode = "";
        this.visitedConnectionGraphStack = new Stack<GraphNodeConnection>();
        this.generatedWhereCode = new LinkedHashSet<String>();
        this.visitedSensorTypes = new LinkedHashSet<String>();
    }

    public String generateCode() {
        this.generatedCode = "";

        // Visit all 'visualization' nodes once
        for (GraphNode node : model.getNodes()) {
            if (node.getType().equals("VISUALIZER")) {
                visitViaReflection(node);
            }
        }

        return this.generatedCode;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }
    
    private void beginService(GraphNode visNode) {
        this.visitedConnectionGraphStack.clear();        

        this.generatedCommentCode = "# Service with visualization widget of type '" + visNode.getType() + "' and sensors of type:";
        this.generatedSelectCode = "";
        this.generatedSelectModifierCode = "";
        this.generatedFromCode = "\n\nFROM <http://lsm.deri.ie/OpenIoT/sensormeta#>\n";
        this.generatedWhereCode.clear();
        this.visitedSensorTypes.clear();
    }

    private void endService() {
        this.generatedCommentCode += "\n#\t - " + org.apache.commons.lang3.StringUtils.join(visitedSensorTypes, "\n#\t - ") + "\n";
        this.generatedCommentCode += "# Generated: " + (new Date()) + "\n\n";

        String codeBlock = generatedCommentCode + "SELECT\n" + generatedSelectModifierCode + " " + generatedSelectCode + generatedFromCode + "\nWHERE {\n\t" + org.apache.commons.lang3.StringUtils.join(generatedWhereCode, "\n\t") + "\n}";
        generatedCode += codeBlock;
    }

    private void encodePropertySelectionFilters( GraphNode sensorNode, GraphNodeEndpoint attributeEndpoint ){
    	this.generatedWhereCode.add("\t#?" + sensorNode.getUID() + " <http://www.loa-cnr.it/ontologies/DUL.owl#hasAttribute> ?" + attributeEndpoint.getUID() + ".");
    }
    
    //-------------------------------------------------------------------------
    // Visitors
    //-------------------------------------------------------------------------
    @Override
    public void defaultVisit(GraphNode node) {
        if( node instanceof GenericSensor){
            visit((GenericSensor)node);
            return;
        }
        
        if (node.getType().equals("VISUALIZER")) {
            visitVisualizer(node);
            return;
        }

        LoggerService.log(Level.SEVERE, "[SparqlGenerator] Default visitor called for node of class: " + node.getClass().getSimpleName());
    }

    public void visitVisualizer(GraphNode node) {
        // Start a new service generation
        beginService(node);

        // Visit incoming neighbors
        for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
            if (endpoint.getType().equals(EndpointType.Output)) {
                continue;
            }

            List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(endpoint);
            for (GraphNodeConnection connection : incomingConnections) {
                // Skip generator nodes
                if( connection.getSourceNode() instanceof TimestampGenerator ){
                    continue;
                }
                
                //
                this.generatedValueSelectCode = "\t";
                this.visitedConnectionGraphStack.push(connection);
                this.visitViaReflection(connection.getSourceNode());

                this.generatedValueSelectCode += " AS ?" + endpoint.getLabel();
                if (!this.generatedSelectCode.isEmpty()) {
                    this.generatedSelectCode += ",\n";
                }
                this.generatedSelectCode += generatedValueSelectCode;

                this.visitedConnectionGraphStack.pop();
            }
        }

        endService();
    }
    
    private void visitIncomingConnections(GraphNode destinationNode) {
        for (GraphNodeEndpoint endpoint : destinationNode.getEndpointDefinitions()) {
            if (endpoint.getType().equals(EndpointType.Output)) {
                continue;
            }

            List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(endpoint);
            for (GraphNodeConnection connection : incomingConnections) {
                this.visitedConnectionGraphStack.push(connection);
                this.visitViaReflection(connection.getSourceNode());
                this.visitedConnectionGraphStack.pop();
            }
        }
    }

    //-------------------------------------------------------------------------
    // Node-specific visitors
    //-------------------------------------------------------------------------    
    public void visit(GenericSensor node) {
        visitedSensorTypes.add(node.getLabel());
        
        // Examine the connection endpoint that lead us to the sensor
        GraphNodeConnection outgoingConnection = visitedConnectionGraphStack.peek();
        GraphNodeEndpoint sourceEndpoint = outgoingConnection.getSourceEndpoint();
        
        // Encode attribute in select statement
        String attribute = "?" + sourceEndpoint.getUID();
        
        // If we are directly connected to a visualizer node then add the distinct keyword on the query start
        if( outgoingConnection.getDestinationNode().getType().equals("VISUALIZER")) {
            if( !generatedSelectModifierCode.contains("DISTINCT") ){
                generatedSelectModifierCode += "DISTINCT ";
            }
        }

        this.generatedValueSelectCode += attribute;
        
        // Encode sensor selection queries in where statement 
        this.generatedWhereCode.add("\t?" + node.getUID() + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>.");
        this.generatedWhereCode.add("\t?" + node.getUID() + " <http://purl.org/net/provenance/ns#PerformedBy> ?" + node.getUID() + "_source.");
        this.generatedWhereCode.add("\t?" + node.getUID() + " <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?" + node.getUID() + "_place.");
        this.generatedWhereCode.add("\t?" + node.getUID() + " <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?" + node.getUID() + "_typeId.");
        this.generatedWhereCode.add("\t?" + node.getUID() + "_typeId <http://www.w3.org/2000/01/rdf-schema#label> '" + node.getLabel() + "'.");
        this.generatedWhereCode.add("\t?" + node.getUID() + " <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?" + node.getUID() + "_place.");
        this.generatedWhereCode.add("\t?" + node.getUID() + "_place geo:geometry ?" + node.getUID() + "_geo.");
        this.generatedWhereCode.add("\tFILTER (<bif:st_intersects>(?" + node.getUID() + "_geo,<bif:st_point>(" + node.getFilterLocationLat() + "," + node.getFilterLocationLon() + ")," + node.getFilterLocationRadius() + ")).");
        
        // Encode attribute selection queries in where statement
        this.encodePropertySelectionFilters(node, sourceEndpoint);
        
        // If sensor node has an incoming filter node connection
        // visit it and append any additional filters
        for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
            if (endpoint.getType().equals(EndpointType.Output)) {
                continue;
            }

            List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(endpoint);
            for (GraphNodeConnection connection : incomingConnections) {
                this.visitedConnectionGraphStack.push(connection);
                this.visitViaReflection(connection.getSourceNode());
                this.visitedConnectionGraphStack.pop();
            }
        }
    }

    //-------------------------------------------------------------------------
    // Filter node visitors
    //-------------------------------------------------------------------------    
    public void visit(org.openiot.ui.requestdefinition.nodes.impl.filters.SelectionFilter node){
        
        // Visit all outgoing connections except the one connecting to the sensor
        for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
            if (endpoint.getType().equals(EndpointType.Input)) {
                continue;
            }

            List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(endpoint);
            for (GraphNodeConnection connection : incomingConnections) {
                if( connection.getDestinationNode() instanceof GenericSensor ){
                    continue;
                }

                this.visitedConnectionGraphStack.push(connection);
                this.visitViaReflection(connection.getDestinationNode());
                this.visitedConnectionGraphStack.pop();
            }
        }
    }
    
    //-------------------------------------------------------------------------
    // Comparator node visitors
    //-------------------------------------------------------------------------    
    public void visit(org.openiot.ui.requestdefinition.nodes.impl.comparators.Compare node) {
        // Since the filter node cloned the original attribute endpoint, we need to 
        // match the incoming endpoint from the last connection on stack to the
        // endpoint with same name from the sensor node (2 connections back)        
        ListIterator<GraphNodeConnection> connectionIt = visitedConnectionGraphStack.listIterator(visitedConnectionGraphStack.size());
        GraphNodeEndpoint sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
        GraphNode sensorNode = connectionIt.previous().getDestinationNode();
         
        GraphNodeEndpoint match = null;
        for( GraphNodeEndpoint test : sensorNode.getEndpointDefinitions() ){
            if( test.getLabel().equals(sourceFilterEndpoint.getLabel())){
                match = test;
                break;
            }
        }
        
        if( match == null ){
            LoggerService.log(Level.SEVERE, "[SparqlGenerator] Could not match filter node endpoint '"+sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
            return;
        }
         
        // Encode attribute selection queries in where statement        
        this.encodePropertySelectionFilters(sensorNode, match);
        
        // Encode filter query in where statement
        this.generatedWhereCode.add("\tFILTER( ?" + match.getUID() + " " + node.getPropertyValueMap().get("OPERATOR") + " " + node.getPropertyValueMap().get("CMP_VALUE") + " ).");
    }

    public void visit(org.openiot.ui.requestdefinition.nodes.impl.comparators.CompareAbsoluteDateTime node) {
        // Since the filter node cloned the original attribute endpoint, we need to 
        // match the incoming endpoint from the last connection on stack to the
        // endpoint with same name from the sensor node (2 connections back)        
        ListIterator<GraphNodeConnection> connectionIt = visitedConnectionGraphStack.listIterator(visitedConnectionGraphStack.size());
        GraphNodeEndpoint sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
        GraphNode sensorNode = connectionIt.previous().getDestinationNode();
         
        GraphNodeEndpoint match = null;
        for( GraphNodeEndpoint test : sensorNode.getEndpointDefinitions() ){
            if( test.getLabel().equals(sourceFilterEndpoint.getLabel())){
                match = test;
                break;
            }
        }
        
        if( match == null ){
            LoggerService.log(Level.SEVERE, "[SparqlGenerator] Could not match filter node endpoint '"+sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
            return;
        }
         
        // Encode attribute selection queries in where statement
        this.encodePropertySelectionFilters(sensorNode, match);

        // Generate date string in appropriate format. The xsd:datetime type formats
        // dates a bit differently than the pattern used by simple date format (separates hours and mins of the timezone with a colon)
        // so we need to patch it
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date cmpValue = (Date)node.getPropertyValueMap().get("CMP_VALUE");
        String formattedDate = sdf.format(cmpValue);
        formattedDate = formattedDate.substring(0, formattedDate.length() - 2) + ":" + formattedDate.substring(formattedDate.length() - 2 );
        
        // Encode filter query in where statement
        this.generatedWhereCode.add("\tFILTER( ?" + match.getUID() + " " + node.getPropertyValueMap().get("OPERATOR") + " \"" + formattedDate + "\"^^xsd:date ).");
    }

    public void visit(org.openiot.ui.requestdefinition.nodes.impl.comparators.CompareRelativeDateTime node) {
        // Since the filter node cloned the original attribute endpoint, we need to 
        // match the incoming endpoint from the last connection on stack to the
        // endpoint with same name from the sensor node (2 connections back)        
        ListIterator<GraphNodeConnection> connectionIt = visitedConnectionGraphStack.listIterator(visitedConnectionGraphStack.size());
        GraphNodeEndpoint sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
        GraphNode sensorNode = connectionIt.previous().getDestinationNode();
         
        GraphNodeEndpoint match = null;
        for( GraphNodeEndpoint test : sensorNode.getEndpointDefinitions() ){
            if( test.getLabel().equals(sourceFilterEndpoint.getLabel())){
                match = test;
                break;
            }
        }
        
        if( match == null ){
            LoggerService.log(Level.SEVERE, "[SparqlGenerator] Could not match filter node endpoint '"+sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
            return;
        }
         
        // Encode attribute selection queries in where statement
        this.encodePropertySelectionFilters(sensorNode, match);

        long scaler = 1L;
        String unit = (String) node.getPropertyValueMap().get("CMP_VALUE_UNIT");
        if( "SECOND(S)".equals(unit) ){
        	scaler = 1L;
        } else if( "MINUTE(S)".equals(unit) ){
        	scaler = 60L;
        }else if( "HOUR(S)".equals(unit) ){
        	scaler = 60 * 60L;
        }else if( "DAY(S)".equals(unit) ){
        	scaler = 24 * 60 * 60L;
        }else if( "MONTH(S)".equals(unit) ){
        	scaler = 30 * 24 * 60 * 60L;
        }else if( "YEAR(S)".equals(unit) ){
        	scaler = 365 * 24 * 60 * 60L;
        }
        long cmpValue = (Long) node.getPropertyValueMap().get("CMP_VALUE") * scaler;        
        
        // Encode filter query in where statement
        this.generatedWhereCode.add("\tFILTER( bif:datediff('second', xsd:dateTime(str(?" + match.getUID() + ")), NOW()) " + node.getPropertyValueMap().get("OPERATOR") + " " + cmpValue + ").");
    }
    
    public void visit(org.openiot.ui.requestdefinition.nodes.impl.comparators.Between node) {
        // Since the filter node cloned the original attribute endpoint, we need to 
        // match the incoming endpoint from the last connection on stack to the
        // endpoint with same name from the sensor node (2 connections back)        
        ListIterator<GraphNodeConnection> connectionIt = visitedConnectionGraphStack.listIterator(visitedConnectionGraphStack.size());
        GraphNodeEndpoint sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
        GraphNode sensorNode = connectionIt.previous().getDestinationNode();
         
        GraphNodeEndpoint match = null;
        for( GraphNodeEndpoint test : sensorNode.getEndpointDefinitions() ){
            if( test.getLabel().equals(sourceFilterEndpoint.getLabel())){
                match = test;
                break;
            }
        }
        
        if( match == null ){
            LoggerService.log(Level.SEVERE, "[SparqlGenerator] Could not match filter node endpoint '"+sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
            return;
        }
         
        // Encode attribute selection queries in where statement
        this.encodePropertySelectionFilters(sensorNode, match);
        
        // Encode filter query in where statement
        this.generatedWhereCode.add("\tFILTER( ?" + match.getUID() + " >= " + node.getPropertyValueMap().get("CMP_VALUE1") + " && ?" + match.getUID() + " <= " + node.getPropertyValueMap().get("CMP_VALUE2") + " ).");
    }

    public void visit(org.openiot.ui.requestdefinition.nodes.impl.comparators.BetweenDateTime node) {
        // Since the filter node cloned the original attribute endpoint, we need to 
        // match the incoming endpoint from the last connection on stack to the
        // endpoint with same name from the sensor node (2 connections back)        
        ListIterator<GraphNodeConnection> connectionIt = visitedConnectionGraphStack.listIterator(visitedConnectionGraphStack.size());
        GraphNodeEndpoint sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
        GraphNode sensorNode = connectionIt.previous().getDestinationNode();
         
        GraphNodeEndpoint match = null;
        for( GraphNodeEndpoint test : sensorNode.getEndpointDefinitions() ){
            if( test.getLabel().equals(sourceFilterEndpoint.getLabel())){
                match = test;
                break;
            }
        }
        
        if( match == null ){
            LoggerService.log(Level.SEVERE, "[SparqlGenerator] Could not match filter node endpoint '"+sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
            return;
        }
         
        // Encode attribute selection queries in where statement
        this.encodePropertySelectionFilters(sensorNode, match);

        // Generate date string in appropriate format. The xsd:datetime type formats
        // dates a bit differently than the pattern used by simple date format (separates hours and mins of the timezone with a colon)
        // so we need to patch it
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date cmpFromValue = (Date)node.getPropertyValueMap().get("CMP_VALUE1");
        String formattedFromDate = sdf.format(cmpFromValue);
        formattedFromDate = formattedFromDate.substring(0, formattedFromDate.length() - 2) + ":" + formattedFromDate.substring(formattedFromDate.length() - 2 );
        Date cmpToValue = (Date)node.getPropertyValueMap().get("CMP_VALUE2");
        String formattedToDate = sdf.format(cmpToValue);
        formattedToDate = formattedToDate.substring(0, formattedToDate.length() - 2) + ":" + formattedToDate.substring(formattedToDate.length() - 2 );
        
        // Encode filter query in where statement
        this.generatedWhereCode.add("\tFILTER( ?" + match.getUID() + " >= \"" + formattedFromDate + "\"^^xsd:date && ?" + match.getUID() + " <= \"" + formattedToDate + "\"^^xsd:date ).");
    }
    
    //-------------------------------------------------------------------------
    // Aggregator node visitors
    //-------------------------------------------------------------------------        
    public void visit(org.openiot.ui.requestdefinition.nodes.impl.aggegators.Min node) {
        this.generatedValueSelectCode += "MIN( ";
        visitIncomingConnections(node);
        this.generatedValueSelectCode += " )";
    }

    public void visit(org.openiot.ui.requestdefinition.nodes.impl.aggegators.Max node) {
        this.generatedValueSelectCode += "MAX( ";
        visitIncomingConnections(node);
        this.generatedValueSelectCode += " )";
    }

    public void visit(org.openiot.ui.requestdefinition.nodes.impl.aggegators.Count node) {
        this.generatedValueSelectCode += "COUNT( ";
        visitIncomingConnections(node);
        this.generatedValueSelectCode += " )";
    }

    public void visit(org.openiot.ui.requestdefinition.nodes.impl.aggegators.Sum node) {
        this.generatedValueSelectCode += "SUM( ";
        visitIncomingConnections(node);
        this.generatedValueSelectCode += " )";
    }

    public void visit(org.openiot.ui.requestdefinition.nodes.impl.aggegators.Average node) {
        this.generatedValueSelectCode += "AVG( ";
        visitIncomingConnections(node);
        this.generatedValueSelectCode += " )";
    }
}
