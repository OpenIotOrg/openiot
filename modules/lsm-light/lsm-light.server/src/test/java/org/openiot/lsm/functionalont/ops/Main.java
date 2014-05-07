package org.openiot.lsm.functionalont.ops;


import java.io.FileNotFoundException;

import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.lsm.sdum.model.entities.Utilities;


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
		OSDSpec osdSpec = loadFromFile("src/test/resources/spec2.xml");
		
		SchedulerOps.registerService(osdSpec);
		
	}
}//class
