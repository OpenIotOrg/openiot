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
package org.openiot.ui.request.definition.web.scopes.controllers.pages;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openiot.commons.osdspec.model.DynamicAttrMaxValue;
import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.QueryControls;
import org.openiot.commons.osdspec.model.QuerySchedule;
import org.openiot.commons.osdspec.model.RequestPresentation;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.models.DefaultGraphModel;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;
import org.openiot.ui.request.commons.nodes.validation.validators.DefaultGraphNodeValidator;
import org.openiot.ui.request.commons.providers.SchedulerAPIWrapper;
import org.openiot.ui.request.commons.providers.exceptions.APIException;
import org.openiot.ui.request.definition.web.factory.PropertyGridFormFactory;
import org.openiot.ui.request.definition.web.generator.SparqlGenerator;
import org.openiot.ui.request.definition.web.jsf.components.events.NodeInsertedEvent;
import org.openiot.ui.request.definition.web.model.nodes.impl.sensors.GenericSensor;
import org.openiot.ui.request.definition.web.scopes.application.ApplicationBean;
import org.openiot.ui.request.definition.web.scopes.session.SessionBean;
import org.openiot.ui.request.definition.web.scopes.session.context.dialogs.FindSensorDialogContext;
import org.openiot.ui.request.definition.web.scopes.session.context.pages.ServiceDesignPageContext;
import org.openiot.ui.request.definition.web.util.FaceletLocalization;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "serviceDesignPageController")
@RequestScoped
public class ServiceDesignPageController implements Serializable {
	private static final long serialVersionUID = 1L;

	// Cached context
	private ServiceDesignPageContext cachedContext;
	// Injected properties
	@ManagedProperty(value = "#{applicationBean}")
	protected transient ApplicationBean applicationBean;
	@ManagedProperty(value = "#{sessionBean}")
	protected transient SessionBean sessionBean;
	protected transient ResourceBundle messages;

	public ServiceDesignPageController() {
		this.messages = FaceletLocalization.getLocalizedResourceBundle();
	}

	public ServiceDesignPageContext getContext() {
		if (cachedContext == null) {
			cachedContext = (ServiceDesignPageContext) (sessionBean == null ? ApplicationBean.lookupSessionBean() : sessionBean).getContext("serviceDesignPageContext");
			if (cachedContext == null) {
				cachedContext = new ServiceDesignPageContext();
				reloadServices();
			}
		}
		return cachedContext;
	}

	// ------------------------------------
	// Controller entrypoints
	// ------------------------------------
	public void keepAlivePing() {

	}

	// ------------------------------------
	// Controller for OAMO editing
	// ------------------------------------

	public void onGraphNodeSelected() {
		ServiceDesignPageContext context = getContext();

		if (context.getGraphModel().getSelectedNode() != null) {
			context.setPropertyEditorModel(PropertyGridFormFactory.generatePropertyGridDynaForm(context.getGraphModel().getSelectedNode()));
		} else {
			context.setPropertyEditorModel(null);
		}
	}

	public void onGraphNodeInserted(NodeInsertedEvent event) {
		ServiceDesignPageContext context = getContext();

		// Lookup node category
		List<GraphNode> nodesInGroup = context.getAvailableNodesByTypeMap().get(event.getNodeGroup());
		if (nodesInGroup == null) {
			return;
		}

		// Lookup node type
		for (GraphNode node : nodesInGroup) {
			if (!node.getLabel().equals(event.getNodeType())) {
				continue;
			}

			// Instanciate a copy of the node
			GraphNode newNode = node.getCopy();
			context.getGraphModel().insert(newNode, event.getX(), event.getY());

			// If we added a sensor, set the search filters into the instance
			if (newNode instanceof GenericSensor) {
				GenericSensor sensor = (GenericSensor) node;
				sensor.getPropertyValueMap().put("LAT", context.getFilterLocationLat());
				sensor.getPropertyValueMap().put("LON", context.getFilterLocationLon());
				sensor.getPropertyValueMap().put("RADIUS", context.getFilterLocationRadius());
			}

			break;
		}
	}

	public void validateDesign() {
		ServiceDesignPageContext context = getContext();
		DefaultGraphNodeValidator validator = new DefaultGraphNodeValidator(context.getGraphModel());
		boolean success = validator.validate();
		context.setGraphValidationErrors(validator.getValidationErrors());
		context.setGraphValidationWarnings(validator.getValidationWarnings());

		if (success) {
			if (validator.getValidationWarnings().isEmpty()) {
				String codeOutput = "";
				SparqlGenerator generator = new SparqlGenerator();

				// Generate code for each visualization node
				GraphModel model = context.getGraphModel();
				for (GraphNode node : model.getNodes()) {
					if (node.getType().equals("VISUALIZER")) {
						codeOutput += generator.generateCode(model, node);
					}
				}
				context.setGeneratedCode(codeOutput);

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "UI_GRAPH_COMPILER_SUCCESS")));
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "UI_VALIDATION_FAILED", validator.getValidationErrors().size(), validator.getValidationWarnings().size())));
		}
	}

	public void executeSensorDiscoveryQuery() {
		ServiceDesignPageContext context = getContext();
		FindSensorDialogContext findContext = (FindSensorDialogContext) (sessionBean == null ? ApplicationBean.lookupSessionBean() : sessionBean).getContext("findSensorDialogContext");

		// Save filter options so we can inject them into the generated sparql
		// queries
		context.setFilterLocationLat(findContext.getSearchCenter().getLatlng().getLat());
		context.setFilterLocationLon(findContext.getSearchCenter().getLatlng().getLng());
		context.setFilterLocationRadius(findContext.getSearchRadius());

		// Execute search and populate sensor toolbox
		try {
			SensorTypes sensorTypes = SchedulerAPIWrapper.getAvailableSensors("user000", context.getFilterLocationLat(), context.getFilterLocationLon(), context.getFilterLocationRadius());
			context.updateAvailableSensors(sensorTypes);
		} catch (APIException ex) {
			LoggerService.log(ex);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_SCHEDULER_SERVICE")));
		}
	}

	// ------------------------------------
	// Controller for service editing
	// ------------------------------------
	public void prepareNewServiceDialog() {
		ServiceDesignPageContext context = getContext();
		context.setNewServiceName(null);
		context.setNewServiceDescription(null);
	}

	public void createNewOAMO() {
		ServiceDesignPageContext context = getContext();
		OAMO oamo = new OAMO();
		oamo.setName(context.getNewServiceName());
		oamo.setDescription(context.getNewServiceDescription());
		context.getOsdSpec().getOAMO().add(oamo);

		activateOAMO(oamo);
	}

	public void clearOAMO() {
		ServiceDesignPageContext context = getContext();
		OAMO selectedOAMO = context.getSelectedOAMO();
		if (selectedOAMO != null) {
			selectedOAMO.setGraphMeta(null);
			selectedOAMO.getOSMO().clear();
		}
		
		context.cleanupWorkspace();
	}

	public void activateOAMO(OAMO oamo) {
		ServiceDesignPageContext context = getContext();
		GraphModel graphModel = new DefaultGraphModel();

		String graphMeta = oamo.getGraphMeta();
		if (graphMeta != null) {
			try {
				JSONObject spec = new JSONObject(new JSONTokener(graphMeta));
				graphModel.importJSON(spec);
			} catch (JSONException ex) {
				LoggerService.log(ex);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_LOADING_APPLICATION_DESIGN")));
				
				context.setSelectedOAMO(null);
				context.setGraphModel(null);
				return;				
			}
		}

		context.setSelectedOAMO(oamo);
		context.setGraphModel(graphModel);
	}

	public void saveServices() {

		if (encodeSelectedOAMO()) {
			try {
				SchedulerAPIWrapper.registerService(getContext().getOsdSpec());
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "UI_GRAPH_SAVE_SUCCESS")));
			} catch (APIException ex) {
				LoggerService.log(ex);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_SCHEDULER_SERVICE")));
			}
		}
	}

	public void resetServices() {
		ServiceDesignPageContext context = getContext();
		context.getOsdSpec().getOAMO().clear();
		context.setSelectedOAMO(null);
	}
	
	public void reloadServices() {
		ServiceDesignPageContext context = getContext();
		
		// Load services
		try {
			OSDSpec osdSpec = SchedulerAPIWrapper.getAvailableServices(ApplicationBean.lookupSessionBean().getUserId());
			context.setOsdSpec(osdSpec);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "SERVICES_LOADED_SUCCESSFULLY")));
			
		} catch (APIException ex) {
			LoggerService.log(ex);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_SCHEDULER_SERVICE")));
		}

		context.setSelectedOAMO(null);
	}

	// ------------------------------------
	// Helpers
	// ------------------------------------
	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	private boolean encodeSelectedOAMO() {
		ServiceDesignPageContext context = getContext();
		DefaultGraphNodeValidator validator = new DefaultGraphNodeValidator(context.getGraphModel());
		boolean success = validator.validate();
		context.setGraphValidationErrors(validator.getValidationErrors());
		context.setGraphValidationWarnings(validator.getValidationWarnings());

		if (success) {
			if (validator.getValidationWarnings().isEmpty()) {
				String codeOutput = "";
				SparqlGenerator generator = new SparqlGenerator();
				GraphModel model = context.getGraphModel();

				OAMO oamo = context.getSelectedOAMO();
				oamo.setId("node://" + model.getUID());
				oamo.setGraphMeta("<![CDATA[" + model.toJSON().toString() + "]]>");

				// Generate an OSMO object for each visualization node
				for (GraphNode node : model.getNodes()) {
					if (node.getType().equals("VISUALIZER")) {
						OSMO osmo = new OSMO();

						// Setup query controlls
						QueryControls queryControls = new QueryControls();
						queryControls.setReportIfEmpty(false);
						QuerySchedule querySchedule = new QuerySchedule();
						queryControls.setQuerySchedule(querySchedule);
						osmo.setQueryControls(queryControls);

						// Setup query request
						QueryRequest queryRequest = new QueryRequest();
						String codeBlock = generator.generateCode(model, node);
						codeOutput += codeBlock;
						queryRequest.setQuery("<![CDATA[" + codeBlock + "]]>");
						osmo.setQueryRequest(queryRequest);

						// Encode dynamic attributes
						for (Map.Entry<GraphNodeProperty, Object> entry : generator.getVariableMap().entrySet()) {
							DynamicAttrMaxValue attr = new DynamicAttrMaxValue();
							attr.setName(entry.getKey().getVariableName());
							attr.setValue(entry.getValue().toString());
							osmo.getDynamicAttrMaxValue().add(attr);
						}

						// Setup visualization params
						RequestPresentation requestPresentation = new RequestPresentation();
						{
							Widget widget = new Widget();
							widget.setWidgetID(node.getUID());
							for (Map.Entry<String, Object> entry : node.getPropertyValueMap().entrySet()) {
								PresentationAttr attr = new PresentationAttr();
								attr.setName(entry.getKey());
								attr.setValue(entry.getValue() != null ? entry.getValue().toString() : null);
								widget.getPresentationAttr().add(attr);
							}

							// Add an extra attribute for the widget class
							// so we know what type of widget to instanciate
							// at the presentation layer
							PresentationAttr attr = new PresentationAttr();
							attr.setName("widgetClass");
							attr.setValue(node.getClass().getCanonicalName());
							widget.getPresentationAttr().add(attr);
							requestPresentation.getWidget().add(widget);
							osmo.setRequestPresentation(requestPresentation);
						}

						oamo.getOSMO().add(osmo);
					}
				}
				context.setGeneratedCode(codeOutput);
				return true;
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "UI_VALIDATION_FAILED", validator.getValidationErrors().size(), validator.getValidationWarnings().size())));
		}
		return false;
	}

}
