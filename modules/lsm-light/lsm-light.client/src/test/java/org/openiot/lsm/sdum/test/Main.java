package org.openiot.lsm.sdum.test;



import java.io.FileNotFoundException;


import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.lsm.functionalont.model.beans.*;
import org.openiot.lsm.security.oauth.LSMSDUMHttpManager;


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
		LSMSDUMHttpManager sdum = new LSMSDUMHttpManager();
		OSDSpec osdSpec = loadFromFile("src/test/resources/spec2.xml");

		OSDSpecBean bean = SchedulerOps_V04.registerService_V4(osdSpec);
		bean.setId("http://lsm.deri.ie/resource/123456");
		sdum.addOSDSPecBean(bean);
//		sdum.deleteOSDSPecBean(bean.getId());
		
//		osdSpec = loadFromFile("src/test/resources/spec3.xml");		
//		bean = SchedulerOps_V04.registerService_V4(osdSpec);		
//		bean.setId("http://lsm.deri.ie/resource/123456");
//		sdum.updateOSDSPecBean(bean);
	}
}//class
