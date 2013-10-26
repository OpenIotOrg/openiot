package org.openiot.sdum.core.api.impl.GetApplication;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.RequestPresentation;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.sdum.core.api.impl.GetService.GetServiceImpl;
import org.openiot.sdum.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetApplicationImpl 
{
	
	private static String openiotFunctionalGraph = "";
	
	private static class Queries
	{		
		public static class RootOAMOData
		{
			private String oamoName; 
			private String userID;
			private String oamoDesc;
			private String oamoGraphMeta;
//			private String serviceID;
			
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
			
//			public String getServiceID() {
//				return serviceID;
//			}
//			public void setServiceID(String serviceID) {
//				this.serviceID = serviceID;
//			}
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
		public static class QueryData
		{
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
		public static class ServiceStatusData
		{
			private String srvcStatusID; 
			private String srvcStatusTime;
			private String srvcStatus;
			
			public String getSrvcStatusID() {
				return srvcStatusID;
			}
			public void setSrvcStatusID(String srvcStatusID) {
				this.srvcStatusID = srvcStatusID;
			}
			public String getSrvcStatusTime() {
				return srvcStatusTime;
			}
			public void setSrvcStatusTime(String srvcStatusTime) {
				this.srvcStatusTime = srvcStatusTime;
			}
			public String getSrvcStatus() {
				return srvcStatus;
			}
			public void setSrvcStatus(String srvcStatus) {
				this.srvcStatus = srvcStatus;
			}			
		}
		public static class WidgetPresentationData
		{
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
		public static class WidgetAttr
		{
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
		

		
		public static RootOAMOData parseOAMORootData(TupleQueryResult qres)
		{
			RootOAMOData rootOAMOData = new RootOAMOData();
			
			try
			{
//				while (qres.hasNext())
//				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();	
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("oamoName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							rootOAMOData.setOamoName(str);
							System.out.print("oamoName: "+rootOAMOData.getOamoName());	
						}
						else if(((String) n).equalsIgnoreCase("userID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							rootOAMOData.setUserID(str);
							System.out.print("userID: "+rootOAMOData.getUserID());	
						}
//						else if(((String) n).equalsIgnoreCase("serviceID"))
//						{
//							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							rootOAMOData.setServiceID(str);
//							System.out.print("serviceID: "+rootOAMOData.getServiceID());	
//						}
						else if(((String) n).equalsIgnoreCase("oamoDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();							
							rootOAMOData.setOamoDesc(str);
							System.out.println("oamoDesc : "+rootOAMOData.getOamoDesc()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("oamoGraphMeta"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							rootOAMOData.setOamoGraphMeta(str);
							System.out.println("oamoGraphMeta : "+rootOAMOData.getOamoGraphMeta()+" ");	
						}
					}
				//}//while
				return rootOAMOData;
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
		public static ArrayList<RootOsmoData> parseOSMOListOfOAMO(TupleQueryResult qres)
		{
			ArrayList<RootOsmoData> osmoDataList = new ArrayList<RootOsmoData>();
			
			try
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();					
					
					RootOsmoData osmoData = new RootOsmoData();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("serviceID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmoData.setId(str);
							System.out.print("serviceID: "+osmoData.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmoData.setName(str);
							System.out.print("srvcName : "+osmoData.getName()+" ");
						}
						else if(((String) n).equalsIgnoreCase("srvcDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmoData.setDesc(str);
							System.out.print("srvcDesc : "+osmoData.getDesc()+" ");
						}
					}
					osmoDataList.add(osmoData);				
				}//while
				return osmoDataList;
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
		public static ArrayList<QueryData> parseOSMOQueryData(TupleQueryResult qres)
		{
			ArrayList<QueryData> queryDataList = new ArrayList<QueryData>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					QueryData queryData = new QueryData();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("queryID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryData.setId(str);
							System.out.print("srvcStatus id: "+queryData.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryString"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryData.setQueryString(str);
							System.out.print("srvcStatusTime : "+queryData.getQueryString()+" ");	
						}
					}
					queryDataList.add(queryData);					
				}//while
				return queryDataList;
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
		public static ArrayList<ServiceStatusData> parseServiceStatusOfOSMO(TupleQueryResult qres)
		{
			ArrayList<ServiceStatusData> serviceStatusDataList = new ArrayList<ServiceStatusData>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					ServiceStatusData srvcStatusData = new ServiceStatusData();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("srvcStatusID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcStatusData.setSrvcStatusID(str);
							System.out.print("srvcStatus id: "+srvcStatusData.getSrvcStatusID()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcStatusTime"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcStatusData.setSrvcStatusTime(str);
							System.out.print("srvcStatusTime : "+srvcStatusData.getSrvcStatusTime()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcStatus"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcStatusData.setSrvcStatus(str);
							System.out.print("srvcStatusStatus : "+srvcStatusData.getSrvcStatus()+" ");	
						}
					}
					serviceStatusDataList.add(srvcStatusData);					
				}//while
				return serviceStatusDataList;
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
		public static ArrayList<WidgetPresentationData> parseWidgetPreListByService(TupleQueryResult qres)
		{
			ArrayList<WidgetPresentationData> widgetPresentationDataList = new ArrayList<WidgetPresentationData>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					WidgetPresentationData widgetPreData = new WidgetPresentationData();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("widgetPreID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widgetPreData.setId(str);
							System.out.print("widgetPreID: "+widgetPreData.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();							
							widgetPreData.setWidgetID(str);
							System.out.print("widgetID: "+widgetPreData.getWidgetID()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetAttrID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widgetPreData.setWidgetAttrID(str);
							System.out.print("widgetAttr: "+widgetPreData.getWidgetAttrID()+" ");	
						}
					}
					widgetPresentationDataList.add(widgetPreData);					
				}//while
				return widgetPresentationDataList;
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
		public static ArrayList<WidgetAttr> parseWidgetAttributes(TupleQueryResult qres)
		{
			ArrayList<WidgetAttr> widgetAttrList = new ArrayList<WidgetAttr>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					WidgetAttr widgetAttr = new WidgetAttr();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("widgetAttrID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widgetAttr.setId(str);
							System.out.print("widgetattr id: "+widgetAttr.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetAttrName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widgetAttr.setName(str);
							System.out.print("widgetAttrName: "+widgetAttr.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetAttrDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widgetAttr.setValue(str);
							System.out.print("widgetAttrDesc: "+widgetAttr.getValue()+" ");	
						}
					}
					widgetAttrList.add(widgetAttr);					
				}//while
				return widgetAttrList;
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
		
		public static String getRootOAMOData(String oamoID)
		{
			StringBuilder update = new StringBuilder();	        
			
			String str=("SELECT ?oamoName ?userID ?oamoGraphMeta ?oamoDesc "//?serviceID "
								+"from <"+openiotFunctionalGraph+"> "
								+"WHERE "
								+"{"
								+"<"+oamoID+"> <http://openiot.eu/ontology/ns/oamoDescription> ?oamoDesc . "
								+"<"+oamoID+"> <http://openiot.eu/ontology/ns/oamoGraphMeta> ?oamoGraphMeta . "
								//+"<"+oamoID+"> <http://openiot.eu/ontology/ns/oamoService> ?serviceID . "
								+"<"+oamoID+"> <http://openiot.eu/ontology/ns/oamoUserOf> ?userID . "
								+"<"+oamoID+">  <http://openiot.eu/ontology/ns/oamoName> ?oamoName . "
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String getOSMOListOfOAMO(String oamoID)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("SELECT ?serviceID ?srvcName ?srvcDesc " 
					+"from <"+openiotFunctionalGraph+"> "
					+"WHERE "
					+"{"
					+" ?serviceID <http://openiot.eu/ontology/ns/oamo> <"+oamoID+"> . "
					+" optional { ?serviceID <http://openiot.eu/ontology/ns/serviceName> ?srvcName . }"
					+" optional { ?serviceID <http://openiot.eu/ontology/ns/serviceDescription> ?srvcDesc . }"													
					+"}");
			
			update.append(str);
			return update.toString();
		}
		public static String getQueryListOfOSMO(String osmoID)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("SELECT ?queryID ?queryString" 
					+"from <"+openiotFunctionalGraph+"> "
					+"WHERE "
					+"{"
					+"?queryID <http://openiot.eu/ontology/ns/queryString> ?queryString . "
					+"<"+osmoID+"> <http://openiot.eu/ontology/ns/query> ?queryID . "
					+"}");
			
			update.append(str);
			return update.toString();
		}		
		public static String getServiceStatusOfOSMO(String osmoID)
		{
			StringBuilder update = new StringBuilder();
			
			String str=("select ?srvcStatusID ?srvcStatus ?srvcStatusTime "
								+"from <"+openiotFunctionalGraph+"> "
								+"WHERE "
								+"{"
								
								+"?srvcStatusID rdf:type ?srvcStatus ."
								+"?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusTime> ?srvcStatusTime ."
								+"?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusOf> <"+osmoID+"> ."								
								+"}");
			
			update.append(str);
			return update.toString();
		}
		public static String getWidgetPreListByService(String srvcID)
		{
			StringBuilder update = new StringBuilder();
			
			String str=("SELECT ?widgetPreID ?widgetID ?widgetAttrID "
								+"from <"+openiotFunctionalGraph+"> "
								+"WHERE "
								+"{"
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetAttribute> ?widgetAttrID . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widget> ?widgetID . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetPresOf> <"+srvcID+"> . "
								+"?widgetPreID rdf:type <http://openiot.eu/ontology/ns/WidgetPresentation> ."
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String getWidgetAttrByWidgetPre(String widgetPreID)
		{
			StringBuilder update = new StringBuilder();
				        	        
	        String str=("SELECT ?widgetAttrID ?widgetAttrName ?widgetAttrDesc "
	        		+"from <"+openiotFunctionalGraph+"> "
					+"WHERE "
					+"{"
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgeAttrDescription> ?widgetAttrDesc . "								
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrName> ?widgetAttrName . "
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrOf> <"+widgetPreID+"> . "					
					+"}");
	        
	        update.append(str);
			return update.toString();
		}
		
	}
	
	/////
	
	final static Logger logger = LoggerFactory.getLogger(GetApplicationImpl.class);
	
	private String oamoID;
	private OAMO oamo;
	
	//cosntructor
	public GetApplicationImpl(String oamoID)
	{
		
		PropertyManagement propertyManagement = new PropertyManagement();
		openiotFunctionalGraph = propertyManagement.getSdumLsmFunctionalGraph();
		
		this.oamoID = oamoID;		
		logger.debug("Received Parameters: " +	"oamoID=" + oamoID );
		findApplication();
	}
	
	public OAMO getOAMO()
	{
		return oamo;
	}
	
	private void findApplication()
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. ",e);
			return;
		}
		
		oamo = new OAMO();
		
		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getRootOAMOData(oamoID));
		Queries.RootOAMOData rootOAMODATA = Queries.parseOAMORootData(qres);
		
		oamo.setName(rootOAMODATA.getOamoName());		
		oamo.setId(rootOAMODATA.getUserID());
		oamo.setDescription(rootOAMODATA.getOamoDesc());
		oamo.setGraphMeta(rootOAMODATA.getOamoGraphMeta());
		
		qres = sparqlCl.sparqlToQResult(Queries.getOSMOListOfOAMO(oamoID));
		ArrayList<Queries.RootOsmoData> OSMODataList = Queries.parseOSMOListOfOAMO(qres);
		
		
		for (Queries.RootOsmoData osmodata : OSMODataList)
		{
			
			GetServiceImpl service = new GetServiceImpl(osmodata.getId());
			OSMO osmo = service.getService();			

			oamo.getOSMO().add(osmo);
		}		
	}
}//class
