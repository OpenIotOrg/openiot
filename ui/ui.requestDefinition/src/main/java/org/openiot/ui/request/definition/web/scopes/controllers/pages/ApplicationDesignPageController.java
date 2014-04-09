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

package org.openiot.ui.request.definition.web.scopes.controllers.pages;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.openiot.ui.request.commons.models.OAMOManager;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;
import org.openiot.ui.request.commons.providers.SchedulerAPIWrapper;
import org.openiot.ui.request.commons.providers.exceptions.APIException;
import org.openiot.ui.request.commons.util.MarshalOSDspecUtils;
import org.openiot.ui.request.definition.web.factory.PropertyGridFormFactory;
import org.openiot.ui.request.definition.web.jsf.components.events.NodeInsertedEvent;
import org.openiot.ui.request.definition.web.model.nodes.impl.sources.GenericSource;
import org.openiot.ui.request.definition.web.model.validation.validators.OpenIoTGraphNodeValidator;
import org.openiot.ui.request.definition.web.scopes.application.ApplicationBean;
import org.openiot.ui.request.definition.web.scopes.session.SessionBean;
import org.openiot.ui.request.definition.web.scopes.session.context.dialogs.FindSensorDialogContext;
import org.openiot.ui.request.definition.web.scopes.session.context.pages.ApplicationDesignPageContext;
import org.openiot.ui.request.definition.web.sparql.SparqlGenerator;
import org.openiot.ui.request.definition.web.util.FaceletLocalization;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "applicationDesignPageController")
@RequestScoped
public class ApplicationDesignPageController implements Serializable {
	private static final long serialVersionUID = 1L;

	// Cached context
	private ApplicationDesignPageContext cachedContext;
	// Injected properties
	@ManagedProperty(value = "#{applicationBean}")
	protected transient ApplicationBean applicationBean;
	@ManagedProperty(value = "#{sessionBean}")
	protected transient SessionBean sessionBean;
	protected transient ResourceBundle messages;

	public ApplicationDesignPageController() {
		this.messages = FaceletLocalization.getLocalizedResourceBundle();
	}

	public ApplicationDesignPageContext getContext() {
		if (cachedContext == null) {
			if (sessionBean.getUserId() == null) {
				return null;
			}

			cachedContext = (ApplicationDesignPageContext) (sessionBean == null ? ApplicationBean.lookupSessionBean() : sessionBean).getContext("applicationDesignPageContext");
			if (cachedContext == null) {
				cachedContext = new ApplicationDesignPageContext();
			}
		}
		return cachedContext;
	}

	// ------------------------------------
	// Controller entrypoints
	// ------------------------------------
	public void keepAlivePing() {

	}

	public void doAccessControl() {
		if (sessionBean.getUserId() == null) {
			applicationBean.redirect("/pages/login.xhtml?faces-redirect=true");
		}
	}

	// ------------------------------------
	// Controller for OAMO editing
	// ------------------------------------

	public void onGraphNodeSelected() {
		ApplicationDesignPageContext context = getContext();

		if (context.getGraphModel().getSelectedNode() != null) {
			context.setPropertyEditorModel(PropertyGridFormFactory.generatePropertyGridDynaForm(context.getGraphModel().getSelectedNode()));
		} else {
			context.setPropertyEditorModel(null);
		}
	}

	public void onGraphNodeInserted(NodeInsertedEvent event) {
		ApplicationDesignPageContext context = getContext();

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
			if (newNode instanceof GenericSource) {
				GenericSource source = (GenericSource) node;
				source.getPropertyValueMap().put("LAT", context.getFilterLocationLat());
				source.getPropertyValueMap().put("LON", context.getFilterLocationLon());
				source.getPropertyValueMap().put("RADIUS", context.getFilterLocationRadius());
			}

			break;
		}
	}

	public void onGraphNodeDeleted() {
		ApplicationDesignPageContext context = getContext();
		context.getGraphModel().setSelectedNode(null);
		context.setPropertyEditorModel(null);
	}

	public void executeSensorDiscoveryQuery() {
		ApplicationDesignPageContext context = getContext();
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
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_DISCOVERY_SERVICE")));
		}
	}

	// ------------------------------------
	// Controllers for application editing
	// ------------------------------------
	public void prepareNewApplicationDialog() {
		ApplicationDesignPageContext context = getContext();
		context.setNewApplicationName(null);
		context.setNewApplicationDescription(null);
	}

	public void createNewApplication() {
		ApplicationDesignPageContext context = getContext();

		// Check for duplicate names
		if (context.getAppManager().exists(context.getNewApplicationName())) {
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", "1");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_DUPLICATE_APPLICATION_NAME", context.getNewApplicationName())));
			return;
		}

		// Create a new OAMO and select it
		context.getAppManager().createOAMO(context.getNewApplicationName(), context.getNewApplicationDescription(), true);

		// Setup workspace
		context.cleanupWorkspace();
		context.setGraphModel(new DefaultGraphModel());
	}

	public void validateApplication() {
		ApplicationDesignPageContext context = getContext();
		OpenIoTGraphNodeValidator validator = new OpenIoTGraphNodeValidator(context.getGraphModel());
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
					if (node.getType().equals("SINK")) {
						codeOutput += StringUtils.join(generator.generateQueriesForNodeEndpoints(model, node), "\n\n#-----------------------------------\n\n");
					}
				}
				context.setGeneratedCode(codeOutput);

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "UI_GRAPH_COMPILER_SUCCESS")));
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "UI_VALIDATION_FAILED", validator.getValidationErrors().size(), validator.getValidationWarnings().size())));
		}
	}

	public void clearApplicationWorkspace() {
		ApplicationDesignPageContext context = getContext();
		context.getAppManager().resetSelectedOAMO();
		context.cleanupWorkspace();
	}

	// ------------------------------------
	// Controllers for application management
	// ------------------------------------
	public void loadApplication(String name) {
		ApplicationDesignPageContext context = getContext();
		context.getAppManager().selectOAMOByName(name);

		GraphModel graphModel = new DefaultGraphModel();
		String graphMeta = context.getAppManager().getSelectedOAMO() != null ? context.getAppManager().getSelectedOAMO().getGraphMeta() : null;
		if (graphMeta != null) {
			try {
				JSONObject spec = new JSONObject(new JSONTokener(graphMeta));
				graphModel.importJSON(spec);
			} catch (JSONException ex) {
				LoggerService.log(ex);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_LOADING_APPLICATION_DESIGN")));

				context.setGraphModel(null);
				context.cleanupWorkspace();
				return;
			}
		}

		context.setGraphModel(graphModel);
		context.clearAvailableSensors();
		applicationBean.redirect("/pages/applicationDesign.xhtml?faces-redirect=true");	
	}

	public void saveApplication() {

		if (encodeApplication()) {
			try {
				getContext().getAppManager().saveSelectedOAMO();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "UI_GRAPH_SAVE_SUCCESS")));
			} catch (APIException ex) {
				LoggerService.log(ex);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_REGISTRATION_SERVICE")));
			}
		}
	}

	public void reloadApplications() {
		ApplicationDesignPageContext context = getContext();
		if (context.getAppManager() == null) {
			context.setAppManager(new OAMOManager());
		}

		// Load services
		try {
			context.getAppManager().loadUserOAMOs(ApplicationBean.lookupSessionBean().getUserId());
			if( !context.getAppManager().getAvailableOAMOs().isEmpty() ){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "APPLICATIONS_LOADED_SUCCESSFULLY")));
			}

		} catch (APIException ex) {
			LoggerService.log(ex);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_REGISTRATION_SERVICE")));
		}

		context.cleanupWorkspace();
	}

	public void prepareImportApplicationsDialog() {
		ApplicationDesignPageContext context = getContext();
		context.setUploadedSpec(null);
		context.setPersistSpec(false);
	}

	public void importApplications() {
		ApplicationDesignPageContext context = getContext();
		if (context.getUploadedSpec() == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_SELECT_A_FILE_TO_UPLOAD")));
			return;
		}

		// Try to parse OSDSpec
		OSDSpec spec = null;
		try {			
			// Unserialize
			JAXBContext jaxbContext = JAXBContext.newInstance(OSDSpec.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			spec = (OSDSpec) um.unmarshal(new StreamSource(
					new StringReader(IOUtils.toString(context.getUploadedSpec().getInputstream(), "UTF-8"))));			
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_COULD_NOT_PARSE_OSDSPEC_FILE")));
			return;
		}

		context.getAppManager().loadOSDSPec(spec);
		context.cleanupWorkspace();	
		
		if( context.isPersistSpec() ){
			try{
				SchedulerAPIWrapper.registerService(context.getAppManager().exportOSDSpec());
			} catch (APIException ex) {
				LoggerService.log(ex);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_REGISTRATION_SERVICE")));
			}
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "INFO_APPLICATION_IMPORT_SUCCESS")));
		applicationBean.redirect("/pages/applicationDesign.xhtml?faces-redirect=true");
	}

	public void exportApplications() throws APIException, IOException {
		ApplicationDesignPageContext context = getContext();
		// If an OAMO is active, make sure we validate and encode it before
		// continuing
		if (context.getAppManager().getSelectedOAMO() != null && !encodeApplication()) {
			context.setExportedSpec(null);
			return;
		}

		// Export spec
		OSDSpec spec = getContext().getAppManager().exportOSDSpec();
		String osdSpecString;
		try {
			osdSpecString = MarshalOSDspecUtils.marshalOSDSpec(spec);
		} catch (Exception e) {
			throw new APIException(e);
		}

		getContext().setExportedSpec(new DefaultStreamedContent(IOUtils.toInputStream(osdSpecString, "UTF-8"), "application/xml", "applications.xml"));
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

	private boolean encodeApplication() {
		ApplicationDesignPageContext context = getContext();
		OpenIoTGraphNodeValidator validator = new OpenIoTGraphNodeValidator(context.getGraphModel());
		boolean success = validator.validate();
		context.setGraphValidationErrors(validator.getValidationErrors());
		context.setGraphValidationWarnings(validator.getValidationWarnings());

		if (success) {
			if (validator.getValidationWarnings().isEmpty()) {
				String codeOutput = "";
				SparqlGenerator generator = new SparqlGenerator();
				GraphModel model = context.getGraphModel();

				OAMO oamo = context.getAppManager().getSelectedOAMO();
				oamo.getOSMO().clear();
				oamo.setGraphMeta(model.toJSON().toString());
				// Force new id generation
				oamo.setId(null);

				// Generate an OSMO object for each visualization node
				for (GraphNode node : model.getNodes()) {
					if (node.getType().equals("SINK")) {
						OSMO osmo = new OSMO();
						//osmo.setId("node://" + node.getUID());
						// Force new id generation
						oamo.setId(null);						

						// Setup query controlls
						QueryControls queryControls = new QueryControls();
						queryControls.setReportIfEmpty(false);
						QuerySchedule querySchedule = new QuerySchedule();
						queryControls.setQuerySchedule(querySchedule);
						osmo.setQueryControls(queryControls);

						// Setup query request
						List<String> queryBlocks = generator.generateQueriesForNodeEndpoints(model, node);
						for (String query : queryBlocks) {
							QueryRequest queryRequest = new QueryRequest();
							queryRequest.setQuery(query);
							osmo.getQueryRequest().add(queryRequest);
						}
						codeOutput += StringUtils.join(queryBlocks, "\n\n#-----------------------------------\n\n");

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
							widget.setWidgetID("node://widget_" + node.getUID());
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
