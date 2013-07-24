package com.lsm.testschema.model;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

import lsm.schema.LSMSchema;

public class Right 
{
	private LSMSchema  myOnt;	
	private LSMSchema  ontInstance;
	
	private Individual rightClassIdv;
	
	private OntClass ontClsRightClass;	
	private OntProperty ontPdescription;
	
	private String id;
	private String description;
	
	public Right(LSMSchema  myOnt,LSMSchema  ontInstance)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		
		initOnt_Right();
	}
	
	private void initOnt_Right()
	{
		ontClsRightClass = myOnt.getClass("http://openiot.eu/ontology/ns/Right");
		ontPdescription = myOnt.createProperty("http://openiot.eu/ontology/ns/rightDescription");					
	}

		
	public void createOnt_Right()
	{		
		rightClassIdv = ontInstance.createIndividual(ontClsRightClass);		
		rightClassIdv.setPropertyValue(ontPdescription, ontInstance.getBase().createTypedLiteral(description));		
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

}//class
