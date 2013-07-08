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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.openiot.ui.request.presentation.web.scopes.session.base.DisposableContext;
import org.primefaces.component.dashboard.Dashboard;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class RequestPresentationPageContext extends DisposableContext {

	private OSDSpec osdSpec;
	private OAMO selectedOAMO;
	private Dashboard dashboard;
	private Map<String, VisualizationWidget> serviceIdToWidgetMap;

	public RequestPresentationPageContext() {
		super();
		this.register();
		this.serviceIdToWidgetMap = new HashMap<String, VisualizationWidget>();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();		
		this.dashboard = (Dashboard) application.createComponent(fc, "org.primefaces.component.Dashboard", "org.primefaces.component.DashboardRenderer");
		this.dashboard.setId("dashboard_" + System.nanoTime());
	}

	@Override
	public String getContextUID() {
		return "requestPresentationPageContext";
	}

	// ------------------------------------
	// Services
	// ------------------------------------

	public OSDSpec getOsdSpec() {
		return osdSpec;
	}

	public void setOsdSpec(OSDSpec osdSpec) {
		this.osdSpec = osdSpec;
	}

	public Map<String, OAMO> getOAMOMap() {
		Map<String, OAMO> map = new LinkedHashMap<String, OAMO>();
		for (OAMO oamo : osdSpec.getOAMO()) {
			map.put(oamo.getName(), oamo);
		}
		return map;
	}

	public OAMO getSelectedOAMO() {
		return selectedOAMO;
	}

	public void setSelectedOAMO(OAMO selectedOAMO) {
		this.selectedOAMO = selectedOAMO;
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
