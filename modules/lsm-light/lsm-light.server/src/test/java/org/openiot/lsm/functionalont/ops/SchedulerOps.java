package org.openiot.lsm.functionalont.ops;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.lsm.schema.LSMSchema;
import org.openiot.lsm.sdum.model.beans.OAMOBean;
import org.openiot.lsm.sdum.model.beans.OSDSpecBean;
import org.openiot.lsm.sdum.model.beans.OSMOBean;
import org.openiot.lsm.sdum.model.beans.PresentationAttrBean;
import org.openiot.lsm.sdum.model.beans.QueryControlsBean;
import org.openiot.lsm.sdum.model.beans.QueryRequestBean;
import org.openiot.lsm.sdum.model.beans.ReqPresentationBean;
import org.openiot.lsm.sdum.model.beans.UserBean;
import org.openiot.lsm.sdum.model.beans.WidgetBean;
import org.openiot.lsm.sdum.model.entities.OAMOEnt;
import org.openiot.lsm.sdum.model.entities.OSDSpecEnt;
import org.openiot.lsm.sdum.model.entities.OSMOEnt;
import org.openiot.lsm.sdum.model.entities.PresentationAttrEnt;
import org.openiot.lsm.sdum.model.entities.QueryControlsEnt;
import org.openiot.lsm.sdum.model.entities.QueryRequestEnt;
import org.openiot.lsm.sdum.model.entities.ReqPresentationEnt;
import org.openiot.lsm.sdum.model.entities.UserEnt;
import org.openiot.lsm.sdum.model.entities.WidgetEnt;

public class SchedulerOps 
{
	
	
	public static String registerService(OSDSpec osdSpec)
	{
		UserBean userBean = new UserBean();
		userBean.setId(osdSpec.getUserID());
		
			OSDSpecBean osdspecBean = new OSDSpecBean();
			osdspecBean.setId(null); // original spec doesn't have an id
			osdspecBean.setUserBean(userBean);
			
			for(OAMO oamoFrmOSDSPEC : osdSpec.getOAMO())
			{
				OAMOBean oamoBean = new OAMOBean();
				oamoBean.setId(oamoFrmOSDSPEC.getId());
				oamoBean.setDescription(oamoFrmOSDSPEC.getDescription());
				oamoBean.setGraphMeta(oamoFrmOSDSPEC.getGraphMeta());
				oamoBean.setName(oamoFrmOSDSPEC.getName());
				
				for(OSMO osmo : oamoFrmOSDSPEC.getOSMO())
				{
					OSMOBean osmoBean = new OSMOBean();
					osmoBean.setId(osmo.getId());
					osmoBean.setDescription(osmo.getDescription());
					osmoBean.setName(osmo.getName());
					osmoBean.setOamoBean(oamoBean);
					
						QueryControlsBean qControls = new QueryControlsBean();
						qControls.setReportIfEmpty(osmo.getQueryControls().isReportIfEmpty());
					
					osmoBean.setQueryControlsBean(qControls);
					
					for(QueryRequest qReq : osmo.getQueryRequest())
					{
							QueryRequestBean qReqBean = new QueryRequestBean();
							qReqBean.setId(null); //original spec doesn't have an id
							qReqBean.setQuery(qReq.getQuery());
							qReqBean.setOsmoBean(osmoBean);
							
						osmoBean.getQueryRequestBean().add(qReqBean);
					}
					
						ReqPresentationBean reqPreBean = new ReqPresentationBean();
						reqPreBean.setId(null); // original spec doesn't have an id
						reqPreBean.setOsmoBean(osmoBean);
						
						for(Widget widget : osmo.getRequestPresentation().getWidget())
						{
								WidgetBean wBean = new WidgetBean();
								wBean.setId(widget.getWidgetID());
								wBean.setReqPresentationBean(reqPreBean);
								
								for(PresentationAttr preAttr : widget.getPresentationAttr())
								{
										PresentationAttrBean preAttrBean = new PresentationAttrBean();
										preAttrBean.setId(null);// original spec doesn't have an id
										preAttrBean.setName(preAttr.getName());
										preAttrBean.setValue(preAttr.getValue());
										
									wBean.getPresentationAttrBeanList().add(preAttrBean);
								}
							
							reqPreBean.getWidgetBeanLsit().add(wBean);
						}
						
					osmoBean.setReqPresentationBean(reqPreBean);	
					
					oamoBean.getOsmoBeanList().add(osmoBean);
				}
				
				osdspecBean.getOamoBeanList().add(oamoBean);
			}

		//////////////////////////////////////////////////////////////
		
		UserEnt usrEnt = new UserEnt();
		usrEnt.setUserBean(userBean);
		//
		usrEnt.createClassIdv();
		
			OSDSpecEnt specEnt1 = new OSDSpecEnt(osdspecBean,usrEnt);
			//
			specEnt1.createClassIdv();
			specEnt1.createPosdpsecOfUser(); //linking osdspec with user
		
			for(OAMOBean oamoBean : osdspecBean.getOamoBeanList())
			{
				OAMOEnt oamoE = new OAMOEnt(oamoBean,specEnt1);
				//
				oamoE.createClassIdv();
				oamoE.createPoamoDescription();
				oamoE.createPoamoGraphMeta();
				oamoE.createPoamoName();
				oamoE.createPoamoOfOSDSpec();
				
					for(OSMOBean osmoBean : oamoBean.getOsmoBeanList())
					{
						OSMOEnt osmoE = new OSMOEnt(osmoBean,oamoE);
						//
						osmoE.createClassIdv();
						osmoE.createPosmoDescription();
						osmoE.createPosmoName();
						osmoE.createPosmoOfOAMO();
						
						QueryControlsEnt qcEnt = new QueryControlsEnt(osmoBean.getQueryControlsBean(),osmoE);
						//
						qcEnt.createClassIdv();
						qcEnt.createPquerycontrolsReportIfEmpty();

						osmoE.setQueryControlsEnt(qcEnt);
						osmoE.createPosmoHasQueryControlsAsString();
						
							for(QueryRequestBean qreqBean : osmoBean.getQueryRequestBean())
							{
								QueryRequestEnt qReqE = new QueryRequestEnt(qreqBean,osmoE);
								//
								qReqE.createAll();
								
								osmoE.getQueryRequestEntList().add(qReqE);
							}
							
						osmoE.createPosmoHasQueryRequestAsString();
						
							ReqPresentationEnt reqPreE = new ReqPresentationEnt(osmoBean.getReqPresentationBean(),osmoE);
							//
							reqPreE.createClassIdv();
							reqPreE.createPreqPresentationOfOSMO();
							
								for(WidgetBean wBean : osmoBean.getReqPresentationBean().getWidgetBeanLsit())
								{
									WidgetEnt wEnt = new WidgetEnt(wBean,reqPreE);
									//
									wEnt.createClassIdv();
									wEnt.createPpresentationAttrName();
									
										for(PresentationAttrBean preAttrBean : wBean.getPresentationAttrBeanList())
										{
											PresentationAttrEnt preAttrEnt = new PresentationAttrEnt(preAttrBean,wEnt);
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
		
		
		LSMSchema rootModel = new  LSMSchema();
		
		rootModel.getBase().add(usrEnt.getClassIndividual().getOntModel().getBaseModel());
		for(OSDSpecEnt specEnt : usrEnt.getSpecEnt())
		{
			rootModel.getBase().add(specEnt.getClassIndividual().getOntModel().getBaseModel());
			for(OAMOEnt oamoEnt : specEnt.getOamoEntList())
			{
				rootModel.getBase().add(oamoEnt.getClassIndividual().getOntModel().getBaseModel());
				for(OSMOEnt osmoEnt : oamoEnt.getOsmoList())
				{
					rootModel.getBase().add(osmoEnt.getClassIndividual().getOntModel().getBaseModel());
				}
			}
		}
				
		//export
		String triples = rootModel.exportToTriples("N-TRIPLE");
		System.out.println(triples);
		return triples;
	}	
	
	public static String OSDSpecBeanToTriples(OSDSpecBean osdspecBean)
	{

		//////////////////////////////////////////////////////////////
		
		UserEnt usrEnt = new UserEnt();
		usrEnt.setUserBean(osdspecBean.getUserBean());
		//
		usrEnt.createClassIdv();
		
			OSDSpecEnt specEnt1 = new OSDSpecEnt(osdspecBean,usrEnt);
			//
			specEnt1.createClassIdv();
			specEnt1.createPosdpsecOfUser(); //linking osdspec with user
		
			for(OAMOBean oamoBean : osdspecBean.getOamoBeanList())
			{
				OAMOEnt oamoE = new OAMOEnt(oamoBean,specEnt1);
				//
				oamoE.createClassIdv();
				oamoE.createPoamoDescription();
				oamoE.createPoamoGraphMeta();
				oamoE.createPoamoName();
				oamoE.createPoamoOfOSDSpec();
				
					for(OSMOBean osmoBean : oamoBean.getOsmoBeanList())
					{
						OSMOEnt osmoE = new OSMOEnt(osmoBean,oamoE);
						//
						osmoE.createClassIdv();
						osmoE.createPosmoDescription();
						osmoE.createPosmoName();
						osmoE.createPosmoOfOAMO();
						
						QueryControlsEnt qcEnt = new QueryControlsEnt(osmoBean.getQueryControlsBean(),osmoE);
						//
						qcEnt.createClassIdv();
						qcEnt.createPquerycontrolsReportIfEmpty();

						osmoE.setQueryControlsEnt(qcEnt);
						osmoE.createPosmoHasQueryControlsAsString();
						
							for(QueryRequestBean qreqBean : osmoBean.getQueryRequestBean())
							{
								QueryRequestEnt qReqE = new QueryRequestEnt(qreqBean,osmoE);
								//
								qReqE.createAll();
								
								osmoE.getQueryRequestEntList().add(qReqE);
							}
							
						osmoE.createPosmoHasQueryRequestAsString();
						
							ReqPresentationEnt reqPreE = new ReqPresentationEnt(osmoBean.getReqPresentationBean(),osmoE);
							//
							reqPreE.createClassIdv();
							reqPreE.createPreqPresentationOfOSMO();
							
								for(WidgetBean wBean : osmoBean.getReqPresentationBean().getWidgetBeanLsit())
								{
									WidgetEnt wEnt = new WidgetEnt(wBean,reqPreE);
									//
									wEnt.createClassIdv();
									wEnt.createPpresentationAttrName();
									
										for(PresentationAttrBean preAttrBean : wBean.getPresentationAttrBeanList())
										{
											PresentationAttrEnt preAttrEnt = new PresentationAttrEnt(preAttrBean,wEnt);
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
		
		
		LSMSchema rootModel = new  LSMSchema();
		
		rootModel.getBase().add(usrEnt.getClassIndividual().getOntModel().getBaseModel());
		for(OSDSpecEnt specEnt : usrEnt.getSpecEnt())
		{
			rootModel.getBase().add(specEnt.getClassIndividual().getOntModel().getBaseModel());
			for(OAMOEnt oamoEnt : specEnt.getOamoEntList())
			{
				rootModel.getBase().add(oamoEnt.getClassIndividual().getOntModel().getBaseModel());
				for(OSMOEnt osmoEnt : oamoEnt.getOsmoList())
				{
					rootModel.getBase().add(osmoEnt.getClassIndividual().getOntModel().getBaseModel());
				}
			}
		}				
		//export
		String triples = rootModel.exportToTriples("TURTLE");
		System.out.println(triples);
		return triples;
	}
}
