package org.openiot.scheduler.core.utils.lsmpa.entities;

/**
 *    Copyright (c) 2011-2014, OpenIoT
 *    
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

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



public class User 
{
	public static class Queries
	{
		public static ArrayList<User> parseUserData(TupleQueryResult qres)
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
									
			String str=("SELECT ?userID ?userName ?userDesc ?userMail ?userPasw from <"+graph+"> "
					+"WHERE "
					+"{"
					+"?userID rdf:type <http://openiot.eu/ontology/ns/User> . "	
					+"?userID <http://openiot.eu/ontology/ns/userMail> ?userMail."
					+"?userID <http://openiot.eu/ontology/ns/userDescription> ?userDesc."
					+"?userID <http://openiot.eu/ontology/ns/userName> ?userName."					
					+"optional { ?userID <http://openiot.eu/ontology/ns/userPassword> ?userPasw. } "
					+"}");
			
			update.append(str);
			return update.toString();
		}
//		public static String selectUserByName(String usrName)
//		{
//			StringBuilder update = new StringBuilder();
//	        update.append(getNamespaceDeclarations());
//			
//			String str=("SELECT ?userID from <"+graph+"> "
//								+"WHERE "
//								+"{"								
//								+"?userID <http://openiot.eu/ontology/ns/userName> ?name FILTER regex(?name, \"" +usrName+ "\" )  . "								
//								+"}");								
//			
//			update.append(str);
//			return update.toString();
//		}
//		public static String selectUserByDescription(String desc)
//		{
//			StringBuilder update = new StringBuilder();
//	        update.append(getNamespaceDeclarations());
//			
//			String str=("SELECT ?userID from <"+graph+"> "
//								+"WHERE "
//								+"{"								
//								+"?userID <http://openiot.eu/ontology/ns/userDescription> ?desc FILTER regex(?desc, \"" +desc+ "\" )  . "								
//								+"}");								
//			
//			update.append(str);
//			return update.toString();
//		}
		public static String selectUserByEmail(String email)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?userID ?userName ?userDesc ?userPasw from <"+graph+"> "
					+"WHERE "
					+"{"
					+"?userID rdf:type <http://openiot.eu/ontology/ns/User> . "
					+"?userID <http://openiot.eu/ontology/ns/userDescription> ?userDesc."
					+"?userID <http://openiot.eu/ontology/ns/userName> ?userName."
					+"optional { ?userID <http://openiot.eu/ontology/ns/userPassword> ?userPasw. } "
					+"?userID <http://openiot.eu/ontology/ns/userMail> \""+email+"\"^^<http://www.w3.org/2001/XMLSchema#string> ."
					+"}");
			
			update.append(str);
			return update.toString();
		}
//		public static String selectUserByAccess(Access access)
//		{
//			StringBuilder update = new StringBuilder();
//	        update.append(getNamespaceDeclarations());
//			
//			String str=("SELECT ?userID from <"+graph+"> "
//								+"WHERE "
//								+"{"								
//								+"?userID <http://openiot.eu/ontology/ns/access> <"+access.getId()+"> )  . "								
//								+"}");								
//			
//			update.append(str);
//			return update.toString();
//		}
		
//		public static String selectUserByNameAndDescription(String usrName,String desc,String email)
//		{
//			StringBuilder update = new StringBuilder();
//	        update.append(getNamespaceDeclarations());
//			
//			String str=("SELECT ?userID from <"+graph+"> "
//								+"WHERE "
//								+"{"								
//								+"?userID <http://openiot.eu/ontology/ns/userName> ?name FILTER regex(?name, \"" +usrName+ "\" )  . "
//								+"?userID <http://openiot.eu/ontology/ns/userDescription> ?desc FILTER regex(?desc, \"" +desc+ "\" )  . "
//								+"?userID <http://openiot.eu/ontology/ns/userMail> ?email FILTER regex(?email, \"" +email+ "\" )  . "
//								+"}");								
//			
//			update.append(str);
//			return update.toString();
//		}
//		public static String selectUserByService(ArrayList<Service> serviceList)
//		{
//			StringBuilder update = new StringBuilder();
//	        update.append(getNamespaceDeclarations());
//			
//	        update.append("SELECT ?userID from <"+graph+"> "
//								+"WHERE "
//								+"{");								
//			for(int i=0; i<serviceList.size(); i++)
//			{
//				update.append("?userID <http://openiot.eu/ontology/ns/userOf> <"+serviceList.get(i).getId()+"> )  . ");				
//			}	        
//	        update.append("}");
//	        
//			return update.toString();
//		}
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
	private OntProperty ontPpasswd;
	//private OntProperty ontPaccess;
	//private OntProperty ontPuserType;
	private OntProperty ontPuserOf;
	
	private String id;
	private String name;
	private String email;
	private String description;
	private String passwd;
	
	private ArrayList<OAMO> oamoList = new ArrayList<OAMO>();
		
	
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
		ontClsUserClass = myOnt.createClass("http://openiot.eu/ontology/ns/User");
		ontPName = myOnt.createProperty("http://openiot.eu/ontology/ns/userName");
		ontPemail = myOnt.createProperty("http://openiot.eu/ontology/ns/userMail");
		ontPdescription = myOnt.createProperty("http://openiot.eu/ontology/ns/userDescription");
		ontPpasswd = myOnt.createProperty("http://openiot.eu/ontology/ns/userPassword");
		//ontPaccess = myOnt.createProperty("http://openiot.eu/ontology/ns/access");
		ontPuserOf = myOnt.createProperty("http://openiot.eu/ontology/ns/userOf");
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
	public void createPpasswd()
	{
		if(passwd!=null)
			userClassIdv.setPropertyValue(ontPpasswd, ontInstance.getBase().createTypedLiteral(passwd));
	}
	
	public void createPuserOf()
	{
		for(int i=0; i<oamoList.size(); i++)
		{
			userClassIdv.addProperty(ontPuserOf, oamoList.get(i).getClassIndividual());			
		}
	}
	
	public void createOnt_USer()
	{
		createClassIdv();
		createPName();
		createPemail();
		createPdescription();
		
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
	public ArrayList<OAMO> getServiceList() {
		return oamoList;
	}
	public void setServiceList(ArrayList<OAMO> serviceList) {
		this.oamoList = serviceList;
	}
	public void addService(OAMO oamo) 
	{
		this.oamoList.add(oamo);
	}
	
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
}//class
