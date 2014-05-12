package org.openiot.scheduler.core.api.impl.GetService;

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
import java.util.Scanner;
import java.util.Set;

import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.QueryControls;
import org.openiot.commons.osdspec.model.RequestPresentation;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
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

public class GetServiceV2Impl 
{
	private static class Queries 
	{
		// containers //
		public static class OsmoRootData 
		{
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
		public static class QControls
		{
			private String qControlsString;
			private boolean reportIfEmpty;
			
			
			public String getqControlsString() {
				return qControlsString;
			}
			public void setqControlsString(String qControlsString) {
				this.qControlsString = qControlsString;
				
				parseQControls();
			}
			
			
			public boolean getReportIfEmpty() {
				return reportIfEmpty;
			}
			public void setReportIfEmpty(boolean reportIfEmpty) {
				this.reportIfEmpty = reportIfEmpty;
			}
			
			// helper methods //
			private void parseQControls()
			{
				String[] ifEmpty = qControlsString.split("\"reportIfEmpty\":");
				setReportIfEmpty(Boolean.parseBoolean(ifEmpty[1].replace("}", "")));
			}
		}
		public static class QueryRequest 
		{
//			private String id;
			private String queryString;
			private String query;

//			public String getId() {
//				return id;
//			}
//			public void setId(String id) {
//				this.id = id;
//			}
			public String getQueryString() {
				return queryString;
			}
			public void setQueryString(String queryString) {
				this.queryString = queryString;
				
				parseQString();
			}
			

			public String getQuery() {
				return query;
			}
			public void setQuery(String query) {
				this.query = query;
			}
			
			// helper methods //
			private void parseQString()
			{
				String[] query = queryString.split("\"queryString\":");
				setQuery(query[1].replaceFirst("\\}", ""));
			}
		}
		
		public static class RequestPreData 
		{
			private String reqPreString;
			private ArrayList<WidgetData> widgetList;
			
			
			public String getReqPreString() {
				return reqPreString;
			}
			public void setReqPreString(String reqPreString) {
				this.reqPreString = reqPreString;
				
				parseReqPresString();
			}
			
			public ArrayList<WidgetData> getWidgetList()	{
				if (widgetList == null) {
					widgetList = new ArrayList<WidgetData>();
		        }
				return widgetList;
			}
			
			// helper methods //
			private void parseReqPresString()
			{
				Scanner sc = new Scanner(reqPreString);
				sc.useDelimiter("\"widget\"");
				while (sc.hasNext()) 
				{
				    String widget = sc.next();
				    if(widget.contains("\"id\"")) {
				    	
				    	WidgetData w = new WidgetData();
				    	getWidgetList().add(w);
				    	
				    	String[] kv = widget.split("\"id\":");
				    	String[] id = kv[1].split("\"preAttrs\":"); w.setWidgetId(id[0].replace(",", ""));
				    	
				    	String[] preAttrs = id[1].replaceFirst("\\{", "").split("\"preAttr\":");
				    			    	
				    	for (int i=1; i<preAttrs.length; i++) {
				    		PresentationAttr preAttr = new PresentationAttr();
				    		
				    		String[] valName = preAttrs[i].replace("{", "").replace("}","").split(",");
				    		String val =valName[0].split("\"value\":")[1]; preAttr.setValue(val);
				    		String name =valName[1].split("\"name\":")[1]; preAttr.setName(name);
				    		
				    		w.getPreAttrList().add(preAttr);
				    	}
				    }
				}
				sc.close();
			}
		}
		public static class WidgetData 
		{
			private String widgetId;
			private ArrayList<PresentationAttr> preAttrList;

			public String getWidgetId() {
				return widgetId;
			}
			public void setWidgetId(String widgetId) {
				this.widgetId = widgetId;
			}
			
			public ArrayList<PresentationAttr> getPreAttrList()	{
				if (preAttrList == null) {
					preAttrList = new ArrayList<PresentationAttr>();
		        }
				return preAttrList;
			}
		}
		public static class PresentationAttr 
		{
			private String name;
			private String value;
			
			
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
		}
		
		// query parser //
		public static OsmoRootData parseOSMORootData(TupleQueryResult qres) 
		{
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
					if (((String) n).equalsIgnoreCase("osmoName")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
						osmo.setName(str);
						logger.debug("srvcName : " + osmo.getName() + " ");
					} else if (((String) n).equalsIgnoreCase("osmoDesc")) {
						String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
						osmo.setDescription(str);
						logger.debug("srvcDesc : " + osmo.getDescription() + " ");
					}
				}

				return osmo;
			} catch (QueryEvaluationException e) {
				logger.error(e.getMessage());
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}

		public static QControls parseOSMOQueryCtrl(TupleQueryResult qres) 
		{
			QControls queryCtrl = new QControls();

			try {
//				while (qres.hasNext()) 
//				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
//					QueryData queryData = new QueryData();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("qCtrl")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							queryCtrl.setqControlsString(str);
							logger.debug("queryCtrl : " + queryCtrl.getqControlsString() + " ");
						}
					}
//					queryDataList.add(queryData);
//				}// while
				return queryCtrl;
			} catch (QueryEvaluationException e) {
				logger.error(e.getMessage());
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}
		
		public static QueryRequest parseOSMOQueryData(TupleQueryResult qres) 
		{
			QueryRequest queryData = new QueryRequest();

			try {
//				while (qres.hasNext()) 
//				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
//					QueryData queryData = new QueryData();

					for (Object n : names) {
//						if (((String) n).equalsIgnoreCase("queryID")) {
//							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
//							queryData.setId(str);
//							logger.debug("queryID id: " + queryData.getId() + " ");
//						} else 
						if (((String) n).equalsIgnoreCase("qReq")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							queryData.setQueryString(str);
							logger.debug("queryString : " + queryData.getQueryString() + " ");
						}
					}
//					queryDataList.add(queryData);
//				}// while
				return queryData;
			} catch (QueryEvaluationException e) {
				logger.error(e.getMessage());
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}
		
		public static RequestPreData parseWidgetPreListByService(TupleQueryResult qres) 
		{
			RequestPreData reqPresentation = new RequestPreData();
			
			try {
//				while (qres.hasNext()) 
//				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("reqPre")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n).stringValue();
							reqPresentation.setReqPreString(str);
							logger.debug("reqPre: " + reqPresentation.getReqPreString() + " ");
						} 
					}
//				}// while
				return reqPresentation;
			} catch (QueryEvaluationException e) {
				logger.error(e.getMessage());
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}

		// queries //
		public static String getOSMORootData(String lsmFunctionalGraph,String osmoID) 
		{
			StringBuilder query = new StringBuilder();

			query.append( "SELECT ?osmoName ?osmoDesc ");
			query.append( "from <" + lsmFunctionalGraph + "> " ); //http://lsm.deri.ie/OpenIoT/guest/functionaldata#
			query.append( "WHERE " );
			query.append( "{" );
			query.append( "optional { <"+osmoID+">  <http://openiot.eu/ontology/ns/osmoDescription> ?osmoDesc . } ");
			query.append( "optional { <"+osmoID+">  <http://openiot.eu/ontology/ns/osmoName> ?osmoName . } ");
			query.append( "}");
			
			return query.toString();
		}

		public static String getQueryControlsOfOSMO(String lsmFunctionalGraph,String osmoID) 
		{
			StringBuilder query = new StringBuilder();

			query.append( "SELECT ?qCtrl ");
			query.append( "from <" + lsmFunctionalGraph + "> " ); //http://lsm.deri.ie/OpenIoT/guest/functionaldata#
			query.append( "WHERE " );
			query.append( "{" );
			query.append( "<"+osmoID+">  <http://openiot.eu/ontology/ns/osmoHasQueryControls> ?qCtrl . ");
			query.append( "}");
			
			return query.toString();
		}
		
		public static String getQueryStringListOfOSMO(String lsmFunctionalGraph,String osmoID) 
		{
			StringBuilder query = new StringBuilder();

			query.append( "SELECT ?qReq ");
			query.append( "from <" + lsmFunctionalGraph + "> " ); //http://lsm.deri.ie/OpenIoT/guest/functionaldata#
			query.append( "WHERE " );
			query.append( "{" );
			query.append( "<"+osmoID+">  <http://openiot.eu/ontology/ns/osmoHasQueryRequest> ?qReq . ");
			query.append( "}");
			
			return query.toString();
		}

		public static String getWidgetPreListByOSMO(String lsmFunctionalGraph,String osmoID) 
		{
			StringBuilder query = new StringBuilder();

			query.append( "SELECT ?reqPre ");
			query.append( "from <" + lsmFunctionalGraph + "> " ); //http://lsm.deri.ie/OpenIoT/guest/functionaldata#
			query.append( "WHERE " );
			query.append( "{" );
			query.append( "<"+osmoID+">  <http://openiot.eu/ontology/ns/osmoHasRequestpresentation> ?reqPre . ");
			query.append( "}");
			
			return query.toString();
		}
	}//class

	/////
	final static Logger logger = LoggerFactory.getLogger(GetServiceV2Impl.class);

	private String openiotFunctionalGraph;
	//
	private String osmoID;
	private OSMO osmo;

	// constructor //
	public GetServiceV2Impl(String osmoID) 
	{
		logger.debug("Received Parameters: " + "osmoID=" + osmoID);

		this.osmoID = osmoID;
		
		PropertyManagement propertyManagement = new PropertyManagement();
		openiotFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
		
		findOSMO();
	}

	public OSMO getService() {
		return osmo;
	}

	
	// core methods //
	private void findOSMO() 
	{
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

		qres = sparqlCl.sparqlToQResult(Queries.getQueryStringListOfOSMO(openiotFunctionalGraph,osmoID));
		Queries.QueryRequest queryReq = Queries.parseOSMOQueryData(qres);

		if (queryReq != null) {
			QueryRequest qr = new QueryRequest();
			qr.setQuery(queryReq.getQuery());
			osmo.getQueryRequest().add(qr);
		}
			
		qres = sparqlCl.sparqlToQResult(Queries.getQueryControlsOfOSMO(openiotFunctionalGraph,osmoID));
		Queries.QControls qCtrl = Queries.parseOSMOQueryCtrl(qres);

		if (qCtrl != null) {
			QueryControls qC = new QueryControls();
			qC.setReportIfEmpty(qCtrl.getReportIfEmpty());
			osmo.setQueryControls(qC);
		}
		
		qres = sparqlCl.sparqlToQResult(Queries.getWidgetPreListByOSMO(openiotFunctionalGraph,osmoID));
		Queries.RequestPreData reqPre = Queries.parseWidgetPreListByService(qres);
		
		RequestPresentation reqp = new RequestPresentation();
		
		for(Queries.WidgetData wData: reqPre.getWidgetList())
		{
			Widget w = new Widget();
			w.setWidgetID(wData.getWidgetId());
			
			for (Queries.PresentationAttr wPreAttr : wData.getPreAttrList()) 
			{
				PresentationAttr pattr = new PresentationAttr();
				pattr.setName(wPreAttr.getName());
				pattr.setValue(wPreAttr.getValue());

				w.getPresentationAttr().add(pattr);
			}
			
			reqp.getWidget().add(w);
		}
		osmo.setRequestPresentation(reqp);
	}
}//class