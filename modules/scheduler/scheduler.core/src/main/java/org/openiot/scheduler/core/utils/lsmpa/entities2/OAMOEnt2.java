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

public class OAMOEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual oamoClassIdv;	
	private OntClass ontClsOAMO;
	private OntProperty ontPoamoDescription;
	private OntProperty ontPoamoGraphMeta;
	private OntProperty ontPoamoName;
	private OntProperty ontPoamoOfOSDSpec;
	private OntProperty ontPoamoHasOSMO; 
	
	//bean properties
	private String id;	
	private String description;
	private String graphMeta;
	private String name;
	
	private OSDSpecEnt2 osdSpecEnt; 
	private ArrayList<OSMOEnt2> osmoList = new ArrayList<OSMOEnt2>();
	
	public OAMOEnt2(OSDSpecEnt2 osdSpecEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_Oamo();
		
		setOsdSpecEnt(osdSpecEnt);
	}
	public OAMOEnt2(String filePath,OSDSpecEnt2 osdSpecEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_Oamo();
		
		setOsdSpecEnt(osdSpecEnt);
	}

	private void initOnt_Oamo()
	{
		ontClsOAMO = ontTemplate.createClass("http://openiot.eu/ontology/ns/OAMO");
		ontPoamoDescription = ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoDescription");
		ontPoamoGraphMeta= ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoGraphMeta");
		ontPoamoName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoName");
		ontPoamoOfOSDSpec = ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoOfOSDSpec");
		ontPoamoHasOSMO = ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoHasOSMO");
	}
	
	////	
	public Individual getClassIndividual()
	{
		return oamoClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(getId()==null)
			oamoClassIdv = ontInstance.createIndividual(ontClsOAMO);
		else
			oamoClassIdv = ontInstance.createIndividual(getId(),ontClsOAMO);
	}
	public void createPoamoDescription()
	{
		if(getDescription()!=null)	
			oamoClassIdv.setPropertyValue(ontPoamoDescription, ontInstance.getBase().createTypedLiteral(getDescription()));
	}
	public void createPoamoGraphMeta()
	{
		if(getGraphMeta()!=null)	
			oamoClassIdv.setPropertyValue(ontPoamoGraphMeta, ontInstance.getBase().createTypedLiteral(getGraphMeta()));
	}
	public void createPoamoName()
	{
		if(getName()!=null)	
			oamoClassIdv.setPropertyValue(ontPoamoName, ontInstance.getBase().createTypedLiteral(getName()));
	}	
	public void createPoamoOfOSDSpec()
	{
		if(osdSpecEnt!=null)	
			oamoClassIdv.addProperty(ontPoamoOfOSDSpec, osdSpecEnt.getClassIndividual());
	}
	public void createPoamoHasOSMO()
	{
		for(int i=0; i<osmoList.size(); i++)
		{
			oamoClassIdv.addProperty(ontPoamoHasOSMO, osmoList.get(i).getClassIndividual());
		}
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
	public String getGraphMeta() {
		return graphMeta;
	}
	public void setGraphMeta(String graphMeta) {
		this.graphMeta = graphMeta;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	public OSDSpecEnt2 getOsdSpecEnt() {
		return osdSpecEnt;
	}
	public void setOsdSpecEnt(OSDSpecEnt2 osdSpecEnt) {
		this.osdSpecEnt = osdSpecEnt;
	}
	
	public ArrayList<OSMOEnt2> getOsmoList() {
		return osmoList;
	}
	public void setOsmoList(ArrayList<OSMOEnt2> osmoList) {
		this.osmoList = osmoList;
	}
}//class