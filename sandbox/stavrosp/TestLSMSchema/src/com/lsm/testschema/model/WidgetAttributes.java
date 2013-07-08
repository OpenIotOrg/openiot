package com.lsm.testschema.model;

import java.util.ArrayList;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
//import com.lsm.testschema.model.Widget.Type;

public class WidgetAttributes 
{
	public static class Queries
	{
		
		
		public static ArrayList<WidgetAttributes> parseService(TupleQueryResult qres)
		{
			ArrayList<WidgetAttributes> widgetAttrList = new ArrayList<WidgetAttributes>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					WidgetAttributes widgetAttr = new WidgetAttributes();
					
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
							widgetAttr.setDescription(str);
							System.out.print("widgetAttrDesc: "+widgetAttr.getDescription()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetAttrOf"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							WidgetPresentation wPre = new WidgetPresentation();
							wPre.setId(str);
							widgetAttr.setWidgetPre(wPre);
							System.out.print("widgetAttrDesc: "+wPre.getId()+" ");	
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

		public static String selectWidgetAttrAll()
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?widgetAttrID ?widgetAttrName ?widgetAttrDesc ?widgetAttrOf from <"+graph+"> "
								+"WHERE "
								+"{"
								+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrOf> ?widgetAttrOf . "
								+"?widgetAttrID <http://openiot.eu/ontology/ns/widgeAttrDescription> ?widgetAttrDesc . "								
								+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrName> ?widgetAttrName . "
								+"?widgetAttrID rdf:type <http://openiot.eu/ontology/ns/WidgetAttr> ."
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectWidgetAttrByName(String widgetAttrName)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
						
			String str=("SELECT ?widgetAttrID ?widgetAttrName ?widgetAttrDesc ?widgetAttrOf from <"+graph+"> "
					+"WHERE "
					+"{"
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrOf> ?widgetAttrOf . "
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgeAttrDescription> ?widgetAttrDesc . "								
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrName> ?name FILTER regex(?name, \"" +widgetAttrName+ "\" )  . "
					+"?widgetAttrID rdf:type <http://openiot.eu/ontology/ns/WidgetAttr> ."
					+"}");
			
			update.append(str);
			return update.toString();
		}
		public static String selectWidgetAttrByDescription(String desc)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
									
			String str=("SELECT ?widgetAttrID ?widgetAttrName ?widgetAttrDesc ?widgetAttrOf from <"+graph+"> "
					+"WHERE "
					+"{"
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrOf> ?widgetAttrOf . "
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrName> ?widgetAttrName . "
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgeAttrDescription> ?desc FILTER regex(?desc, \"" +desc+ "\" )  . "
					+"?widgetAttrID rdf:type <http://openiot.eu/ontology/ns/WidgetAttr> ."
					+"}");
			
			update.append(str);
			return update.toString();
		}		
		public static String selectWidgetAttrByWidgetPre(WidgetPresentation widgetPre)//query returns list of widgets
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
				        	        
	        String str=("SELECT ?widgetAttrID ?widgetAttrName ?widgetAttrDesc ?widgetAttrOf from <"+graph+"> "
					+"WHERE "
					+"{"
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrOf> ?widgetAttrOf . "
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgeAttrDescription> ?widgetAttrDesc . "								
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrName> ?widgetAttrName . "
					+"?widgetAttrID <http://openiot.eu/ontology/ns/widgetAttrOf> <"+widgetPre.getId()+"> . "					
					+"}");
	        
	        update.append(str);
			return update.toString();
		}
	}//class
	
	private LSMSchema  myOnt;	
	private LSMSchema  ontInstance;
	private String graph;
	private LSMTripleStore lsmStore;
	
	private Individual widgetAttrClassIdv;
	
	private OntClass ontClsWidgetAttrClass;
	private OntProperty ontPwidgetAttrName;
	private OntProperty ontPwidgetAttrDescription;	
	private OntProperty ontPwidgetAttrOf;
	
	private String id;
	private String description;
	private String name;
	private WidgetPresentation widgetPres;
	
	//constructor
	public WidgetAttributes()
	{}
	public WidgetAttributes(LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		initOnt_WidgetAttr();
		//createClassIdv();
	}
	public WidgetAttributes(String classIdvURL,LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		id=classIdvURL;
		
		initOnt_WidgetAttr();
//		createClassIdv();
	}
	
	
	private void initOnt_WidgetAttr()
	{
		ontClsWidgetAttrClass = myOnt.createClass("http://openiot.eu/ontology/ns/WidgetAttr");				
		ontPwidgetAttrDescription = myOnt.createProperty("http://openiot.eu/ontology/ns/widgeAttrDescription");
		ontPwidgetAttrName = myOnt.createProperty("http://openiot.eu/ontology/ns/widgetAttrName");
		ontPwidgetAttrOf= myOnt.createProperty("http://openiot.eu/ontology/ns/widgetAttrOf");
	}
	
	public void createClassIdv()
	{
		if(id==null)
			widgetAttrClassIdv = ontInstance.createIndividual(ontClsWidgetAttrClass);
		else
			widgetAttrClassIdv = ontInstance.createIndividual(id,ontClsWidgetAttrClass);
	}
	
//	public void createOnt_Widget()
//	{		
//		if(description!=null)
//			widgetAttrClassIdv.setPropertyValue(ontPwidgetAttrDescription, ontInstance.getBase().createTypedLiteral(description));
//		if(name!=null)
//			widgetAttrClassIdv.setPropertyValue(ontPwidgetAttrName, ontInstance.getBase().createTypedLiteral(name));		
//
//		widgetAttrClassIdv.addProperty(ontPwidgetAttrOf, widgetPres.getClassIndividual());
//	}
	
	public void createPdesc() throws NullPointerException
	{
		if(description!=null)
			widgetAttrClassIdv.setPropertyValue(ontPwidgetAttrDescription, ontInstance.getBase().createTypedLiteral(description));
		else
			throw new NullPointerException("mandatory attribute:description is null");
	}
	public void createPname() throws NullPointerException
	{
		if(name!=null)
			widgetAttrClassIdv.setPropertyValue(ontPwidgetAttrName, ontInstance.getBase().createTypedLiteral(name));
		else
			throw new NullPointerException("mandatory attribute:name is null");
	}
	public void createPWidgetAttrOf() throws NullPointerException
	{
		if(widgetPres!=null)
			widgetAttrClassIdv.addProperty(ontPwidgetAttrOf, widgetPres.getClassIndividual());
		else
			throw new NullPointerException("Cannot create Widget Presentation attribute: null reference");
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
		return widgetAttrClassIdv;
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
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	public WidgetPresentation getWidgetPres() {
		return widgetPres;
	}
	public void setWidgetPre(WidgetPresentation widgetPres) {
		this.widgetPres = widgetPres;
	}
}//class
