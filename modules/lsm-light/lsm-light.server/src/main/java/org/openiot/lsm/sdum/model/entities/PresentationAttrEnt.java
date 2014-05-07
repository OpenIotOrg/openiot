package org.openiot.lsm.sdum.model.entities;

import java.util.ArrayList;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import org.openiot.lsm.schema.LSMSchema;
import org.openiot.lsm.sdum.model.beans.PresentationAttrBean;
import org.openiot.lsm.sdum.model.beans.ReqPresentationBean;
import org.openiot.lsm.sdum.model.beans.WidgetBean;
import org.openiot.lsm.sdum.model.entities.WidgetEnt;

public class PresentationAttrEnt 
{
	public static class Queries
	{
		public static ArrayList<PresentationAttrBean> parsePresAttrsOfWidget(TupleQueryResult qres)
		{
			ArrayList<PresentationAttrBean> presentationAttrList = new ArrayList<PresentationAttrBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					PresentationAttrBean presentationAttrBean = new PresentationAttrBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("presAttrID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							presentationAttrBean.setId(str);
							System.out.println("presAttrID: "+presentationAttrBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("attrValue"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							presentationAttrBean.setValue(str);
							System.out.println("attrValue: "+presentationAttrBean.getValue()+" ");							
						}
						else if(((String) n).equalsIgnoreCase("attrName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							presentationAttrBean.setName(str);
							System.out.println("attrName: "+presentationAttrBean.getName()+" ");							
						}
					}//for
					presentationAttrList.add(presentationAttrBean);
				}//while
				return presentationAttrList;
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
		
		public static String selectPresentationAttrByWidget(String graph,WidgetBean widgetBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
			queryStr.append("SELECT ?presAttrID  ?attrValue ?attrName ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?presAttrID rdf:type <http://openiot.eu/ontology/ns/PresentationAttr> . ");
			queryStr.append("?presAttrID <http://openiot.eu/ontology/ns/presentationAttrOfWidget> <"+widgetBean.getId()+"> . "); 
			queryStr.append("?presAttrID <http://openiot.eu/ontology/ns/presentationAttrValue> ?attrValue . "); 
			queryStr.append("?presAttrID <http://openiot.eu/ontology/ns/presentationAttrName> ?attrName . ");
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual presentationAttrClassIdv;
	private OntClass ontClsPresentationAttr;
	private OntProperty ontPpresentationAttrValue;
	private OntProperty ontPpresentationAttrName;
	private OntProperty ontPpresentationAttrOfWidget;// not in spec
	
	private PresentationAttrBean presentationAttrBean;
	private WidgetEnt widgetEnt;// not inspec
	
	//constructor
	public PresentationAttrEnt(PresentationAttrBean presentationAttrBean, WidgetEnt widgetEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_PresAttr();
		
		setPresentationAttrBean(presentationAttrBean);
		setWidgetEnt(widgetEnt);
	}
	public PresentationAttrEnt(String filePath, PresentationAttrBean presentationAttrBean, WidgetEnt widgetEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_PresAttr();
		
		setPresentationAttrBean(presentationAttrBean);
		setWidgetEnt(widgetEnt);
	}
	
	private void initOnt_PresAttr()
	{
		ontClsPresentationAttr = ontTemplate.createClass("http://openiot.eu/ontology/ns/PresentationAttr");
		ontPpresentationAttrValue = ontTemplate.createProperty("http://openiot.eu/ontology/ns/presentationAttrValue");
		ontPpresentationAttrName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/presentationAttrName");
		ontPpresentationAttrOfWidget = ontTemplate.createProperty("http://openiot.eu/ontology/ns/presentationAttrOfWidget");
	}
	
	////
	public Individual getClassIndividual()
	{
		return presentationAttrClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(presentationAttrBean.getId()==null)
			presentationAttrClassIdv = ontInstance.createIndividual(ontClsPresentationAttr);
		else
			presentationAttrClassIdv = ontInstance.createIndividual(presentationAttrBean.getId(),ontClsPresentationAttr);
	}
	public void createPpresentationAttrName()
	{
		if(presentationAttrBean.getValue()!=null)	
			presentationAttrClassIdv.setPropertyValue(ontPpresentationAttrValue, ontInstance.getBase().createTypedLiteral(presentationAttrBean.getValue()));
	}
	public void createPpresentationAttrValue()
	{
		if(presentationAttrBean.getName()!=null)	
			presentationAttrClassIdv.setPropertyValue(ontPpresentationAttrName, ontInstance.getBase().createTypedLiteral(presentationAttrBean.getName()));
	}
	public void createPpresentationAttrOfWidget()
	{
		if(widgetEnt!=null)	
			presentationAttrClassIdv.addProperty(ontPpresentationAttrOfWidget, widgetEnt.getClassIndividual());
	}
	
	
	public void createAll()
	{
		createClassIdv();
		createPpresentationAttrName();
		createPpresentationAttrValue();
		createPpresentationAttrOfWidget();
	}
	
	public PresentationAttrBean getPresentationAttrBean() {
		return presentationAttrBean;
	}
	public void setPresentationAttrBean(PresentationAttrBean presentationAttrBean) {
		this.presentationAttrBean = presentationAttrBean;
	}
	public WidgetEnt getWidgetEnt() {
		return widgetEnt;
	}
	public void setWidgetEnt(WidgetEnt widgetEnt) {
		this.widgetEnt = widgetEnt;
	}
}
