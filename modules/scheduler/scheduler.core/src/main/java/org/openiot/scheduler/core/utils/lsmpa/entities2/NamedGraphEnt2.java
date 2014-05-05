package org.openiot.scheduler.core.utils.lsmpa.entities2;

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


public class NamedGraphEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;

	private Individual namedGraphClassIdv;
	private OntClass ontClsNamedGraph;
	private OntProperty ontPnamedGraphURI;
	private OntProperty ontPnamedGraphOfQueryRequest;
	
	//bean properties
	private String id;
	private String namedGraphURI;
	
	private QueryRequestEnt2 queryRequestEnt;
	
	public NamedGraphEnt2()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_DefaultRequest();
	}
	public NamedGraphEnt2(String filePath)
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
		if(getId()==null)
			namedGraphClassIdv = ontInstance.createIndividual(ontClsNamedGraph);
		else
			namedGraphClassIdv = ontInstance.createIndividual(getId(),ontClsNamedGraph);
	}
	public void createPdefaultGraphURI()
	{
		if(getNamedGraphURI()!=null)	
			namedGraphClassIdv.setPropertyValue(ontPnamedGraphURI, ontInstance.getBase().createTypedLiteral(getNamedGraphURI()));
	}
	public void createPdefaultGraphOfQueryRequest()
	{
		if(queryRequestEnt!=null)	
			namedGraphClassIdv.addProperty(ontPnamedGraphOfQueryRequest, queryRequestEnt.getClassIndividual());
	}
	
	
	public QueryRequestEnt2 getQueryRequestEnt() {
		return queryRequestEnt;
	}
	public void setQueryRequestEnt(QueryRequestEnt2 queryRequestEnt) {
		this.queryRequestEnt = queryRequestEnt;
	}
	
	public String getNamedGraphURI() {
		return namedGraphURI;
	}
	public void setNamedGraphURI(String namedGraphURI) {
		this.namedGraphURI = namedGraphURI;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
