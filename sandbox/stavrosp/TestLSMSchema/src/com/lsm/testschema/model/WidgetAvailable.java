package com.lsm.testschema.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.lsm.testschema.model.ServiceStatus.State;

import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

public class WidgetAvailable 
{
	
	public static class Queries
	{
		public static ArrayList<WidgetAvailable> parseService(TupleQueryResult qres)
		{
			ArrayList<WidgetAvailable> widgetList = new ArrayList<WidgetAvailable>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					WidgetAvailable widget = new WidgetAvailable();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("widgetID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widget.setId(str);
							System.out.print("widget id: "+widget.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widget.setDescription(str);
							System.out.print("widgetDesc: "+widget.getDescription()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widget.setName(str);
							System.out.print("widgetName: "+widget.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetLocation"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widget.setLocationURL(str);
							System.out.print("widgetLocation: "+widget.getLocationURL()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetType"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widget.setType(str);
							System.out.print("widgetType: "+widget.getType()+" ");	
						}
					}
					widgetList.add(widget);					
				}//while
				return widgetList;
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
		
		private static String graph = "http://lsm.deri.ie/OpenIoT/testSchema#";
		
		private static String getNamespaceDeclarations() 
		{
	        StringBuilder declarations = new StringBuilder();
	        declarations.append("PREFIX : <" + "http://openiot.eu/ontology/ns/" + "> \n");
	        //declarations.append("PREFIX spt: <" + "http://spitfire-project.eu/ontology/ns/" + "> \n");
	        declarations.append("PREFIX rdf: <" + RDF.getURI() + "> \n");//http://www.w3.org/1999/02/22-rdf-syntax-ns#
	        declarations.append("PREFIX rdfs: <" + RDFS.getURI() + "> \n");//http://www.w3.org/2000/01/rdf-schema#
	        declarations.append("PREFIX xsd: <" + XSD.getURI() + "> \n");
	        //declarations.append("PREFIX owl: <" + OWL.getURI() + "> \n");
	        //declarations.append("PREFIX ssn: <" + "http://purl.oclc.org/NET/ssnx/ssn#" + "> \n");
	        //declarations.append("PREFIX dul: <" + "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#" + "> \n");
	        //declarations.append("PREFIX oiot: <" + "http://openiot.eu/ontology/ns/" + "> \n");
	        //declarations.append("base oiot: <" + "http://openiot.eu/ontology/ns/clouddb" + "> \n");	       
	        declarations.append("\n");
	        
	        return declarations.toString();
	    }

		public static String selectWidgetAvailAll()
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?widgetID ?widgetDesc ?widgetName ?widgetLocation ?widgetType from <"+graph+"> "
								+"WHERE "
								+"{"
								+"?widgetID <http://openiot.eu/ontology/ns/widgetType> ?widgetType . "
								+"?widgetID <http://openiot.eu/ontology/ns/widgetDescription> ?widgetDesc . "
								+"?widgetID <http://openiot.eu/ontology/ns/widgetName> ?widgetName . "
								+"?widgetID <http://openiot.eu/ontology/ns/widgetLocation> ?widgetLocation . "
								+"?widgetID rdf:type <http://openiot.eu/ontology/ns/Widget> . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectWidgetByName(String widgetName)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?widgetID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?widgetID <http://openiot.eu/ontology/ns/widgetName> ?name FILTER regex(?name, \"" +widgetName+ "\" )  . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectWidgetByDescription(String desc)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?widgetID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?widgetID <http://openiot.eu/ontology/ns/widgetDescription> ?desc FILTER regex(?desc, \"" +desc+ "\" )  . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectWidgetByLocation(String url)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?widgetID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?widgetID <http://openiot.eu/ontology/ns/widgetLocation> ?loc FILTER regex(?loc, \"" +url+ "\" )  . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
//		public static String selectWidgetBySrvc(Service service)//query returns list of widgets
//		{
//			StringBuilder update = new StringBuilder();
//	        update.append(getNamespaceDeclarations());
//			
//	        String str=("SELECT ?widgetID from <"+graph+"> "
//						+"WHERE "
//						+"{"								
//						+"?widgetID <http://openiot.eu/ontology/ns/widgetOf> <"+service.getClassIndividual()+"> . "								
//						+"}");	
//	        
//	        update.append(str);
//			return update.toString();
//		}
	}//class
	
	private LSMSchema  myOnt;	
	private LSMSchema  ontInstance;
	private String graph;
	private LSMTripleStore lsmStore;
	
	private Individual widgetClassIdv;
	
	private OntClass ontClsWidgetClass;	
	private OntProperty ontPwidgetDescription;
	private OntProperty ontPwidgetName;
	private OntProperty ontPwidgetLocation;
	private OntProperty ontPwidgeType;
	private OntProperty ontPwidgetOf;
	////
	////
	private String id;
	private String description;
	private String name;
	private String locationURL;
	private String type;
//	private Type type;
	private WidgetPresentation widgetPre;
	
	
	public WidgetAvailable()
	{		
	}
	public WidgetAvailable(LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)//,String type)//Type type)
	{		
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		this.type=type;
		
		initOnt_Widget();
		//createClassIdv();
	}
	public WidgetAvailable(String classIdvURL,LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)//,String type)//Type type)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		this.type=type;
		
		id=classIdvURL;
		
		initOnt_Widget();
		//createClassIdv();
	}
	
//	public enum Type 
//	{
//		GAUGE("http://openiot.eu/ontology/ns/Gauge"),
//		CHART("http://openiot.eu/ontology/ns/Chart"),
//		BAR_CHART("http://openiot.eu/ontology/ns/Barchart"),
//		PROGRESS_BAR("http://openiot.eu/ontology/ns/Progressbar");
//		
//		private String code;
//
//		//constructor
//		private Type(String c){
//			code = c;
//		}
//
//		public String getCode() {
//			return code;
//		}
//		
//		public static Type toEnum(String str)
//		{
//			if(str.equals(Type.GAUGE.getCode()))
//				return Type.GAUGE;
//			else if(str.equals(Type.CHART.getCode()))
//				return Type.CHART;
//			else if(str.equals(Type.BAR_CHART.getCode()))
//				return Type.BAR_CHART;			
//			else if(str.equals(Type.PROGRESS_BAR.getCode()))
//				return Type.PROGRESS_BAR;			
//			else
//				return null;
//		}
//	}//enum
	
	private void initOnt_Widget()
	{
		ontClsWidgetClass = myOnt.createClass("http://openiot.eu/ontology/ns/Widget");
		ontPwidgeType = myOnt.createProperty("http://openiot.eu/ontology/ns/widgetType");//RDF.type.getURI());		
		ontPwidgetDescription = myOnt.createProperty("http://openiot.eu/ontology/ns/widgetDescription");
		ontPwidgetName = myOnt.createProperty("http://openiot.eu/ontology/ns/widgetName");
		ontPwidgetLocation = myOnt.createProperty("http://openiot.eu/ontology/ns/widgetLocation");
		ontPwidgetOf = myOnt.createProperty("http://openiot.eu/ontology/ns/widgetOf");
	}
	
	public void createClassIdv()
	{
		if(id==null)
			widgetClassIdv = ontInstance.createIndividual(ontClsWidgetClass);
		else
			widgetClassIdv = ontInstance.createIndividual(id,ontClsWidgetClass);		
	}
	public void createPtype()
	{
		if(type!=null)
			widgetClassIdv.setPropertyValue(ontPwidgeType,ontInstance.getBase().createTypedLiteral(type));
	}
	public void createPdescription()
	{
		if(description!=null)
			widgetClassIdv.setPropertyValue(ontPwidgetDescription, ontInstance.getBase().createTypedLiteral(description));
	}
	public void createPname()
	{
		if(name!=null)
			widgetClassIdv.setPropertyValue(ontPwidgetName, ontInstance.getBase().createTypedLiteral(name));
	}
	public void createPlocationURL()
	{
		if(locationURL!=null)
			widgetClassIdv.setPropertyValue(ontPwidgetLocation, ontInstance.getBase().createTypedLiteral(locationURL));
	}
	public void createPWidgetOf()
	{
		if(widgetPre!=null)
			widgetClassIdv.addProperty(ontPwidgetOf, widgetPre.getClassIndividual());
	}

		
	////
	
	public LSMSchema getOnt()
	{
		return myOnt;
	}
	
	public LSMSchema getOntInstance()
	{
		return ontInstance;
	}
	
	public Individual getClassIndividual()
	{
		return widgetClassIdv;
	}
		
	////
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id=id;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocationURL() {
		return locationURL;
	}
	public void setLocationURL(String locationURL) {
		this.locationURL = locationURL;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public WidgetPresentation getWidgetPre() {
		return widgetPre;
	}
	public void setWidgetPre(WidgetPresentation widgetPre) {
		this.widgetPre = widgetPre;
	}
}//class
