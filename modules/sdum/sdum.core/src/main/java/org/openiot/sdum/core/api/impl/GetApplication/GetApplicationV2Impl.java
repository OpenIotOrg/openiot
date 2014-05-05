package org.openiot.sdum.core.api.impl.GetApplication;

/**
 *    Copyright (c) 2011-2014, OpenIoT
 *    
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */


import java.util.ArrayList;
import java.util.Set;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.sdum.core.api.impl.GetService.GetServiceV2Impl;
import org.openiot.sdum.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 *
 */

public class GetApplicationV2Impl 
{
	private static class Queries 
	{
		public static class RootOAMOData 
		{
			private String oamoName;
			private String userID;
			private String oamoDesc;
			private String oamoGraphMeta;

			// private String serviceID;

			public String getOamoName() {
				return oamoName;
			}

			public void setOamoName(String oamoName) {
				this.oamoName = oamoName;
			}

			public String getUserID() {
				return userID;
			}

			public void setUserID(String userID) {
				this.userID = userID;
			}

			public String getOamoDesc() {
				return oamoDesc;
			}

			public void setOamoDesc(String oamoDesc) {
				this.oamoDesc = oamoDesc;
			}

			public String getOamoGraphMeta() {
				return oamoGraphMeta;
			}

			public void setOamoGraphMeta(String oamoGraphMeta) {
				this.oamoGraphMeta = oamoGraphMeta;
			}

			// public String getServiceID() {
			// return serviceID;
			// }
			// public void setServiceID(String serviceID) {
			// this.serviceID = serviceID;
			// }
		}
		public static class RootOsmoData 
		{
			private String id;
			private String name;
			private String desc;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getDesc() {
				return desc;
			}

			public void setDesc(String desc) {
				this.desc = desc;
			}

		}

		public static RootOAMOData parseOAMORootData(TupleQueryResult qres) 
		{
			RootOAMOData rootOAMOData = new RootOAMOData();

			try {
				// while (qres.hasNext())
				// {
				BindingSet b = qres.next();
				Set names = b.getBindingNames();

				for (Object n : names) {
					if (((String) n).equalsIgnoreCase("oamoName")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
						rootOAMOData.setOamoName(str);
						logger.debug("oamoName: " + rootOAMOData.getOamoName());
					} else if (((String) n).equalsIgnoreCase("userID")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
						rootOAMOData.setUserID(str);
						logger.debug("userID: " + rootOAMOData.getUserID());
					}
					// else if(((String) n).equalsIgnoreCase("serviceID"))
					// {
					// String str = (b.getValue((String) n)==null) ? null :
					// b.getValue((String) n).stringValue();
					// rootOAMOData.setServiceID(str);
					// System.out.print("serviceID: "+rootOAMOData.getServiceID());
					// }
					else if (((String) n).equalsIgnoreCase("oamoDesc")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
						rootOAMOData.setOamoDesc(str);
						logger.debug("oamoDesc : " + rootOAMOData.getOamoDesc()	+ " ");
					} else if (((String) n).equalsIgnoreCase("oamoGraphMeta")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
						rootOAMOData.setOamoGraphMeta(str);
						logger.debug("oamoGraphMeta : " + rootOAMOData.getOamoGraphMeta() + " ");
					}
				}
				// }//while
				return rootOAMOData;
			} catch (QueryEvaluationException e) {
				logger.error(e.getMessage());
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}

		public static ArrayList<RootOsmoData> parseOSMOListOfOAMO(TupleQueryResult qres) 
		{
			ArrayList<RootOsmoData> osmoDataList = new ArrayList<RootOsmoData>();

			try {
				while (qres.hasNext()) 
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();

					RootOsmoData osmoData = new RootOsmoData();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("osmoID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							osmoData.setId(str);
							logger.debug("osmoID: " + osmoData.getId() + " ");
						} else if (((String) n).equalsIgnoreCase("osmoName")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							osmoData.setName(str);
							logger.debug("osmoName : " + osmoData.getName() + " ");
						} else if (((String) n).equalsIgnoreCase("osmoDesc")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							osmoData.setDesc(str);
							logger.debug("osmoDesc : " + osmoData.getDesc() + " ");
						}
					}
					osmoDataList.add(osmoData);
				}// while
				return osmoDataList;
			} catch (QueryEvaluationException e) {
				logger.error(e.getMessage());
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}

		
		public static String getRootOAMOData(String lsmFunctionalGraph,String oamoID) {
			StringBuilder query = new StringBuilder();

			query.append( "SELECT ?oamoName ?oamoDesc ?oamoGraphMeta ?specID "); //?osmoID
			query.append( "from <" + lsmFunctionalGraph + "> " ); //http://lsm.deri.ie/OpenIoT/guest/functionaldata#
			query.append( "WHERE " );
			query.append( "{" );
			query.append( "<"+oamoID+"> <http://openiot.eu/ontology/ns/oamoOfOSDSpec> ?specID . "); 
//			query.append( "<"+ oamoID + "> <http://openiot.eu/ontology/ns/oamoHasOSMO> ?osmoID . ");
			query.append( "optional { <"+ oamoID + "> <http://openiot.eu/ontology/ns/oamoGraphMeta> ?oamoGraphMeta . } "); 
			query.append( "optional { <"+ oamoID + "> <http://openiot.eu/ontology/ns/oamoDescription> ?oamoDesc . } ");
			query.append( "optional { <"+ oamoID + "> <http://openiot.eu/ontology/ns/oamoName> ?oamoName . } ");
			query.append( "}");
			
//			SELECT ?oamoName ?oamoDesc ?oamoGraphMeta ?osmoID ?specID
//					from <http://lsm.deri.ie/OpenIoT/guest/functionaldata#>
//					WHERE
//					{
//					<nodeID://b15002> <http://openiot.eu/ontology/ns/oamoOfOSDSpec> ?specID.
//					<nodeID://b15002> <http://openiot.eu/ontology/ns/oamoHasOSMO> ?osmoID .
//					optional { ://o<nodeID://b15002> <httppeniot.eu/ontology/ns/oamoGraphMeta> ?oamoGraphMeta . }
//					optional { <nodeID://b15002> <http://openiot.eu/ontology/ns/oamoDescription> ?oamoDesc . }
//					optional { <nodeID://b15002> <http://openiot.eu/ontology/ns/oamoName> ?oamoName . }
//					}
			
			return query.toString();
		}

		public static String getOSMOListOfOAMO(String lsmFunctionalGraph,String oamoID) {

			StringBuilder query = new StringBuilder();

			query.append( "SELECT ?osmoID ?osmoName ?osmoDesc ");
			query.append( "from <" + lsmFunctionalGraph + "> " ); //http://lsm.deri.ie/OpenIoT/guest/functionaldata#
			query.append( "WHERE " );
			query.append( "{" );
			query.append( "?osmoID <http://openiot.eu/ontology/ns/osmoOfOAMO> <"+oamoID+"> . "); 
			query.append( "optional { ?osmoID  <http://openiot.eu/ontology/ns/osmoDescription> ?osmoDesc . } ");
			query.append( "optional { ?osmoID  <http://openiot.eu/ontology/ns/osmoName> ?osmoName . } ");
			query.append( "}");

//			SELECT ?osmoID ?osmoName ?osmoDesc
//					from <http://lsm.deri.ie/OpenIoT/guest/functionaldata#>
//					WHERE
//					{
//					?osmoID <http://openiot.eu/ontology/ns/osmoOfOAMO> <nodeID://b15002> . 
//					optional { ?osmoID  <http://openiot.eu/ontology/ns/osmoDescription> ?osmoDesc . }
//					optional { ?osmoID  <http://openiot.eu/ontology/ns/osmoName> ?osmoName . }
//					}
			
			return query.toString();
		}
	}
	
	//////
	
	final static Logger logger = LoggerFactory.getLogger(GetApplicationV2Impl.class);
	
	private String lsmFunctionalGraph;
	private String oamoID;
	//
	private OAMO oamo;

	// Constructor //
	public GetApplicationV2Impl(String oamoID) 
	{		
		logger.debug("Received Parameters: " + "oamoID=" + oamoID);
		
		this.oamoID = oamoID;
		
		PropertyManagement propertyManagement = new PropertyManagement();		
		lsmFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
		
		findApplication();
	}
	
	public OAMO getOAMO() {
		return oamo;
	}
	
	
	// core methods //
	private void findApplication() 
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {
			logger.error("Init sparql repository error. ", e);
			return;
		}

		oamo = new OAMO();

		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getRootOAMOData(lsmFunctionalGraph,oamoID));
		Queries.RootOAMOData rootOAMODATA = Queries.parseOAMORootData(qres);

		oamo.setName(rootOAMODATA.getOamoName());
		oamo.setId(rootOAMODATA.getUserID());
		oamo.setDescription(rootOAMODATA.getOamoDesc());
		oamo.setGraphMeta(rootOAMODATA.getOamoGraphMeta());

		qres = sparqlCl.sparqlToQResult(Queries.getOSMOListOfOAMO(lsmFunctionalGraph,oamoID));
		ArrayList<Queries.RootOsmoData> OSMODataList = Queries.parseOSMOListOfOAMO(qres);

		for (Queries.RootOsmoData osmodata : OSMODataList) {
			
			// !!!! depends on this one
			GetServiceV2Impl service = new GetServiceV2Impl(osmodata.getId());
			OSMO osmo = service.getService();

			oamo.getOSMO().add(osmo);
		}
	}
}// class
