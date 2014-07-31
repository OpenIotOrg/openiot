package org.openiot.scheduler.core.api.impl.RegisterService;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

//import lsm.beans.User;
import org.openiot.lsm.schema.LSMSchema;
import org.openiot.lsm.server.LSMTripleStore;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.commons.util.PropertyManagement;
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
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * 
 */
public class RegisterServiceImpl {

	final static Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

	private String lsmFunctionalGraph;
	private String lsmUserName;
	private String lsmPassword;
	private String lsmDeriServer;
	//
	private OSDSpec osdSpec;

	private String replyMessage = "";

	// constructor
	public RegisterServiceImpl(OSDSpec osdSpec) {

		PropertyManagement propertyManagement = new PropertyManagement();
		lsmFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
		lsmUserName = propertyManagement.getSchedulerLsmUserName();
		lsmPassword = propertyManagement.getSchedulerLsmPassword();
		lsmDeriServer = propertyManagement.getSchedulerLsmRemoteServer();
		
		this.osdSpec = osdSpec;

		logger.debug("Recieved OSDSpec from User with userID: " + osdSpec.getUserID());

		registerService();
	}


	/**
	 * @return String
	 */
	public String replyMessage() {
		return replyMessage;
	}

	private void registerService() {
//		User user = new User();
//		user.setUsername(lsmUserName);
//		user.setPass(lsmPassword);

		LSMTripleStore lsmStore = new LSMTripleStore(lsmDeriServer);
//		lsmStore.setUser(user);

		LSMSchema myOnt = new LSMSchema(OntModelSpec.OWL_DL_MEM);
		LSMSchema myOntInstance = new LSMSchema();

		org.openiot.scheduler.core.utils.lsmpa.entities.User userEnt = new org.openiot.scheduler.core.utils.lsmpa.entities.User(
				myOnt, myOntInstance, lsmFunctionalGraph, lsmStore);
		userEnt.setId(osdSpec.getUserID());
		// //
		userEnt.createClassIdv();

		for (OAMO oamo : osdSpec.getOAMO()) {
			logger.debug("OAMO Description: {}  ID: {}", oamo.getDescription(), oamo.getId());
			logger.debug("OAMO Name: {}", oamo.getName());

			org.openiot.scheduler.core.utils.lsmpa.entities.OAMO oamoEnt = new org.openiot.scheduler.core.utils.lsmpa.entities.OAMO(
					myOnt, myOntInstance, lsmFunctionalGraph, lsmStore);
			oamoEnt.setId(oamo.getId());
			oamoEnt.setName(oamo.getName());
			oamoEnt.setUser(userEnt);
			oamoEnt.setDescription(oamo.getDescription());
			oamoEnt.setGraphMeta(oamo.getGraphMeta());
			//
			oamoEnt.createClassIdv();
			oamoEnt.createPoamoName();
			oamoEnt.createPoamoUserOf();
			oamoEnt.createPoamoDescription();
			oamoEnt.createPoamoGraphMeta();

			userEnt.addService(oamoEnt);
			//
			userEnt.createPuserOf();

			for (OSMO osmo : oamo.getOSMO()) {
				logger.debug("OSMO ID: {}", osmo.getId());
				logger.debug("OSMO Name: {}", osmo.getName());
				logger.debug("OSMO Description: {}", osmo.getDescription());
				for (QueryRequest qr : osmo.getQueryRequest()) {
					logger.debug("qr.getQuery():" + qr.getQuery());
				}

				Service srvcEnt = new Service(myOnt, myOntInstance, lsmFunctionalGraph, lsmStore);
				srvcEnt.setId(osmo.getId());
				srvcEnt.setName(osmo.getName());
				srvcEnt.setDescription(osmo.getDescription());
				//

				for (QueryRequest qr : osmo.getQueryRequest()) {
					Query qstring = new Query(myOnt, myOntInstance, lsmFunctionalGraph, lsmStore);
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

				for (Widget widget : osmo.getRequestPresentation().getWidget()) {
					WidgetPresentation widgetPre = new WidgetPresentation(myOnt, myOntInstance,	lsmFunctionalGraph, lsmStore);
					widgetPre.setService(srvcEnt);
					//
					widgetPre.createClassIdv();
					widgetPre.createPwidgetPresOf();
					//
					srvcEnt.addWidgetPresentation(widgetPre);
					srvcEnt.createPwidgetPres();

					logger.debug("widget available id: {}", widget.getWidgetID());
					WidgetAvailable wAvail = new WidgetAvailable(myOnt, myOntInstance, lsmFunctionalGraph,lsmStore);
					wAvail.setId(widget.getWidgetID());
					wAvail.setWidgetPre(widgetPre);
					// /
					wAvail.createClassIdv();
					wAvail.createPWidgetOf();
					//
					widgetPre.setWidgetAvailable(wAvail);
					widgetPre.createPwidget();
					// //
					for (PresentationAttr pAttr : widget.getPresentationAttr()) {
						logger.debug("pAttr id: {} --- name: {}", pAttr.getName(), pAttr.getValue());

						WidgetAttributes wAttr = new WidgetAttributes(myOnt, myOntInstance,lsmFunctionalGraph, lsmStore);
						wAttr.setDescription(pAttr.getValue());
						wAttr.setName(pAttr.getName());
						wAttr.setWidgetPre(widgetPre);
						// /
						wAttr.createClassIdv();
						wAttr.createPdesc();
						wAttr.createPname();
						wAttr.createPWidgetAttrOf();
						//
						widgetPre.addWidgetAttr(wAttr);
						widgetPre.createPwidgetAttr();

					}// PresentationAttr
				}// widget

				srvcEnt.setOAMO(oamoEnt);
				// //
				srvcEnt.createPOAMO();

				oamoEnt.addService(srvcEnt);
				//
				oamoEnt.createPoamoService();
				//

			}// osmo
		}// oamo

		logger.debug(myOntInstance.exportToTriples("TURTLE"));
//		boolean ok = 
		lsmStore.pushRDF(lsmFunctionalGraph, myOntInstance.exportToTriples("N-TRIPLE"),"","");

//		if (ok) {
//			replyMessage = "regester service successfull";
//		} else {
//			replyMessage = "regester service error";
//		}

		logger.debug(replyMessage);
	}
}
