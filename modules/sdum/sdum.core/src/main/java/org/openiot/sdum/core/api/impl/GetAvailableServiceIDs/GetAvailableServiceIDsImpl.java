package org.openiot.sdum.core.api.impl.GetAvailableServiceIDs;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.descriptiveids.model.DescriptiveID;
import org.openiot.sdum.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAvailableServiceIDsImpl 
{
	private static class Queries
	{	
		private static String openiotFunctionalGraph = "http://lsm.deri.ie/OpenIoT/testSchema#";

		public static DescreptiveIDs parseAvailableOSMOIDsByOAMO(TupleQueryResult qres)
		{
			DescreptiveIDs descreptiveIDs = new DescreptiveIDs();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					DescriptiveID descID = new DescriptiveID();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("serviceID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							descID.setId(str);
							System.out.println("srvc id: "+descID.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							descID.setName(str);
							System.out.println("srvcName : "+descID.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							descID.setDescription(str);
							System.out.println("srvcDesc : "+descID.getDescription()+" ");	
						}
					}
					descreptiveIDs.getDescriptiveID().add(descID);					
				}//while
				return descreptiveIDs;
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
		
		public static String getAvailableOSMOIDsByOAMO(String oamoID)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("SELECT ?serviceID ?srvcName ?srvcDesc " 
					+"from <"+openiotFunctionalGraph+"> "
					+"WHERE "
					+"{"					
					+"?serviceID <http://openiot.eu/ontology/ns/serviceDescription> ?srvcDesc . "
					+"?serviceID <http://openiot.eu/ontology/ns/queryString> ?srvcQstring . "
					+"?serviceID <http://openiot.eu/ontology/ns/oamo> <"+oamoID+"> . "								
					+"}");								
						
			update.append(str);
			return update.toString();
		}		
	}

	/////
	final static Logger logger = LoggerFactory.getLogger(GetAvailableServiceIDsImpl.class);	
	
	private String applicationID;
	private DescreptiveIDs descriptiveIDs;
	
	//constructor
	public GetAvailableServiceIDsImpl(String applicationID)
	{
		this.applicationID=applicationID;
		logger.debug("Received Parameters: " +	"applicationID=" + applicationID );
		findAvailableServiceIDs();
	}
	
	public DescreptiveIDs getAvailableServiceIDs()
	{
		return descriptiveIDs;
	}
	
	private void findAvailableServiceIDs()
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. ",e);
			return;
		}		
		
		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getAvailableOSMOIDsByOAMO(applicationID));
		descriptiveIDs = Queries.parseAvailableOSMOIDsByOAMO(qres);
	}
	
}
