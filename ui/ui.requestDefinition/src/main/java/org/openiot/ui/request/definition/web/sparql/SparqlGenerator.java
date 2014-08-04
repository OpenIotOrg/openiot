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

package org.openiot.ui.request.definition.web.sparql;

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
import org.openiot.ui.request.definition.web.model.nodes.impl.sinks.Passthrough;
import org.openiot.ui.request.definition.web.model.nodes.impl.sinks.Pie;
import org.openiot.ui.request.definition.web.model.nodes.impl.sources.GenericSource;
import org.openiot.ui.request.definition.web.sparql.nodes.base.AbstractSparqlNode;
import org.openiot.ui.request.definition.web.sparql.nodes.base.AggregateExpression;
import org.openiot.ui.request.definition.web.sparql.nodes.base.Comment;
import org.openiot.ui.request.definition.web.sparql.nodes.base.Expression;
import org.openiot.ui.request.definition.web.sparql.nodes.base.From;
import org.openiot.ui.request.definition.web.sparql.nodes.base.Group;
import org.openiot.ui.request.definition.web.sparql.nodes.base.Order;
import org.openiot.ui.request.definition.web.sparql.nodes.base.Root;
import org.openiot.ui.request.definition.web.sparql.nodes.base.Scope;
import org.openiot.ui.request.definition.web.sparql.nodes.base.Select;
import org.openiot.ui.request.definition.web.sparql.nodes.base.SensorSelectExpression;
import org.openiot.ui.request.definition.web.sparql.nodes.base.Where;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class SparqlGenerator extends AbstractGraphNodeVisitor {

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
	private boolean sinkNodeNeedsGeoCoords;
	//
	private GenericSource targetDataSource;
	private GraphNodeEndpoint targetAttribute;
	private Set<String> visitedSensorTypes;
	private Map<GraphNodeProperty, Object> variableMap;
	private Stack<GraphNodeConnection> visitedConnectionGraphStack;
	// Generated code blocks
	private List<String> queryBlocks;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public SparqlGenerator() {
		this.visitedConnectionGraphStack = new Stack<GraphNodeConnection>();
		this.visitedSensorTypes = new LinkedHashSet<String>();
		this.variableMap = new LinkedHashMap<GraphNodeProperty, Object>();
		this.queryBlocks = new ArrayList<String>();
	}

	public List<String> generateQueriesForNodeEndpoints(GraphModel model, GraphNode visualizerNode) {
		this.model = model;
		this.queryBlocks.clear();
		reset();

		// Generate code for passed node
		visitSink(visualizerNode);

		// Return output
		return this.queryBlocks;
	}

	public Map<GraphNodeProperty, Object> getVariableMap() {
		return variableMap;
	}

	// -------------------------------------------------------------------------
	// Query blocks
	// -------------------------------------------------------------------------

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

	private void generateAttributeSubQueryWhereCode(GenericSource sensorNode, GraphNodeEndpoint attributeEndpoint) {

		// Encode attribute selection
		if (attributeEndpoint != null) {
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

		subWhereNode.appendToScope(new SensorSelectExpression(sensorNode.getUID(),sensorNode.getLabel() ,latQuery, lonQuery, radiusQuery, this.sinkNodeNeedsGeoCoords));
	}

	private void defineVariable(GraphNodeProperty property, Object defaultValue) {
		this.variableMap.put(property, defaultValue);
	}

	// -------------------------------------------------------------------------
	// Generic non-sink node visitors
	// -------------------------------------------------------------------------
	@Override
	public void defaultVisit(GraphNode node) {
		if (node instanceof GenericSource) {
			visit((GenericSource) node);
			return;
		}

		LoggerService.log(Level.SEVERE, "[SparqlGenerator] Default visitor called for node of class: " + node.getClass().getSimpleName());
	}

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
	// Sink node Visitors
	// -------------------------------------------------------------------------

	public void visitSink(GraphNode node) {
		// Only process the viz node that was passed to the
		// 'generateQueriesForNodeEndpoints' method
		if (!node.getType().equals("SINK")) {
			return;
		}

		this.sinkNodeNeedsGeoCoords = false;

		if (node instanceof LineChart) {
			visitLineChartSink((LineChart) node);
		} else if (node instanceof Passthrough) {
			visitPassthroughSink((Passthrough) node);
		} else if (node instanceof org.openiot.ui.request.definition.web.model.nodes.impl.sinks.Map) {
			visitMapSink((org.openiot.ui.request.definition.web.model.nodes.impl.sinks.Map) node);
		} else if (node instanceof Pie) {
			visitPieSink((Pie) node);
		} else {
			visitGenericSink(node);
		}
	}

	public void visitGenericSink(GraphNode node) {

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

				// Generate new scope for assembling the selection queries
				// *unless* this is a grp_Date scope where
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

	public void visitMapSink(org.openiot.ui.request.definition.web.model.nodes.impl.sinks.Map node) {
		this.sinkNodeNeedsGeoCoords = true;
		beginQueryBlock(node, 1, 1);

		// Visit incoming neighbors
		for (GraphNodeEndpoint endpoint : node.getEndpointDefinitions()) {
			if (endpoint.getType().equals(EndpointType.Output)) {
				continue;
			}

			// If this is a LAT or LON connection, skip it because we will
			// include it
			// together with the VALUE endpoint
			if (endpoint.getScope().contains("geo_lat") || endpoint.getScope().contains("geo_lon")) {
				continue;
			}

			LoggerService.log(Level.INFO, "Visit endpoint with scope: " + endpoint.getScope());

			List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(endpoint);
			for (GraphNodeConnection connection : incomingConnections) {

				// Generate primary select (also include LAT/LON fields)
				primarySelectNode.appendToScope(new Expression("?" + endpoint.getLabel()));
				primarySelectNode.appendToScope(new Expression("?LAT"));
				primarySelectNode.appendToScope(new Expression("?LON"));

				// Generate new scope for assembling the selection queries
				// *unless* this is a grp_Date scope where
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
				subSelectNode.appendToScope(new Expression("?" + targetDataSource.getUID() + "_lat AS ?LAT"));
				subSelectNode.appendToScope(new Expression("?" + targetDataSource.getUID() + "_lon AS ?LON"));

				//
				this.visitedConnectionGraphStack.pop();
			}
		}

		endQueryBlock();
	}

	public void visitLineChartSink(LineChart node) {
		String xAxisType = (String) node.getPropertyValueMap().get("X_AXIS_TYPE");

		// Group queries for xy tuples
		int seriesCount = Integer.valueOf((String) node.getPropertyValueMap().get("SERIES"));
		for (int i = 0; i < seriesCount; i++) {
			// Start a new code block for each series
			beginQueryBlock(node, i + 1, seriesCount);

			GraphNodeEndpoint xEndpoint = node.getEndpointByLabel("x" + (i + 1));
			GraphNodeEndpoint yEndpoint = node.getEndpointByLabel("y" + (i + 1));

			// Follow Y axis value
			for (GraphNodeConnection connection : model.findGraphEndpointConnections(yEndpoint)) {

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
			if (xEndpoint != null) {
				for (GraphNodeConnection connection : model.findGraphEndpointConnections(xEndpoint)) {
					if (xAxisType.equals("Date (observation)")) {
						String timeComponent = connection.getSourceEndpoint().getLabel().replace("grp_recordTime_", "");
						// Note: We need to apply an aggregation function to
						// timestamp components
						// for the value grouping to work
						subSelectNode.appendToScope(new Expression("AVG( fn:" + timeComponent + "-from-dateTime(?" + targetDataSource.getUID() + "_recordTime) ) AS ?" + xEndpoint.getLabel() + "_" + timeComponent));
						primarySelectNode.appendToScope(new Expression("?" + xEndpoint.getLabel() + "_" + timeComponent));
					} else {
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

	public void visitPassthroughSink(Passthrough node) {

		// Generate one query per attribute
		int attrCount = Integer.valueOf(node.getPropertyValueMap().get("ATTRIBUTES").toString());
		for (int i = 0; i < attrCount; i++) {
			// Start a new code block for each attribute
			beginQueryBlock(node, i + 1, attrCount);

			GraphNodeEndpoint attrEndpoint = node.getEndpointByLabel("attr" + (i + 1));

			// Follow attr value
			for (GraphNodeConnection connection : model.findGraphEndpointConnections(attrEndpoint)) {

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

				subSelectNode.appendToScope(new Expression("AS ?" + attrEndpoint.getLabel()));
				primarySelectNode.appendToScope(new Expression("?" + attrEndpoint.getLabel()));
			}

			endQueryBlock();
		}
	}

	public void visitPieSink(Pie node) {

		// Generate one query per attribute
		int seriesCount = Integer.valueOf((String) node.getPropertyValueMap().get("SERIES"));
		for (int i = 0; i < seriesCount; i++) {
			// Start a new code block for each series
			beginQueryBlock(node, i + 1, seriesCount);

			GraphNodeEndpoint attrEndpoint = node.getEndpointByLabel("y" + (i + 1));

			// Follow attr value
			for (GraphNodeConnection connection : model.findGraphEndpointConnections(attrEndpoint)) {

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

				subSelectNode.appendToScope(new Expression("AS ?" + attrEndpoint.getLabel()));
				primarySelectNode.appendToScope(new Expression("?" + attrEndpoint.getLabel()));
			}

			endQueryBlock();
		}
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

	protected void generateTimeGroups(List<String> groupList) {
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
		List<String> groupList = (List<String>) node.getPropertyValueMap().get("GROUPS");
		for (String group : groupList) {
			String timeComponent = group.replace("recordTime_", "");
			subGroupNode.appendToScope(new Expression("( fn:" + timeComponent + "-from-dateTime(?" + targetDataSource.getUID() + "_recordTime) )"));
			subOrderNode.appendToScope(new Expression("( fn:" + timeComponent + "-from-dateTime(?" + targetDataSource.getUID() + "_recordTime) )"));
		}

		// Follow the connection that matches our endpoint label (ie the
		// currently grouped property)
		List<GraphNodeConnection> incomingConnections = model.findGraphEndpointConnections(attributesEndpoint);
		String attrName = ourEndpoint.getLabel().replace("grp_", "");
		for (GraphNodeConnection connection : incomingConnections) {
			if (!attrName.equals(connection.getSourceEndpoint().getLabel())) {
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
		String cmpValue = "" + Long.valueOf(node.getPropertyValueMap().get("CMP_VALUE").toString()) * scaler;

		GraphNodeProperty prop = node.getPropertyByName("CMP_VALUE");
		if (prop.isVariable()) {
			defineVariable(prop, cmpValue);
			cmpValue = "#" + prop.getVariableName() + "#";
		}

		subWhereNode.appendToScope(new Expression("FILTER( bif:datediff('second', xsd:dateTime(str(?" + targetDataSource.getUID() + "_recordTime)), bif:curdatetime()) " + node.getPropertyValueMap().get("OPERATOR") + " " + cmpValue + ")."));
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

		subWhereNode.appendToScope(new Expression("FILTER (?" + targetDataSource.getUID() + "_recordTime >= \"" + formattedFromDate + "\"^^xsd:date AND ?" + targetDataSource.getUID() + "_recordTime <= \"" + formattedToDate + "\"^^xsd:date )."));
	}

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
