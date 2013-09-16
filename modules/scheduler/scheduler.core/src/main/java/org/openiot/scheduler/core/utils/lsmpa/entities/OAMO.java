package org.openiot.scheduler.core.utils.lsmpa.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

public class OAMO 
{
	public static class Queries
	{
		public static ArrayList<OAMO> parseOAMO(TupleQueryResult qres)
		{
			ArrayList<OAMO> oamoList = new ArrayList<OAMO>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					OAMO oamo = new OAMO();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("oamoID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							oamo.setId(str);
							System.out.println("oamoID: "+oamo.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("oamoName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							oamo.setName(str);
							System.out.println("oamoName : "+oamo.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("userID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							User user = new User();
							user.setId(str);
							oamo.setUser(user);
							System.out.println("userID : "+oamo.getUser().getId()+" ");	
						}
//						else if(((String) n).equalsIgnoreCase("srvcID"))
//						{
//							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							Service service = new Service();
//							service.setId(str);
//							oamo.addService(service);							
//							System.out.print("service : "+service.getId()+" ");	
//						}
					}
					oamoList.add(oamo);					
				}//while
				return oamoList;
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

		public static String selectOAMOByUser(User usr)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?oamoID ?oamoName ?userID from <"+graph+"> "
								+"WHERE "
								+"{"
								//+"?oamoID <http://openiot.eu/ontology/ns/oamoService> ?srvcID . "
								+"?oamoID <http://openiot.eu/ontology/ns/oamoUserOf> ?userID . "
								+"?oamoID <http://openiot.eu/ontology/ns/oamoName> ?oamoName . "
								+"?oamoID <http://openiot.eu/ontology/ns/oamoUserOf> <"+usr.getId()+"> . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}		
	}//class
	
	
	private LSMSchema  myOnt;	
	private LSMSchema  ontInstance;
	private String graph;
	private LSMTripleStore lsmStore;
	
	private Individual oamoClassIdv;	
	
	private OntClass ontClsOAMOClass;
	private OntProperty ontPoamoName;
	private OntProperty ontPoamoUserOf;
	private OntProperty ontPoamoService;
	
	private String id;
	private String name;	
	private List<Service> servicesList = new ArrayList<Service>();
	private User user;
	
	public OAMO()
	{		
	}
	public OAMO(LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		initOnt_Oamo();		
	}
	public OAMO(String classIdvURL,LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		this.id=classIdvURL;
		initOnt_Oamo();		
	}
		
	private void initOnt_Oamo()
	{
		ontClsOAMOClass = myOnt.createClass("http://openiot.eu/ontology/ns/OAMO");
		ontPoamoName = myOnt.createProperty("http://openiot.eu/ontology/ns/oamoName");
		ontPoamoUserOf = myOnt.createProperty("http://openiot.eu/ontology/ns/oamoUserOf");
		ontPoamoService = myOnt.createProperty("http://openiot.eu/ontology/ns/oamoService");
	}
	
	public void createClassIdv()
	{
		if(id==null)
			oamoClassIdv = ontInstance.createIndividual(ontClsOAMOClass);
		else
			oamoClassIdv = ontInstance.createIndividual(id,ontClsOAMOClass);
	}	
	public void createPoamoName()
	{
		if(name!=null)	
			oamoClassIdv.setPropertyValue(ontPoamoName, ontInstance.getBase().createTypedLiteral(name));
	}	
	public void createPoamoUserOf()
	{
		if(ontPoamoUserOf!=null)	
			oamoClassIdv.setPropertyValue(ontPoamoUserOf, user.getClassIndividual());
	}
	public void createPoamoService()
	{
		for(int i=0; i<servicesList.size(); i++)
		{
			oamoClassIdv.addProperty(ontPoamoService, servicesList.get(i).getClassIndividual());
		}
	}
	
	public void createOnt_OAMO()
	{
		createClassIdv();		
		createPoamoName();
		createPoamoUserOf();
		createPoamoService();
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
		return oamoClassIdv;
	}
	////
	
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
	
	public List<Service> getServicesList() {
		return servicesList;
	}
	public void addService(Service service) {
		this.servicesList.add(service);
	}
	public void setServicesList(List<Service> servicesList) {
		this.servicesList = servicesList;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}//class