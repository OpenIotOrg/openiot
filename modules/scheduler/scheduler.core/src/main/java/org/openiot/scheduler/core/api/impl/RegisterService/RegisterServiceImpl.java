package org.openiot.scheduler.core.api.impl.RegisterService;


/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */


import lsm.beans.User;
import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.scheduler.core.utils.lsmpa.entities.Query;
import org.openiot.scheduler.core.utils.lsmpa.entities.Service;
import org.openiot.scheduler.core.utils.lsmpa.entities.WidgetAttributes;
import org.openiot.scheduler.core.utils.lsmpa.entities.WidgetAvailable;
import org.openiot.scheduler.core.utils.lsmpa.entities.WidgetPresentation;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.TupleQueryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModelSpec;


/**
 *  @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 *  @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
public class RegisterServiceImpl {
	
	
	private OSDSpec osdSpec;
	
	final static Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);
	
	private String replyMessage= "unsuccessfuly";
	
	
	
	public RegisterServiceImpl (OSDSpec osdSpec){
		
		this.osdSpec = osdSpec;
		
		logger.debug("Recieved OSDSpec from User with userID: " + osdSpec.getUserID());
	

		registerService();				
	}		

	
	private void registerService() 
	{
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);		
		
		LSMSchema myOnt  =  new  LSMSchema (OntModelSpec.OWL_DL_MEM);
		LSMSchema myOntInstance = new LSMSchema();
		
		org.openiot.scheduler.core.utils.lsmpa.entities.User userEnt = new org.openiot.scheduler.core.utils.lsmpa.entities.User(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		userEnt.setId(osdSpec.getUserID());		
//		//
		userEnt.createClassIdv();
		
		for (OAMO oamo: osdSpec.getOAMO())
		{			
			logger.debug("OAMO Description: {}  ID: {}",oamo.getDescription(), oamo.getId());
			logger.debug("OAMO Name: {}",oamo.getName());			
			
			org.openiot.scheduler.core.utils.lsmpa.entities.OAMO oamoEnt = new org.openiot.scheduler.core.utils.lsmpa.entities.OAMO(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);				
			oamoEnt.setId(oamo.getId());
			oamoEnt.setName(oamo.getName());
			oamoEnt.setUser(userEnt);
			//
			oamoEnt.createClassIdv();
			oamoEnt.createPoamoName();
			oamoEnt.createPoamoUserOf();
			
			userEnt.addService(oamoEnt);
			//
			userEnt.createPuserOf();
			
			
			for (OSMO osmo : oamo.getOSMO())
			{
				logger.debug("OSMO ID: {}",osmo.getId());
				logger.debug("OSMO Name: {}",osmo.getName());
				logger.debug("OSMO Description: {}",osmo.getDescription());				
				for (QueryRequest qr : osmo.getQueryRequest()) {
					logger.debug("qr.getQuery():" + qr.getQuery());
				}				
				
				Service srvcEnt = new Service(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);	
				srvcEnt.setId(osmo.getId());
				srvcEnt.setName(osmo.getName());
				srvcEnt.setDescription(osmo.getDescription());
				//
				
				
				for (QueryRequest qr :osmo.getQueryRequest())
				{
					Query qstring = new Query(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);					
					qstring.setqString(qr.getQuery());
					//
					qstring.createClassIdv();
					qstring.createPqueryString();					
					
					srvcEnt.addQueryString(qstring);
				}
				
				//
				srvcEnt.createClassIdv();
				srvcEnt.createPserviceName();
				srvcEnt.createPserviceDescription();
				srvcEnt.createPqString();								
								
				
				for (Widget widget : osmo.getRequestPresentation().getWidget())
				{
					WidgetPresentation widgetPre  = new WidgetPresentation(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
					widgetPre.setService(srvcEnt);
					//
					widgetPre.createClassIdv();					
					widgetPre.createPwidgetPresOf();
					//
					srvcEnt.addWidgetPresentation(widgetPre);
					srvcEnt.createPwidgetPres();
					
					logger.debug("widget available id: {}",widget.getWidgetID());
					WidgetAvailable wAvail = new WidgetAvailable(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);				
					wAvail.setId(widget.getWidgetID());
					wAvail.setWidgetPre(widgetPre);
					///
					wAvail.createClassIdv();
					wAvail.createPWidgetOf();
					//
					widgetPre.setWidgetAvailable(wAvail);
					widgetPre.createPwidget();
////										
					for (PresentationAttr pAttr : widget.getPresentationAttr())
					{
						logger.debug("pAttr id: {} --- name: {}",pAttr.getName(),pAttr.getValue());
						
						WidgetAttributes wAttr = new WidgetAttributes(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
						wAttr.setDescription(pAttr.getValue());
						wAttr.setName(pAttr.getName());
						wAttr.setWidgetPre(widgetPre);
						///
						wAttr.createClassIdv();
						wAttr.createPdesc();
						wAttr.createPname();
						wAttr.createPWidgetAttrOf();
						//
						widgetPre.addWidgetAttr(wAttr);
						widgetPre.createPwidgetAttr();
						
					}//PresentationAttr					
				}//widget
				
				srvcEnt.setOAMO(oamoEnt);
//				//
				srvcEnt.createPOAMO();
				
				oamoEnt.addService(srvcEnt);
				//
				oamoEnt.createPoamoService();
//				

				
			}//osmo			
		}//oamo

				
		logger.debug(myOntInstance.exportToTriples("TURTLE"));
		boolean ok = lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/testSchema#",myOntInstance.exportToTriples("N-TRIPLE"));

		if(ok){
			replyMessage= "regester service successfull";
		}
		else{
			replyMessage= "regester service error";
		}
		
		logger.debug(replyMessage);
	}
	
	/**
	 * @return String
	 */
	public String replyMessage()
	{	
		return replyMessage;
	}
}
