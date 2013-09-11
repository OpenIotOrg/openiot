package org.openiot.scheduler.core.api.impl.GetAvailableApps;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.descriptiveids.model.DescriptiveID;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.scheduler.core.api.impl.GetApplication.GetApplicationImpl;
import org.openiot.scheduler.core.api.impl.GetAvailableAppIDs.GetAvailableAppIDsImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAvailableAppsImpl
{
	final static Logger logger = LoggerFactory.getLogger(GetAvailableAppsImpl.class);
	
	private String userID;
	private OSDSpec osdSpec;
	
	//constructor
	public GetAvailableAppsImpl(String userID)
	{
		this.userID=userID;
	}
	
	public OSDSpec getAvailableApps()
	{
		return osdSpec;
	}
	
	public void findAvailableApps()
	{		
		osdSpec = new OSDSpec();
		osdSpec.setUserID(userID);
		
		GetAvailableAppIDsImpl availableAppIDs = new GetAvailableAppIDsImpl(userID);
		DescreptiveIDs ids= availableAppIDs.getAvailableAppIDs();
		
		
		for(DescriptiveID id : ids.getDescriptiveID())
		{
			GetApplicationImpl application = new GetApplicationImpl(id.getId());
			
			osdSpec.getOAMO().add(application.getOAMO());
		}		
	}
}
