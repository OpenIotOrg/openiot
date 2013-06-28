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
package org.openiot.ui.request.presentation.web.scopes.session.context.pages;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.openiot.ui.request.presentation.web.scopes.session.base.DisposableContext;
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.panel.Panel;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class RequestPresentationPageContext extends DisposableContext {

	private OSDSpec osdSpec;
	private Map<String, OAMO> applicationMap;
	private Dashboard dashboard;
	private String activeApplication;
	private Map<String, VisualizationWidget> serviceIdToWidgetMap;

	public RequestPresentationPageContext() {
		super();
		this.register();

		initialize();
	}

	@Override
	public String getContextUID() {
		return "requestPresentationPageContext";
	}

	public OSDSpec getOsdSpec() {
		return osdSpec;
	}

	public Map<String, OAMO> getApplicationMap() {
		return applicationMap;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public Dashboard getDashboard() {
		return this.dashboard;
	}

	public String getActiveApplication() {
		return activeApplication;
	}

	public void setActiveApplication(String activeApplication) {
		this.activeApplication = activeApplication;
		loadApplicationDashboard(activeApplication, 2);
	}

	
	public Map<String, VisualizationWidget> getServiceIdToWidgetMap() {
		return serviceIdToWidgetMap;
	}

	// ------------------------------------
	// Helpers
	// ------------------------------------
	private void initialize() {
		this.serviceIdToWidgetMap = new HashMap<String, VisualizationWidget>();
		
		// Load services
		osdSpec = getAvailableServices("user000");

		// Parse applications
		applicationMap = new LinkedHashMap<String, OAMO>();
		for (OAMO oamo : osdSpec.getOAMO()) {
			applicationMap.put(oamo.getName(), oamo);
		}

		// Preselect first application
		if (!applicationMap.isEmpty()) {
			setActiveApplication(applicationMap.keySet().iterator().next());
		}
	}

	private void loadApplicationDashboard(String applicationName, int columnCount) {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();

		OAMO oamo = getApplicationMap().get(applicationName);
		this.serviceIdToWidgetMap.clear();

		Dashboard dashboard = (Dashboard) application.createComponent(fc, "org.primefaces.component.Dashboard", "org.primefaces.component.DashboardRenderer");
		dashboard.setId("dashboard_" + System.nanoTime());
		DashboardModel model = new DefaultDashboardModel();
		for (int i = 0, n = columnCount; i < n; i++) {
			DashboardColumn column = new DefaultDashboardColumn();
			model.addColumn(column);
		}
		dashboard.setModel(model);

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
				if( serviceId == null || serviceId.isEmpty() ){
					serviceId = "service_" + System.nanoTime();
				}
				this.serviceIdToWidgetMap.put(serviceId, visualizationWidget);

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

		setDashboard(dashboard);
	}

	private OSDSpec getAvailableServices(String user) {
		InputStream is = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream("/org/openiot/ui/request/presentation/web/demo/demo-osdspec.xml");
			String osdSpecString = org.apache.commons.io.IOUtils.toString(is);

			// Unserialize
			JAXBContext jaxbContext = JAXBContext.newInstance(OSDSpec.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			return (OSDSpec) um.unmarshal(new StreamSource(new StringReader(osdSpecString)));

		} catch (Exception ex) {
			ex.printStackTrace();
			LoggerService.log(ex);
			ResourceBundle messages = FaceletLocalization.getLocalizedResourceBundle();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_DISCOVERY_SERVICE")));
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex1) {
				}
			}
		}
		return null;
	}
}
