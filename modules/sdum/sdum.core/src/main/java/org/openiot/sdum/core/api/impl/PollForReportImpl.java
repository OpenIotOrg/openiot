package org.openiot.sdum.core.api.impl;


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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

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
import org.openiot.sdum.core.utils.sparql.SesameSPARQLClient;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author kefnik
 *
 */
public class PollForReportImpl 
{
	private static class Queries
	{		
		private static String openiotTestGraph = "http://lsm.deri.ie/OpenIoT/testSchema#";
				
		public static String parseQueryFromService(TupleQueryResult qres)
		{
			String query = null;
			try
			{
//				while (qres.hasNext())
//				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
//					String sensorType = null;
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("srvcQstring"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							query =str;
							System.out.print("query string: "+query+" ");	
						}
					}
//					sensorTypes.add(sensorType);					
//				}//while
				return query;
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
		public static ArrayList<ServicePresentationData> parseWPresentationFromService(TupleQueryResult qres)
		{
			ArrayList<ServicePresentationData> srvcPreDataList = new ArrayList<ServicePresentationData>();
			try
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					ServicePresentationData srvcPreData = new ServicePresentationData();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("widgetPreID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcPreData.setWidgetPreID(str);
							System.out.print("widgetPreID : "+str+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcPreData.setWidgetID(str);
							System.out.print("widgetID : "+str+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetAttrID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcPreData.setWidgetAttrID(str);
							System.out.print("widgetAttrID : "+str+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetAttrName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcPreData.setWidgetAttrName(str);
							System.out.print("widgetAttrName : "+str+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetAttrDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcPreData.setWidgetAttrDesc(str);
							System.out.print("widgetAttrDesc : "+str+" ");	
						}
					}
					srvcPreDataList.add(srvcPreData);					
				}//while
				return srvcPreDataList;
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
		
		
		public static String getQueryFromService(String serviceID)
		{
			StringBuilder update = new StringBuilder();	        
			
			String str=("SELECT ?srvcQstring " 
					+"from <"+openiotTestGraph+"> "
					+"WHERE "
					+"{"
					+"<"+serviceID+"> <http://openiot.eu/ontology/ns/queryString> ?srvcQstring . "													
					+"}");	
			
			update.append(str);
			return update.toString();
		}
		public static String getWPresentationFromService(String serviceID)
		{
			StringBuilder update = new StringBuilder();	        
			
			String str=("SELECT ?widgetPreID ?widgetID  ?widgetAttrID  ?widgetAttrName ?widgetAttrDesc " 
					+"from <"+openiotTestGraph+"> "
					+"WHERE "
					+"{"
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgeAttrDescription> ?widgetAttrDesc . "
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrName> ?widgetAttrName . "
					
					+"?widgetPreID <http://openiot.eu/ontology/ns/widgetAttribute> ?widgetAttrID . "
					+"?widgetPreID <http://openiot.eu/ontology/ns/widget> ?widgetID . "
					+"?widgetPreID <http://openiot.eu/ontology/ns/widgetPresOf> <"+serviceID+"> ."
					+"}");	
			
			update.append(str);
			return update.toString();
		}
	}
	
	final static Logger logger = LoggerFactory.getLogger(PollForReportImpl.class);
	
	private String serviceID;
	private SdumServiceResultSet sdumServiceResultSet=null;
	
	//cosntructor
	public PollForReportImpl(String serviceID) 
	{		
		logger.debug("Recieved Parameters: serviceID= {}", serviceID);
		
		this.serviceID=serviceID;
		
		pollForReport();		
	}	
	
	
	public SdumServiceResultSet getSdumServiceResultSet() 
	{
		return sdumServiceResultSet;
	}
	
	
	//helper methods
	private void pollForReport() 
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(PollForReportImpl.Queries.getQueryFromService(this.serviceID));				
		String serviceQuery = PollForReportImpl.Queries.parseQueryFromService(qres);
				
		//String xmlResult = sparqlCl.sparqlToXml(serviceQuery);
		TupleQueryResult qres2 = sparqlCl.sparqlToQResult(serviceQuery);
		

		SdumServiceResultSet testSdumServiceResultSet2 = new SdumServiceResultSet();
		
		try
		{
			Sparql sparql = new Sparql();
			
			Head head = new Head();
			for (String value : qres2.getBindingNames()) 
			{			
				Variable var = new Variable();
				var.setName(value);
				head.getVariable().add(var);
			}
			
			sparql.setHead(head);
						
			Results sparqlResults = new Results();	
			while (qres2.hasNext())
			{				
				BindingSet b = qres2.next();
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
			
			testSdumServiceResultSet2.setQueryResult(queryResult);			
		} 
		catch (QueryEvaluationException e)			
		{				
			e.printStackTrace();			
		}
		catch (Exception e)			
		{				
			e.printStackTrace();			
		}
				
		/////////////////////////////////////////////////////////////////////////////////

		TupleQueryResult qres3 = sparqlCl.sparqlToQResult(PollForReportImpl.Queries.getWPresentationFromService(this.serviceID));				
		ArrayList<ServicePresentationData> srvcPreDatas = PollForReportImpl.Queries.parseWPresentationFromService(qres3);
		
		ArrayList<String> distinctWidgetP = new ArrayList<String>();
		
		//Fill the RequestPresentation
		RequestPresentation requestPresentation = new RequestPresentation();
		
		for (ServicePresentationData srvcPreData : srvcPreDatas) 
		{
			Widget widget = new Widget();
			widget.setWidgetID(srvcPreData.getWidgetID());
			
			int idx = checkExists(widget.getWidgetID(),requestPresentation);
			if(idx==-1){
				requestPresentation.getWidget().add(widget);
			}
			else{
				widget = requestPresentation.getWidget().get(idx);
			}
			
			PresentationAttr presentationAttr = new PresentationAttr();
			presentationAttr.setName(srvcPreData.getWidgetAttrName());
			presentationAttr.setValue(srvcPreData.getWidgetAttrDesc());
			
			widget.getPresentationAttr().add(presentationAttr);			
		}
		
		testSdumServiceResultSet2.setRequestPresentation(requestPresentation);
	
		this.sdumServiceResultSet = testSdumServiceResultSet2;
	}
	
	private int checkExists(String widgetID,RequestPresentation requestPresentation)
	{
		if(requestPresentation.getWidget().isEmpty())
		{
			return -1;
		}
		else
		{
			for (int i=0; i<requestPresentation.getWidget().size(); i++) 
			{
				if(widgetID.equals(requestPresentation.getWidget().get(i).getWidgetID()))
				{
					return i;
				}				
			}
			
			return -1;
		}
	}
}
