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

package org.openiot.ui.request.presentation.web.scopes.session.context.pages;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.openiot.ui.request.commons.models.OAMOManager;
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.openiot.ui.request.presentation.web.scopes.session.base.DisposableContext;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.model.DashboardColumn;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class RequestPresentationPageContext extends DisposableContext {

	private OAMOManager appManager;
	private Dashboard dashboard;
	private Map<String, VisualizationWidget> serviceIdToWidgetMap;

	public RequestPresentationPageContext() {
		super();
		this.register();

		this.serviceIdToWidgetMap = new LinkedHashMap<String, VisualizationWidget>();

		// Generate a dashboard model that will be bound to the view.
		// Note: JSF needs an existing instance to render the form. Its
		// *contents* however will be
		// automatically refreshed when a specific application is selected
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();
		this.dashboard = (Dashboard) application.createComponent(fc, "org.primefaces.component.Dashboard", "org.primefaces.component.DashboardRenderer");
		this.dashboard.setId("dashboard_" + System.nanoTime());
	}

	@Override
	public String getContextUID() {
		return "requestPresentationPageContext";
	}

	public OAMOManager getAppManager() {
		return appManager;
	}

	public void setAppManager(OAMOManager appManager) {
		this.appManager = appManager;
	}

	public void clear() {
		getAppManager().selectOAMO(null);
		if (dashboard.getModel() != null) {
			dashboard.getChildren().clear();
			for (DashboardColumn column : dashboard.getModel().getColumns()) {
				column.getWidgets().clear();
			}
		}
		serviceIdToWidgetMap.clear();
		dashboard.setModel(null);
	}

	// ------------------------------------
	// Dashboards
	// ------------------------------------

	public Dashboard getDashboard() {
		return this.dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public Map<String, VisualizationWidget> getServiceIdToWidgetMap() {
		return serviceIdToWidgetMap;
	}
}
