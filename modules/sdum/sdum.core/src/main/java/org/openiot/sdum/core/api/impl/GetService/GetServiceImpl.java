package org.openiot.sdum.core.api.impl.GetService;

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

import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.RequestPresentation;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.sdum.core.utils.sparql.SesameSPARQLClient;
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

public class GetServiceImpl {

	

	private static class Queries {
		public static class OsmoRootData {
			private String id;
			private String name;
			private String description;

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

			public String getDescription() {
				return description;
			}

			public void setDescription(String description) {
				this.description = description;
			}
		}
		public static class QueryData {
			private String id;
			private String queryString;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getQueryString() {
				return queryString;
			}

			public void setQueryString(String queryString) {
				this.queryString = queryString;
			}
		}
		public static class WidgetPresentationData {
			private String id;
			private String widgetID;
			private String widgetAttrID;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getWidgetID() {
				return widgetID;
			}

			public void setWidgetID(String widgetID) {
				this.widgetID = widgetID;
			}

			public String getWidgetAttrID() {
				return widgetAttrID;
			}

			public void setWidgetAttrID(String widgetAttrID) {
				this.widgetAttrID = widgetAttrID;
			}
		}
		public static class WidgetAttr {
			private String id;
			private String value;
			private String name;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}
		}

		public static OsmoRootData parseOSMORootData(TupleQueryResult qres) {
			OsmoRootData osmo = new OsmoRootData();

			try {

				BindingSet b = qres.next();
				Set names = b.getBindingNames();

				for (Object n : names) {
					// if(((String) n).equalsIgnoreCase("serviceID"))
					// {
					// String str = (b.getValue((String) n)==null) ? null :
					// b.getValue((String) n).stringValue();
					// osmo.setId(str);
					// System.out.print("serviceID: "+osmo.getId()+" ");
					// }
					if (((String) n).equalsIgnoreCase("srvcName")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
								.stringValue();
						osmo.setName(str);
						System.out.print("srvcName : " + osmo.getName() + " ");
					} else if (((String) n).equalsIgnoreCase("srvcDesc")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
								.stringValue();
						osmo.setDescription(str);
						System.out.print("srvcDesc : " + osmo.getDescription() + " ");
					}
				}

				return osmo;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static ArrayList<QueryData> parseOSMOQueryData(TupleQueryResult qres) {
			ArrayList<QueryData> queryDataList = new ArrayList<QueryData>();

			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					QueryData queryData = new QueryData();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("queryID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							queryData.setId(str);
							System.out.print("queryID id: " + queryData.getId() + " ");
						} else if (((String) n).equalsIgnoreCase("queryString")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							queryData.setQueryString(str);
							System.out.print("queryString : " + queryData.getQueryString() + " ");
						}
					}
					queryDataList.add(queryData);
				}// while
				return queryDataList;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static ArrayList<WidgetPresentationData> parseWidgetPreListByService(TupleQueryResult qres) {
			ArrayList<WidgetPresentationData> widgetPresentationDataList = new ArrayList<WidgetPresentationData>();
			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					WidgetPresentationData widgetPreData = new WidgetPresentationData();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("widgetPreID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							widgetPreData.setId(str);
							System.out.print("widgetPreID: " + widgetPreData.getId() + " ");
						} else if (((String) n).equalsIgnoreCase("widgetID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							widgetPreData.setWidgetID(str);
							System.out.print("widgetID: " + widgetPreData.getWidgetID() + " ");
						} else if (((String) n).equalsIgnoreCase("widgetAttrID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							widgetPreData.setWidgetAttrID(str);
							System.out.print("widgetAttr: " + widgetPreData.getWidgetAttrID() + " ");
						}
					}
					widgetPresentationDataList.add(widgetPreData);
				}// while
				return widgetPresentationDataList;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static ArrayList<WidgetAttr> parseWidgetAttributes(TupleQueryResult qres) {
			ArrayList<WidgetAttr> widgetAttrList = new ArrayList<WidgetAttr>();
			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					WidgetAttr widgetAttr = new WidgetAttr();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("widgetAttrID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							widgetAttr.setId(str);
							System.out.print("widgetattr id: " + widgetAttr.getId() + " ");
						} else if (((String) n).equalsIgnoreCase("widgetAttrName")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							widgetAttr.setName(str);
							System.out.print("widgetAttrName: " + widgetAttr.getName() + " ");
						} else if (((String) n).equalsIgnoreCase("widgetAttrDesc")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							widgetAttr.setValue(str);
							System.out.print("widgetAttrDesc: " + widgetAttr.getValue() + " ");
						}
					}
					widgetAttrList.add(widgetAttr);
				}// while
				return widgetAttrList;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static String getOSMORootData(String openiotFunctionalGraph,String osmoID) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?srvcName ?srvcDesc " 
					+ "from <" + openiotFunctionalGraph + "> " 
					+ "WHERE "
					+ "{" 
					+ "optional {<" + osmoID + "> <http://openiot.eu/ontology/ns/serviceName> ?srvcName . }" 
					+ "optional {<" + osmoID + "> <http://openiot.eu/ontology/ns/serviceDescription> ?srvcDesc . }" 
					+ "}");

			update.append(str);
			return update.toString();
		}

		public static String getQueryListOfOSMO(String openiotFunctionalGraph,String osmoID) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?queryID ?queryString " 
					+ "from <" + openiotFunctionalGraph + "> "
					+ "WHERE " 
					+ "{" 
					+ "?queryID <http://openiot.eu/ontology/ns/queryString> ?queryString . "
					+ "<" + osmoID + "> <http://openiot.eu/ontology/ns/query> ?queryID . "
					// TODO:check for descirption
					+ "}");

			update.append(str);
			return update.toString();
		}

		public static String getWidgetPreListByOSMO(String openiotFunctionalGraph,String osmoID) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?widgetPreID ?widgetID ?widgetAttrID " 
					+ "from <" + openiotFunctionalGraph + "> " 
					+ "WHERE " 
					+ "{"
					+ "?widgetPreID <http://openiot.eu/ontology/ns/widgetAttribute> ?widgetAttrID . "
					+ "?widgetPreID <http://openiot.eu/ontology/ns/widget> ?widgetID . "
					+ "?widgetPreID <http://openiot.eu/ontology/ns/widgetPresOf> <" + osmoID + "> . "
					+ "?widgetPreID rdf:type <http://openiot.eu/ontology/ns/WidgetPresentation> ." 
					+ "}");

			update.append(str);
			return update.toString();
		}

		public static String getWidgetAttrByWidgetPre(String openiotFunctionalGraph,String widgetPreID) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?widgetAttrID ?widgetAttrName ?widgetAttrDesc "
					+ "from <" + openiotFunctionalGraph + "> " 
					+ "WHERE " 
					+ "{"
					+ "?widgetAttrID <http://openiot.eu/ontology/ns/widgeAttrDescription> ?widgetAttrDesc . "
					+ "?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrName> ?widgetAttrName . "
					+ "?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrOf> <" + widgetPreID + "> . " 
					+ "}");

			update.append(str);
			return update.toString();
		}
	}

	// //
	final static Logger logger = LoggerFactory.getLogger(GetServiceImpl.class);

	private String openiotFunctionalGraph;
	//
	private String osmoID;
	private OSMO osmo;

	public GetServiceImpl(String osmoID) {

		PropertyManagement propertyManagement = new PropertyManagement();
		openiotFunctionalGraph = propertyManagement.getSdumLsmFunctionalGraph();

		this.osmoID = osmoID;
		logger.debug("Received Parameters: " + "osmoID=" + osmoID);
		findOSMO();
	}

	public OSMO getService() {
		return osmo;
	}

	private void findOSMO() {
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {
			logger.error("Init sparql repository error. ", e);
			return;
		}

		osmo = new OSMO();
		osmo.setId(osmoID);

		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getOSMORootData(openiotFunctionalGraph,osmoID));
		Queries.OsmoRootData osmoRoot = Queries.parseOSMORootData(qres);

		if (osmoRoot != null) {
			osmo.setDescription(osmoRoot.getDescription());
			osmo.setName(osmoRoot.getName());
		}

		qres = sparqlCl.sparqlToQResult(Queries.getQueryListOfOSMO(openiotFunctionalGraph,osmoID));
		ArrayList<Queries.QueryData> queryDataList = Queries.parseOSMOQueryData(qres);

		for (Queries.QueryData queryData : queryDataList) {
			QueryRequest qr = new QueryRequest();
			qr.setQuery(queryData.getQueryString());
			osmo.getQueryRequest().add(qr);
		}

		qres = sparqlCl.sparqlToQResult(Queries.getWidgetPreListByOSMO(openiotFunctionalGraph,osmoID));
		ArrayList<Queries.WidgetPresentationData> widgetPresentationDataList = Queries
				.parseWidgetPreListByService(qres);

		for (Queries.WidgetPresentationData widgetPresentationData : widgetPresentationDataList) {
			RequestPresentation reqp = new RequestPresentation();

			Widget w = new Widget();
			w.setWidgetID(widgetPresentationData.getId());

			qres = sparqlCl.sparqlToQResult(Queries.getWidgetAttrByWidgetPre(openiotFunctionalGraph,widgetPresentationData.getId()));
			ArrayList<Queries.WidgetAttr> widgetAttr = Queries.parseWidgetAttributes(qres);

			for (Queries.WidgetAttr wattr : widgetAttr) {
				PresentationAttr pattr = new PresentationAttr();
				pattr.setName(wattr.getName());
				pattr.setValue(wattr.getValue());

				w.getPresentationAttr().add(pattr);
			}

			reqp.getWidget().add(w);
			osmo.setRequestPresentation(reqp);
		}
	}
}
