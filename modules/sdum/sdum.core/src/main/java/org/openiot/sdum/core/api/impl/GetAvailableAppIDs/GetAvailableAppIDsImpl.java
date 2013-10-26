package org.openiot.sdum.core.api.impl.GetAvailableAppIDs;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.descriptiveids.model.DescriptiveID;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.sdum.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAvailableAppIDsImpl 
{
	private static String openiotFunctionalGraph = "";
	
	private static class Queries
	{		
		public static class OAMOData
		{
			private String oamoName; 
			private String oamoID;
			
			
			public String getOamoName() {
				return oamoName;
			}
			public void setOamoName(String oamoName) {
				this.oamoName = oamoName;
			}
			
			public String getOamoID() {
				return oamoID;
			}
			public void setOamoID(String oamoID) {
				this.oamoID = oamoID;
			}
		}



		public static ArrayList<OAMOData> parseAvailableAppIDsOfUser(TupleQueryResult qres)
		{
			ArrayList<OAMOData> oamoDataList = new ArrayList<OAMOData>();
			
			try
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();					
					
					OAMOData oamoData = new OAMOData();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("oamoID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							oamoData.setOamoID(str);
							System.out.print("oamoID: "+oamoData.getOamoID());	
						}
						else if(((String) n).equalsIgnoreCase("oamoName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							oamoData.setOamoName(str);
							System.out.print("oamoName: "+oamoData.getOamoName());	
						}
					}
					oamoDataList.add(oamoData);				
				}//while
				return oamoDataList;
			} 
			catch (QueryEvaluationException e)			
			{				
				e.printStackTrace();
				return null;
			}
			catch (Exception e)			
			{				
				e.printStackTrace();
				return null;
			}
		}
		
		public static String getAvailableAppIDsOfUser(String userID)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("SELECT ?oamoID ?oamoName  " 
					+"from <"+openiotFunctionalGraph+"> "
					+"WHERE "
					+"{"
					+"?oamoID <http://openiot.eu/ontology/ns/oamoName> ?oamoName . "
					+"?oamoID <http://openiot.eu/ontology/ns/oamoUserOf> <"+userID+"> . "								
					+"}");
			
			update.append(str);
			return update.toString();
		}		
	}	
	
	/////
	
	final static Logger logger = LoggerFactory.getLogger(GetAvailableAppIDsImpl.class);	
	
	private String userID;
	private DescreptiveIDs descriptiveIDs;
	
	//constructor
	public GetAvailableAppIDsImpl(String userID)
	{
		
		PropertyManagement propertyManagement = new PropertyManagement();
		openiotFunctionalGraph = propertyManagement.getSdumLsmFunctionalGraph();
		
		
		this.userID = userID;
		
		logger.debug("Recieved Parameters: " +
				"userID=" + userID );				

		findAvailableAppIDs();
	}
	
	public DescreptiveIDs getAvailableAppIDs()
	{
		return descriptiveIDs;
	}
	
	private void findAvailableAppIDs()
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. ",e);
			return;
		}
		
		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getAvailableAppIDsOfUser(userID));
		ArrayList<Queries.OAMOData> appDataOfUserList = Queries.parseAvailableAppIDsOfUser(qres);

		descriptiveIDs = new DescreptiveIDs();
		
		for (Queries.OAMOData oamoData :appDataOfUserList)
		{
			DescriptiveID dID = new DescriptiveID();
			dID.setId(oamoData.getOamoID());
			dID.setName(oamoData.getOamoName());
			
			descriptiveIDs.getDescriptiveID().add(dID);
		}	
	}	
}
