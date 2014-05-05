package org.openiot.scheduler.core.api.impl.GetAvailableAppIDs;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.descriptiveids.model.DescriptiveID;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * 
 */

public class GetAvailableAppIDsV2Impl 
{
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

			try {
				while (qres.hasNext()) 
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();

					OAMOData oamoData = new OAMOData();

					for (Object n : names) 
					{
						if (((String) n).equalsIgnoreCase("oamoID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							oamoData.setOamoID(str);
							logger.debug("oamoID: " + oamoData.getOamoID());
						} else if (((String) n).equalsIgnoreCase("oamoName")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							oamoData.setOamoName(str);
							logger.debug("oamoName: " + oamoData.getOamoName());
						}
					}
					oamoDataList.add(oamoData);
				}// while
				return oamoDataList;
			} catch (QueryEvaluationException e) {
				logger.debug(e.getMessage());
				return null;
			} catch (Exception e) {
				logger.debug(e.getMessage());
				return null;
			}
		}

		public static String getAvailableAppIDsOfUser(String lsmFunctionalGraph,String userID) 
		{
			StringBuilder query = new StringBuilder();

			query.append( "SELECT ?oamoID ?oamoName "); 
			query.append( "from <" + lsmFunctionalGraph + "> " ); //http://lsm.deri.ie/OpenIoT/guest/functionaldata#
			query.append( "WHERE " );
			query.append( "{" );
			query.append( "?oamoID <http://openiot.eu/ontology/ns/oamoName> ?oamoName . ");
			query.append( "?specID <http://openiot.eu/ontology/ns/osdpsecHasOamo> ?oamoID . ");
			query.append( "?specID <http://openiot.eu/ontology/ns/osdspecOfUser> <" + userID + "> . ");
			query.append( "}");

			return query.toString();
		}
	}//class

	//////

	final static Logger logger = LoggerFactory.getLogger(GetAvailableAppIDsV2Impl.class);

	private String lsmFunctionalGraph;
	//
	private String userID;
	private DescreptiveIDs descriptiveIDs;

	// constructor //
	public GetAvailableAppIDsV2Impl(String userID) 
	{
		logger.debug("Recieved Parameters: " + "userID=" + userID);
		
		this.userID = userID;
		
		PropertyManagement propertyManagement = new PropertyManagement();		
		lsmFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
				
		findAvailableAppIDs();
	}


	public DescreptiveIDs getAvailableAppIDs() {
		return descriptiveIDs;
	}

	
	// core methods //
	private void findAvailableAppIDs() 
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {
			logger.error("Init sparql repository error. ", e);
			return;
		}

		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getAvailableAppIDsOfUser(lsmFunctionalGraph,userID));
		ArrayList<Queries.OAMOData> appDataOfUserList = Queries.parseAvailableAppIDsOfUser(qres);

		descriptiveIDs = new DescreptiveIDs();

		for (Queries.OAMOData oamoData : appDataOfUserList) {
			DescriptiveID dID = new DescriptiveID();
			dID.setId(oamoData.getOamoID());
			dID.setName(oamoData.getOamoName());

			descriptiveIDs.getDescriptiveID().add(dID);
		}
	}
}//class
