package org.openiot.lsm.functionalont.model.entities;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.lsm.functionalont.model.beans.DefaultGraphBean;
import org.openiot.lsm.functionalont.model.beans.NamedGraphBean;
import org.openiot.lsm.functionalont.model.beans.OSMOBean;
import org.openiot.lsm.functionalont.model.beans.QueryRequestBean;
import org.openiot.lsm.schema.LSMSchema;
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

public class QueryRequestEnt 
{
	public static class Queries
	{
		public static ArrayList<QueryRequestBean> parseQueryReqBean(TupleQueryResult qres)
		{
			ArrayList<QueryRequestBean> queryRequestBeanList = new ArrayList<QueryRequestBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					boolean found =false;
					QueryRequestBean queryRequestBean = new QueryRequestBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("queryReqID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryRequestBean.setId(str);
							System.out.println("queryReqID: "+queryRequestBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryReqQuery"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							queryRequestBean.setQuery(str);
							System.out.println("oamoName : "+queryRequestBean.getQuery()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryReqDefGraph")) //TODO: check if it will work
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(QueryRequestBean object : queryRequestBeanList)
							{
								if(object.getId().equals(queryRequestBean.getId()))	{
									 //grab already added QueryRequestBean and add  queryReqDefGraph
									object.getDefaultGraphBeanList().add(new DefaultGraphBean(str));
								    found = true;
							    }
							    else {
							    	 //this QueryRequestBean exist in the list 
							    	queryRequestBean.getDefaultGraphBeanList().add(new DefaultGraphBean(str));
							        found = false;
							    }
							}
						}
						else if(((String) n).equalsIgnoreCase("queryReqNamedGraph")) //TODO: check if it will work
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(QueryRequestBean object : queryRequestBeanList)
							{
								if(object.getId().equals(queryRequestBean.getId()))	{
									 //grab already added QueryRequestBean and add  queryReqNamedGraph
									object.getNamedGraphBeanList().add(new NamedGraphBean(str));
								    found = true;
							    }
							    else {
							    	 //this QueryRequestBean exist in the list 
							    	queryRequestBean.getNamedGraphBeanList().add(new NamedGraphBean(str));
							        found = false;
							    }
							}
						}
					}//for
					
					if(!found)
						queryRequestBeanList.add(queryRequestBean);
					
				}//while
				return queryRequestBeanList;
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
		
		public static String selectQueryReqsByOSMO(String graph,OSMOBean osmoBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
			queryStr.append("SELECT ?queryReqID ?queryReqQuery ?queryReqDefGraph ?queryReqNamedGraph ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?queryReqID rdf:type <http://openiot.eu/ontology/ns/QueryRequest> . ");
			queryStr.append("?queryReqID <http://openiot.eu/ontology/ns/queryreqOfOsmo> <"+osmoBean.getId()+"> . "); 
			queryStr.append("?queryReqID <http://openiot.eu/ontology/ns/osmoHasQueryControls> ?queryReqQuery . "); 
			queryStr.append("optional {?osmoID <http://openiot.eu/ontology/ns/queryreqHasDefGraph> ?queryReqDefGraph . }"); //may return many graphs 
			queryStr.append("optional {?osmoID <http://openiot.eu/ontology/ns/queryreqHasNamedGraph> ?queryReqNamedGraph . }"); //may return many graphs
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual queryReqClassIdv;
	private OntClass ontClsQueryReq;
	private OntProperty ontPqueryreqQuery;	
	private OntProperty ontPqueryreqHasDefaultGraph;	 
	private OntProperty ontPqueryreqHasNamedGraph;
	private OntProperty ontPqueryreqOfOsmo;  //not in osdspec model
	
	private QueryRequestBean queryRequestBean;	
	private ArrayList<DefaultGraphEnt> defaultGraphList = new ArrayList<DefaultGraphEnt>();
	private ArrayList<NamedGraphEnt> namedGraphList = new ArrayList<NamedGraphEnt>();
	private OSMOEnt osmoEnt; //not in osdspec model
	

	
	public QueryRequestEnt(QueryRequestBean queryRequestBean, OSMOEnt osmoEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_QueryRequest();	
		
		setQReqBean(queryRequestBean);
		setOsmoEnt(osmoEnt);
	}
	public QueryRequestEnt(String filePath, QueryRequestBean queryRequestBean, OSMOEnt osmoEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_QueryRequest();
		
		setQReqBean(queryRequestBean);
		setOsmoEnt(osmoEnt);
	}
	
	private void initOnt_QueryRequest()
	{
		ontClsQueryReq = ontTemplate.createClass("http://openiot.eu/ontology/ns/QueryRequest");
		ontPqueryreqQuery = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryreqQuery");
		ontPqueryreqHasDefaultGraph = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryreqHasDefGraph");
		ontPqueryreqHasNamedGraph = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryreqHasNamedGraph");
		ontPqueryreqOfOsmo = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryreqOfOsmo");
	}
	
	////
	public Individual getClassIndividual()
	{
		return queryReqClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(queryRequestBean.getId()==null)
			queryReqClassIdv = ontInstance.createIndividual(ontClsQueryReq);
		else
			queryReqClassIdv = ontInstance.createIndividual(queryRequestBean.getId(),ontClsQueryReq);
	}
	public void createPqueryreqQuery()
	{
		if(queryRequestBean.getQuery()!=null)	
			queryReqClassIdv.setPropertyValue(ontPqueryreqQuery, ontInstance.getBase().createTypedLiteral(queryRequestBean.getQuery()));
	}
	public void createPqueryDefaultGraph()
	{
		for(int i=0; i<defaultGraphList.size(); i++)
		{
			queryReqClassIdv.addProperty(ontPqueryreqHasDefaultGraph, defaultGraphList.get(i).getClassIndividual());
		}
	}
	public void createPqueryNamedGraph()
	{
		for(int i=0; i<namedGraphList.size(); i++)
		{
			queryReqClassIdv.addProperty(ontPqueryreqHasNamedGraph, namedGraphList.get(i).getClassIndividual());
		}
	}	
	public void createPqueryreqOfOsmo()
	{
		if(osmoEnt!=null)	
			queryReqClassIdv.addProperty(ontPqueryreqOfOsmo, osmoEnt.getClassIndividual());
	}

	
	public void createAll()
	{
		createClassIdv();
		createPqueryreqQuery();
		createPqueryreqOfOsmo();
	}
	
	
	
	public QueryRequestBean getQReqBean() {
		return queryRequestBean;
	}
	public void setQReqBean(QueryRequestBean queryRequestBean) {
		this.queryRequestBean = queryRequestBean;
	}
	
	public ArrayList<DefaultGraphEnt> getDefaultGraphList() {
		return defaultGraphList;
	}
	public void setDefaultGraphList(ArrayList<DefaultGraphEnt> defaultGraphList) {
		this.defaultGraphList = defaultGraphList;
	}
	public ArrayList<NamedGraphEnt> getNamedGraphList() {
		return namedGraphList;
	}
	public void setNamedGraphList(ArrayList<NamedGraphEnt> namedGraphList) {
		this.namedGraphList = namedGraphList;
	}
	public OSMOEnt getOsmoEnt() {
		return osmoEnt;
	}
	public void setOsmoEnt(OSMOEnt osmoEnt) {
		this.osmoEnt = osmoEnt;
	}
}
