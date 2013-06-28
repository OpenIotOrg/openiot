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
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.commons.sparql.protocoltypes.model.QueryResult;
import org.openiot.commons.sparql.result.model.Binding;
import org.openiot.commons.sparql.result.model.Literal;
import org.openiot.commons.sparql.result.model.Result;
import org.openiot.commons.sparql.result.model.Results;
import org.openiot.commons.sparql.result.model.Sparql;
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.openiot.ui.request.presentation.web.scopes.application.ApplicationBean;
import org.openiot.ui.request.presentation.web.scopes.session.SessionBean;
import org.openiot.ui.request.presentation.web.scopes.session.context.pages.RequestPresentationPageContext;
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;

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
			cachedContext = (RequestPresentationPageContext) (sessionBean == null ? ApplicationBean.lookupSessionBean() : sessionBean).getContext("requestPresentationPageContext");
			if (cachedContext == null) {
				cachedContext = new RequestPresentationPageContext();
			}
		}
		return cachedContext;
	}

	// ------------------------------------
	// Controller entrypoints
	// ------------------------------------
	public void keepAlivePing() {

	}

	public void updateDashboard() {
		RequestPresentationPageContext context = getContext();
		if( context.getDashboard() == null ){
			return;
		}
		
		// Simulate data retrieval
		for( Map.Entry<String, VisualizationWidget> entry : context.getServiceIdToWidgetMap().entrySet() ){
			SdumServiceResultSet resultSet = new SdumServiceResultSet();
			QueryResult qr = new QueryResult();
			resultSet.setQueryResult(qr);
			
			Sparql sparqlResult = new Sparql();
			qr.setSparql(sparqlResult);
			Results results = new Results();
			sparqlResult.setResults(results);
			
			Result result = new Result();
			results.getResult().add(result);
			
			Binding binding = new Binding();
			binding.setName("y1");
			Literal literal = new Literal();
			literal.setContent((Math.random() * 40.0) + "");
			binding.setLiteral(literal);			
			result.getBinding().add(binding);
			
			entry.getValue().processData(resultSet);
		}
	}
	
	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
