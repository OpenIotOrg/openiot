package org.openiot.lsm.sdum.model.entities;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.lsm.schema.LSMSchema;
import org.openiot.lsm.sdum.model.beans.OSDSpecBean;
import org.openiot.lsm.sdum.model.beans.UserBean;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;

public class UserEnt 
{
	public static class Queries
	{
		public static ArrayList<UserBean> parseUserData(TupleQueryResult qres)
		{
			ArrayList<UserBean> userList = new ArrayList<UserBean>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set<String> names = b.getBindingNames();
					
					boolean found =false;
					UserBean user = new UserBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("userID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setId(str);
							System.out.println("user id: "+user.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("userName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setName(str);
							System.out.println("userName : "+user.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("userDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setDescription(str);
							System.out.println("userDesc : "+user.getDescription()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("userPasw"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setPasswd(str);
							System.out.println("userPasw : "+user.getPasswd()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("userMail"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setEmail(str);
							System.out.println("userMail : "+user.getEmail()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("osdSpec"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(UserBean object : userList)
							{
								if(object.getId().equals(user.getId()))	{
									 //grab already added user and add osdspec 
									object.getOsdSpecBean().add(new OSDSpecBean(str));
								    found = true;
							    }
							    else {
							    	 //this user doesnt exist in the list 
							    	user.getOsdSpecBean().add(new OSDSpecBean(str));
							        found = false;
							    }
							}							
						}
					}
					
					if(!found)
						userList.add(user);					
				}//while
				return userList;
			} 
			catch (QueryEvaluationException e)			
			{				
				e.printStackTrace();
				return null;
			}
			catch (Exception e)			
			{				
				e.printStackTrace();
				return null;
			}
		}
		
//		private static String getNamespaceDeclarations() 
//		{
//	        StringBuilder declarations = new StringBuilder();
//	        declarations.append("PREFIX : <" + "http://openiot.eu/ontology/ns/" + "> \n");
//	        //declarations.append("PREFIX spt: <" + "http://spitfire-project.eu/ontology/ns/" + "> \n");
//	        declarations.append("PREFIX rdf: <" + RDF.getURI() + "> \n");//http://www.w3.org/1999/02/22-rdf-syntax-ns#
//	        declarations.append("PREFIX rdfs: <" + RDFS.getURI() + "> \n");//http://www.w3.org/2000/01/rdf-schema#
//	        declarations.append("PREFIX xsd: <" + XSD.getURI() + "> \n");
//	        //declarations.append("PREFIX owl: <" + OWL.getURI() + "> \n");
//	        //declarations.append("PREFIX ssn: <" + "http://purl.oclc.org/NET/ssnx/ssn#" + "> \n");
//	        //declarations.append("PREFIX dul: <" + "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#" + "> \n");
//	        //declarations.append("PREFIX oiot: <" + "http://openiot.eu/ontology/ns/" + "> \n");
//	        //declarations.append("base oiot: <" + "http://openiot.eu/ontology/ns/clouddb" + "> \n");	       
//	        declarations.append("\n");
//	        
//	        return declarations.toString();
//	    }
		
		public static String selectAllUsers(String graph)
		{
			StringBuilder queryStr = new StringBuilder();
	        //queryStr.append(getNamespaceDeclarations());
			
			queryStr.append("SELECT ?userID ?userName ?userDesc ?userMail ?userPasw ?osdSpec ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?userID rdf:type <http://openiot.eu/ontology/ns/User> . ");								
			queryStr.append("?userID <http://openiot.eu/ontology/ns/userName> ?userName. ");
			queryStr.append("?userID <http://openiot.eu/ontology/ns/userPassword> ?userPasw. ");
			queryStr.append("?userID <http://openiot.eu/ontology/ns/userMail> ?userMail. ");
			queryStr.append("optional { ?userID <http://openiot.eu/ontology/ns/userHasOSDSpec> ?osdSpec. }");
			queryStr.append("optional { ?userID <http://openiot.eu/ontology/ns/userDescription> ?userDesc. }");
			queryStr.append("}");								
			
			return queryStr.toString();
		}
		
	}//queries

	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual userClassIdv;	
	private OntClass ontClsUser;
	private OntProperty ontPuserName;
	private OntProperty ontPuserEmail;
	private OntProperty ontPuserDescription;
	private OntProperty ontPuserPasswd;
	private OntProperty ontPuserHasOSDSpec;
	
	private UserBean userBean;
	private ArrayList<OSDSpecEnt> specEnt = new ArrayList<OSDSpecEnt>();
	
	//constructors
	public UserEnt()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_USer();
	}
	public UserEnt(String filePath,LSMSchema ontInstance)
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
		if(userBean.getId()==null)
			userClassIdv = ontInstance.createIndividual(ontClsUser);
		else
			userClassIdv = ontInstance.createIndividual(userBean.getId(),ontClsUser);
	}
	public void createPuserName()
	{
		if(userBean.getName()!=null)		
			userClassIdv.setPropertyValue(ontPuserName, ontInstance.getBase().createTypedLiteral(userBean.getName()));
	}
	public void createPuserEmail()
	{
		if(userBean.getEmail()!=null)
			userClassIdv.setPropertyValue(ontPuserEmail, ontInstance.getBase().createTypedLiteral(userBean.getEmail()));
	}
	public void createPuserDesc()
	{
		if(userBean.getDescription()!=null)
			userClassIdv.setPropertyValue(ontPuserDescription, ontInstance.getBase().createTypedLiteral(userBean.getDescription()));
	}
	public void createPuserPasswd()
	{
		if(userBean.getPasswd()!=null)
			userClassIdv.setPropertyValue(ontPuserPasswd, ontInstance.getBase().createTypedLiteral(userBean.getPasswd()));
	}	
	public void createPuserHasSpec()
	{
		if(userBean.getOsdSpecBean()!=null && !userBean.getOsdSpecBean().isEmpty())
		{
			for(int i=0; i<specEnt.size(); i++)
			{
				userClassIdv.addProperty(ontPuserHasOSDSpec, specEnt.get(i).getClassIndividual());
			}
		}
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
	
	
	
	
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public ArrayList<OSDSpecEnt> getSpecEnt() {
		return specEnt;
	}
	
}
