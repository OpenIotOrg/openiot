package com.lsm.testschema.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class ServiceStatus 
{
	
	public static class Queries
	{
		public static ArrayList<ServiceStatus> parseService(TupleQueryResult qres)
		{
			ArrayList<ServiceStatus> serviceStatusList = new ArrayList<ServiceStatus>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					ServiceStatus srvcStatus = new ServiceStatus();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("srvcStatusID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcStatus.setId(str);
							System.out.print("srvcStatus id: "+srvcStatus.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcStatusTime"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcStatus.setTime(str);
							System.out.print("srvcStatusTime : "+srvcStatus.getTime()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcStatusStatus"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							srvcStatus.setStatus(ServiceStatus.State.toEnum(str));
							System.out.print("srvcStatusStatus : "+srvcStatus.getStatus().getCode()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("srvcStatusOf"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							Service srvc = new Service();
							srvc.setId(str);
							srvcStatus.setServiceOf(srvc);
							System.out.print("srvcStatusOf : "+srvcStatus.getServiceOf().getId()+" ");	
						}
					}
					serviceStatusList.add(srvcStatus);					
				}//while
				return serviceStatusList;
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
				
//		public static String selectAllSrvcStatus()//check it!!!
//		{
//			StringBuilder update = new StringBuilder();
//	        update.append(getNamespaceDeclarations());
//			
//			String str=("SELECT ?srvcStatusID from <"+graph+"> "
//								+"WHERE "
//								+"{"
//								
//								+"?srvcStatusID rdf:type <"+ServiceStatus.State.INITIALIZING+"> . "
//								+"?srvcStatusID rdf:type <"+ServiceStatus.State.INITIALIZING+"> . "
//								+"?srvcStatusID rdf:type <"+ServiceStatus.State.INITIALIZING+"> . "
//								+"?srvcStatusID rdf:type <"+ServiceStatus.State.INITIALIZING+"> . "
//								+"}");								
//			
//			update.append(str);
//			return update.toString();
//		}
		public static String selectAllSrvcStatusByStatus(ServiceStatus.State status)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?srvcStatusID from <"+graph+"> "
								+"WHERE "
								+"{"
								
								+"?srvcStatusID rdf:type <"+status.getCode()+"> . "
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		public static String selectSrvcStatusByStatuses(List<ServiceStatus.State> statusList)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
	        update.append("SELECT ?srvcStatusID from <"+graph+"> "
					+"WHERE "
					+"{");								
			
	        for(int i=0; i<statusList.size(); i++)
			{
				update.append("?srvcStatusID rdf:type <"+statusList.get(i).getCode()+"> . ");				
			}	        
			
			update.append("}");		
			return update.toString();
		}
		public static String selectSrvcStatusByTime(String time)//check it
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?srvcStatusID from <"+graph+"> "
								+"WHERE "
								+"{"								
								+"?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusTime> ?desc FILTER regex(?desc, \"" +time+ "\" )  . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}		
		public static String selectSrvcStatusByService(Service service)
		{
			StringBuilder update = new StringBuilder();
	        update.append(getNamespaceDeclarations());
			
			String str=("SELECT ?srvcStatusID ?srvcStatusTime ?srvcStatusStatus ?srvcStatusOf <"+graph+"> "
								+"WHERE "
								+"{"
								+"?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusOf> ?srvcStatusOf . "
								+"?srvcStatusID rdf:type ?srvcStatusStatus . "
								+"?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusTime> ?srvcStatusTime . "
								+"?srvcStatusID <http://openiot.eu/ontology/ns/serviceStatusOf> <"+service.getId()+"> . "								
								+"}");								
			
			update.append(str);
			return update.toString();
		}
		
	}//class
	
	
	private LSMSchema  myOnt;
	private LSMSchema  ontInstance;
	private String graph;
	private LSMTripleStore lsmStore;
	
	private Individual serviceStatusClassIdv;
	
	private OntClass ontClsserviceStatusClass;
	private OntProperty ontPsrvcStatusTime;
	private OntProperty ontPsrvcStatusOf;
		
	private String id;	
	private String serviceStatusTime;
	private State status;
	private Service service;
		
	
	public ServiceStatus()
	{
		
	}
	public ServiceStatus(LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore,State status )//String id,State status)
	{		
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		this.status=status;
		
		initOnt_ServiceStatus();
		//createClassIdv();
	}
	public ServiceStatus(String classIdvURL,LSMSchema  myOnt,LSMSchema  ontInstance,String graph,LSMTripleStore lsmStore,State status )//String id,State status)
	{		
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		this.graph = graph;
		this.lsmStore=lsmStore;
		
		this.status=status;
		
		this.id=classIdvURL;
						
		initOnt_ServiceStatus();
		//createClassIdv();
	}
	
	
	public enum State 
	{
		INITIALIZING("http://openiot.eu/ontology/ns/Initializing"),
		ENABLED("http://openiot.eu/ontology/ns/Enabled"),
		ERROR("http://openiot.eu/ontology/ns/Error"),
		IN_PROGRESS("http://openiot.eu/ontology/ns/Inprogress"),
		SUSPENDED("http://openiot.eu/ontology/ns/Suspended"),
		UNSATISFIED("http://openiot.eu/ontology/ns/Unsatisfied");
		
		private String code;

		//constructor
		private State(String c){
			code = c;
		}

		public String getCode() {
			return code;
		}
		
		public static State toEnum(String str)
		{
			if(str.equals(State.INITIALIZING.getCode()))
				return State.INITIALIZING;
			else if(str.equals(State.ENABLED.getCode()))
				return State.ENABLED;
			else if(str.equals(State.ERROR.getCode()))
				return State.ERROR;
			else if(str.equals(State.IN_PROGRESS.getCode()))
				return State.IN_PROGRESS;
			else if(str.equals(State.SUSPENDED.getCode()))
				return State.SUSPENDED;
			else if(str.equals(State.UNSATISFIED.getCode()))
				return State.UNSATISFIED;
			else
				return null;
		}
	}//enum
	
	
	private void initOnt_ServiceStatus()
	{
		
		//serviceStatusClass = myOnt.getClass("http://openiot.eu/ontology/ns/ServiceStatus");
		//serviceStatusClass = myOnt.getClass("http://openiot.eu/ontology/ns/Initializing");
		ontClsserviceStatusClass = myOnt.createClass(status.getCode());		
		ontPsrvcStatusTime = myOnt.getProperty("http://openiot.eu/ontology/ns/serviceStatusTime");
		ontPsrvcStatusOf =  myOnt.getProperty("http://openiot.eu/ontology/ns/serviceStatusOf");
	}
	
	public void createClassIdv()
	{
		if(id==null)
			serviceStatusClassIdv = ontInstance.createIndividual(ontClsserviceStatusClass);
		else
			serviceStatusClassIdv = ontInstance.createIndividual(id,ontClsserviceStatusClass);
	}
	
//	public void createOnt_ServiceStatus()
//	{	
//		serviceStatusClassIdv = ontInstance.createIndividual(status.getCode()+"#"+this.id, serviceStatusClass);
//		serviceStatusClassIdv.setPropertyValue(srvcStatusTime, ontInstance.getBase().createTypedLiteral(serviceStatusTime));
//		serviceStatusClassIdv.addProperty(srvcOf, serviceOf.getClassIndividual());
//	}
//	public void createOnt_ServiceStatus2()
//	{		
//		//serviceStatusClassIdv.addRDFType(myOnt.getIndividual(status.getCode()));
//		serviceStatusClassIdv.setPropertyValue(ontPsrvcStatusTime, ontInstance.getBase().createTypedLiteral(serviceStatusTime));
//		serviceStatusClassIdv.addProperty(ontPsrvcStatusOf, service.getClassIndividual());
//	}
	
	public void createPsrvcStatTime()
	{
		if(serviceStatusTime!=null)	
			serviceStatusClassIdv.setPropertyValue(ontPsrvcStatusTime, ontInstance.getBase().createTypedLiteral(serviceStatusTime));		
	}
	public void createPsrvcStatOf()
	{
		if(service!=null)
			serviceStatusClassIdv.addProperty(ontPsrvcStatusOf, service.getClassIndividual());
	}
	
	/////
	
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
		return serviceStatusClassIdv;
	}
	
	/////	
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id=id;
	}

	public String getTime() {
		return serviceStatusTime;
	}
	public void setTime(String serviceStatusTime) 
	{
		this.serviceStatusTime = serviceStatusTime;
	}

	public Service getServiceOf() {
		return service;
	}
	public void setServiceOf(Service serviceOf) 
	{
		this.service = serviceOf;
	}
	
	
	public State getStatus() {
		return status;
	}
	public void setStatus(State status) {
		this.status = status;
	}
}//class
