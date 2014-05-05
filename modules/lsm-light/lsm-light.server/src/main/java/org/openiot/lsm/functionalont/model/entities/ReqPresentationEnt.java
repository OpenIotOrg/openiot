package org.openiot.lsm.functionalont.model.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import org.openiot.lsm.functionalont.model.beans.NamedGraphBean;
import org.openiot.lsm.functionalont.model.beans.OSMOBean;
import org.openiot.lsm.functionalont.model.beans.QueryControlsBean;
import org.openiot.lsm.functionalont.model.beans.QueryRequestBean;
import org.openiot.lsm.functionalont.model.beans.QueryScheduleBean;
import org.openiot.lsm.functionalont.model.beans.ReqPresentationBean;
import org.openiot.lsm.functionalont.model.beans.WidgetBean;
import org.openiot.lsm.functionalont.model.entities.OSMOEnt;
import org.openiot.lsm.functionalont.model.entities.WidgetEnt;
import org.openiot.lsm.schema.LSMSchema;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class ReqPresentationEnt 
{
	public static class Queries
	{
		public static ArrayList<ReqPresentationBean> parseReqPresentation(TupleQueryResult qres)
		{
			ArrayList<ReqPresentationBean> reqPresentationBeanList = new ArrayList<ReqPresentationBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					boolean found=false;
					ReqPresentationBean reqPresentationBean = new ReqPresentationBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("reqPresentationID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							reqPresentationBean.setId(str);
							System.out.println("reqPresentationID: "+reqPresentationBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("reqPresentationWidget"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(ReqPresentationBean object : reqPresentationBeanList)
							{
								if(object.getId().equals(reqPresentationBean.getId()))	{
									 //grab already added ReqPresentationBean and add  WidgetBean
									object.getWidgetBeanLsit().add(new WidgetBean(str));
								    found = true;
							    }
							    else {
							    	 //this ReqPresentationBean exist in the list 
							    	reqPresentationBean.getWidgetBeanLsit().add(new WidgetBean(str));
							        found = false;
							    }
							}
						}
					}//for
					
					if(!found)
						reqPresentationBeanList.add(reqPresentationBean);
					
				}//while
				return reqPresentationBeanList;
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
		
		public static String selectReqPresentationByOSMO(String graph,OSMOBean osmoBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
			queryStr.append("SELECT ?reqPresentationID  ?reqPresentationWidget ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?reqPresentationID rdf:type <http://openiot.eu/ontology/ns/ReqPresentation> . ");
			queryStr.append("?reqPresentationID <http://openiot.eu/ontology/ns/reqpresentationOfOSMO> <"+osmoBean.getId()+"> . "); 
			queryStr.append("?reqPresentationID <http://openiot.eu/ontology/ns/reqpresentationHasWidget> ?reqPresentationWidget . "); 
			queryStr.append("}");
			
			return queryStr.toString();
		}
		
	}
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual reqPresentationClassIdv;
	private OntClass ontClsReqPresentation;
	private OntProperty ontPreqPresentationHasWidget;
	private OntProperty ontPreqPresentationOfOSMO; //not in spec
	
	private ReqPresentationBean reqPresentationBean;	
	private ArrayList<WidgetEnt> widgetEntList = new ArrayList<WidgetEnt>();
	private OSMOEnt osmoEnt;
	
	
	public ReqPresentationEnt(ReqPresentationBean reqPresentationBean,OSMOEnt osmoEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_ReqPres();
		
		setReqPresentationBean(reqPresentationBean);
		setOsmoEnt(osmoEnt);
	}
	public ReqPresentationEnt(String filePath,ReqPresentationBean reqPresentationBean,OSMOEnt osmoEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_ReqPres();
		
		setReqPresentationBean(reqPresentationBean);
		setOsmoEnt(osmoEnt);
	}
	
	private void initOnt_ReqPres()
	{
		ontClsReqPresentation = ontTemplate.createClass("http://openiot.eu/ontology/ns/ReqPresentation");
		ontPreqPresentationHasWidget = ontTemplate.createProperty("http://openiot.eu/ontology/ns/reqpresentationHasWidget");
		ontPreqPresentationOfOSMO= ontTemplate.createProperty("http://openiot.eu/ontology/ns/reqpresentationOfOSMO");
	}

	
	////
	public Individual getClassIndividual()
	{
		return reqPresentationClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(reqPresentationBean.getId()==null)
			reqPresentationClassIdv = ontInstance.createIndividual(ontClsReqPresentation);
		else
			reqPresentationClassIdv = ontInstance.createIndividual(reqPresentationBean.getId(),ontClsReqPresentation);
	}
	public void createPreqPresentationHasWidget()
	{
		for(int i=0; i<widgetEntList.size(); i++)
		{
			reqPresentationClassIdv.addProperty(ontPreqPresentationHasWidget, widgetEntList.get(i).getClassIndividual());
		}
	}
	public void createPreqPresentationHasWidgetAsString()
	{
		for(int i=0; i<widgetEntList.size(); i++)
		{
			reqPresentationClassIdv.addProperty(ontPreqPresentationHasWidget, widgetEntList.get(i).getWidgetBean().toStringIdPreAttr());
		}
	}
	public void createPreqPresentationOfOSMO()
	{
		if(osmoEnt!=null)	
			reqPresentationClassIdv.addProperty(ontPreqPresentationOfOSMO, osmoEnt.getClassIndividual());
	}
	
	
	public ReqPresentationBean getReqPresentationBean() {
		return reqPresentationBean;
	}
	public void setReqPresentationBean(ReqPresentationBean reqPresentationBean) {
		this.reqPresentationBean = reqPresentationBean;
	}
	
	public ArrayList<WidgetEnt> getWidgetEntList() {
		return widgetEntList;
	}
	public void setWidgetEntList(ArrayList<WidgetEnt> widgetEntList) {
		this.widgetEntList = widgetEntList;
	}
	
	public OSMOEnt getOsmoEnt() {
		return osmoEnt;
	}
	public void setOsmoEnt(OSMOEnt osmoEnt) {
		this.osmoEnt = osmoEnt;
	}
	
	
}
