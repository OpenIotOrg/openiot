package org.openiot.functionalont.main;


import java.io.FileNotFoundException;

import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.lsm.functionalont.model.entities.Utilities;
import org.openiot.lsm.functionalont.ops.SchedulerOps;


public class Main 
{
	private static OSDSpec loadFromFile(String osdSpecFilePathName) 
	{				
		OSDSpec osdSpec = null;
		
		//Open and Deserialize OSDSPec form file
		try {
			osdSpec = Utilities.Deserializer.deserializeOSDSpecFile(osdSpecFilePathName);
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		
		return osdSpec;		
	}
			
	
	public static void main(String[] args) 
	{
		OSDSpec osdSpec = loadFromFile("spec2.xml");
		
		SchedulerOps.registerService(osdSpec);
		
	}
}//class
