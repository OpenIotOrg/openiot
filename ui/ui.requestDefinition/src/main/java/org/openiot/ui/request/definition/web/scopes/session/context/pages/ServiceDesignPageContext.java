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
package org.openiot.ui.request.definition.web.scopes.session.context.pages;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.sensortypes.model.MeasurementCapability;
import org.openiot.commons.sensortypes.model.SensorType;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.ui.request.commons.annotations.scanners.GraphNodeScanner;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.definition.web.model.nodes.impl.sources.GenericSource;
import org.openiot.ui.request.definition.web.model.validation.GraphValidationError;
import org.openiot.ui.request.definition.web.model.validation.GraphValidationWarning;
import org.openiot.ui.request.definition.web.scopes.session.base.DisposableContext;
import org.primefaces.extensions.model.dynaform.DynaFormModel;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class ServiceDesignPageContext extends DisposableContext {

	// The Node graph model
	private OSDSpec osdSpec;
	private OAMO selectedOAMO;
	private GraphModel graphModel;
	// The active property editor form model
	private DynaFormModel propertyEditorModel;
	// The generated (prettyprinted) SPARQL code
	private String generatedCode;
	// The graph validation output (warnings and errors)
	private List<GraphValidationError> graphValidationErrors;
	private List<GraphValidationWarning> graphValidationWarnings;
	// A list of available nodes grouped by type (sensors are always first)
	private Map<String, List<GraphNode>> availableNodesByTypeMap;
	// Selected console tab index
	private int selectedConsoleTabIndex;
	// Selected filters
	private double filterLocationLat;
	private double filterLocationLon;
	private double filterLocationRadius;
	// New application
	private String newServiceName;
	private String newServiceDescription;

	public ServiceDesignPageContext() {
		super();
		this.register();

		graphValidationErrors = new ArrayList<GraphValidationError>();
		graphValidationWarnings = new ArrayList<GraphValidationWarning>();
		availableNodesByTypeMap = new TreeMap<String, List<GraphNode>>();

		// Scan for available nodes
		detectAvailableNodes();
	}

	@Override
	public String getContextUID() {
		return "serviceDesignPageContext";
	}

	// --------------------------------------------------------------------------
	// Workspace
	// --------------------------------------------------------------------------

	public GraphModel getGraphModel() {
		return graphModel;
	}

	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	public DynaFormModel getPropertyEditorModel() {
		return propertyEditorModel;
	}

	public void setPropertyEditorModel(DynaFormModel propertyEditorModel) {
		this.propertyEditorModel = propertyEditorModel;
	}

	public String getGeneratedCode() {
		return generatedCode;
	}

	public void setGeneratedCode(String generatedCode) {
		this.generatedCode = generatedCode;
	}

	public List<GraphValidationError> getGraphValidationErrors() {
		return graphValidationErrors;
	}

	public void setGraphValidationErrors(List<GraphValidationError> graphValidationErrors) {
		this.graphValidationErrors = graphValidationErrors;
	}

	public List<GraphValidationWarning> getGraphValidationWarnings() {
		return graphValidationWarnings;
	}

	public void setGraphValidationWarnings(List<GraphValidationWarning> graphValidationWarnings) {
		this.graphValidationWarnings = graphValidationWarnings;
	}

	public Map<String, List<GraphNode>> getAvailableNodesByTypeMap() {
		return availableNodesByTypeMap;
	}

	public int getSelectedConsoleTabIndex() {
		return selectedConsoleTabIndex;
	}

	public void setSelectedConsoleTabIndex(int selectedConsoleTabIndex) {
		this.selectedConsoleTabIndex = selectedConsoleTabIndex;
	}

	public void cleanupWorkspace() {
		if( graphModel != null ){
			graphModel.clear();
		}
		graphValidationErrors.clear();
		graphValidationWarnings.clear();
		generatedCode = null;

		filterLocationLat = 0;
		filterLocationLon = 0;
		filterLocationRadius = 0;
		clearAvailableSensors();
	}

	// --------------------------------------------------------------------------
	// Sensor lookup
	// --------------------------------------------------------------------------

	public double getFilterLocationLat() {
		return filterLocationLat;
	}

	public void setFilterLocationLat(double filterLocationLat) {
		this.filterLocationLat = filterLocationLat;
	}

	public double getFilterLocationLon() {
		return filterLocationLon;
	}

	public void setFilterLocationLon(double filterLocationLon) {
		this.filterLocationLon = filterLocationLon;
	}

	public double getFilterLocationRadius() {
		return filterLocationRadius;
	}

	public void setFilterLocationRadius(double filterLocationRadius) {
		this.filterLocationRadius = filterLocationRadius;
	}

	public void clearAvailableSensors(){
		List<GraphNode> sensorList = availableNodesByTypeMap.get("SOURCE");
		sensorList.clear();
	}
	
	public void updateAvailableSensors(SensorTypes sensorTypes) {
		List<GraphNode> sensorList = availableNodesByTypeMap.get("SOURCE");
		sensorList.clear();
		for (SensorType sensorType : sensorTypes.getSensorType()) {
			GenericSource source = new GenericSource();
			source.setLabel(sensorType.getName());
			source.setType("SOURCE");

			// Copy selected filter params
			source.getPropertyValueMap().put("LAT", filterLocationLat);
			source.getPropertyValueMap().put("LON", filterLocationLon);
			source.getPropertyValueMap().put("RADIUS", filterLocationRadius);

			// Initialize sensor endpoints
			List<GraphNodeEndpoint> endpointList = new ArrayList<GraphNodeEndpoint>();
			source.setEndpointDefinitions(endpointList);

			// Add an additional endpoint for filtering options
			GraphNodeEndpoint endpoint = new DefaultGraphNodeEndpoint();
			endpoint.setAnchor(AnchorType.Left);
			endpoint.setConnectorType(ConnectorType.Dot);
			endpoint.setMaxConnections(1);
			endpoint.setRequired(false);
			endpoint.setType(EndpointType.Input);
			endpoint.setLabel("SEL_FILTER_IN");
			endpoint.setScope("Sensor");
			endpointList.add(endpoint);

			for (MeasurementCapability cap : sensorType.getMeasurementCapability()) {
				if (cap.getUnit() == null || cap.getUnit().isEmpty()) {
					continue;
				}

				endpoint = new DefaultGraphNodeEndpoint();
				endpoint.setAnchor(AnchorType.Right);
				endpoint.setConnectorType(ConnectorType.Rectangle);
				endpoint.setMaxConnections(-1);
				endpoint.setRequired(false);
				endpoint.setType(EndpointType.Output);
				String label = cap.getType();
				if (label.contains("#")) {
					label = label.substring(label.indexOf('#') + 1);
				}
				if (!cap.getUnit().isEmpty() && cap.getUnit().get(0).getName() != null && !cap.getUnit().get(0).getName().equals("null") && !cap.getUnit().get(0).getName().isEmpty()) {
					label += " (" + cap.getUnit().get(0).getName() + ")";
				}
				endpoint.setLabel(label);
				endpoint.setUserData(cap.getType());

				String scope = "Number";
				String capScope = cap.getUnit().get(0).getType();
				if( StringUtils.containsIgnoreCase(capScope, "Int")){
					scope = "Integer";
				} else if( StringUtils.containsIgnoreCase(capScope, "Long")){
					scope = "Long";
				} else if( StringUtils.containsIgnoreCase(capScope, "Float")){
					scope = "Float";
				} else if( StringUtils.containsIgnoreCase(capScope, "Double")){
					scope = "Double";
				} else if( StringUtils.containsIgnoreCase(capScope, "Decimal")){
					scope = "Number";
				} else if( StringUtils.containsIgnoreCase(capScope, "Date")){
					scope = "Date";
				}
				
				endpoint.setScope("sensor_" + scope);
			
				// Add to endpoint list
				endpointList.add(endpoint);
			}
			
			// Add additional endpoint for sensor lat
			endpoint = new DefaultGraphNodeEndpoint();
			endpoint.setAnchor(AnchorType.Right);
			endpoint.setConnectorType(ConnectorType.Rectangle);
			endpoint.setMaxConnections(-1);
			endpoint.setRequired(false);
			endpoint.setType(EndpointType.Output);
			endpoint.setLabel("LAT");
			endpoint.setUserData("geo:lat");
			endpoint.setScope("geo_lat");
			endpointList.add(endpoint);

			// Add additional endpoint for sensor lon
			endpoint = new DefaultGraphNodeEndpoint();
			endpoint.setAnchor(AnchorType.Right);
			endpoint.setConnectorType(ConnectorType.Rectangle);
			endpoint.setMaxConnections(-1);
			endpoint.setRequired(false);
			endpoint.setType(EndpointType.Output);
			endpoint.setLabel("LON");
			endpoint.setUserData("geo:lon");
			endpoint.setScope("geo_lon");
			endpointList.add(endpoint);

			// Add sensor to toolbox
			sensorList.add(source);
		}
	}

	// --------------------------------------------------------------------------
	// Service management
	// --------------------------------------------------------------------------

	public OSDSpec getOsdSpec() {
		return osdSpec;
	}

	public void setOsdSpec(OSDSpec osdSpec) {
		this.osdSpec = osdSpec;
		cleanupWorkspace();
		setSelectedOAMO(null);
	}

	public OAMO getSelectedOAMO() {
		return selectedOAMO;
	}

	public void setSelectedOAMO(OAMO selectedOAMO) {
		this.selectedOAMO = selectedOAMO;
	}

	public Map<String, OAMO> getOAMOMap() {
		Map<String, OAMO> map = new LinkedHashMap<String, OAMO>();
		for (OAMO oamo : osdSpec.getOAMO()) {
			map.put(oamo.getName(), oamo);
		}
		return map;
	}

	// --------------------------------------------------------------------------
	// New service
	// --------------------------------------------------------------------------

	public String getNewServiceName() {
		return newServiceName;
	}

	public void setNewServiceName(String newServiceName) {
		this.newServiceName = newServiceName;
	}

	public String getNewServiceDescription() {
		return newServiceDescription;
	}

	public void setNewServiceDescription(String newServiceDescription) {
		this.newServiceDescription = newServiceDescription;
	}

	// --------------------------------------------------------------------------
	// GraphNode annotation scanner
	// --------------------------------------------------------------------------
	private void detectAvailableNodes() {
		availableNodesByTypeMap.clear();
		availableNodesByTypeMap.put("SOURCE", new ArrayList<GraphNode>());

		LoggerService.log(Level.FINE, "[ServiceDesignPageContext] Scanning for available graph node classes");
		Set<Class<?>> graphNodeClasses = GraphNodeScanner.detectGraphNodeClasses("org.openiot.ui.request.definition.web.model.nodes.impl");
		if (graphNodeClasses == null) {
			LoggerService.log(Level.WARNING, "[ServiceDesignPageContext] No graph node classes detected");
			return;
		}

		for (Class<?> classDefinition : graphNodeClasses) {
			try {
				LoggerService.log(Level.FINE, "[ServiceDesignPageContext]   Detected graph node class: " + classDefinition.getCanonicalName());
				GraphNode instance = (GraphNode) (classDefinition.newInstance());

				// Lookup type
				String type = instance.getType();
				if (type == null) {
					type = "UNKNOWN";
				}
				List<GraphNode> groupList = availableNodesByTypeMap.get(type);
				if (groupList == null) {
					groupList = new ArrayList<GraphNode>();
					availableNodesByTypeMap.put(type, groupList);
				}

				groupList.add(instance);

			} catch (Throwable ex) {
				LoggerService.log(ex);
			}
		}
	}
}
