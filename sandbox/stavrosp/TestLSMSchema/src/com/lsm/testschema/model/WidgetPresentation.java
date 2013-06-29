package com.lsm.testschema.model;

import java.util.ArrayList;
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

import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

public class WidgetPresentation 
{
	public static class Queries
	{
		public static ArrayList<WidgetPresentation> parseService(TupleQueryResult qres)
		{
			ArrayList<WidgetPresentation> widgetPreList = new ArrayList<WidgetPresentation>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					WidgetPresentation widgetPre = new WidgetPresentation();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("widgetPreID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widgetPre.setId(str);
							System.out.print("widget attr id: "+widgetPre.getId()+" ");	
						}
//						else if(((String) n).equalsIgnoreCase("widgetPreDesc"))
//						{
//							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							widgetPre.setDescription(str);
//							System.out.print("widgetPreDesc: "+widgetPre.getDescription()+" ");	
//						}
						else if(((String) n).equalsIgnoreCase("widgetPreOf"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							Service srvc = new Service();
							srvc.setId(str);
							widgetPre.setService(srvc);
							System.out.print("widgetPreOf: "+widgetPre.getService().getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widget"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							WidgetAvailable widget = new WidgetAvailable();
							widget.setId(str);
							
							widgetPre.setWidgetAvailable(widget);
							System.out.print("widget: "+widgetPre.getWidgetAvailable().getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetAttr"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							WidgetAttributes wAttr = new WidgetAttributes();
							wAttr.setId(str);
							widgetPre.addWidgetAttr(wAttr);
							System.out.print("widgetAttr: "+widgetPre.getWidgetAttrList().get(0).getId()+" ");	
						}
					}
					widgetPreList.add(widgetPre);					
				}//while
				return widgetPreList;
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
		
		
		public static String selectWidgetPreAll()
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?widgetPreID  ?widgetPreOf ?widget ?widgetAttr from <"+graph+"> "
								+"WHERE "
								+"{"
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetAttribute> ?widgetAttr . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widget> ?widget . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetPresOf> ?widgetPreOf . "								
								//+"?widgetPreID <http://openiot.eu/ontology/ns/widgetPresDescription> ?widgetPreDesc . "
								+"?widgetPreID rdf:type <http://openiot.eu/ontology/ns/WidgetPresentation> ."
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectWidgetPreByService(Service srvc)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?widgetPreID  ?widgetPreOf ?widget ?widgetAttr from <"+graph+"> "
								+"WHERE "
								+"{"
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetAttribute> ?widgetAttr . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widget> ?widget . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetPresOf> ?widgetPreOf . "								
								//+"?widgetPreID <http://openiot.eu/ontology/ns/widgetPresDescription> ?widgetPreDesc . "
								+"?widgetPreID <http://openiot.eu/ontology/ns/widgetPresOf> <"+srvc.getId()+"> . "
								+"?widgetPreID rdf:type <http://openiot.eu/ontology/ns/WidgetPresentation> ."
								+"}");								
			
			update.append(str);
			return update.toString();
		}
	}//class
	
	
	private LSMSchema  myOnt;	
	private LSMSchema  ontInstance;
	private String graph;
	private LSMTripleStore lsmStore;
	
	private Individual widgetPresClassIdv;
	
	
	private OntClass ontClsWidgetPresClass;	
	private OntProperty ontPwidgetPresDescription;
	private OntProperty ontPwidgetPresOf;
	private OntProperty ontPwidget;	
	private OntProperty ontPwidgetAttr;		
	////
	////
	private String id;
	private String description;
	private Service service;
	//private ArrayList<WidgetAvailable> widgetList = new ArrayList<WidgetAvailable>();
	private WidgetAvailable wAvailable = new WidgetAvailable();
	private ArrayList<WidgetAttributes> widgetAttrList = new ArrayList<WidgetAttributes>();
		
	
	public WidgetPresentation()
	{		
	}
	public WidgetPresentation(LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{		
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;		
		
		initOnt_WidgetPres();		
	}
	public WidgetPresentation(String classIdvURL,LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)//,String type)//Type type)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;		
		
		id=classIdvURL;
		
		initOnt_WidgetPres();		
	}
	
	private void initOnt_WidgetPres()
	{
		ontClsWidgetPresClass = myOnt.createClass("http://openiot.eu/ontology/ns/WidgetPresentation");
		ontPwidgetPresDescription = myOnt.createProperty("http://openiot.eu/ontology/ns/widgetPresDescription");
		ontPwidgetPresOf = myOnt.createProperty("http://openiot.eu/ontology/ns/widgetPresOf");
		ontPwidget= myOnt.createProperty("http://openiot.eu/ontology/ns/widget");		
		ontPwidgetAttr= myOnt.createProperty("http://openiot.eu/ontology/ns/widgetAttribute");
	}

	public void createClassIdv()
	{
		if(id==null)
			widgetPresClassIdv = ontInstance.createIndividual(ontClsWidgetPresClass);
		else
			widgetPresClassIdv = ontInstance.createIndividual(id,ontClsWidgetPresClass);		
	}
	public void createPdescription()
	{
		if(description!=null)
			widgetPresClassIdv.setPropertyValue(ontPwidgetPresDescription, ontInstance.getBase().createTypedLiteral(description));
	}
	public void createPwidgetPresOf()
	{
		if(service!=null)
			widgetPresClassIdv.addProperty(ontPwidgetPresOf, service.getClassIndividual());
	}
	public void createPwidget()
	{
		widgetPresClassIdv.addProperty(ontPwidget, wAvailable.getClassIndividual());
	}
	public void createPwidgetAttr()
	{
		for(int i=0; i<widgetAttrList.size(); i++)
		{
			widgetPresClassIdv.addProperty(ontPwidgetAttr, widgetAttrList.get(i).getClassIndividual());
		}
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
		return widgetPresClassIdv;
	}
		
	////
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	
	public WidgetAvailable getWidgetAvailable() {
		return this.wAvailable;
	}
	public void setWidgetAvailable(WidgetAvailable widgetAvailable) {
		this.wAvailable = widgetAvailable;
	}
	
//	public ArrayList<WidgetAvailable> getWidgetList() {
//		return widgetList;
//	}
//	public void setWidgetList(ArrayList<WidgetAvailable> widgetList) {
//		this.widgetList = widgetList;
//	}
//	public void addWidget(WidgetAvailable widget) {
//		this.widgetList.add(widget);
//	}
	
	public ArrayList<WidgetAttributes> getWidgetAttrList() {
		return widgetAttrList;
	}
	public void setWidgetAttrList(ArrayList<WidgetAttributes> widgetAttrList) {
		this.widgetAttrList = widgetAttrList;
	}
	public void addWidgetAttr(WidgetAttributes widgetAttr) {
		this.widgetAttrList.add(widgetAttr);
	}
	
}//class
