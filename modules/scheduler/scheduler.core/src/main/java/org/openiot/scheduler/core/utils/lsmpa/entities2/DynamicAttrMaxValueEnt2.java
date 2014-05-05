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

public class DynamicAttrMaxValueEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual  dynamicAttrMaxValueClassIdv;
	private OntClass ontClsDynamicAttrMaxValue;
	private OntProperty ontPdynamicattrmaxvalueName;
	private OntProperty ontPdynamicattrmaxvalueValue;
	private OntProperty ontPdynamicattrmaxvalueOfOsmo;
	
	//bean properties
	private String id;
	private String value;
	private String name;
	
	private OSMOEnt2 osmoEnt;// not inspec
	
	public DynamicAttrMaxValueEnt2()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_DynamicAttrMaxValue();
	}
	public DynamicAttrMaxValueEnt2(String filePath)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_DynamicAttrMaxValue();
	}
	
	private void initOnt_DynamicAttrMaxValue()
	{
		ontClsDynamicAttrMaxValue = ontTemplate.createClass("http://openiot.eu/ontology/ns/DynamicAttrMaxValue");
		ontPdynamicattrmaxvalueName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/dynamicattrmaxvalueName");
		ontPdynamicattrmaxvalueValue = ontTemplate.createProperty("http://openiot.eu/ontology/ns/dynamicattrmaxvalueValue");
		ontPdynamicattrmaxvalueOfOsmo = ontTemplate.createProperty("http://openiot.eu/ontology/ns/dynamicattrmaxvalueOfOsmo");
	}
	
	////
	public Individual getClassIndividual()
	{
		return dynamicAttrMaxValueClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(getId()==null)
			dynamicAttrMaxValueClassIdv = ontInstance.createIndividual(ontClsDynamicAttrMaxValue);
		else
			dynamicAttrMaxValueClassIdv = ontInstance.createIndividual(getId(),ontClsDynamicAttrMaxValue);
	}
	public void createPdynamicattrmaxvalueName()
	{
		if(getValue()!=null)	
			dynamicAttrMaxValueClassIdv.setPropertyValue(ontPdynamicattrmaxvalueName, ontInstance.getBase().createTypedLiteral(getValue()));
	}
	public void createPdynamicattrmaxvalueValue()
	{
		if(getName()!=null)	
			dynamicAttrMaxValueClassIdv.setPropertyValue(ontPdynamicattrmaxvalueValue, ontInstance.getBase().createTypedLiteral(getName()));
	}
	public void createPdynamicattrmaxvalueOfOsmo()
	{
		if(osmoEnt!=null)	
			dynamicAttrMaxValueClassIdv.addProperty(ontPdynamicattrmaxvalueOfOsmo, osmoEnt.getClassIndividual());
	}
	
	
	public OSMOEnt2 getOsmoEnt() {
		return osmoEnt;
	}
	public void setOsmoEnt(OSMOEnt2 osmoEnt) {
		this.osmoEnt = osmoEnt;
	}
	
	
	public String getId() {
		return id;
	}	
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
