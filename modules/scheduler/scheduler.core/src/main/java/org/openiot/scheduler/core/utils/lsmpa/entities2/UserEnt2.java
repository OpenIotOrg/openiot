package org.openiot.scheduler.core.utils.lsmpa.entities2;

import java.util.ArrayList;
import java.util.List;
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

public class UserEnt2 
{

	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual userClassIdv;	
	private OntClass ontClsUser;
	private OntProperty ontPuserName;
	private OntProperty ontPuserEmail;
	private OntProperty ontPuserDescription;
	private OntProperty ontPuserPasswd;
	private OntProperty ontPuserHasOSDSpec;
	
	//bean properties
	private String id;
	private String name;
	private String email;
	private String description;
	private String passwd;
	private ArrayList<OSDSpecEnt2> specEnt = new ArrayList<OSDSpecEnt2>();
	
	//constructors
	public UserEnt2()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_USer();
	}
	public UserEnt2(String filePath,LSMSchema ontInstance)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=ontInstance;
		
		initOnt_USer();
	}	
		
	private void initOnt_USer()
	{
		ontClsUser = ontTemplate.createClass("http://openiot.eu/ontology/ns/User");
		ontPuserName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/userName");
		ontPuserEmail = ontTemplate.createProperty("http://openiot.eu/ontology/ns/userMail");
		ontPuserDescription = ontTemplate.createProperty("http://openiot.eu/ontology/ns/userDescription");
		ontPuserPasswd = ontTemplate.createProperty("http://openiot.eu/ontology/ns/userPassword");
		ontPuserHasOSDSpec = ontTemplate.createProperty("http://openiot.eu/ontology/ns/userHasOSDSpec");
	}
	
	////	
	public Individual getClassIndividual()
	{
		return userClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(getId()==null)
			userClassIdv = ontInstance.createIndividual(ontClsUser);
		else
			userClassIdv = ontInstance.createIndividual(getId(),ontClsUser);
	}
	public void createPuserName()
	{
		if(getName()!=null)		
			userClassIdv.setPropertyValue(ontPuserName, ontInstance.getBase().createTypedLiteral(getName()));
	}
	public void createPuserEmail()
	{
		if(getEmail()!=null)
			userClassIdv.setPropertyValue(ontPuserEmail, ontInstance.getBase().createTypedLiteral(getEmail()));
	}
	public void createPuserDesc()
	{
		if(getDescription()!=null)
			userClassIdv.setPropertyValue(ontPuserDescription, ontInstance.getBase().createTypedLiteral(getDescription()));
	}
	public void createPuserPasswd()
	{
		if(getPasswd()!=null)
			userClassIdv.setPropertyValue(ontPuserPasswd, ontInstance.getBase().createTypedLiteral(getPasswd()));
	}	
	public void createPuserHasSpec()
	{
//		if(userBean.getOsdSpecBean()!=null && !userBean.getOsdSpecBean().isEmpty())
//		{
			for(int i=0; i<specEnt.size(); i++)
			{
				userClassIdv.addProperty(ontPuserHasOSDSpec, specEnt.get(i).getClassIndividual());
			}
//		}
	}
	
//	public void createPuserHasSpec2()
//	{
//		if(userBean.getOsdSpecBean()!=null && !userBean.getOsdSpecBean().isEmpty())
//		{
//			for(int i=0; i<userBean.getOsdSpecBean().size(); i++)
//			{
//				OSDSpecEnt osdSpecEnt = new OSDSpecEnt();
//				osdSpecEnt.setOSDSpecBean(userBean.getOsdSpecBean().get(i));
//				osdSpecEnt.setUserEnt(this);
//				//
//				osdSpecEnt.createClassIdv();
//				osdSpecEnt.createPosdpsecOfUser();
//				
//				this.getSpecEnt().add(osdSpecEnt);
//				
//				userClassIdv.addProperty(ontPuserHasOSDSpec, osdSpecEnt.getClassIndividual());
//			}
//		}
//	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	
	public ArrayList<OSDSpecEnt2> getSpecEnt() {
		return specEnt;
	}
	
}
