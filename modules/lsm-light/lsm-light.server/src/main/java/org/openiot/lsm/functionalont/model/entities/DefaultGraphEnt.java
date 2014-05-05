package org.openiot.lsm.functionalont.model.entities;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.lsm.functionalont.model.beans.DefaultGraphBean;
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



public class DefaultGraphEnt 
{
	public static class Queries
	{
		public static ArrayList<DefaultGraphBean> parseDefGraphURIOfQRequest(TupleQueryResult qres)
		{
			ArrayList<DefaultGraphBean> defaultGraphBeanList = new ArrayList<DefaultGraphBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					DefaultGraphBean defaultGraphBean = new DefaultGraphBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("defGraphID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							defaultGraphBean.setId(str);
							System.out.println("defGraphID: "+defaultGraphBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("defGraphURI"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							defaultGraphBean.setDefaultGraphURI(str);
							System.out.println("defGraphURI : "+defaultGraphBean.getDefaultGraphURI()+" ");	
						}
					}//for
					
					defaultGraphBeanList.add(defaultGraphBean);
					
				}//while
				return defaultGraphBeanList;
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
		
		public static String selectDefGraphByQRequest(String graph,QueryRequestBean qrBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
			queryStr.append("SELECT ?defGraphID ?defGraphURI ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?defGraphID rdf:type <http://openiot.eu/ontology/ns/DefaultGraph> . ");
			queryStr.append("?defGraphID <http://openiot.eu/ontology/ns/defaultgraphOfQueryRequest> <"+qrBean.getId()+"> . "); 
			queryStr.append("?defGraphID <http://openiot.eu/ontology/ns/defaultgraphURI> ?defGraphURI . "); 
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;

	private Individual defaultGraphClassIdv;
	private OntClass ontClsDefaultGraph;
	private OntProperty ontPdefaultGraphURI;
	private OntProperty ontPdefaultGraphOfQueryRequest;
	
	private DefaultGraphBean defaultGraphBean;
	private QueryRequestEnt queryRequestEnt;
	
	public DefaultGraphEnt()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_DefaultRequest();
	}
	public DefaultGraphEnt(String filePath)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_DefaultRequest();		
	}
	
	private void initOnt_DefaultRequest()
	{
		ontClsDefaultGraph = ontTemplate.createClass("http://openiot.eu/ontology/ns/DefaultGraph");
		ontPdefaultGraphURI = ontTemplate.createProperty("http://openiot.eu/ontology/ns/defaultgraphURI");
		ontPdefaultGraphOfQueryRequest = ontTemplate.createProperty("http://openiot.eu/ontology/ns/defaultgraphOfQueryRequest");
	}
	
	////
	public Individual getClassIndividual()
	{
		return defaultGraphClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(defaultGraphBean.getId()==null)
			defaultGraphClassIdv = ontInstance.createIndividual(ontClsDefaultGraph);
		else
			defaultGraphClassIdv = ontInstance.createIndividual(defaultGraphBean.getId(),ontClsDefaultGraph);
	}
	public void createPdefaultGraphURI()
	{
		if(defaultGraphBean.getDefaultGraphURI()!=null)	
			defaultGraphClassIdv.setPropertyValue(ontPdefaultGraphURI, ontInstance.getBase().createTypedLiteral(defaultGraphBean.getDefaultGraphURI()));
	}
	public void createPdefaultGraphOfQueryRequest()
	{
		if(queryRequestEnt!=null)	
			defaultGraphClassIdv.addProperty(ontPdefaultGraphOfQueryRequest, queryRequestEnt.getClassIndividual());
	}
	
	
	public QueryRequestEnt getQueryRequestEnt() {
		return queryRequestEnt;
	}
	public void setQueryRequestEnt(QueryRequestEnt queryRequestEnt) {
		this.queryRequestEnt = queryRequestEnt;
	}
}
