package org.openiot.scheduler.core.api.impl;


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





import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lsm.beans.User;
import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.Widget;
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
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
public class RegisterServiceImpl {
	
	
	private OSDSpec osdSpec;
	
	final static Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);
	
	private String replyMessage= "unsuccessfuly";
	
	
	
	public RegisterServiceImpl (OSDSpec osdSpec){	

		
		this.osdSpec = osdSpec;
		
		logger.debug("Recieved OSDSpec from User with userID: " + osdSpec.getUserID());
		
		
		//uncoment after test
//		registerService();
		registerServiceTestV1();
		//insertUser();		
	}
	
		
//	private void registerService() 
//	{		
//		for (OAMO oamo: osdSpec.getOAMO())
//		{			
//			logger.debug("OAMO Description: {}  ID: {}",oamo.getDescription(), oamo.getId());
//			logger.debug("OAMO Name: {}",oamo.getName());			
//			
//			for (OSMO osmo : oamo.getOSMO())
//			{								
//				logger.debug("OSMO Description: {}  ID: {}",osmo.getDescription(), osmo.getId());
//				logger.debug("OSMO Name: {}",osmo.getName());
//				
//				logger.debug("Query request query{}",osmo.getQueryRequest().getQuery());
//				
//				//List<Widget> wList = osmo.getRequestPresentation().getWidget();
//				for (Widget widget : osmo.getRequestPresentation().getWidget())
//				{
//					logger.debug("widget id: {}",widget.getWidgetID());
//					for (PresentationAttr pAttr : widget.getPresentationAttr())
//					{
//						logger.debug("pAttr id: {} --- name: {}",pAttr.getName(),pAttr.getValue());
//					}//PresentationAttr
//				}//widget
//			}//osmo
//		}//oamo
//		
//		replyMessage= "successfuly";
//	}
	
	private void registerServiceTestV1() 
	{
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);		
		
		LSMSchema myOnt  =  new  LSMSchema (OntModelSpec.OWL_DL_MEM);
		LSMSchema myOntInstance = new LSMSchema();
		
		org.openiot.scheduler.core.utils.lsmpa.entities.User schedulerUser = new org.openiot.scheduler.core.utils.lsmpa.entities.User(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		schedulerUser.setId(osdSpec.getUserID());		
//		//
		schedulerUser.createClassIdv();
		
		for (OAMO oamo: osdSpec.getOAMO())
		{			
			logger.debug("OAMO Description: {}  ID: {}",oamo.getDescription(), oamo.getId());
			logger.debug("OAMO Name: {}",oamo.getName());			
			
			Service srvc = null;
			for (OSMO osmo : oamo.getOSMO())
			{
				logger.debug("OSMO ID: {}",osmo.getId());
				logger.debug("OSMO Name: {}",osmo.getName());
				logger.debug("OSMO Description: {}",osmo.getDescription());
				logger.debug("Query request query{}",osmo.getQueryRequest().getQuery());				
				
				srvc = new Service(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);	
				srvc.setName(osmo.getName());
				srvc.setDescription(osmo.getDescription());
				srvc.setQueryString(osmo.getQueryRequest().getQuery());
				srvc.setUser(schedulerUser);
				srvc.setId(osmo.getId());
				//
				srvc.createClassIdv();
				srvc.createPserviceName();
				srvc.createPserviceDescription();
				srvc.createPqString();								
				srvc.createPUser();
				//
				schedulerUser.addService(srvc);
				schedulerUser.createPuserOf();
				

								
				WidgetPresentation widgetPre = null;
				for (Widget widget : osmo.getRequestPresentation().getWidget())
				{
					widgetPre = new WidgetPresentation(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
					widgetPre.setService(srvc);
					//
					widgetPre.createClassIdv();					
					widgetPre.createPwidgetPresOf();
					//
					srvc.addWidgetPresentation(widgetPre);
					srvc.createPwidgetPres();
					
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
					
					WidgetAttributes wAttr = null;
					for (PresentationAttr pAttr : widget.getPresentationAttr())
					{
						logger.debug("pAttr id: {} --- name: {}",pAttr.getName(),pAttr.getValue());
						
						wAttr = new WidgetAttributes(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
						wAttr.setDescription(pAttr.getName());
						wAttr.setName(pAttr.getValue());
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
			}//osmo
		}//oamo

				
		logger.debug(myOntInstance.exportToTriples("TURTLE"));
		boolean ok = lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/testSchema#",myOntInstance.exportToTriples("N-TRIPLE"));
		
		if(ok)
		{
//			StringBuilder listsID = new StringBuilder();
//			
//			SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
//			for(int i=0; i<schedulerUser.getServiceList().size(); i++)
//			{
//				TupleQueryResult qres = sparqlCl.sparqlToQResult(Service.Queries.
//							selectSrvcByUserByNameByDescByQuery(
//									schedulerUser,
//									schedulerUser.getServiceList().get(i).getName(), 
//									schedulerUser.getServiceList().get(i).getDescription(),
//									schedulerUser.getServiceList().get(i).getDescription()));
//				ArrayList<Service> sl= Service.Queries.parseService(qres);
//				listsID.append(sl.get(0));
//				listsID.append(",");
//			}
//			
//			for(int i=0; i<schedulerUser.getServiceList().size(); i++)
//			{
//				replyMessage= "added:"+listsID.toString();
//			}
			replyMessage= "regestering successfull";
			
		}
		else
		{
			replyMessage= "error regestering service";
		}
	}
	
	
	
	/**
	 * @return String
	 */
	public String replyMessage()
	{	
		return replyMessage;
	}
}
