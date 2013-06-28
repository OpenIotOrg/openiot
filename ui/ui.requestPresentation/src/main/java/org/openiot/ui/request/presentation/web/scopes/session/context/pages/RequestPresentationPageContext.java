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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.presentation.web.scopes.session.base.DisposableContext;
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class RequestPresentationPageContext extends DisposableContext{

	private OSDSpec osdSpec;
	private Map<String, OAMO> applicationMap;
	
	public RequestPresentationPageContext() {
		super();
		this.register();
		
		initialize();
	}

	@Override
	public String getContextUID() {
		return "requestPresentationPageContext";
	}
	
	// ------------------------------------
	// Helpers
	// ------------------------------------
	private void initialize(){
		// Load services
		osdSpec = getAvailableServices("user000");
		
		// Parse applications
		applicationMap = new LinkedHashMap<String, OAMO>();
		for( OAMO oamo : osdSpec.getOAMO() ){
			applicationMap.put(oamo.getName(), oamo);
		}		
	}
	
	private OSDSpec getAvailableServices(String user) {
		InputStream is = null;
		try{
			is = this.getClass().getClassLoader().getResourceAsStream("/org/openiot/ui/request/presentation/web/demo/demo-osdspec.xml");
			String osdSpecString = org.apache.commons.io.IOUtils.toString(is);
			
			// Unserialize
			JAXBContext jaxbContext = JAXBContext.newInstance(OSDSpec.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			return (OSDSpec) um.unmarshal(new StreamSource(new StringReader(osdSpecString)));
			
		}catch(Exception ex){
			ex.printStackTrace();
			LoggerService.log(ex);
			ResourceBundle messages = FaceletLocalization.getLocalizedResourceBundle();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, messages.getString("GROWL_ERROR_HEADER"), FaceletLocalization.getLocalisedMessage(messages, "ERROR_CONNECTING_TO_DISCOVERY_SERVICE")));
		}finally{
			if( is != null ){
				try{
					is.close();
				}catch(IOException ex1){}
			}
		}
		return null;
	}
	
}
