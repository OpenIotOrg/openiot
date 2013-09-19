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
package org.openiot.ui.request.presentation.web.scopes.controllers.pages;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.providers.SDUMAPIWrapper;
import org.openiot.ui.request.commons.providers.SchedulerAPIWrapper;
import org.openiot.ui.request.commons.providers.exceptions.APIException;
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.openiot.ui.request.presentation.web.scopes.application.ApplicationBean;
import org.openiot.ui.request.presentation.web.scopes.session.SessionBean;
import org.openiot.ui.request.presentation.web.scopes.session.context.pages.RequestPresentationPageContext;
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@ManagedBean(name = "requestPresentationPageController")
@RequestScoped
public class RequestPresentationPageController implements Serializable {
	private static final long serialVersionUID = 1L;

	// Cached context
	private RequestPresentationPageContext cachedContext;
	// Injected properties
	@ManagedProperty(value = "#{applicationBean}")
	protected transient ApplicationBean applicationBean;
	@ManagedProperty(value = "#{sessionBean}")
	protected transient SessionBean sessionBean;
	protected transient ResourceBundle messages;

	public RequestPresentationPageController() {
		this.messages = FaceletLocalization.getLocalizedResourceBundle();
	}

	public RequestPresentationPageContext getContext() {
		if (cachedContext == null) {
			if (sessionBean.getUserId() == null) {
				return null;
			}
			cachedContext = (RequestPresentationPageContext) (sessionBean == null ? ApplicationBean.lookupSessionBean() : sessionBean).getContext("requestPresentationPageContext");
			if (cachedContext == null) {
				cachedContext = new RequestPresentationPageContext();
				reloadApplications();
				try {
					LoggerService.log(Level.INFO, SchedulerAPIWrapper.marshalOSDSpec(cachedContext.getAppManager().exportOSDSpec()));
				} catch (Exception ex) {
					LoggerService.log(ex);
				}
			}
		}
		return cachedContext;
	}

	public void doAccessControl() {
		if (sessionBean.getUserId() == null) {
			applicationBean.redirect("/pages/login.xhtml?faces-redirect=true");
		}
	}

	// ------------------------------------
	// Controller entrypoints
	// ------------------------------------
	public void keepAlivePing() {
	}

	// ------------------------------------
	// Controllers for application management
	// ------------------------------------
	public void reloadApplications() {
		RequestPresentationPageContext context = getContext();

		// Load services
		try {
			context.getAppManager().loadUserOAMOs(ApplicationBean.lookupSessionBean().getUserId());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("GROWL_INFO_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "APPLICATIONS_LOADED_SUCCESSFULLY")));

		} catch (APIException ex) {
			LoggerService.log(ex);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_REGISTRATION_SERVICE")));
		}

		context.clear();
	}

	public void loadApplication(String name) {
		RequestPresentationPageContext context = getContext();
		context.clear();

		context.getAppManager().selectOAMOByName(name);
		generateDashboardFromOAMO(context.getAppManager().getSelectedOAMO(), 2);
	}

	// ------------------------------------
	// Dashboard management
	// ------------------------------------
	public Dashboard getDashboard() {
		RequestPresentationPageContext context = getContext();

		if (context != null) {
			return context.getDashboard();
		}
		return null;
	}

	public void setDashboard(Dashboard dashboard) {
		RequestPresentationPageContext context = getContext();

		if (context != null) {
			context.setDashboard(dashboard);
		}
	}

	public void updateDashboard() {
		RequestPresentationPageContext context = getContext();
		if (context.getDashboard() == null) {
			return;
		}

		for (Map.Entry<String, VisualizationWidget> entry : context.getServiceIdToWidgetMap().entrySet()) {
			String serviceId = entry.getKey();

			// Fetch data
			try {
				SdumServiceResultSet resultSet = SDUMAPIWrapper.pollForReport(serviceId);
				entry.getValue().processData(resultSet);
			} catch (APIException ex) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_SDUM_SERVICE")));
			}
		}
	}

	// ------------------------------------
	// Helpers
	// ------------------------------------

	private void generateDashboardFromOAMO(OAMO oamo, int columnCount) {
		RequestPresentationPageContext context = getContext();
		context.getServiceIdToWidgetMap().clear();

		Dashboard dashboard = context.getDashboard();
		DashboardModel model = new DefaultDashboardModel();
		for (int i = 0, n = columnCount; i < n; i++) {
			DashboardColumn column = new DefaultDashboardColumn();
			model.addColumn(column);
		}
		dashboard.setRendered(true);
		dashboard.setModel(model);

		LoggerService.log(Level.INFO, "OSMOs: " + oamo.getOSMO().size());

		int nextColumn = 0;
		for (int index = 0; index < oamo.getOSMO().size(); index++) {
			OSMO osmo = oamo.getOSMO().get(index);
			List<PresentationAttr> presentationAttributes = osmo.getRequestPresentation().getWidget().get(0).getPresentationAttr();

			// Discover widget class
			String widgetClass = null;
			for (PresentationAttr attr : presentationAttributes) {
				if ("widgetClass".equals(attr.getName())) {
					widgetClass = attr.getValue().substring(attr.getValue().lastIndexOf('.') + 1);
					break;
				}
			}
			if (widgetClass == null) {
				continue;
			}

			// Try to instanciate widget
			try {
				VisualizationWidget visualizationWidget = (VisualizationWidget) Class.forName("org.openiot.ui.request.presentation.web.model.nodes.impl." + widgetClass).newInstance();
				String serviceId = osmo.getId();
				if (serviceId == null || serviceId.isEmpty()) {
					serviceId = "service_" + System.nanoTime();
				}
				context.getServiceIdToWidgetMap().put(serviceId, visualizationWidget);

				// Instanciate renderer widget
				UIComponent widgetView = visualizationWidget.createWidget(presentationAttributes);

				dashboard.getChildren().add(widgetView);
				DashboardColumn column = dashboard.getModel().getColumn(nextColumn % columnCount);
				column.addWidget(widgetView.getId());

				nextColumn++;

			} catch (InstantiationException e) {
				LoggerService.log(e);
			} catch (IllegalAccessException e) {
				LoggerService.log(e);
			} catch (ClassNotFoundException e) {
				LoggerService.log(e);
				e.printStackTrace();
			} catch (ClassCastException e) {
				LoggerService.log(e);
				e.printStackTrace();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
