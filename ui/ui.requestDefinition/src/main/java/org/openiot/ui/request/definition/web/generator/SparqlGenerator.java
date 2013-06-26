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
package org.openiot.ui.request.definition.web.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.nodes.base.AbstractGraphNodeVisitor;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.impl.generators.TimestampGenerator;
import org.openiot.ui.request.commons.nodes.impl.sensors.GenericSensor;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.logging.LoggerService;

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
    //
    private GenericSensor targetSensor;
    private GraphNodeEndpoint targetAttribute;
    private String generatedAttributeAggregationCode;
    private Set<String> selectQueryFields;    
    private Map<String, Map<String, Set<String>>> selectSubQueriesPerSensorPerAttribute;
    private Map<String, Map<String, Set<String>>> whereSubQueriesPerSensorPerAttribute;
    private Set<String> visitedSensorTypes;
    private Stack<GraphNodeConnection> visitedConnectionGraphStack;

    public SparqlGenerator(GraphModel model) {
        this.model = model;
        this.generatedCode = "";
        this.visitedConnectionGraphStack = new Stack<GraphNodeConnection>();
        this.visitedSensorTypes = new LinkedHashSet<String>();
        this.selectQueryFields = new LinkedHashSet<String>();
        this.selectSubQueriesPerSensorPerAttribute = new LinkedHashMap<String, Map<String, Set<String>>>();
        this.whereSubQueriesPerSensorPerAttribute = new LinkedHashMap<String, Map<String, Set<String>>>();
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
        this.selectQueryFields.clear();
        this.selectSubQueriesPerSensorPerAttribute.clear();
        this.visitedSensorTypes.clear();
    }

    private void endService() {
    	// Setup comments    	
        this.generatedCommentCode += "\n#\t - " + org.apache.commons.lang3.StringUtils.join(visitedSensorTypes, "\n#\t - ") + "\n";
        this.generatedCommentCode += "# Generated: " + (new Date()) + "\n";

        // Assemble code fragments
        generatedCode += generatedCommentCode;
        generatedCode += "\nSELECT " + org.apache.commons.lang3.StringUtils.join(selectQueryFields, ", ") + "\n";
        generatedCode += "\nFROM <http://lsm.deri.ie/OpenIoT/sensormeta#>\n";
        generatedCode += "\nWHERE\n";
        generatedCode += "{\n";

    	for( Map.Entry<String, Map<String, Set<String>>> subQueriesPerSensor : whereSubQueriesPerSensorPerAttribute.entrySet()){

    		for( Map.Entry<String, Set<String>> subQueryCode : subQueriesPerSensor.getValue().entrySet() ){
        		// Terminate all open subquery filters
    			subQueryCode.getValue().add("}");
    			
        		// Begin nested select query
        		generatedCode += "\t{\n";
        		
        		// Encode select statement values
        		Set<String> selectSubQueryValues = selectSubQueriesPerSensorPerAttribute.get(subQueriesPerSensor.getKey()).get(subQueryCode.getKey());
        		generatedCode += "\t\tSELECT ";
        		generatedCode += org.apache.commons.lang3.StringUtils.join(selectSubQueryValues, ", ") + "\n";
        		
        		generatedCode += "\t\tWHERE\n";
        		generatedCode += "\t\t{\n";

        		{
        			// Encode where clause
        			generatedCode += "\t\t\t" + org.apache.commons.lang3.StringUtils.join(subQueryCode.getValue(), "\n\t\t\t") + "\n";
        		}        		
        			
        		generatedCode += "\t\t}\n";
        		
        		// End nested select query
        		generatedCode += "\t}\n";        		
    		}
    	}
        generatedCode += "\n}\n";
    }

    private void genereteAttributeSubQuerySelectCode( GenericSensor sensorNode, GraphNodeEndpoint attributeEndpoint, String attributeExpression ){
		Map<String, Set<String>> subQueriesPerSensor = this.selectSubQueriesPerSensorPerAttribute.get(sensorNode.getUID());
		if( subQueriesPerSensor == null ){
			subQueriesPerSensor = new LinkedHashMap<String, Set<String>>();
			this.selectSubQueriesPerSensorPerAttribute.put(sensorNode.getUID(), subQueriesPerSensor);
		}
		
		Set<String> subQueryCode = subQueriesPerSensor.get(attributeEndpoint.getUID());
		if( subQueryCode == null ){
			subQueryCode = new LinkedHashSet<String>();
			subQueriesPerSensor.put(attributeEndpoint.getUID(), subQueryCode);
		}

		subQueryCode.add(attributeExpression);
    }
    
	private void generateAttributeSubQueryWhereCode( GenericSensor sensorNode, GraphNodeEndpoint attributeEndpoint ){
		// Check if it already exists
		Map<String, Set<String>> subQueriesPerSensor = this.whereSubQueriesPerSensorPerAttribute.get(sensorNode.getUID());
		if( subQueriesPerSensor == null ){
			subQueriesPerSensor = new LinkedHashMap<String, Set<String>>();
			this.whereSubQueriesPerSensorPerAttribute.put(sensorNode.getUID(), subQueriesPerSensor);
		}
		
		Set<String> subQueryCode = subQueriesPerSensor.get(attributeEndpoint.getUID());
		if( subQueryCode != null ){
			return;
		}		
		subQueryCode = new LinkedHashSet<String>();
		subQueriesPerSensor.put(attributeEndpoint.getUID(), subQueryCode);
		
		
	    // Encode sensor selection queries in where statement 
		subQueryCode.add("?" + sensorNode.getUID() + "_record <http://lsm.deri.ie/ont/lsm.owl#value> ?" + attributeEndpoint.getUID() + ".");
		
		// Encode predicates that dont appear in select query in a filter exists clause (optimization)
		subQueryCode.add("FILTER EXISTS {");
		subQueryCode.add("\t?" + sensorNode.getUID() + "_record <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + attributeEndpoint.getUserData() + ">.");
		subQueryCode.add("\t?" + sensorNode.getUID() + "_record <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?" + sensorNode.getUID() + "_propertyOf.");
		subQueryCode.add("\t?" + sensorNode.getUID() + "_propertyOf <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?" + sensorNode.getUID() + ".");
	    subQueryCode.add("\t?" + sensorNode.getUID() + " <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?" + sensorNode.getUID() + "_typeId.");
	    subQueryCode.add("\t?" + sensorNode.getUID() + "_typeId <http://www.w3.org/2000/01/rdf-schema#label> '" + sensorNode.getLabel() + "'.");
	    subQueryCode.add("\t?" + sensorNode.getUID() + " <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?" + sensorNode.getUID() + "_place.");
	    subQueryCode.add("\t?" + sensorNode.getUID() + "_place geo:geometry ?" + sensorNode.getUID() + "_geo.");
	    subQueryCode.add("\tFILTER (<bif:st_intersects>(?" + sensorNode.getUID() + "_geo,<bif:st_point>(" + sensorNode.getFilterLocationLat() + "," + sensorNode.getFilterLocationLon() + ")," + sensorNode.getFilterLocationRadius() + ")).");

	    // NOTE: We leave the filter clause open so we can keep appending entries for further filtering.
	    // It will be terminated when we finish processing the origin visualizer node
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
                
                // Explore graph till we reach a sensor node. 
                // At the same time build the aggregation function on the selected attribute
                generatedAttributeAggregationCode = "";
                targetSensor = null;
                targetAttribute = null;
                this.visitedConnectionGraphStack.push(connection);
                this.visitViaReflection(connection.getSourceNode());

                // Add the endpoint label in the root select field list
                this.selectQueryFields.add("?" + endpoint.getLabel());
                
                // Add the generated aggregation code to the appropriate subquery
                this.genereteAttributeSubQuerySelectCode(targetSensor, targetAttribute, generatedAttributeAggregationCode + " AS ?" + endpoint.getLabel());

                //
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

        // Remember the sensor we landed to as well as the target attribute endpoint
        targetSensor = node;
        targetAttribute = sourceEndpoint;

        // Encode attribute in aggregation statement
        String attribute = "?" + sourceEndpoint.getUID();
        this.generatedAttributeAggregationCode += attribute;
        
        // If we are directly connected to a visualizer node then add the distinct keyword on the aggregation query
        if( outgoingConnection.getDestinationNode().getType().equals("VISUALIZER")) {
            if( !generatedAttributeAggregationCode.contains("DISTINCT") ){
            	generatedAttributeAggregationCode = "DISTINCT " + generatedAttributeAggregationCode;
            }
        }
        
        // Generate nested where clause for selected attribute
        this.generateAttributeSubQueryWhereCode( targetSensor, targetAttribute );
        
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
    public void visit(org.openiot.ui.request.commons.nodes.impl.filters.SelectionFilter node){
        
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
    public void visit(org.openiot.ui.request.commons.nodes.impl.comparators.Compare node) {
    	/*
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
        */
    }

    public void visit(org.openiot.ui.request.commons.nodes.impl.comparators.CompareAbsoluteDateTime node) {
        // Since the filter node cloned the original attribute endpoint, we need to 
        // match the incoming endpoint from the last connection on stack to the
        // endpoint with same name from the sensor node (2 connections back)        
        ListIterator<GraphNodeConnection> connectionIt = visitedConnectionGraphStack.listIterator(visitedConnectionGraphStack.size());
        GraphNodeEndpoint sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
        GraphNode sensorNode = targetSensor;
         
        GraphNodeEndpoint match = null;
        for( GraphNodeEndpoint test : sensorNode.getEndpointDefinitions() ){
            if( test.getLabel().equals(sourceFilterEndpoint.getLabel())){
                match = test;
                break;
            }
        }
        
        if( match == null && !sourceFilterEndpoint.getLabel().equals("REC_TIMESTAMP")){
            LoggerService.log(Level.SEVERE, "[SparqlGenerator] Could not match filter node endpoint '"+sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
            return;
        }
         
        // Add a time filter clause for all queried attributes of this sensor
        Map<String, Set<String>> subQueriesPerSensor = this.whereSubQueriesPerSensorPerAttribute.get(sensorNode.getUID());
		if( subQueriesPerSensor == null ){
			LoggerService.log(Level.SEVERE, "[SparqlGenerator] Reached a comparison node with no queried attributes!");
			return;
		}
		
        // Generate date string in appropriate format. The xsd:datetime type formats
        // dates a bit differently than the pattern used by simple date format (separates hours and mins of the timezone with a colon)
        // so we need to patch it
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date cmpValue = (Date)node.getPropertyValueMap().get("CMP_VALUE");
        String formattedDate = sdf.format(cmpValue);
        formattedDate = formattedDate.substring(0, formattedDate.length() - 2) + ":" + formattedDate.substring(formattedDate.length() - 2 );
	
		for( Set<String> subQueryCode : subQueriesPerSensor.values() ){
	     
	        // Encode filter query in where statement
	        subQueryCode.add("\t?" + sensorNode.getUID() + "_record <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?" + sensorNode.getUID() + "_recordTime.");
	        subQueryCode.add("\tFILTER ( ?" + sensorNode.getUID() + "_recordTime " + node.getPropertyValueMap().get("OPERATOR") + " \"" + formattedDate + "\"^^xsd:date ).");
		}
    }

    public void visit(org.openiot.ui.request.commons.nodes.impl.comparators.CompareRelativeDateTime node) {
        // Since the filter node cloned the original attribute endpoint, we need to 
        // match the incoming endpoint from the last connection on stack to the
        // endpoint with same name from the sensor node (2 connections back)        
        ListIterator<GraphNodeConnection> connectionIt = visitedConnectionGraphStack.listIterator(visitedConnectionGraphStack.size());
        GraphNodeEndpoint sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
        GraphNode sensorNode = targetSensor;
         
        GraphNodeEndpoint match = null;
        for( GraphNodeEndpoint test : sensorNode.getEndpointDefinitions() ){
            if( test.getLabel().equals(sourceFilterEndpoint.getLabel())){
                match = test;
                break;
            }
        }
        
        if( match == null && !sourceFilterEndpoint.getLabel().equals("REC_TIMESTAMP")){
            LoggerService.log(Level.SEVERE, "[SparqlGenerator] Could not match filter node endpoint '"+sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
            return;
        }
         
        
        // Add a time filter clause for all queried attributes of this sensor
        Map<String, Set<String>> subQueriesPerSensor = this.whereSubQueriesPerSensorPerAttribute.get(sensorNode.getUID());
		if( subQueriesPerSensor == null ){
			LoggerService.log(Level.SEVERE, "[SparqlGenerator] Reached a comparison node with no queried attributes!");
			return;
		}

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
        long cmpValue = ((Number)node.getPropertyValueMap().get("CMP_VALUE")).longValue() * scaler;        
		
		for( Set<String> subQueryCode : subQueriesPerSensor.values() ){
	     
	        // Encode filter query in where statement
	        subQueryCode.add("\t?" + sensorNode.getUID() + "_record <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?" + sensorNode.getUID() + "_recordTime.");
	        subQueryCode.add("\tFILTER( bif:datediff('second', xsd:dateTime(str(?" + sensorNode.getUID() + "_recordTime)), NOW()) " + node.getPropertyValueMap().get("OPERATOR") + " " + cmpValue + ").");
		}
    }
    
    public void visit(org.openiot.ui.request.commons.nodes.impl.comparators.Between node) {
    	/*
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
        */
    }

    public void visit(org.openiot.ui.request.commons.nodes.impl.comparators.BetweenDateTime node) {
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
        
        if( match == null && !sourceFilterEndpoint.getLabel().equals("REC_TIMESTAMP")){
            LoggerService.log(Level.SEVERE, "[SparqlGenerator] Could not match filter node endpoint '"+sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
            return;
        }
         
        // Add a time filter clause for all queried attributes of this sensor
        Map<String, Set<String>> subQueriesPerSensor = this.whereSubQueriesPerSensorPerAttribute.get(sensorNode.getUID());
		if( subQueriesPerSensor == null ){
			LoggerService.log(Level.SEVERE, "[SparqlGenerator] Reached a comparison node with no queried attributes!");
			return;
		}

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
	
		for( Set<String> subQueryCode : subQueriesPerSensor.values() ){
	     
	        // Encode filter query in where statement
	        subQueryCode.add("\t?" + sensorNode.getUID() + "_record <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?" + sensorNode.getUID() + "_recordTime.");
	        subQueryCode.add("\tFILTER( ?" + sensorNode.getUID() + "_recordTime >= \"" + formattedFromDate + "\"^^xsd:date && ?" + sensorNode.getUID() + "_recordTime <= \"" + formattedToDate + "\"^^xsd:date ).");

		}
    }
    
    //-------------------------------------------------------------------------
    // Aggregator node visitors
    //-------------------------------------------------------------------------        
    public void visit(org.openiot.ui.request.commons.nodes.impl.aggegators.Min node) {
        this.generatedAttributeAggregationCode += "MIN( ";
        visitIncomingConnections(node);
        this.generatedAttributeAggregationCode += " )";
    }

    public void visit(org.openiot.ui.request.commons.nodes.impl.aggegators.Max node) {
        this.generatedAttributeAggregationCode += "MAX( ";
        visitIncomingConnections(node);
        this.generatedAttributeAggregationCode += " )";
    }

    public void visit(org.openiot.ui.request.commons.nodes.impl.aggegators.Count node) {
        this.generatedAttributeAggregationCode += "COUNT( ";
        visitIncomingConnections(node);
        this.generatedAttributeAggregationCode += " )";
    }

    public void visit(org.openiot.ui.request.commons.nodes.impl.aggegators.Sum node) {
        this.generatedAttributeAggregationCode += "SUM( ";
        visitIncomingConnections(node);
        this.generatedAttributeAggregationCode += " )";
    }

    public void visit(org.openiot.ui.request.commons.nodes.impl.aggegators.Average node) {
        this.generatedAttributeAggregationCode += "AVG( ";
        visitIncomingConnections(node);
        this.generatedAttributeAggregationCode += " )";
    }
}
