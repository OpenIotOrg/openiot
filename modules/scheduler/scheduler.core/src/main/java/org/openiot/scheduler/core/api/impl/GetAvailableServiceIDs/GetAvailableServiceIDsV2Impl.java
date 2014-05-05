package org.openiot.scheduler.core.api.impl.GetAvailableServiceIDs;

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
 */

public class GetAvailableServiceIDsV2Impl {
	
	private static class Queries 
	{
		public static DescreptiveIDs parseAvailableOSMOIDsByOAMO(TupleQueryResult qres) 
		{
			DescreptiveIDs dids = new DescreptiveIDs();
			
			try {
				while (qres.hasNext()) 
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					DescriptiveID id = new DescriptiveID();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("osmoID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							id.setId(str);
							logger.debug("osmoID: " + id.getId() + " ");
						} else if (((String) n).equalsIgnoreCase("osmoName")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							id.setName(str);
							logger.debug("osmoName : " + id.getName() + " ");
						} else if (((String) n).equalsIgnoreCase("osmoDesc")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							id.setDescription(str);
							logger.debug("osmoDesc : " + id.getDescription() + " ");
						}
					}
					dids.getDescriptiveID().add(id);
				}// while
				return dids;
			} catch (QueryEvaluationException e) {
				logger.error(e.getMessage());
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}

		public static String getAvailableOSMOIDsByOAMO(String lsmFunctionalGraph,String oamoID) {
			StringBuilder query = new StringBuilder();

			query.append( "SELECT ?osmoID ?osmoName ?osmoDesc ");
			query.append( "from <" + lsmFunctionalGraph + "> " ); //http://lsm.deri.ie/OpenIoT/guest/functionaldata#
			query.append( "WHERE " );
			query.append( "{" );
			query.append( "?osmoID <http://openiot.eu/ontology/ns/osmoOfOAMO> <"+oamoID+"> . "); 
			query.append( "optional { ?osmoID  <http://openiot.eu/ontology/ns/osmoDescription> ?osmoDesc . } ");
			query.append( "optional { ?osmoID  <http://openiot.eu/ontology/ns/osmoName> ?osmoName . } ");
			query.append( "}");
			
			return query.toString();
		}
	}//class

	///////
	
	final static Logger logger = LoggerFactory.getLogger(GetAvailableServiceIDsV2Impl.class);

	private String openiotFunctionalGraph;
	//
	private String applicationID;
	private DescreptiveIDs descriptiveIDs;

	// constructor //
	public GetAvailableServiceIDsV2Impl(String applicationID)
	{
		logger.debug("Received Parameters: " + "applicationID=" + applicationID);
		
		this.applicationID = applicationID;
		
		PropertyManagement propertyManagement = new PropertyManagement();		
		openiotFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
		
		findAvailableServiceIDs();
	}

	public DescreptiveIDs getAvailableServiceIDs() {
		return descriptiveIDs;
	}

	
	// core methods //
	private void findAvailableServiceIDs() 
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {
			logger.error("Init sparql repository error. ", e);
			return;
		}

		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getAvailableOSMOIDsByOAMO(openiotFunctionalGraph,applicationID));
		descriptiveIDs = Queries.parseAvailableOSMOIDsByOAMO(qres);
	}
}
