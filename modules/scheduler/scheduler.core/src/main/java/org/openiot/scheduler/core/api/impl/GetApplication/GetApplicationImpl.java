package org.openiot.scheduler.core.api.impl.GetApplication;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSMO;
import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.osdspec.model.RequestPresentation;
import org.openiot.commons.osdspec.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryRequest;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetApplicationImpl 
{
	private static class Queries
	{		
		public static class RootOAMOData
		{
			private String oamoName; 
			private String userID;
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
			
//			public String getServiceID() {
//				return serviceID;
//			}
//			public void setServiceID(String serviceID) {
//				this.serviceID = serviceID;
//			}
		}
		public static class OSMOData
		{
			private String id; 
			private String name;
			private String desc;
			private String qString;
			
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
			
			public String getqString() {
				return qString;
			}
			public void setqString(String qString) {
				this.qString = qString;
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
		
		private static String openiotFunctionalGraph = "http://lsm.deri.ie/OpenIoT/testSchema#";
		
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
		public static ArrayList<OSMOData> parseServiceStatusOfOSMO(TupleQueryResult qres)
		{
			ArrayList<OSMOData> osmoDataList = new ArrayList<OSMOData>();
			
			try
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();					
					
					OSMOData osmoData = new OSMOData();
					
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
						else if(((String) n).equalsIgnoreCase("srvcQstring"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmoData.setqString(str);
							System.out.print("srvcQstring : "+osmoData.getqString()+" ");
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
		public static ArrayList<ServiceStatusData> parseService(TupleQueryResult qres)
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
			
			String str=("SELECT ?oamoName ?userID "//?serviceID "
								+"from <"+openiotFunctionalGraph+"> "
								+"WHERE "
								+"{"
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
			
			String str=("SELECT ?serviceID ?srvcName ?srvcDesc ?srvcQstring " 
					+"from <"+openiotFunctionalGraph+"> "
					+"WHERE "
					+"{"
					+"?serviceID <http://openiot.eu/ontology/ns/serviceName> ?srvcName . "
					+"?serviceID <http://openiot.eu/ontology/ns/serviceDescription> ?srvcDesc . "
					+"?serviceID <http://openiot.eu/ontology/ns/queryString> ?srvcQstring . "
					+"?serviceID <http://openiot.eu/ontology/ns/oamo> <"+oamoID+"> . "								
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
		this.oamoID = oamoID;		
		logger.debug("Recieved Parameters: " +	"oamoID=" + oamoID );
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
		
		qres = sparqlCl.sparqlToQResult(Queries.getOSMOListOfOAMO(oamoID));
		ArrayList<Queries.OSMOData> OSMODataList = Queries.parseServiceStatusOfOSMO(qres);
		
//		for (Queries.OSMOData osmodata : OSMODataList)
//		{
////			qres = sparqlCl.sparqlToQResult(Queries.getServiceStatusOfOSMO(osmodata.getId()));
////			ArrayList<Queries.ServiceStatusData> serviceStatusDataList = Queries.parseService(qres);
//		}
		
		for (Queries.OSMOData osmodata : OSMODataList)
		{
			OSMO osmo = new OSMO();
			osmo.setId(osmodata.getId());
			osmo.setName(osmodata.getName());
			osmo.setDescription(osmodata.getDesc());
			
			QueryRequest qr = new QueryRequest();
			qr.setQuery(osmodata.getqString());
			osmo.setQueryRequest(qr);
			
			qres = sparqlCl.sparqlToQResult(Queries.getWidgetPreListByService(osmodata.getId()));
			ArrayList<Queries.WidgetPresentationData> widgetPresentationDataList =  Queries.parseWidgetPreListByService(qres);
			
			for (Queries.WidgetPresentationData widgetPresentationData : widgetPresentationDataList)
			{
				RequestPresentation reqp = new RequestPresentation();
				
				Widget w = new Widget();
				w.setWidgetID(widgetPresentationData.getId());
				
				qres = sparqlCl.sparqlToQResult(Queries.getWidgetAttrByWidgetPre(widgetPresentationData.getId()));
				ArrayList<Queries.WidgetAttr> widgetAttr =  Queries.parseWidgetAttributes(qres);
				
				for (Queries.WidgetAttr wattr : widgetAttr)
				{
					PresentationAttr pattr = new PresentationAttr();
					pattr.setName(wattr.getName());
					pattr.setValue(wattr.getValue());
					
					w.getPresentationAttr().add(pattr);
				}
							
				reqp.getWidget().add(w);
				osmo.setRequestPresentation(reqp);
			}
			oamo.getOSMO().add(osmo);
		}		
	}
}//class
