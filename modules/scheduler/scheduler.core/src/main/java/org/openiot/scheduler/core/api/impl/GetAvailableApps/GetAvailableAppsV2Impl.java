package org.openiot.scheduler.core.api.impl.GetAvailableApps;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.descriptiveids.model.DescriptiveID;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.scheduler.core.api.impl.GetApplication.GetApplicationV2Impl;
import org.openiot.scheduler.core.api.impl.GetAvailableAppIDs.GetAvailableAppIDsV2Impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 *
 */
public class GetAvailableAppsV2Impl
{
	final static Logger logger = LoggerFactory.getLogger(GetAvailableAppsV2Impl.class);
	
	private String userID;
	private OSDSpec osdSpec;
	
	// constructor //
	public GetAvailableAppsV2Impl(String userID)
	{
		logger.debug("Received Parameters: " +	"userID=" + userID );
		
		this.userID=userID;
		
		findAvailableApps();
	}
	
	public OSDSpec getAvailableApps(){
		return osdSpec;
	}
	
	
	// core methods //
	public void findAvailableApps()
	{		
		osdSpec = new OSDSpec();
		osdSpec.setUserID(userID);
		
		// !!!! depends on this one
		GetAvailableAppIDsV2Impl availableAppIDs = new GetAvailableAppIDsV2Impl(userID);
		DescreptiveIDs ids= availableAppIDs.getAvailableAppIDs();
		
		
		for(DescriptiveID id : ids.getDescriptiveID()) {
			// !!!! depends on this one
			GetApplicationV2Impl application = new GetApplicationV2Impl(id.getId());
			osdSpec.getOAMO().add(application.getOAMO());
		}		
	}
}//class
