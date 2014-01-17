package org.openiot.scheduler.core.utils.lsmpa.entities;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

import org.openiot.lsm.schema.LSMSchema;
import org.openiot.lsm.server.LSMTripleStore;

public class Query 
{
	private LSMSchema  myOnt;	
	private LSMSchema  ontInstance;
	private String graph;
	private LSMTripleStore lsmStore;
	
	private Individual queryStringClassIdv;
	
	private OntClass ontClsQueryStringClass;
	private OntProperty ontPqsDescription;
	private OntProperty ontPqsQuery;
	
	private String id;
	private String qString;
	private String description;
	
	public Query()
	{}
 	public Query(LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		initOnt_QueryString();		
	}
	public Query(String classIdvURL,LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		this.id=classIdvURL;
		
		initOnt_QueryString();		
	}
	
	private void initOnt_QueryString()
	{
		ontClsQueryStringClass = myOnt.createClass("http://openiot.eu/ontology/ns/Query");
		ontPqsDescription = myOnt.createProperty("http://openiot.eu/ontology/ns/queryDescription");
		ontPqsQuery = myOnt.createProperty("http://openiot.eu/ontology/ns/queryString");
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
		return queryStringClassIdv;
	}
	
	////
	
	public void createClassIdv()
	{
		if(id==null)
			queryStringClassIdv = ontInstance.createIndividual(ontClsQueryStringClass);
		else
			queryStringClassIdv = ontInstance.createIndividual(id,ontClsQueryStringClass);
	}	
	public void createPqueryDescription()
	{
		if(description!=null)	
			queryStringClassIdv.setPropertyValue(ontPqsDescription, ontInstance.getBase().createTypedLiteral(description));
	}
	public void createPqueryString()
	{
		if(qString!=null)	
			queryStringClassIdv.setPropertyValue(ontPqsQuery, ontInstance.getBase().createTypedLiteral(qString));
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getqString() {
		return qString;
	}
	public void setqString(String qString) {
		this.qString = qString;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
