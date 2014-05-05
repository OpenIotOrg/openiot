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

public class QueryRequestEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual queryReqClassIdv;
	private OntClass ontClsQueryReq;
	private OntProperty ontPqueryreqQuery;	
	private OntProperty ontPqueryreqHasDefaultGraph;	 
	private OntProperty ontPqueryreqHasNamedGraph;
	private OntProperty ontPqueryreqOfOsmo;  //not in osdspec model
	
	//bean properties
	private String id;
	private String query;
	private ArrayList<DefaultGraphEnt2> defaultGraphList = new ArrayList<DefaultGraphEnt2>();
	private ArrayList<NamedGraphEnt2> namedGraphList = new ArrayList<NamedGraphEnt2>();
	private OSMOEnt2 osmoEnt; //not in osdspec model
	

	
	public QueryRequestEnt2(OSMOEnt2 osmoEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_QueryRequest();	
		
		setOsmoEnt(osmoEnt);
	}
	public QueryRequestEnt2(String filePath, OSMOEnt2 osmoEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_QueryRequest();
		
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
		if(getId()==null)
			queryReqClassIdv = ontInstance.createIndividual(ontClsQueryReq);
		else
			queryReqClassIdv = ontInstance.createIndividual(getId(),ontClsQueryReq);
	}
	public void createPqueryreqQuery()
	{
		if(getQuery()!=null)	
			queryReqClassIdv.setPropertyValue(ontPqueryreqQuery, ontInstance.getBase().createTypedLiteral(getQuery()));
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
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String toStringQueryStr() {
		
		StringBuffer q = new StringBuffer();
		q.append("\"qreq\":");
		q.append("{");

		q.append("\"queryString\":"+getQuery());
			
		q.append("}");
		
		return q.toString();
	}
	
	public ArrayList<DefaultGraphEnt2> getDefaultGraphList() {
		return defaultGraphList;
	}
	public void setDefaultGraphList(ArrayList<DefaultGraphEnt2> defaultGraphList) {
		this.defaultGraphList = defaultGraphList;
	}
	public ArrayList<NamedGraphEnt2> getNamedGraphList() {
		return namedGraphList;
	}
	public void setNamedGraphList(ArrayList<NamedGraphEnt2> namedGraphList) {
		this.namedGraphList = namedGraphList;
	}
	public OSMOEnt2 getOsmoEnt() {
		return osmoEnt;
	}
	public void setOsmoEnt(OSMOEnt2 osmoEnt) {
		this.osmoEnt = osmoEnt;
	}
}
