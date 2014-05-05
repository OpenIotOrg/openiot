package org.openiot.sdum.core.api.impl.PollForReport;

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

import org.openiot.commons.sdum.serviceresultset.model.PresentationAttr;
import org.openiot.commons.sdum.serviceresultset.model.RequestPresentation;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.commons.sdum.serviceresultset.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryResult;
import org.openiot.commons.sparql.result.model.Binding;
import org.openiot.commons.sparql.result.model.Head;
import org.openiot.commons.sparql.result.model.Literal;
import org.openiot.commons.sparql.result.model.Result;
import org.openiot.commons.sparql.result.model.Results;
import org.openiot.commons.sparql.result.model.Sparql;
import org.openiot.commons.sparql.result.model.Variable;
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
 */

public class PollForReportV2Impl 
{	
	private static class Queries
	{
		// containers //
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
				    	String[] id = kv[1].split("\"preAttrs\":"); w.setWidgetId(id[0]);
				    	
				    	String[] preAttrs = id[1].replaceFirst("\\{", "").split("\"preAttr\":");
				    			    	
				    	for (int i=1; i<preAttrs.length; i++) {
				    		PresentationAttr preAttr = new PresentationAttr();
				    		
				    		String[] valName = preAttrs[i].replace("{", "").replace("}","").split(",");
				    		String val =valName[0].split("\"value\":")[1]; preAttr.setName(val);
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
		
		// parsers //
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
	
	final static Logger logger = LoggerFactory.getLogger(PollForReportV2Impl.class);
	
	private String openiotFunctionalGraph;
	//
	private String serviceID;
	private SdumServiceResultSet sdumServiceResultSet=null;
	
	// cosntructor //
	public PollForReportV2Impl(String serviceID) 
	{		
		logger.debug("Recieved Parameters: serviceID= {}", serviceID);
		
		this.serviceID=serviceID;
		
		PropertyManagement propertyManagement = new PropertyManagement();
		openiotFunctionalGraph = propertyManagement.getSdumLsmFunctionalGraph();
		
		pollForReport();		
	}	
	
	
	public SdumServiceResultSet getSdumServiceResultSet() {
		return sdumServiceResultSet;
	}
	
	
	// core methods //
	private void pollForReport() 
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. ",e);
			return;
		}
		
		// get query stored from request definition
		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getQueryStringListOfOSMO(openiotFunctionalGraph,serviceID));
		Queries.QueryRequest queryRequest = Queries.parseOSMOQueryData(qres);
		
		// execute query... 
		QueryResult queryResult = executeQuery(sparqlCl,queryRequest.getQuery());
		// if something went wrong then queryResult will be null
		if(queryResult==null) {
			this.sdumServiceResultSet = null; // might be better to create it and give an empty xml to the gui
			return;
		}
		// if all went well ... create a sdumServiceResultSet ...
		this.sdumServiceResultSet = new SdumServiceResultSet();
		// ... and add result to sdumServiceResultSet
		this.sdumServiceResultSet.getQueryResult().add(queryResult);
		
		///////////////////////////////////
		
		TupleQueryResult qres2 = sparqlCl.sparqlToQResult(Queries.getWidgetPreListByOSMO(openiotFunctionalGraph,this.serviceID));				
		Queries.RequestPreData reqPreData = Queries.parseWidgetPreListByService(qres2);
		
		//Fill the RequestPresentation
		RequestPresentation requestPresentation = new RequestPresentation();
		
		for (Queries.WidgetData wData : reqPreData.getWidgetList()) 
		{
			Widget widget = new Widget();
			widget.setWidgetID(wData.getWidgetId());
			
			for (Queries.PresentationAttr wPreAttr : wData.getPreAttrList()) 
			{
				PresentationAttr pattr = new PresentationAttr();
				pattr.setName(wPreAttr.getName());
				pattr.setValue(wPreAttr.getValue());

				widget.getPresentationAttr().add(pattr);
			}
			
			requestPresentation.getWidget().add(widget);
		}
		//add the RequestPresentation to the sdumServiceResultSet
		this.sdumServiceResultSet.setRequestPresentation(requestPresentation);
	}
	
	private QueryResult executeQuery(SesameSPARQLClient sparqlClient,String query)
	{
		try
		{
			TupleQueryResult qresultOfOSMOQuery = sparqlClient.sparqlToQResult(query);
			
			if (qresultOfOSMOQuery != null)	
			{
				Sparql sparql = new Sparql();
				
				Head head = new Head();
				for (String value : qresultOfOSMOQuery.getBindingNames()) 
				{			
					Variable var = new Variable();
					var.setName(value);
					head.getVariable().add(var);
				}
					
				sparql.setHead(head);
							
				Results sparqlResults = new Results();	
				while (qresultOfOSMOQuery.hasNext())
				{				
					BindingSet b = qresultOfOSMOQuery.next();
					Set<String> names = b.getBindingNames();				
					
					Result sparqlResult = new Result();
					for (String n : names)
					{
						Binding sparqlResultBinding = new Binding();
						sparqlResultBinding.setName(n);
						
						Literal literalValue = new Literal();
						literalValue.setContent(b.getValue((String) n).stringValue());
						sparqlResultBinding.setLiteral(literalValue );
	//					sparqlResultBinding.setBnode("BnodeValue");		
	//					sparqlResultBinding.setUri("UriValue");
						
						sparqlResult.getBinding().add(sparqlResultBinding);
					}
					
					sparqlResults.getResult().add(sparqlResult);				
				}//while
				
				sparql.setResults(sparqlResults);
				
				QueryResult queryResult = new QueryResult();
				queryResult.setSparql(sparql);
				
				return queryResult;
			}
			else
				return null;
		} catch (QueryEvaluationException e) {				
			logger.error("executeQuery",e);
			return null;
		}catch (Exception e) {				
			logger.error("executeQuery",e);
			return null;
		}
	}
}
