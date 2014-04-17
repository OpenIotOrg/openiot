package org.openiot.lsm.sdum.test;


import java.util.Date;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.QuerySchedule;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.lsm.functionalont.model.beans.*;


public class SchedulerOps_V04 
{
	
	
	public static OSDSpecBean registerService_V4(OSDSpec osdSpec)
	{
		// i only need the id here in order to link user with osdspec
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
			return osdspecBean;

	}
	
	
	
}
