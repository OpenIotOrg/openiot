package org.openiot.scheduler.core.utils.lsmpa.entities2;

import java.util.ArrayList;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import org.openiot.lsm.schema.LSMSchema;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class OSMOEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual osmoClassIdv;
	private OntClass ontClsOSMO;	
	private OntProperty ontPosmoHasQueryControls;
	private OntProperty ontPosmoHasRequestPresentation;
	private OntProperty ontPosmoHasQueryRequest;
	private OntProperty ontPosmoHasDynamicAttrMaxValue;
	private OntProperty ontPosmoDescription;
	private OntProperty ontPosmoName;		
	private OntProperty ontPosmoOfOAMO;//not in osdspec
	
	//bean properties
	private String id;
	private String description;
	private String name;
	private QueryControlsEnt2 queryControlsEnt;
	private ReqPresentationEnt2 reqPresentationEnt;	
	private ArrayList<QueryRequestEnt2> queryRequestEntList = new ArrayList<QueryRequestEnt2>();
	private ArrayList<DynamicAttrMaxValueEnt2> dynamicAttrMaxValueEntList;
	private OAMOEnt2 oamoEnt;//not in osdspec

	
	//constructor
	public OSMOEnt2(OAMOEnt2 oamoEnt )
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
	
		initOnt_Osmo();

		setOamoEnt(oamoEnt);
	}
	public OSMOEnt2(String filePath,OAMOEnt2 oamoEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_Osmo();
		
		setOamoEnt(oamoEnt);
	}
	
		
	private void initOnt_Osmo()
	{
		ontClsOSMO = ontTemplate.createClass("http://openiot.eu/ontology/ns/OSMO");
		ontPosmoHasQueryControls = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoHasQueryControls");
		ontPosmoHasRequestPresentation= ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoHasRequestpresentation");
		ontPosmoHasQueryRequest = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoHasQueryRequest");
		ontPosmoHasDynamicAttrMaxValue = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoHasDynamicAttrMaxValue");
		ontPosmoDescription = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoDescription");
		ontPosmoName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoName");
		ontPosmoOfOAMO = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoOfOAMO");
	}
	
	////
	public Individual getClassIndividual()
	{
		return osmoClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(getId()==null)
			osmoClassIdv = ontInstance.createIndividual(ontClsOSMO);
		else
			osmoClassIdv = ontInstance.createIndividual(getId(),ontClsOSMO);
	}
	public void createPosmoHasQueryControls()
	{
		if(queryControlsEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoHasQueryControls, queryControlsEnt.getClassIndividual());
	}
	public void createPosmoHasQueryControlsAsString()
	{
		if(queryControlsEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoHasQueryControls, ontInstance.getBase().createTypedLiteral(queryControlsEnt.toStringIfEmpty()));
	}
	public void createPosmoHasRequestpresentation()
	{
		if(reqPresentationEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoHasRequestPresentation, reqPresentationEnt.getClassIndividual());
	}
	public void createPosmoHasRequestpresentationAsString()
	{
		if(reqPresentationEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoHasRequestPresentation, ontInstance.getBase().createTypedLiteral(reqPresentationEnt.toStringWidget()));
	}
	public void createPosmoHasQueryRequest()
	{
//		for(int i=0; i<queryRequestEntList.size(); i++)
//		{
			osmoClassIdv.addProperty(ontPosmoHasQueryRequest, queryRequestEntList.get(0).getClassIndividual());
//		}
	}
	public void createPosmoHasQueryRequestAsString()
	{
//		for(int i=0; i<queryRequestEntList.size(); i++)
//		{
			osmoClassIdv.addProperty(ontPosmoHasQueryRequest, ontInstance.getBase().createTypedLiteral(queryRequestEntList.get(0).toStringQueryStr()));
//		}
	}
	public void createPosmoHasDynamicAttrMaxValue()
	{
		for(int i=0; i<dynamicAttrMaxValueEntList.size(); i++)
		{
			osmoClassIdv.addProperty(ontPosmoHasDynamicAttrMaxValue, dynamicAttrMaxValueEntList.get(i).getClassIndividual());
		}
	}
	public void createPosmoDescription()
	{
		if(getDescription()!=null)	
			osmoClassIdv.setPropertyValue(ontPosmoDescription, ontInstance.getBase().createTypedLiteral(getDescription()));
	}
	public void createPosmoName()
	{
		if(getName()!=null)	
			osmoClassIdv.setPropertyValue(ontPosmoName, ontInstance.getBase().createTypedLiteral(getName()));
	}
	public void createPosmoOfOAMO()
	{
		if(oamoEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoOfOAMO, oamoEnt.getClassIndividual());
	}

	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public ReqPresentationEnt2 getReqPresentationEnt() {
		return reqPresentationEnt;
	}
	public void setReqPresentationEnt(ReqPresentationEnt2 reqPresentationEnt) {
		this.reqPresentationEnt = reqPresentationEnt;
	}
	public ArrayList<QueryRequestEnt2> getQueryRequestEntList() {
		return queryRequestEntList;
	}
	public void setQueryRequestEntList(	ArrayList<QueryRequestEnt2> queryRequestEntList) {
		this.queryRequestEntList = queryRequestEntList;
	}
	public ArrayList<DynamicAttrMaxValueEnt2> getDynamicAttrMaxValueEntList() {
		return dynamicAttrMaxValueEntList;
	}
	public void setDynamicAttrMaxValueEntList(ArrayList<DynamicAttrMaxValueEnt2> dynamicAttrMaxValueEntList) {
		this.dynamicAttrMaxValueEntList = dynamicAttrMaxValueEntList;
	}
	public OAMOEnt2 getOamoEnt() {
		return oamoEnt;
	}
	public void setOamoEnt(OAMOEnt2 oamoEnt) {
		this.oamoEnt = oamoEnt;
	}
	public QueryControlsEnt2 getQueryControlsEnt() {
		return queryControlsEnt;
	}
	public void setQueryControlsEnt(QueryControlsEnt2 queryControlsEnt) {
		this.queryControlsEnt = queryControlsEnt;
	}
}
