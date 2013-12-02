package org.openiot.scheduler.core.api.impl.GetServiceStatus;


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
import java.util.List;
import java.util.Set;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class GetServiceStatus {

	private static class Queries {
		public static class ServiceStatusData {
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

		public static ArrayList<String> parseServicesOfOAMO(TupleQueryResult qres) {
			ArrayList<String> oamoServicesList = new ArrayList<String>();

			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();

					String oamoService = null;

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("serviceID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							oamoService = str;
							System.out.print("oamo service: " + oamoService);
						}
					}
					oamoServicesList.add(oamoService);
				}// while
				return oamoServicesList;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static ServiceStatusData parseServiceStatusOfOSMO(TupleQueryResult qres) {
			ServiceStatusData serviceStatusData = new ServiceStatusData();
			try {
				// while (qres.hasNext())
				// {
				BindingSet b = qres.next();
				Set names = b.getBindingNames();

				for (Object n : names) {
					if (((String) n).equalsIgnoreCase("srvcStatus")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
								.stringValue();
						serviceStatusData.setServiceStatus(str);
						System.out.print("setServiceStatus: " + serviceStatusData.getServiceStatus() + " ");
					} else if (((String) n).equalsIgnoreCase("srvcStatusTime")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
								.stringValue();
						serviceStatusData.setServiceStatusTime(str);
						System.out
								.print("srvcStatusTime : " + serviceStatusData.getServiceStatusTime() + " ");
					}
				}

				// }//while
				return serviceStatusData;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static String getServicesOfOAMO(String lsmFunctionalGraph,String oamoID) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?serviceID  " 
					+ "from <" + lsmFunctionalGraph + "> " 
					+ "WHERE " 
					+ "{"
					+ "?serviceID <http://openiot.eu/ontology/ns/oamo> <" + oamoID + "> . "
					+ "}");

			update.append(str);
			return update.toString();
		}

		public static String getServiceStatusOfOSMO(String lsmFunctionalGraph,String serviceID) {
			StringBuilder update = new StringBuilder();

			String str = ("select ?srvcStatus ?srvcStatusTime " 
					+ "from <" + lsmFunctionalGraph + "> "
					+ "WHERE " 
					+ "{"
					+ "?srvcStatusID rdf:type ?srvcStatus ."
					+ "?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusTime> ?srvcStatusTime ."
					+ "?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusOf> <" + serviceID + "> ."
					+ "}");

			update.append(str);
			return update.toString();
		}
	}

	// ///

	final static Logger logger = LoggerFactory.getLogger(GetServiceStatus.class);

	private String lsmFunctionalGraph;
	//
	private String oamoID;

	// constructor
	public GetServiceStatus(String oamoID) {

		PropertyManagement propertyManagement = new PropertyManagement();
		lsmFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();

		this.oamoID = oamoID;

		logger.debug("Recieved Parameters: " + "serviceID=" + oamoID);

		findServiceStatus();
	}

	// public ServiceStatus getServiceStatus()
	// {
	// return serviceStatusData;
	// }


	private void findServiceStatus() {
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {
			logger.error("Init sparql repository error. ", e);
			return;
		}

		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getServicesOfOAMO(lsmFunctionalGraph,oamoID));
		ArrayList<String> oamoServicesList = Queries.parseServicesOfOAMO(qres);
		for (String oamoID : oamoServicesList) {
			qres = sparqlCl.sparqlToQResult(Queries.getServiceStatusOfOSMO(lsmFunctionalGraph,oamoID));
			Queries.ServiceStatusData serviceStatusData = Queries.parseServiceStatusOfOSMO(qres);

			// need to populate the ServiceStatus object which is described in
			// Community&DevelopmentCookbook
		}
	}

}// class
