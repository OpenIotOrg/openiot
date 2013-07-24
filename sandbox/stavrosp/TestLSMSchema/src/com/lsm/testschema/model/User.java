package com.lsm.testschema.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;


//private String id;
//private String name;
//private String email;
//private String description;
//private Access access;
////private String userType;
//private ArrayList<Service> serviceList = new ArrayList<Service>();

//ontClsUserClass = myOnt.getClass("http://openiot.eu/ontology/ns/User");
//ontPName = myOnt.createProperty("http://openiot.eu/ontology/ns/userName");
//ontPemail = myOnt.createProperty("http://openiot.eu/ontology/ns/userMail");
//ontPdescription = myOnt.createProperty("http://openiot.eu/ontology/ns/userDescription");
//ontPaccess = myOnt.getProperty("http://openiot.eu/ontology/ns/access");
//ontPuserOf = myOnt.getProperty("http://openiot.eu/ontology/ns/userOf");

public class User 
{
	public static class Queries
	{
		public static ArrayList<User> parseUser(TupleQueryResult qres)
		{
			ArrayList<User> userList = new ArrayList<User>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					User user = new User();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("userID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setId(str);
							System.out.print("user id: "+user.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("userName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setName(str);
							System.out.print("userName : "+user.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("userDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setDescription(str);
							System.out.print("userDesc : "+user.getDescription()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("userMail"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							user.setEmail(str);
							System.out.print("userMail : "+user.getEmail()+" ");	
						}
					}
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
		
		private static String graph = "http://lsm.deri.ie/OpenIoT/testSchema#";
		
		private static String getNamespaceDeclarations() 
		{
	        StringBuilder declarations = new StringBuilder();
	        declarations.append("PREFIX : <" + "http://openiot.eu/ontology/ns/" + "> \n");
	        //declarations.append("PREFIX spt: <" + "http://spitfire-project.eu/ontology/ns/" + "> \n");
	        declarations.append("PREFIX rdf: <" + RDF.getURI() + "> \n");//http://www.w3.org/1999/02/22-rdf-syntax-ns#
	        declarations.append("PREFIX rdfs: <" + RDFS.getURI() + "> \n");//http://www.w3.org/2000/01/rdf-schema#
	        declarations.append("PREFIX xsd: <" + XSD.getURI() + "> \n");
	        //declarations.append("PREFIX owl: <" + OWL.getURI() + "> \n");
	        //declarations.append("PREFIX ssn: <" + "http://purl.oclc.org/NET/ssnx/ssn#" + "> \n");
	        //declarations.append("PREFIX dul: <" + "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#" + "> \n");
	        //declarations.append("PREFIX oiot: <" + "http://openiot.eu/ontology/ns/" + "> \n");
	        //declarations.append("base oiot: <" + "http://openiot.eu/ontology/ns/clouddb" + "> \n");	       
	        declarations.append("\n");
	        
	        return declarations.toString();
	    }
				
		public static String selectAllUsers()
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?userID ?userName ?userDesc ?userMail from <"+graph+"> "
								+"WHERE "
								+"{"
								+"?userID <http://openiot.eu/ontology/ns/userMail> ?userMail."
								+"?userID <http://openiot.eu/ontology/ns/userDescription> ?userDesc."
								+"?userID <http://openiot.eu/ontology/ns/userName> ?userName."
								+"?userID rdf:type <http://openiot.eu/ontology/ns/User> . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectUserByName(String usrName)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?userID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?userID <http://openiot.eu/ontology/ns/userName> ?name FILTER regex(?name, \"" +usrName+ "\" )  . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectUserByDescription(String desc)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?userID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?userID <http://openiot.eu/ontology/ns/userDescription> ?desc FILTER regex(?desc, \"" +desc+ "\" )  . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectUserByEmail(String email)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?userID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?userID <http://openiot.eu/ontology/ns/userMail> ?email FILTER regex(?email, \"" +email+ "\" )  . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectUserByAccess(Access access)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?userID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?userID <http://openiot.eu/ontology/ns/access> <"+access.getId()+"> )  . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		
		public static String selectUserByNameAndDescription(String usrName,String desc,String email)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?userID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?userID <http://openiot.eu/ontology/ns/userName> ?name FILTER regex(?name, \"" +usrName+ "\" )  . "
								+"?userID <http://openiot.eu/ontology/ns/userDescription> ?desc FILTER regex(?desc, \"" +desc+ "\" )  . "
								+"?userID <http://openiot.eu/ontology/ns/userMail> ?email FILTER regex(?email, \"" +email+ "\" )  . "
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectUserByService(ArrayList<Service> serviceList)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
	        update.append("SELECT ?userID from <"+graph+"> "
								+"WHERE "
								+"{");								
			for(int i=0; i<serviceList.size(); i++)
			{
				update.append("?userID <http://openiot.eu/ontology/ns/userOf> <"+serviceList.get(i).getId()+"> )  . ");				
			}	        
	        update.append("}");
	        
			return update.toString();
		}
	}//class
	
	
	
	private LSMSchema  myOnt;	
	private LSMSchema  ontInstance;
	private String graph;
	private LSMTripleStore lsmStore;
	
	private Individual userClassIdv;
	
	private OntClass ontClsUserClass;
	private OntProperty ontPName;
	private OntProperty ontPemail;
	private OntProperty ontPdescription;
	private OntProperty ontPaccess;
	//private OntProperty ontPuserType;
	private OntProperty ontPuserOf;
	
	private String id;
	private String name;
	private String email;
	private String description;
	private Access access;
	//private String userType;
	private ArrayList<Service> serviceList = new ArrayList<Service>();
		
	
	public User()
	{

	}
	public User(LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		initOnt_USer();
		//createClassIdv();
	}
	public User(String classIdvURL,LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;		
		this.id=classIdvURL;
		
		initOnt_USer();
		//createClassIdv();
	}
		
	private void initOnt_USer()
	{
		ontClsUserClass = myOnt.getClass("http://openiot.eu/ontology/ns/User");
		ontPName = myOnt.createProperty("http://openiot.eu/ontology/ns/userName");
		ontPemail = myOnt.createProperty("http://openiot.eu/ontology/ns/userMail");
		ontPdescription = myOnt.createProperty("http://openiot.eu/ontology/ns/userDescription");
		ontPaccess = myOnt.getProperty("http://openiot.eu/ontology/ns/access");
		ontPuserOf = myOnt.getProperty("http://openiot.eu/ontology/ns/userOf");
	}
	
	public void createClassIdv()
	{
		if(id==null)
			userClassIdv = ontInstance.createIndividual(ontClsUserClass);
		else
			userClassIdv = ontInstance.createIndividual(id,ontClsUserClass);
	}
	public void createPName()
	{
		if(name!=null)		
			userClassIdv.setPropertyValue(ontPName, ontInstance.getBase().createTypedLiteral(name));
	}
	public void createPemail()
	{
		if(email!=null)
			userClassIdv.setPropertyValue(ontPemail, ontInstance.getBase().createTypedLiteral(email));
	}
	public void createPdescription()
	{
		if(description!=null)
			userClassIdv.setPropertyValue(ontPdescription, ontInstance.getBase().createTypedLiteral(description));
	}
	public void createPaccess()
	{
		if(access!=null && access.getClassIndividual()!=null)
			userClassIdv.addProperty(ontPaccess, access.getClassIndividual());
	}
	public void createPuserOf()
	{
		for(int i=0; i<serviceList.size(); i++)
		{
			userClassIdv.addProperty(ontPuserOf, serviceList.get(i).getClassIndividual());
			
		}
	}
	
	public void createOnt_USer()
	{
		createClassIdv();
		createPName();
		createPemail();
		createPdescription();
		createPaccess();
		createPuserOf();
	}
	
	////
	
	public LSMSchema getOnt()
	{
		return myOnt;
	}
	
	public LSMSchema getOntInstance()
	{
		return ontInstance;
	}
	
	public Individual getClassIndividual()
	{
		return userClassIdv;
	}
		
	////
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id=id;
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


	public Access getAccess() {
		return access;	
	}
	public void setAccess(Access access) {
		this.access=access;
	}

	
//	public String getUserType() {
//		return userType;
//	}
//	public void setUserType(String userType) {
//		this.userType = userType;
//	}
	
	public ArrayList<Service> getServiceList() {
		return serviceList;
	}
	public void setServiceList(ArrayList<Service> serviceList) {
		this.serviceList = serviceList;
	}
	public void addService(Service service) 
	{
		this.serviceList.add(service);
	}
	
}//class
