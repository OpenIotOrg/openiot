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
 */

public class GetAvailableServiceIDsImpl {
	
	private static class Queries {

		public static DescreptiveIDs parseAvailableOSMOIDsByOAMO(TupleQueryResult qres) {
			DescreptiveIDs dids = new DescreptiveIDs();
			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					DescriptiveID id = new DescriptiveID();
					;

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("serviceID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							id.setId(str);
							logger.debug("srvc id: " + id.getId() + " ");
						} else if (((String) n).equalsIgnoreCase("srvcName")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							id.setName(str);
							logger.debug("srvcName : " + id.getName() + " ");
						} else if (((String) n).equalsIgnoreCase("srvcDesc")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							id.setDescription(str);
							logger.debug("srvcDesc : " + id.getDescription() + " ");
						}
					}
					dids.getDescriptiveID().add(id);
				}// while
				return dids;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static String getAvailableOSMOIDsByOAMO(String openiotFunctionalGraph,String oamoID) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?serviceID ?srvcName ?srvcDesc "
					+ "from <" 	+ openiotFunctionalGraph + "> "
					+ "WHERE "
					+ "{"
						+ "{"
						+ "SELECT ?serviceID "
						+ "WHERE "
						+ "{"
						+ "?serviceID <http://openiot.eu/ontology/ns/oamo> <" + oamoID+ "> . "
						+ "}"					
						+ "}"
						
					+ "optional { ?serviceID <http://openiot.eu/ontology/ns/serviceName> ?srvcName  . }"
					+ "optional { ?serviceID <http://openiot.eu/ontology/ns/serviceDescription> ?srvcDesc  . }" 
					+ "}");

			update.append(str);
			return update.toString();
		}
	}

	// ///
	final static Logger logger = LoggerFactory.getLogger(GetAvailableServiceIDsImpl.class);

	private String openiotFunctionalGraph;
	//
	private String applicationID;
	private DescreptiveIDs descriptiveIDs;

	// constructor
	public GetAvailableServiceIDsImpl(String applicationID) {
		
		PropertyManagement propertyManagement = new PropertyManagement();		
		openiotFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
		
		this.applicationID = applicationID;
		logger.debug("Received Parameters: " + "applicationID=" + applicationID);
		findAvailableServiceIDs();
	}

	public DescreptiveIDs getAvailableServiceIDs() {
		return descriptiveIDs;
	}

	private void findAvailableServiceIDs() {
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
