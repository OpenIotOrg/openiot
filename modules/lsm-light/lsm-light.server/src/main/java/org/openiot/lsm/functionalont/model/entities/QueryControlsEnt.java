package org.openiot.lsm.functionalont.model.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import org.openiot.lsm.functionalont.model.entities.OSMOEnt;
import org.openiot.lsm.functionalont.model.entities.QueryScheduleEnt;
import org.openiot.lsm.functionalont.model.beans.DefaultGraphBean;
import org.openiot.lsm.functionalont.model.beans.NamedGraphBean;
import org.openiot.lsm.functionalont.model.beans.OSMOBean;
import org.openiot.lsm.functionalont.model.beans.QueryControlsBean;
import org.openiot.lsm.functionalont.model.beans.QueryRequestBean;
import org.openiot.lsm.functionalont.model.beans.QueryScheduleBean;
import org.openiot.lsm.schema.LSMSchema;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class QueryControlsEnt 
{
	public static class Queries
	{
		public static ArrayList<QueryControlsBean> parseQueryReqBean(TupleQueryResult qres)
		{
			ArrayList<QueryControlsBean> queryControlsBeanList = new ArrayList<QueryControlsBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					QueryControlsBean queryControlsBean = new QueryControlsBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("queryContrlsID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryControlsBean.setId(str);
							System.out.println("queryControlsBean: "+queryControlsBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryContrlsTrigger"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryControlsBean.setTrigger(str);
							System.out.println("queryContrlsTrigger : "+queryControlsBean.getTrigger()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryContrlsIfEmpty"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryControlsBean.setReportIfEmpty(Boolean.parseBoolean(str));
							System.out.println("queryContrlsTrigger : "+queryControlsBean.getReportIfEmpty()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryContrlsInitRecTime"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryControlsBean.setInitialRecordTime(new Date(str));//TODO:check it
							System.out.println("queryContrlsInitRecTime : "+queryControlsBean.getInitialRecordTime()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryContrlsQSched"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryControlsBean.setQuerySchedBean(new QueryScheduleBean(str));
							System.out.println("queryContrlsQSched : "+queryControlsBean.getQuerySchedBean().getId()+" ");	
						}
					}//for
					
					queryControlsBeanList.add(queryControlsBean);
					
				}//while
				return queryControlsBeanList;
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
		
		public static String selectQueryControlsByOSMO(String graph,OSMOBean osmoBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
			queryStr.append("SELECT ?queryContrlsID ?queryContrlsTrigger ?queryContrlsIfEmpty ?queryContrlsInitRecTime ?queryContrlsQSched ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?queryContrlsID rdf:type <http://openiot.eu/ontology/ns/QueryControls> . ");
			queryStr.append("?queryContrlsID <http://openiot.eu/ontology/ns/querycontrolsOfOSMO> <"+osmoBean.getId()+"> . "); 
			queryStr.append("?queryContrlsID <http://openiot.eu/ontology/ns/querycontrolsReportIfEmpty> ?queryContrlsIfEmpty . "); 
			queryStr.append("optional { ?queryContrlsID <http://openiot.eu/ontology/ns/querycontrolsTrigger> ?queryContrlsTrigger . }"); 
			queryStr.append("optional { ?queryContrlsID <http://openiot.eu/ontology/ns/querycontrolsInitialRecTime> ?queryContrlsInitRecTime . }"); 
			queryStr.append("optional { ?queryContrlsID <http://openiot.eu/ontology/ns/querycontrolsHasQuerySchedule> ?queryContrlsQSched . }"); 
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual queryControlsClassIdv;
	private OntClass ontClsQueryControls;
	private OntProperty ontPquerycontrolsHasQuerySchedule;
	private OntProperty ontPquerycontrolsTrigger;
	private OntProperty ontPquerycontrolsReportIfEmpty;
	private OntProperty ontPquerycontrolsInitialRecTime;
	private OntProperty ontPquerycontrolsOfOSMO;	

	private QueryControlsBean queryControlsBean;
	private QueryScheduleEnt queryScheduleEnt;
	private OSMOEnt osmoEnt;
	
	
	public QueryControlsEnt(QueryControlsBean qcBean,OSMOEnt osmoEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_QueryControls();
		
		setQueryControlsBean(qcBean);
		setOsmoEnt(osmoEnt);
	}
	public QueryControlsEnt(String filePath,QueryControlsBean qcBean,OSMOEnt osmoEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_QueryControls();
		
		setQueryControlsBean(qcBean);
		setOsmoEnt(osmoEnt);
	}
	
	private void initOnt_QueryControls()
	{
		ontClsQueryControls = ontTemplate.createClass("http://openiot.eu/ontology/ns/QueryControls");
		ontPquerycontrolsHasQuerySchedule = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsHasQuerySchedule");
		ontPquerycontrolsTrigger = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsTrigger");
		ontPquerycontrolsReportIfEmpty = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsReportIfEmpty");
		ontPquerycontrolsInitialRecTime = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsInitialRecTime");
		ontPquerycontrolsOfOSMO = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsOfOSMO");
	}
	
	
	////
	public Individual getClassIndividual()
	{
		return queryControlsClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(queryControlsBean.getId()==null)
			queryControlsClassIdv = ontInstance.createIndividual(ontClsQueryControls);
		else
			queryControlsClassIdv = ontInstance.createIndividual(queryControlsBean.getId(),ontClsQueryControls);
	}
	public void createPquerycontrolsHasQuerySchedule()
	{
		if(queryScheduleEnt!=null)	
			queryControlsClassIdv.addProperty(ontPquerycontrolsHasQuerySchedule, queryScheduleEnt.getClassIndividual());
	}
	public void createPquerycontrolsTrigger()
	{
		if(queryControlsBean.getTrigger()!=null)	
			queryControlsClassIdv.setPropertyValue(ontPquerycontrolsTrigger, ontInstance.getBase().createTypedLiteral(queryControlsBean.getTrigger()));
	}
	public void createPquerycontrolsReportIfEmpty()
	{
		if(queryControlsBean.getReportIfEmpty()!=null)	
			queryControlsClassIdv.setPropertyValue(ontPquerycontrolsReportIfEmpty, ontInstance.getBase().createTypedLiteral(queryControlsBean.getReportIfEmpty().booleanValue()));
	}
	public void createPquerycontrolsInitialRecTime()
	{
		if(queryControlsBean.getInitialRecordTime()!=null)	
			queryControlsClassIdv.setPropertyValue(ontPquerycontrolsInitialRecTime, ontInstance.getBase().createTypedLiteral(queryControlsBean.getInitialRecordTime()));
	}
	public void createPquerycontrolsOfOSMO()
	{
		if(osmoEnt!=null)	
			queryControlsClassIdv.addProperty(ontPquerycontrolsOfOSMO, osmoEnt.getClassIndividual());
	}
	
	
	
	
	
	public QueryControlsBean getQueryControlsBean() {
		return queryControlsBean;
	}
	public void setQueryControlsBean(QueryControlsBean queryControlsBean) {
		this.queryControlsBean = queryControlsBean;
	}
	
	
	
	public OSMOEnt getOsmoEnt() {
		return osmoEnt;
	}
	public void setOsmoEnt(OSMOEnt osmoEnt) {
		this.osmoEnt = osmoEnt;
	}
	public QueryScheduleEnt getQueryScheduleEnt() {
		return queryScheduleEnt;
	}
	public void setQueryScheduleEnt(QueryScheduleEnt queryScheduleEnt) {
		this.queryScheduleEnt = queryScheduleEnt;
	}
}
