package org.openiot.lsm.functionalont.model.entities;

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

import org.openiot.lsm.functionalont.model.beans.OSMOBean;
import org.openiot.lsm.functionalont.model.beans.PresentationAttrBean;
import org.openiot.lsm.functionalont.model.beans.ReqPresentationBean;
import org.openiot.lsm.functionalont.model.beans.WidgetBean;
import org.openiot.lsm.functionalont.model.entities.PresentationAttrEnt;
import org.openiot.lsm.functionalont.model.entities.ReqPresentationEnt;
import org.openiot.lsm.schema.LSMSchema;

public class WidgetEnt 
{
	public static class Queries
	{
		public static ArrayList<WidgetBean> parseReqPresWidget(TupleQueryResult qres)
		{
			ArrayList<WidgetBean> widgetBeanList = new ArrayList<WidgetBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					boolean found=false;
					WidgetBean widgetBean = new WidgetBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("widgetID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							widgetBean.setId(str);
							System.out.println("widgetID: "+widgetBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("widgetPresAttr"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(WidgetBean object : widgetBeanList)
							{
								if(object.getId().equals(widgetBean.getId()))	{
									 //grab already added WidgetBean and add  presentationAttr
									object.getPresentationAttrBeanList().add(new PresentationAttrBean(str));
								    found = true;
							    }
							    else {
							    	 //this WidgetBean exist in the list 
							    	widgetBean.getPresentationAttrBeanList().add(new PresentationAttrBean(str));
							        found = false;
							    }
							}
						}
					}//for
					
					if(!found)
						widgetBeanList.add(widgetBean);
					
				}//while
				return widgetBeanList;
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
		
		public static String selectWidgetByReqPresentation(String graph,ReqPresentationBean reqPresBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
			queryStr.append("SELECT ?widgetID  ?widgetPresAttr ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?widgetID rdf:type <http://openiot.eu/ontology/ns/WIDGET> . ");
			queryStr.append("?widgetID <http://openiot.eu/ontology/ns/widgetOfRequestPresentation> <"+reqPresBean.getId()+"> . "); 
			queryStr.append("?widgetID <http://openiot.eu/ontology/ns/widgetHasPresAttr> ?widgetPresAttr . "); 
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}
	
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;

	private Individual widgetClassIdv;
	private OntClass ontClsWidget;
	private OntProperty ontPwidgetHasPresentationAttr;
	private OntProperty ontPwidgetOfRequestPresentation;
	
	private WidgetBean widgetBean;	
	private ArrayList<PresentationAttrEnt> presentationAttrEntList = new ArrayList<PresentationAttrEnt>();
	private ReqPresentationEnt reqPresentationEnt;
	

	
	public WidgetEnt(WidgetBean widgetBean,ReqPresentationEnt reqPresentationEnt )
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_Widget();
		
		setWidgetBean(widgetBean);
		setReqPresentationEnt(reqPresentationEnt);
	}
	public WidgetEnt(String filePath , WidgetBean widgetBean,ReqPresentationEnt reqPresentationEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_Widget();
		
		setWidgetBean(widgetBean);
		setReqPresentationEnt(reqPresentationEnt);
	}
	
	private void initOnt_Widget()
	{
		ontClsWidget = ontTemplate.createClass("http://openiot.eu/ontology/ns/WIDGET");
		ontPwidgetHasPresentationAttr = ontTemplate.createProperty("http://openiot.eu/ontology/ns/widgetHasPresAttr");
		ontPwidgetOfRequestPresentation = ontTemplate.createProperty("http://openiot.eu/ontology/ns/widgetOfRequestPresentation");
	}
	
	////
	public Individual getClassIndividual()
	{
		return widgetClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(widgetBean.getId()==null)
			widgetClassIdv = ontInstance.createIndividual(ontClsWidget);
		else
			widgetClassIdv = ontInstance.createIndividual(widgetBean.getId(),ontClsWidget);
	}
	public void createPwidgetHasPresAttr()
	{
		for(int i=0; i<presentationAttrEntList.size(); i++)
		{
			widgetClassIdv.addProperty(ontPwidgetHasPresentationAttr, presentationAttrEntList.get(i).getClassIndividual());
		}
	}
	public void createPwidgetHasPresAttrAsString()
	{
		for(int i=0; i<presentationAttrEntList.size(); i++)
		{
			widgetClassIdv.addProperty(ontPwidgetHasPresentationAttr, presentationAttrEntList.get(i).getPresentationAttrBean().toStringValName());
		}
	}
	public void createPpresentationAttrName()
	{
		if(reqPresentationEnt!=null)	
			widgetClassIdv.addProperty(ontPwidgetOfRequestPresentation, reqPresentationEnt.getClassIndividual());
	}
	
	
	public WidgetBean getWidgetBean() {
		return widgetBean;
	}
	public void setWidgetBean(WidgetBean widgetBean) {
		this.widgetBean = widgetBean;
	}
	
	public ArrayList<PresentationAttrEnt> getPresentationAttrEntList() {
		return presentationAttrEntList;
	}
	public void setPresentationAttrEntList(
			ArrayList<PresentationAttrEnt> presentationAttrEntList) {
		this.presentationAttrEntList = presentationAttrEntList;
	}
	public ReqPresentationEnt getReqPresentationEnt() {
		return reqPresentationEnt;
	}
	public void setReqPresentationEnt(ReqPresentationEnt reqPresentationEnt) {
		this.reqPresentationEnt = reqPresentationEnt;
	}
}
