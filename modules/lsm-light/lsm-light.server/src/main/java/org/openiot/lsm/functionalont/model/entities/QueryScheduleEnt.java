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

import org.openiot.lsm.functionalont.model.entities.QueryControlsEnt;
import org.openiot.lsm.functionalont.model.beans.OAMOBean;
import org.openiot.lsm.functionalont.model.beans.OSDSpecBean;
import org.openiot.lsm.functionalont.model.beans.OSMOBean;
import org.openiot.lsm.functionalont.model.beans.QueryControlsBean;
import org.openiot.lsm.functionalont.model.beans.QueryScheduleBean;
import org.openiot.lsm.schema.LSMSchema;

public class QueryScheduleEnt 
{
	public static class Queries
	{
		public static ArrayList<QueryScheduleBean> parseOAMO(TupleQueryResult qres)
		{
			ArrayList<QueryScheduleBean> queryScheduleBeanList = new ArrayList<QueryScheduleBean>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					QueryScheduleBean queryScheduleBean = new QueryScheduleBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("querScheduleID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryScheduleBean.setId(str);
							System.out.println("querScheduleID: "+queryScheduleBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("qschedSecond"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryScheduleBean.setSecond(str);
							System.out.println("qschedSecond : "+queryScheduleBean.getSecond()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("qschedMinute"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();							
							queryScheduleBean.setMinute(str);
							System.out.println("qschedMinute : "+queryScheduleBean.getMinute()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("qschedDayOfMonth"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryScheduleBean.setDayOfMonth(str);
							System.out.println("qschedDayOfMonth : "+queryScheduleBean.getDayOfMonth()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("qschedMonth"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryScheduleBean.setMonth(str);
							System.out.println("qschedMonth : "+queryScheduleBean.getMonth()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("qschedDayOfWeek"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryScheduleBean.setDayOfWeek(str);
							System.out.println("qschedDayOfWeek : "+queryScheduleBean.getDayOfWeek()+" ");	
						}
					}//for					
					queryScheduleBeanList.add(queryScheduleBean);					
				}//while
				return queryScheduleBeanList;
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
		
		public static String selectQScheduleByQControls(String graph,QueryControlsBean qControlsBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
	        queryStr.append("SELECT ?querScheduleID ?qschedSecond ?qschedMinute ?qschedDayOfMonth ?qschedMonth ?qschedDayOfWeek ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?querScheduleID rdf:type <http://openiot.eu/ontology/ns/QuerySchedule> . ");
			queryStr.append("?querScheduleID <http://openiot.eu/ontology/ns/queryscheduleOfQueryControls> <"+qControlsBean.getId()+"> . ");			
			queryStr.append("optional {?querScheduleID <http://openiot.eu/ontology/ns/queryscheduleSecond> ?qschedSecond . }");
			queryStr.append("optional {?querScheduleID <http://openiot.eu/ontology/ns/queryscheduleMinute> ?qschedMinute . }");
			queryStr.append("optional {?querScheduleID <http://openiot.eu/ontology/ns/queryscheduleDayOfMonth> ?qschedDayOfMonth . }");
			queryStr.append("optional {?querScheduleID <http://openiot.eu/ontology/ns/queryscheduleMonth> ?qschedMonth . }");
			queryStr.append("optional {?querScheduleID <http://openiot.eu/ontology/ns/queryscheduleDayOfWeek> ?qschedDayOfWeek . }");
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}//queries
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual queryScheduleClassIdv;
	private OntClass ontClsQuerySchedule;
	private OntProperty ontPqueryscheduleSecond;
	private OntProperty ontPqueryscheduleMinute;
	private OntProperty ontPqueryscheduleHour;		
	private OntProperty ontPqueryscheduleDayOfMonth;
	private OntProperty ontPqueryscheduleMonth;
	private OntProperty ontPqueryscheduleDayOfWeek;
	private OntProperty ontPqueryscheduleOfQueryControls;
	
	private QueryScheduleBean queryScheduleBean;
	private QueryControlsEnt queryControlsEnt;
	
	public QueryScheduleEnt()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_QuerySchedule();
	}
	public QueryScheduleEnt(String filePath)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_QuerySchedule();		
	}
	
	private void initOnt_QuerySchedule()
	{
		ontClsQuerySchedule = ontTemplate.createClass("http://openiot.eu/ontology/ns/QuerySchedule");
		ontPqueryscheduleSecond = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleSecond");
		ontPqueryscheduleMinute = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleMinute");
		ontPqueryscheduleDayOfMonth = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleDayOfMonth");
		ontPqueryscheduleMonth = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleMonth");
		ontPqueryscheduleDayOfWeek = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleDayOfWeek");
		ontPqueryscheduleOfQueryControls = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleOfQueryControls");
	}

	
	////
	public Individual getClassIndividual()
	{
		return queryScheduleClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(queryScheduleBean.getId()==null)
			queryScheduleClassIdv = ontInstance.createIndividual(ontClsQuerySchedule);
		else
			queryScheduleClassIdv = ontInstance.createIndividual(queryScheduleBean.getId(),ontClsQuerySchedule);
	}
	public void createPqueryscheduleSecond()
	{
		if(queryScheduleBean.getSecond()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleSecond, ontInstance.getBase().createTypedLiteral(queryScheduleBean.getSecond()));
	}
	public void createPqueryscheduleMinute()
	{
		if(queryScheduleBean.getMinute()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleMinute, ontInstance.getBase().createTypedLiteral(queryScheduleBean.getMinute()));
	}
	public void createPqueryscheduleHour()
	{
		if(queryScheduleBean.getHour()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleHour, ontInstance.getBase().createTypedLiteral(queryScheduleBean.getHour()));
	}	
	public void createPqueryscheduledayOfMonth()
	{
		if(queryScheduleBean.getDayOfMonth()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleDayOfMonth, ontInstance.getBase().createTypedLiteral(queryScheduleBean.getDayOfMonth()));
	}
	public void createPqueryscheduleMonth()
	{
		if(queryScheduleBean.getMonth()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleMonth, ontInstance.getBase().createTypedLiteral(queryScheduleBean.getMonth()));
	}
	public void createPqueryscheduledayOfWeek()
	{
		if(queryScheduleBean.getDayOfWeek()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleDayOfWeek, ontInstance.getBase().createTypedLiteral(queryScheduleBean.getDayOfWeek()));
	}
	public void createPqueryscheduleOfQControls()
	{
		if(queryControlsEnt!=null)	
			queryScheduleClassIdv.addProperty(ontPqueryscheduleOfQueryControls,queryControlsEnt.getClassIndividual());
	}
	
	
	public QueryScheduleBean getQueryScheduleBean() {
		return queryScheduleBean;
	}
	public void setQueryScheduleBean(QueryScheduleBean queryScheduleBean) {
		this.queryScheduleBean = queryScheduleBean;
	}
	
	public OntProperty getOntPqueryscheduleOfQueryControls() {
		return ontPqueryscheduleOfQueryControls;
	}
	public void setOntPqueryscheduleOfQueryControls(
			OntProperty ontPqueryscheduleOfQueryControls) {
		this.ontPqueryscheduleOfQueryControls = ontPqueryscheduleOfQueryControls;
	}
}
