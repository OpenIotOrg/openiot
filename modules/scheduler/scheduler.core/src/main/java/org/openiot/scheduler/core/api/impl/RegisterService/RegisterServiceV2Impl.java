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

import org.openiot.lsm.schema.LSMSchema;
import org.openiot.lsm.server.LSMTripleStore;
import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.scheduler.core.utils.lsmpa.entities2.OAMOEnt2;
import org.openiot.scheduler.core.utils.lsmpa.entities2.OSDSpecEnt2;
import org.openiot.scheduler.core.utils.lsmpa.entities2.OSMOEnt2;
import org.openiot.scheduler.core.utils.lsmpa.entities2.PresentationAttrEnt2;
import org.openiot.scheduler.core.utils.lsmpa.entities2.QueryControlsEnt2;
import org.openiot.scheduler.core.utils.lsmpa.entities2.QueryRequestEnt2;
import org.openiot.scheduler.core.utils.lsmpa.entities2.ReqPresentationEnt2;
import org.openiot.scheduler.core.utils.lsmpa.entities2.UserEnt2;
import org.openiot.scheduler.core.utils.lsmpa.entities2.WidgetEnt2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * 
 */
public class RegisterServiceV2Impl {

	final static Logger logger = LoggerFactory.getLogger(RegisterServiceV2Impl.class);

	private String lsmFunctionalGraph;
	//private String lsmUserName;
	//private String lsmPassword;
	private String lsmDeriServer;

	private String replyMessage = "";
	//
	private OSDSpec osdSpec;

	// constructor //
	public RegisterServiceV2Impl(OSDSpec osdSpec) {

		logger.debug("Recieved OSDSpec from User with userID: " + osdSpec.getUserID());
		
		this.osdSpec = osdSpec;
		
		PropertyManagement propertyManagement = new PropertyManagement();
		lsmFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
		//lsmUserName = propertyManagement.getSchedulerLsmUserName();
		//lsmPassword = propertyManagement.getSchedulerLsmPassword();
		lsmDeriServer = propertyManagement.getSchedulerLsmRemoteServer();

		registerService();
	}

	public String replyMessage() {
		return replyMessage;
	}

	
	// core methods //
	private void registerService()
	{
		LSMTripleStore lsmStore = new LSMTripleStore(lsmDeriServer);
		
		// need to add timeout to pushRDF
		boolean ok = lsmStore.pushRDF(lsmFunctionalGraph, convertToTriples("N-TRIPLE"));

		if (ok) {
			replyMessage = "register service successful";
		} else {
			replyMessage = "register service error";
		}

		logger.debug(replyMessage);
	}
	
	private String convertToTriples(String tripleFormat)
	{
			UserEnt2 usrEnt = new UserEnt2();
			usrEnt.setId(osdSpec.getUserID());					
			//
			usrEnt.createClassIdv();
			
				OSDSpecEnt2 specEnt1 = new OSDSpecEnt2(usrEnt);
				specEnt1.setId(null);
				//
				specEnt1.createClassIdv();
				specEnt1.createPosdpsecOfUser(); //linking osdspec with user
				
				for(OAMO oamoFrmOSDSPEC : osdSpec.getOAMO())
				{
					OAMOEnt2 oamoE = new OAMOEnt2(specEnt1);
					oamoE.setId(oamoFrmOSDSPEC.getId());
					oamoE.setName(oamoFrmOSDSPEC.getName());
					oamoE.setDescription(oamoFrmOSDSPEC.getDescription());
					oamoE.setGraphMeta(oamoFrmOSDSPEC.getGraphMeta());
					//
					oamoE.createClassIdv();
					oamoE.createPoamoDescription();
					oamoE.createPoamoGraphMeta();
					oamoE.createPoamoName();
					oamoE.createPoamoOfOSDSpec();
					
					for(OSMO osmoFromSpec : oamoFrmOSDSPEC.getOSMO())
					{
						OSMOEnt2 osmoE = new OSMOEnt2(oamoE);
						osmoE.setId(osmoFromSpec.getId());
						osmoE.setName(osmoFromSpec.getName());
						osmoE.setDescription(osmoFromSpec.getDescription());
						//
						osmoE.createClassIdv();
						osmoE.createPosmoDescription();
						osmoE.createPosmoName();
						osmoE.createPosmoOfOAMO();
						
							QueryControlsEnt2 qcEnt = new QueryControlsEnt2(osmoE);
							qcEnt.setTrigger(osmoFromSpec.getQueryControls().getTrigger());
							qcEnt.setReportIfEmpty(osmoFromSpec.getQueryControls().isReportIfEmpty());
							//
							qcEnt.createClassIdv();
							qcEnt.createPquerycontrolsReportIfEmpty();
						
						osmoE.setQueryControlsEnt(qcEnt);
						osmoE.createPosmoHasQueryControlsAsString();
						
							for(QueryRequest qreqFromOsmo : osmoFromSpec.getQueryRequest())
							{
								QueryRequestEnt2 qReqE = new QueryRequestEnt2(osmoE);
								qReqE.setQuery(qreqFromOsmo.getQuery());
								//
								qReqE.createAll();
								
								osmoE.getQueryRequestEntList().add(qReqE);
							}
						
						osmoE.createPosmoHasQueryRequestAsString();
	
							ReqPresentationEnt2 reqPreE = new ReqPresentationEnt2(osmoE);
							//
							reqPreE.createClassIdv();
							reqPreE.createPreqPresentationOfOSMO();
						
						for(Widget w : osmoFromSpec.getRequestPresentation().getWidget())
						{
							WidgetEnt2 wEnt = new WidgetEnt2(reqPreE);
							wEnt.setId(w.getWidgetID());
							//
							wEnt.createClassIdv();
							wEnt.createPpresentationAttrName();
							
								for(PresentationAttr preAttrBean : w.getPresentationAttr())
								{
									PresentationAttrEnt2 preAttrEnt = new PresentationAttrEnt2(wEnt);
									preAttrEnt.setName(preAttrBean.getName());
									preAttrEnt.setValue(preAttrBean.getValue());
									//
									preAttrEnt.createAll();
									
									wEnt.getPresentationAttrEntList().add(preAttrEnt);
								}
									
							wEnt.createPwidgetHasPresAttrAsString();
							
							reqPreE.getWidgetEntList().add(wEnt);
						}
						
						reqPreE.createPreqPresentationHasWidgetAsString();
						
					osmoE.setReqPresentationEnt(reqPreE);
					osmoE.createPosmoHasRequestpresentationAsString();
					
					oamoE.getOsmoList().add(osmoE);
					}
					
				oamoE.createPoamoHasOSMO();
				
				specEnt1.getOamoEntList().add(oamoE);
					
				}
				specEnt1.createPosdpsecHasOamo();	
				
		usrEnt.getSpecEnt().add(specEnt1);
		usrEnt.createPuserHasSpec(); //linking user with osdspec 
		
		////////////////////////////////////////////////
	
		LSMSchema rootModel = new  LSMSchema();
		
		rootModel.getBase().add(usrEnt.getClassIndividual().getOntModel().getBaseModel());
		for(OSDSpecEnt2 specEnt : usrEnt.getSpecEnt())
		{
			rootModel.getBase().add(specEnt.getClassIndividual().getOntModel().getBaseModel());
			for(OAMOEnt2 oamoEnt : specEnt.getOamoEntList())
			{
				rootModel.getBase().add(oamoEnt.getClassIndividual().getOntModel().getBaseModel());
				for(OSMOEnt2 osmoEnt : oamoEnt.getOsmoList())
				{
					rootModel.getBase().add(osmoEnt.getClassIndividual().getOntModel().getBaseModel());
//						rootModel.getBase().add(osmoEnt.getQueryControlsEnt().getOntModel().getBaseModel());
//						rootModel.getBase().add(osmoEnt.getReqPresentationEnt().getClassIndividual().getOntModel().getBaseModel());
					
//						for(WidgetEnt wEnt : osmoEnt.getReqPresentationEnt().getWidgetEntList())
//						{
//							rootModel.getBase().add(wEnt.getClassIndividual().getOntModel().getBaseModel());
//							for(PresentationAttrEnt preAttr : wEnt.getPresentationAttrEntList())
//							{
//								rootModel.getBase().add(preAttr.getClassIndividual().getOntModel().getBaseModel());
//							}
//						}
//						
//						for(QueryRequestEnt qReqEnt : osmoEnt.getQueryRequestEntList())
//						{
//							rootModel.getBase().add(qReqEnt.getClassIndividual().getOntModel().getBaseModel());
//						}
				}
			}
		}
		
		//convert
		String triples = rootModel.exportToTriples(tripleFormat);
		logger.debug(triples);
		
		return triples;
	}

}//class
