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

public class OSDSpecEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual osdspecClassIdv;
	private OntClass ontClsOSDSpec;	
	private OntProperty ontPosdpsecOfUser;
	private OntProperty ontPosdpsecHasOamo;
	
	//bean properties
	private String id;
	private UserEnt2 userEnt;
	private ArrayList<OAMOEnt2> oamoEntList = new ArrayList<OAMOEnt2>();
	

	
	public OSDSpecEnt2(UserEnt2 userEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_OSDSpecEnt();
		
		setUserEnt(userEnt);
	}
	public OSDSpecEnt2(String filePath,UserEnt2 userEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_OSDSpecEnt();
		
		setUserEnt(userEnt);
	}
	
	private void initOnt_OSDSpecEnt()
	{
		ontClsOSDSpec = ontTemplate.createClass("http://openiot.eu/ontology/ns/OSDSPEC");
		ontPosdpsecOfUser = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osdspecOfUser");
		ontPosdpsecHasOamo = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osdpsecHasOamo");
	}
	
	////
	public Individual getClassIndividual()
	{
		return osdspecClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(getId()==null)
			osdspecClassIdv = ontInstance.createIndividual(ontClsOSDSpec);
		else
			osdspecClassIdv = ontInstance.createIndividual(getId(),ontClsOSDSpec);
	}
	public void createPosdpsecOfUser()
	{
		if(userEnt!=null)	
			osdspecClassIdv.addProperty(ontPosdpsecOfUser, userEnt.getClassIndividual());
	}
	public void createPosdpsecHasOamo()
	{
		for(int i=0; i<oamoEntList.size(); i++)
		{
			osdspecClassIdv.addProperty(ontPosdpsecHasOamo, oamoEntList.get(i).getClassIndividual());
		}
	}
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	public UserEnt2 getUserEnt() {
		return userEnt;
	}
	public void setUserEnt(UserEnt2 userEnt) {
		this.userEnt = userEnt;
	}
	public ArrayList<OAMOEnt2> getOamoEntList() {
		return oamoEntList;
	}

}//class
