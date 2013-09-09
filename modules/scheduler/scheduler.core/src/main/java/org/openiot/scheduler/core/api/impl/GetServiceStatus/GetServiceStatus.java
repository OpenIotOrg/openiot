package org.openiot.scheduler.core.api.impl.GetServiceStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetServiceStatus 
{
	private static class Queries
	{		
		public static class ServiceStatusData
		{
			private String serviceStatus; 
			private String serviceStatusTime;
			
			public String getServiceStatus() {
				return serviceStatus;
			}
			public void setServiceStatus(String serviceStatus) {
				this.serviceStatus = serviceStatus;
			}
			
			public String getServiceStatusTime() {
				return serviceStatusTime;
			}
			public void setServiceStatusTime(String serviceStatusTime) {
				this.serviceStatusTime = serviceStatusTime;
			}
		}
		
		private static String openiotFunctionalGraph = "http://lsm.deri.ie/OpenIoT/testSchema#";
		
		public static ArrayList<String> parseServicesOfOAMO(TupleQueryResult qres)
		{
			ArrayList<String> oamoServicesList = new ArrayList<String>();
			
			try
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();					
					
					String oamoService = null;
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("serviceID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							oamoService=str;
							System.out.print("oamo service: "+oamoService);	
						}						
					}
					oamoServicesList.add(oamoService);				
				}//while
				return oamoServicesList;
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
		public static ServiceStatusData parseServiceStatusOfOSMO(TupleQueryResult qres)
		{
			ServiceStatusData serviceStatusData = new ServiceStatusData();
			try
			{
//				while (qres.hasNext())
//				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();					
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("srvcStatus"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							serviceStatusData.setServiceStatus(str);
							System.out.print("setServiceStatus: "+serviceStatusData.getServiceStatus()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcStatusTime"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							serviceStatusData.setServiceStatusTime(str);
							System.out.print("srvcStatusTime : "+serviceStatusData.getServiceStatusTime()+" ");
						}
					}
									
//				}//while
				return serviceStatusData;
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
		
		public static String getServicesOfOAMO(String oamoID)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("SELECT ?serviceID  "
					+"from <"+openiotFunctionalGraph+"> "
					+"WHERE "
					+"{"					
					
					+"?serviceID <http://openiot.eu/ontology/ns/oamo> <"+oamoID+"> . "	
					
					+"}");
			
			update.append(str);
			return update.toString();
		}
		public static String getServiceStatusOfOSMO(String serviceID)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("select ?srvcStatus ?srvcStatusTime "
								+"from <"+openiotFunctionalGraph+"> "
								+"WHERE "
								+"{"								
								
								+"?srvcStatusID rdf:type ?srvcStatus ."
								+"?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusTime> ?srvcStatusTime ."								
								+"?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusOf> <"+serviceID+"> ."
								
								+"}");
			
			update.append(str);
			return update.toString();
		}		
	}
	
	/////
	
	final static Logger logger = LoggerFactory.getLogger(GetServiceStatus.class);

	private String oamoID;	
	
	//constructor
	public GetServiceStatus(String oamoID) 
	{
		this.oamoID = oamoID;

		logger.debug("Recieved Parameters: " +
				"serviceID=" + oamoID);				

		findServiceStatus();
	}
	
//	public ServiceStatus getServiceStatus()
//	{
//		return serviceStatusData;
//	}
	
	private void findServiceStatus()
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. ",e);
			return;
		}
		
		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getServicesOfOAMO(oamoID));
		ArrayList<String> oamoServicesList = Queries.parseServicesOfOAMO(qres);
		for (String oamoID :oamoServicesList)
		{
			qres = sparqlCl.sparqlToQResult(Queries.getServiceStatusOfOSMO(oamoID));
			Queries.ServiceStatusData serviceStatusData = Queries.parseServiceStatusOfOSMO(qres);
			
			//need to populate the ServiceStatus object which is described in Community&DevelopmentCookbook
		}
	}
	
}//class
