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

import org.openiot.lsm.functionalont.model.entities.QueryRequestEnt;
import org.openiot.lsm.functionalont.model.beans.DefaultGraphBean;
import org.openiot.lsm.functionalont.model.beans.NamedGraphBean;
import org.openiot.lsm.functionalont.model.beans.QueryRequestBean;
import org.openiot.lsm.schema.LSMSchema;


public class NamedGraphEnt 
{
	public static class Queries
	{
		public static ArrayList<NamedGraphBean> parseNamedGraphURIOfQRequest(TupleQueryResult qres)
		{
			ArrayList<NamedGraphBean> namedGraphBeanList = new ArrayList<NamedGraphBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					NamedGraphBean namedGraphBean = new NamedGraphBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("namedGraphID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							namedGraphBean.setId(str);
							System.out.println("namedGraphID: "+namedGraphBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("namedGraphURI"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							namedGraphBean.setNamedGraphURI(str);
							System.out.println("namedGraphURI : "+namedGraphBean.getNamedGraphURI()+" ");	
						}
					}//for
					
					namedGraphBeanList.add(namedGraphBean);
					
				}//while
				return namedGraphBeanList;
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
		
		public static String selectNamedGraphByQRequest(String graph,QueryRequestBean qrBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
			queryStr.append("SELECT ?namedGraphID ?namedGraphURI ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?namedGraphID rdf:type <http://openiot.eu/ontology/ns/NamedGraph> . ");
			queryStr.append("?namedGraphID <http://openiot.eu/ontology/ns/namedgraphOfQueryRequest> <"+qrBean.getId()+"> . "); 
			queryStr.append("?namedGraphID <http://openiot.eu/ontology/ns/namedgraphURI> ?namedGraphURI . "); 
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;

	private Individual namedGraphClassIdv;
	private OntClass ontClsNamedGraph;
	private OntProperty ontPnamedGraphURI;
	private OntProperty ontPnamedGraphOfQueryRequest;
	
	private DefaultGraphBean namedGraphBean;
	private QueryRequestEnt queryRequestEnt;
	
	public NamedGraphEnt()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_DefaultRequest();
	}
	public NamedGraphEnt(String filePath)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_DefaultRequest();		
	}
	
	private void initOnt_DefaultRequest()
	{
		ontClsNamedGraph = ontTemplate.createClass("http://openiot.eu/ontology/ns/NamedGraph");
		ontPnamedGraphURI = ontTemplate.createProperty("http://openiot.eu/ontology/ns/namedgraphURI");
		ontPnamedGraphOfQueryRequest = ontTemplate.createProperty("http://openiot.eu/ontology/ns/namedgraphOfQueryRequest");
	}
	
	////
	public Individual getClassIndividual()
	{
		return namedGraphClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(namedGraphBean.getId()==null)
			namedGraphClassIdv = ontInstance.createIndividual(ontClsNamedGraph);
		else
			namedGraphClassIdv = ontInstance.createIndividual(namedGraphBean.getId(),ontClsNamedGraph);
	}
	public void createPdefaultGraphURI()
	{
		if(namedGraphBean.getDefaultGraphURI()!=null)	
			namedGraphClassIdv.setPropertyValue(ontPnamedGraphURI, ontInstance.getBase().createTypedLiteral(namedGraphBean.getDefaultGraphURI()));
	}
	public void createPdefaultGraphOfQueryRequest()
	{
		if(queryRequestEnt!=null)	
			namedGraphClassIdv.addProperty(ontPnamedGraphOfQueryRequest, queryRequestEnt.getClassIndividual());
	}
	
	
	public QueryRequestEnt getQueryRequestEnt() {
		return queryRequestEnt;
	}
	public void setQueryRequestEnt(QueryRequestEnt queryRequestEnt) {
		this.queryRequestEnt = queryRequestEnt;
	}
}
