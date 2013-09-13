package org.openiot.scheduler.core.api.impl.GetService;

import java.util.ArrayList;
import java.util.Set;

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

public class GetServiceImpl 
{
	private static class Queries
	{		
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
		
		public static OSMO parseOSMORootData(TupleQueryResult qres)
		{
			OSMO osmo = new OSMO();
			
			try
			{
//				while (qres.hasNext())
//				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();					
					
					
					for (Object n : names)
					{						
//						if(((String) n).equalsIgnoreCase("serviceID"))
//						{
//							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							osmo.setId(str);
//							System.out.print("serviceID: "+osmo.getId()+" ");	
//						}
						if(((String) n).equalsIgnoreCase("srvcName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmo.setName(str);
							System.out.print("srvcName : "+osmo.getName()+" ");
						}
						else if(((String) n).equalsIgnoreCase("srvcDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmo.setDescription(str);
							System.out.print("srvcDesc : "+osmo.getDescription()+" ");
						}
						else if(((String) n).equalsIgnoreCase("srvcQstring"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							QueryRequest qr = new QueryRequest();
							qr.setQuery(str);							
							osmo.getQueryRequest().add(qr);
							System.out.print("srvcQstring : "+osmo.getQueryRequest().get(0).getQuery()+" ");
						}
					}						
//				}//while
				return osmo;
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
		
		public static String getOSMORootData(String osmoID)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("SELECT ?srvcName ?srvcDesc ?srvcQstring " 
					+"from <"+openiotFunctionalGraph+"> "
					+"WHERE "
					+"{"
					+"<"+osmoID+"> <http://openiot.eu/ontology/ns/serviceName> ?srvcName . "
					+"<"+osmoID+"> <http://openiot.eu/ontology/ns/serviceDescription> ?srvcDesc . "
					+"<"+osmoID+"> <http://openiot.eu/ontology/ns/queryString> ?srvcQstring . "
					+"}");
			
			update.append(str);
			return update.toString();
		}
		public static String getWidgetPreListByOSMO(String osmoID)
		{
			StringBuilder update = new StringBuilder();
			
			String str=("SELECT ?widgetPreID ?widgetID ?widgetAttrID "
								+"from <"+openiotFunctionalGraph+"> "
								+"WHERE "
								+"{"
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetAttribute> ?widgetAttrID . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widget> ?widgetID . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetPresOf> <"+osmoID+"> . "
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
	
	////
	final static Logger logger = LoggerFactory.getLogger(GetServiceImpl.class);
	
	private String osmoID;
	private OSMO osmo;
	
	public GetServiceImpl (String osmoID)
	{
		this.osmoID=osmoID;
		logger.debug("Received Parameters: " +	"osmoID=" + osmoID );
		findOSMO();
	}
	
	public OSMO getService()
	{
		return osmo;		
	}
	
	
	private void findOSMO()
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. ",e);
			return;
		}
		
		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getOSMORootData(osmoID));
		osmo =  Queries.parseOSMORootData(qres);
		osmo.setId(osmoID);
				
		qres = sparqlCl.sparqlToQResult(Queries.getWidgetPreListByOSMO(osmoID));
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
	}	
}
