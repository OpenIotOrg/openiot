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
package sparql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.base.AbstractGraphNodeVisitor;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;
import org.openiot.ui.request.definition.web.model.nodes.impl.sinks.LineChart;
import org.openiot.ui.request.definition.web.model.nodes.impl.sources.GenericSource;

import sparql.nodes.base.AbstractSparqlNode;
import sparql.nodes.base.AggregateExpression;
import sparql.nodes.base.Comment;
import sparql.nodes.base.Expression;
import sparql.nodes.base.From;
import sparql.nodes.base.Group;
import sparql.nodes.base.Order;
import sparql.nodes.base.Root;
import sparql.nodes.base.Scope;
import sparql.nodes.base.Select;
import sparql.nodes.base.SensorSelectExpression;
import sparql.nodes.base.Where;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class Generator extends AbstractGraphNodeVisitor {

	private GraphModel model;
	// Nodes
	private Root primaryRootNode;
	private Comment primaryCommentNode;
	private Select primarySelectNode;
	private From primaryFromNode;
	private Where primaryWhereNode;
	private Group primaryGroupNode;
	//
	private AbstractSparqlNode subSelectOriginalNode;
	private AbstractSparqlNode subSelectNode;
	private Where subWhereNode;
	private Group subGroupNode;
	private Order subOrderNode;
	//
	private GenericSource targetDataSource;
	private GraphNodeEndpoint targetAttribute;
	private Set<String> visitedSensorTypes;
	private Map<GraphNodeProperty, Object> variableMap;
	private Stack<GraphNodeConnection> visitedConnectionGraphStack;
	// Generated code blocks
	private List<String> queryBlocks;

	public Generator() {
		this.visitedConnectionGraphStack = new Stack<GraphNodeConnection>();
		this.visitedSensorTypes = new LinkedHashSet<String>();
		this.variableMap = new LinkedHashMap<GraphNodeProperty, Object>();
		this.queryBlocks = new ArrayList<String>();
	}

	public List<String> generateQueriesForNodeEndpoints(GraphModel model, GraphNode visualizerNode) {
		this.model = model;
		reset();

		// Generate code for passed node
		visitViaReflection(visualizerNode);

		// Return output
		return this.queryBlocks;
	}

	public Map<GraphNodeProperty, Object> getVariableMap() {
		return variableMap;
	}

	private void reset() {
		this.visitedConnectionGraphStack.clear();
		this.visitedSensorTypes.clear();
		this.variableMap.clear();

		// Allocate initial nodes
		primaryCommentNode = new Comment();
		primarySelectNode = new Select();
		primaryFromNode = new From(AbstractSparqlNode.GRAPH_DATA_URI);
		primaryWhereNode = new Where();
		primaryGroupNode = new Group();

		primaryRootNode = new Root();
		primaryRootNode.appendToScope(primaryCommentNode);
		primaryRootNode.appendToScope(primarySelectNode);
		primaryRootNode.appendToScope(primaryFromNode);
		primaryRootNode.appendToScope(primaryWhereNode);
		primaryRootNode.appendToScope(primaryGroupNode);
	}

	private void beginQueryBlock(GraphNode visNode, int tupleIndex, int totalTuples) {
		reset();
		this.visitedConnectionGraphStack.clear();

		primaryCommentNode.appendComment("[" + tupleIndex + " / " + totalTuples + "] visualization type: '" + visNode.getLabel() + "' and sensors of type:");
		this.visitedSensorTypes.clear();
	}

	private void endQueryBlock() {
		// Setup comments
		primaryCommentNode.appendComment("\t - " + org.apache.commons.lang3.StringUtils.join(visitedSensorTypes, "\n#\t - "));
		primaryCommentNode.appendComment("Generated: " + (new Date()));
		if (this.variableMap.size() > 0) {
			primaryCommentNode.appendComment("Using " + this.variableMap.size() + " variable" + (this.variableMap.size() > 1 ? "s" : "") + ":");
			for (Map.Entry<GraphNodeProperty, Object> entry : variableMap.entrySet()) {
				primaryCommentNode.appendComment("- " + entry.getKey().getVariableName() + " (default value: " + entry.getValue() + ")");
			}
		}
		
		queryBlocks.add(primaryRootNode.generate());
	}

	/*
	 * private void genereteAttributeSubQuerySelectCode(GenericSource
	 * sensorNode, GraphNodeEndpoint attributeEndpoint, String
	 * attributeExpression) { Map<String, Set<String>> subQueriesPerSensor =
	 * this.selectSubQueriesPerSensorPerAttribute.get(sensorNode.getUID()); if
	 * (subQueriesPerSensor == null) { subQueriesPerSensor = new
	 * LinkedHashMap<String, Set<String>>();
	 * this.selectSubQueriesPerSensorPerAttribute.put(sensorNode.getUID(),
	 * subQueriesPerSensor); }
	 * 
	 * Set<String> subQueryCode =
	 * subQueriesPerSensor.get(attributeEndpoint.getUID()); if (subQueryCode ==
	 * null) { subQueryCode = new LinkedHashSet<String>();
	 * subQueriesPerSensor.put(attributeEndpoint.getUID(), subQueryCode); }
	 * 
	 * subQueryCode.add(attributeExpression); }
	 */

	private void generateAttributeSubQueryWhereCode(GenericSource sensorNode, GraphNodeEndpoint attributeEndpoint) {

		// Encode attribute selection
		if( attributeEndpoint != null ){
			subWhereNode.appendToScope(new Expression("?" + sensorNode.getUID() + "_record <http://lsm.deri.ie/ont/lsm.owl#value> ?" + attributeEndpoint.getUID() + " ."));
			subWhereNode.appendToScope(new Expression("?" + sensorNode.getUID() + "_record <http://www.w3.org/2000/01/rdf-schema#label> '" + attributeEndpoint.getUserData() + "' ."));
		}
		subWhereNode.appendToScope(new Expression("?" + sensorNode.getUID() + "_record <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?" + sensorNode.getUID() + "_recordTime ."));

		// Encode sensor selection expression
		GraphNodeProperty prop = sensorNode.getPropertyByName("LAT");
		Object latQuery = sensorNode.getPropertyValueMap().get("LAT");
		if (prop.isVariable()) {
			defineVariable(prop, latQuery);
			latQuery = "#" + prop.getVariableName() + "#";
		}
		prop = sensorNode.getPropertyByName("LON");
		Object lonQuery = sensorNode.getPropertyValueMap().get("LON");
		if (prop.isVariable()) {
			defineVariable(prop, lonQuery);
			lonQuery = "#" + prop.getVariableName() + "#";
		}
		prop = sensorNode.getPropertyByName("RADIUS");
		Object radiusQuery = sensorNode.getPropertyValueMap().get("RADIUS");
		if (prop.isVariable()) {
			defineVariable(prop, radiusQuery);
			radiusQuery = "#" + prop.getVariableName() + "#";
		}

		subWhereNode.appendToScope(new SensorSelectExpression(sensorNode.getUID(), latQuery, lonQuery, radiusQuery));
	}

	private void defineVariable(GraphNodeProperty property, Object defaultValue) {
		this.variableMap.put(property, defaultValue);
	}

	// -------------------------------------------------------------------------
	// Visitors
	// -------------------------------------------------------------------------
	@Override
	public void defaultVisit(GraphNode node) {
		if (node instanceof GenericSource) {
			visit((GenericSource) node);
			return;
		}

		if (node.getType().equals("SINK")) {
			if( node instanceof LineChart ){
				visitSink((LineChart)node);
			}else{
				visitSink(node);
			}
			return;
		}

		LoggerService.log(Level.SEVERE, "[SparqlGenerator] Default visitor called for node of class: " + node.getClass().getSimpleName());
	}
	
	public void visitSink(LineChart node){
		System.out.println("Visit LineChart version");

		String xAxisType = (String) node.getPropertyValueMap().get("X_AXIS_TYPE");
		
		// Group queries for xy tuples
		int seriesCount = Integer.valueOf((String) node.getPropertyValueMap().get("SERIES"));
		for (int i =0 ; i < seriesCount; i++) {
			// Start a new code block for each series
			beginQueryBlock(node, i+1, seriesCount);
			
			GraphNodeEndpoint xEndpoint = node.getEndpointByLabel("x" + (i+1));
			GraphNodeEndpoint yEndpoint = node.getEndpointByLabel("y" + (i+1));

			// Follow Y axis value
			for( GraphNodeConnection connection : model.findGraphEndpointConnections(yEndpoint)){
 
				Scope subScope = new Scope();
				primaryWhereNode.appendToScope(subScope);
	
				// Generate subquery helpers
				subSelectNode = subSelectOriginalNode = new Select();
				subWhereNode = new Where();
				subGroupNode = new Group();
				subOrderNode = new Order();
				subScope.appendToScope(subSelectNode);
				subScope.appendToScope(subWhereNode);
				subScope.appendToScope(subGroupNode);
				subScope.appendToScope(subOrderNode);
	
				// Explore graph till we reach a sensor node.
				targetDataSource = null;
				targetAttribute = null;
				this.visitedConnectionGraphStack.push(connection);
				this.visitViaReflection(connection.getSourceNode());
				this.visitedConnectionGraphStack.pop();

				subSelectNode.appendToScope(new Expression("AS ?" + yEndpoint.getLabel()));	
				primarySelectNode.appendToScope(new Expression("?" + yEndpoint.getLabel()));
			}
			
			// Process x axis endpoint
			if( xEndpoint != null ){
				for( GraphNodeConnection connection : model.findGraphEndpointConnections(xEndpoint)){
					if( xAxisType.equals("Date (observation)")){
						String timeComponent = connection.getSourceEndpoint().getLabel().replace("grp_recordTime_", "");
						subSelectNode.appendToScope(new Expression("( fn:"+ timeComponent +"-from-dateTime(?" + targetDataSource.getUID() + "_recordTime) ) AS ?" + xEndpoint.getLabel() + "_" + timeComponent));
						primarySelectNode.appendToScope(new Expression("?" + xEndpoint.getLabel() + "_" + timeComponent));
					}else{
						Scope subScope = new Scope();
						primaryWhereNode.appendToScope(subScope);
			
						// Generate subquery helpers
						subSelectNode = subSelectOriginalNode = new Select();
						subWhereNode = new Where();
						subGroupNode = new Group();
						subOrderNode = new Order();
						subScope.appendToScope(subSelectNode);
						subScope.appendToScope(subWhereNode);
						subScope.appendToScope(subGroupNode);
						subScope.appendToScope(subOrderNode);
			
						// Explore graph till we reach a sensor node.
						targetDataSource = null;
						targetAttribute = null;
						this.visitedConnectionGraphStack.push(connection);
						this.visitViaReflection(connection.getSourceNode());
						this.visitedConnectionGraphStack.pop();
			
						subSelectNode.appendToScope(new Expression("AS ?" + xEndpoint.getLabel()));		
						primarySelectNode.appendToScope(new Expression("?" + xEndpoint.getLabel()));
					}
				}
			}		

			endQueryBlock();
		}
	}

	public void visitSink(GraphNode node) {
		beginQueryBlock(node, 1, 1);
		
		// Visit incoming neighbors
		for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
			if (endpoint.getType().equals(EndpointType.Output)) {
				continue;
			}

			List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(endpoint);
			for (GraphNodeConnection connection : incomingConnections) {

				// Generate primary select
				primarySelectNode.appendToScope(new Expression("?" + endpoint.getLabel()));

				// Generate new scope for assembling the selection queries *unless* this is a grp_Date scope where
				// we re use the current 
				Scope subScope = new Scope();
				primaryWhereNode.appendToScope(subScope);

				// Generate subquery helpers
				subSelectNode = subSelectOriginalNode = new Select();
				subWhereNode = new Where();
				subGroupNode = new Group();
				subScope.appendToScope(subSelectNode);
				subScope.appendToScope(subWhereNode);
				subScope.appendToScope(subGroupNode);

				// Explore graph till we reach a sensor node.
				targetDataSource = null;
				targetAttribute = null;
				this.visitedConnectionGraphStack.push(connection);
				this.visitViaReflection(connection.getSourceNode());

				// Append the endpoint label to the end of the generated select
				// statement
				subSelectNode.appendToScope(new Expression("AS ?" + endpoint.getLabel()));

				//
				this.visitedConnectionGraphStack.pop();
			}
		}

		endQueryBlock();
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

	// -------------------------------------------------------------------------
	// Node-specific visitors
	// -------------------------------------------------------------------------
	public void visit(GenericSource node) {
		visitedSensorTypes.add(node.getLabel());

		// Examine the connection endpoint that lead us to the sensor
		GraphNodeConnection outgoingConnection = visitedConnectionGraphStack.peek();
		GraphNodeEndpoint sourceEndpoint = outgoingConnection.getSourceEndpoint();

		// Remember the sensor we landed to as well as the target attribute
		// endpoint
		targetDataSource = node;
		targetAttribute = sourceEndpoint;

		// Encode selection query
		subSelectNode.appendToScope(new Expression("?" + sourceEndpoint.getUID()));

		// Generate nested where clause for selected attribute
		this.generateAttributeSubQueryWhereCode(targetDataSource, targetAttribute);

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

	// -------------------------------------------------------------------------
	// Filter node visitors
	// -------------------------------------------------------------------------
	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.filters.SelectionFilter node) {

		// Visit all outgoing connections except the one connecting to the
		// sensor
		for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
			if (endpoint.getType().equals(EndpointType.Input)) {
				continue;
			}

			List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(endpoint);
			for (GraphNodeConnection connection : incomingConnections) {
				if (connection.getDestinationNode() instanceof GenericSource) {
					continue;
				}

				this.visitedConnectionGraphStack.push(connection);
				this.visitViaReflection(connection.getDestinationNode());
				this.visitedConnectionGraphStack.pop();
			}
		}
	}
	
	protected void generateTimeGroups(List<String> groupList){
	}

	@SuppressWarnings("unchecked")
	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.filters.Group node) {
		
		// Unwind the stack till we find the sink
		GraphNodeEndpoint ourEndpoint = null;
		ListIterator<GraphNodeConnection> connectionIt = visitedConnectionGraphStack.listIterator(visitedConnectionGraphStack.size());
		ourEndpoint = connectionIt.previous().getSourceEndpoint();
		
		// Get attributes endpoint
		GraphNodeEndpoint attributesEndpoint = node.getEndpointByLabel("ATTRIBUTES");
		this.targetDataSource = (GenericSource) model.findGraphEndpointConnections(attributesEndpoint).get(0).getSourceNode();

		// Generate groups
		List<String> groupList = (List<String>)node.getPropertyValueMap().get("GROUPS");
		for( String group : groupList ){
			String timeComponent = group.replace("recordTime_",  "");
			subGroupNode.appendToScope(new Expression("( fn:"+ timeComponent +"-from-dateTime(?" + targetDataSource.getUID() + "_recordTime) )"));
			subOrderNode.appendToScope(new Expression("( fn:"+ timeComponent +"-from-dateTime(?" + targetDataSource.getUID() + "_recordTime) )"));
		}
		
		// Follow the connection that matches our endpoint label (ie the currently grouped property)
		List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(attributesEndpoint);
		String attrName = ourEndpoint.getLabel().replace("grp_", "");
		for (GraphNodeConnection connection : incomingConnections) {
			if(!attrName.equals(connection.getSourceEndpoint().getLabel())){
				continue;
			}
			
			this.visitedConnectionGraphStack.push(connection);
			this.visitViaReflection(connection.getSourceNode());
			this.visitedConnectionGraphStack.pop();
		}			
	}

	// -------------------------------------------------------------------------
	// Comparator node visitors
	// -------------------------------------------------------------------------
	/*
	 * public void
	 * visit(org.openiot.ui.request.definition.web.model.nodes.impl.comparators
	 * .Compare node) {
	 * 
	 * // Since the filter node cloned the original attribute endpoint, we need
	 * to // match the incoming endpoint from the last connection on stack to
	 * the // endpoint with same name from the sensor node (2 connections back)
	 * ListIterator<GraphNodeConnection> connectionIt =
	 * visitedConnectionGraphStack
	 * .listIterator(visitedConnectionGraphStack.size()); GraphNodeEndpoint
	 * sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
	 * GraphNode sensorNode = connectionIt.previous().getDestinationNode();
	 * 
	 * GraphNodeEndpoint match = null; for( GraphNodeEndpoint test :
	 * sensorNode.getEndpointDefinitions() ){ if(
	 * test.getLabel().equals(sourceFilterEndpoint.getLabel())){ match = test;
	 * break; } }
	 * 
	 * if( match == null ){ LoggerService.log(Level.SEVERE,
	 * "[SparqlGenerator] Could not match filter node endpoint '"
	 * +sourceFilterEndpoint.getLabel()+"' to original sensor endpoint");
	 * return; }
	 * 
	 * // Encode attribute selection queries in where statement
	 * this.encodePropertySelectionFilters(sensorNode, match);
	 * 
	 * // Encode filter query in where statement
	 * this.generatedWhereCode.add("\tFILTER( ?" + match.getUID() + " " +
	 * node.getPropertyValueMap().get("OPERATOR") + " " +
	 * node.getPropertyValueMap().get("CMP_VALUE") + " ).");
	 * 
	 * }
	 */

	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.comparators.CompareAbsoluteDateTime node) {
		// Generate date string in appropriate format.
		// xsd:datetime type formats dates a bit differently than the pattern
		// used by simple date
		// format (separates hours and mins of the timezone with a colon) so we
		// need to patch it here

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date cmpValue = (Date) node.getPropertyValueMap().get("CMP_VALUE");
		String formattedDate = sdf.format(cmpValue);
		formattedDate = formattedDate.substring(0, formattedDate.length() - 2) + ":" + formattedDate.substring(formattedDate.length() - 2);

		GraphNodeProperty prop = node.getPropertyByName("CMP_VALUE");
		if (prop.isVariable()) {
			defineVariable(prop, formattedDate);
			formattedDate = "#" + prop.getVariableName() + "#";
		}

		subWhereNode.appendToScope(new Expression("FILTER (?" + targetDataSource.getUID() + "_recordTime " + node.getPropertyValueMap().get("OPERATOR") + " \"" + formattedDate + "\"^^xsd:date )."));
	}

	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.comparators.CompareRelativeDateTime node) {

		long scaler = 1L;
		String unit = (String) node.getPropertyValueMap().get("CMP_VALUE_UNIT");
		if ("SECOND(S)".equals(unit)) {
			scaler = 1L;
		} else if ("MINUTE(S)".equals(unit)) {
			scaler = 60L;
		} else if ("HOUR(S)".equals(unit)) {
			scaler = 60 * 60L;
		} else if ("DAY(S)".equals(unit)) {
			scaler = 24 * 60 * 60L;
		} else if ("MONTH(S)".equals(unit)) {
			scaler = 30 * 24 * 60 * 60L;
		} else if ("YEAR(S)".equals(unit)) {
			scaler = 365 * 24 * 60 * 60L;
		}
		String cmpValue = "" + ((Number) node.getPropertyValueMap().get("CMP_VALUE")).longValue() * scaler;

		GraphNodeProperty prop = node.getPropertyByName("CMP_VALUE");
		if (prop.isVariable()) {
			defineVariable(prop, cmpValue);
			cmpValue = "#" + prop.getVariableName() + "#";
		}

		subWhereNode.appendToScope(new Expression("FILTER( bif:datediff('second', xsd:dateTime(str(?" + targetDataSource.getUID() + "_recordTime)), NOW()) " + node.getPropertyValueMap().get("OPERATOR") + " " + cmpValue + ")."));
	}

	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.comparators.BetweenDateTime node) {
		// Generate date string in appropriate format.
		// xsd:datetime type formats dates a bit differently than the pattern
		// used by simple date
		// format (separates hours and mins of the timezone with a colon) so we
		// need to patch it here
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date cmpFromValue = (Date) node.getPropertyValueMap().get("CMP_VALUE1");
		String formattedFromDate = sdf.format(cmpFromValue);
		formattedFromDate = formattedFromDate.substring(0, formattedFromDate.length() - 2) + ":" + formattedFromDate.substring(formattedFromDate.length() - 2);
		Date cmpToValue = (Date) node.getPropertyValueMap().get("CMP_VALUE2");
		String formattedToDate = sdf.format(cmpToValue);
		formattedToDate = formattedToDate.substring(0, formattedToDate.length() - 2) + ":" + formattedToDate.substring(formattedToDate.length() - 2);

		GraphNodeProperty prop = node.getPropertyByName("CMP_VALUE1");
		if (prop.isVariable()) {
			defineVariable(prop, formattedFromDate);
			formattedFromDate = "#" + prop.getVariableName() + "#";
		}

		prop = node.getPropertyByName("CMP_VALUE2");
		if (prop.isVariable()) {
			defineVariable(prop, formattedToDate);
			formattedToDate = "#" + prop.getVariableName() + "#";
		}

		subWhereNode.appendToScope(new Expression("FILTER (?" + targetDataSource.getUID() + "_recordTime >= \"" + formattedFromDate + "\"^^xsd:date && ?" + targetDataSource.getUID() + "_recordTime <= \"" + formattedToDate + "\"^^xsd:date )."));
	}

	/**
	 * public void
	 * visit(org.openiot.ui.request.definition.web.model.nodes.impl.comparators
	 * .Between node) {
	 * 
	 * // Since the filter node cloned the original attribute endpoint, we need
	 * // to // match the incoming endpoint from the last connection on stack to
	 * the // endpoint with same name from the sensor node (2 connections back)
	 * ListIterator<GraphNodeConnection> connectionIt =
	 * visitedConnectionGraphStack
	 * .listIterator(visitedConnectionGraphStack.size()); GraphNodeEndpoint
	 * sourceFilterEndpoint = connectionIt.previous().getSourceEndpoint();
	 * GraphNode sensorNode = connectionIt.previous().getDestinationNode();
	 * 
	 * GraphNodeEndpoint match = null; for (GraphNodeEndpoint test :
	 * sensorNode.getEndpointDefinitions()) { if
	 * (test.getLabel().equals(sourceFilterEndpoint.getLabel())) { match = test;
	 * break; } }
	 * 
	 * if (match == null) { LoggerService.log(Level.SEVERE,
	 * "[SparqlGenerator] Could not match filter node endpoint '" +
	 * sourceFilterEndpoint.getLabel() + "' to original sensor endpoint");
	 * return; }
	 * 
	 * // Encode attribute selection queries in where statement
	 * this.encodePropertySelectionFilters(sensorNode, match);
	 * 
	 * // Encode filter query in where statement
	 * this.generatedWhereCode.add("\tFILTER( ?" + match.getUID() + " >= " +
	 * node.getPropertyValueMap().get("CMP_VALUE1") + " && ?" + match.getUID() +
	 * " <= " + node.getPropertyValueMap().get("CMP_VALUE2") + " ).");
	 * 
	 * }
	 */

	// -------------------------------------------------------------------------
	// Aggregator node visitors
	// -------------------------------------------------------------------------
	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.aggegators.Min node) {
		AbstractSparqlNode current = subSelectNode;
		subSelectNode = subSelectNode.appendToScope(new AggregateExpression("MIN"));
		visitIncomingConnections(node);
		subSelectNode = current;
	}

	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.aggegators.Max node) {
		AbstractSparqlNode current = subSelectNode;
		subSelectNode = subSelectNode.appendToScope(new AggregateExpression("MAX"));
		visitIncomingConnections(node);
		subSelectNode = current;
	}

	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.aggegators.Count node) {
		AbstractSparqlNode current = subSelectNode;
		subSelectNode = subSelectNode.appendToScope(new AggregateExpression("COUNT"));
		visitIncomingConnections(node);
		subSelectNode = current;
	}

	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.aggegators.Sum node) {
		AbstractSparqlNode current = subSelectNode;
		subSelectNode = subSelectNode.appendToScope(new AggregateExpression("SUM"));
		visitIncomingConnections(node);
		subSelectNode = current;
	}

	public void visit(org.openiot.ui.request.definition.web.model.nodes.impl.aggegators.Average node) {
		AbstractSparqlNode current = subSelectNode;
		subSelectNode = subSelectNode.appendToScope(new AggregateExpression("AVG"));
		visitIncomingConnections(node);
		subSelectNode = current;
	}
}
