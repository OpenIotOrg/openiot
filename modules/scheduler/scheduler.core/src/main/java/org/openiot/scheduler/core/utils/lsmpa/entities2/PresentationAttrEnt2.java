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

public class PresentationAttrEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual presentationAttrClassIdv;
	private OntClass ontClsPresentationAttr;
	private OntProperty ontPpresentationAttrValue;
	private OntProperty ontPpresentationAttrName;
	private OntProperty ontPpresentationAttrOfWidget;// not in spec
	
	private String id;
	private String value;
	private String name;
	private WidgetEnt2 widgetEnt;// not inspec
	
	//constructor
	public PresentationAttrEnt2(WidgetEnt2 widgetEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_PresAttr();
		
		setWidgetEnt(widgetEnt);
	}
	public PresentationAttrEnt2(String filePath,WidgetEnt2 widgetEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_PresAttr();
		
		setWidgetEnt(widgetEnt);
	}
	
	private void initOnt_PresAttr()
	{
		ontClsPresentationAttr = ontTemplate.createClass("http://openiot.eu/ontology/ns/PresentationAttr");
		ontPpresentationAttrValue = ontTemplate.createProperty("http://openiot.eu/ontology/ns/presentationAttrValue");
		ontPpresentationAttrName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/presentationAttrName");
		ontPpresentationAttrOfWidget = ontTemplate.createProperty("http://openiot.eu/ontology/ns/presentationAttrOfWidget");
	}
	
	////
	public Individual getClassIndividual()
	{
		return presentationAttrClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(getId()==null)
			presentationAttrClassIdv = ontInstance.createIndividual(ontClsPresentationAttr);
		else
			presentationAttrClassIdv = ontInstance.createIndividual(getId(),ontClsPresentationAttr);
	}
	public void createPpresentationAttrName()
	{
		if(getValue()!=null)	
			presentationAttrClassIdv.setPropertyValue(ontPpresentationAttrValue, ontInstance.getBase().createTypedLiteral(getValue()));
	}
	public void createPpresentationAttrValue()
	{
		if(getName()!=null)	
			presentationAttrClassIdv.setPropertyValue(ontPpresentationAttrName, ontInstance.getBase().createTypedLiteral(getName()));
	}
	public void createPpresentationAttrOfWidget()
	{
		if(widgetEnt!=null)	
			presentationAttrClassIdv.addProperty(ontPpresentationAttrOfWidget, widgetEnt.getClassIndividual());
	}
	
	
	public void createAll()
	{
		createClassIdv();
		createPpresentationAttrName();
		createPpresentationAttrValue();
		createPpresentationAttrOfWidget();
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
	public String toStringValName() {
		StringBuffer preAttr = new StringBuffer();
		preAttr.append("\"preAttr\":");
		preAttr.append("{");
		
		preAttr.append("\"value\":"+getValue());preAttr.append(",");
		preAttr.append("\"name\":"+getName());
		
		preAttr.append("}");
		return preAttr.toString();
	}
	
	public WidgetEnt2 getWidgetEnt() {
		return widgetEnt;
	}
	public void setWidgetEnt(WidgetEnt2 widgetEnt) {
		this.widgetEnt = widgetEnt;
	}
}
