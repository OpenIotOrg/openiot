package com.lsm.testschema.gui;

import com.lsm.testschema.model.*;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.swing.JFrame;

import org.openrdf.query.TupleQueryResult;

import lsm.beans.User;
import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.lsm.testschema.model.ServiceStatus.State;
import com.lsm.testschema.queryhelper.SesameSPARQLClient;


public class Main 
{
	public static void runGUI()
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
//					MainForm window = new MainForm();
//					window.getFrame().setVisible(true);
					MainForm.getMainForm().setVisible(true);
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public static boolean delete()
	{
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);
		
		return lsmStore.deleteTriples("http://lsm.deri.ie/OpenIoT/testSchema#");
	}
	
	public static void test1()
	{
		//Push data into LSM		
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);
		
		LSMSchema myOnt  =  new  LSMSchema("files\\savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
		LSMSchema myOntInstance = new LSMSchema();
		
		
		
		Access acs = new Access(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		acs.setDescription("access description");
		acs.updateOnt_Access();
		
		com.lsm.testschema.model.User usr = new com.lsm.testschema.model.User(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		usr.setName("a user");
		usr.setEmail("email");
		usr.setDescription("description");
//		usr.setServiceList(serviceList);
		usr.setAccess(acs);
		usr.createOnt_USer();
		
		acs.addAccessOfUserList(usr);
		acs.updateOnt_Access();
		
		
//		Service srvc = new Service(myOnt, myOntInstance);
//		srvc.setName("my service 553");
//		srvc.setDescription("my service description");
//		srvc.setQueryString("SELECT * from");		
		//srvc.setUser(usr);
//		srvc.createOnt_Service();
		
//		ServiceStatus srvcStatus = new ServiceStatus(myOnt,myOntInstance, "1", State.INITIALIZING);
//		srvcStatus.setServiceStatusTime(new Date().toGMTString());
//		srvcStatus.setServiceOf(srvc);
		//srvcStatus.createOnt_ServiceStatus2();
				
//		ServiceStatus srvcStatus2 = new ServiceStatus(myOnt,myOntInstance, "2", State.ENABLED);
//		srvcStatus2.setServiceStatusTime(new Date().toGMTString());
//		srvcStatus2.setServiceOf(srvc);
		//srvcStatus2.createOnt_ServiceStatus2();
				
		//srvc.addServiceStatus(srvcStatus);
		//srvc.addServiceStatus(srvcStatus2);
		//srvc.createOnt_Service();
		
		
		System.out.println(myOntInstance.exportToTriples("TURTLE"));
		
		//boolean pushedOK = lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/testSchema#",myOntInstance.exportToTriples("N-TRIPLE"));
		//System.out.println(pushedOK);
//				
//				ServiceStatus srvcStatus3 = new ServiceStatus(myOnt,myOntInstance, "3", State.SUSPENDED);
//				srvcStatus3.setServiceStatusTime(new Date().toGMTString());
//				srvcStatus3.setServiceOf(srvc);
//				srvcStatus3.createOnt_ServiceStatus2();
//				
//				srvc.addServiceStatus(srvcStatus3);
//				srvc.createOnt_Service();
//						
		//System.out.println(myOntInstance.exportToTriples("TURTLE"));			
		//lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/testSchema#",ontInstance.exportToTriples("N-TRIPLE"));
	}
	
	public static void insertAccessAndUser()
	{
		//Push data into LSM		
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);
		
		LSMSchema myOnt  =  new  LSMSchema("files\\savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
		LSMSchema myOntInstance = new LSMSchema();				
		
		
		Access acs = new Access(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		acs.setDescription("access description");
		
		com.lsm.testschema.model.User usr = new com.lsm.testschema.model.User(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		usr.setName("b user");
		usr.setEmail("a email");
		usr.setDescription("a description");
//		usr.setServiceList(serviceList);
		usr.setAccess(acs);				
		
		acs.addAccessOfUserList(usr);		
		
		acs.createOnt_Access();
		usr.createOnt_USer();
		
		
		System.out.println(myOntInstance.exportToTriples("TURTLE"));
	}
	public static void insertAccess()
	{
		//Push data into LSM		
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);
		
		LSMSchema myOnt  =  new  LSMSchema("files\\savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
		LSMSchema myOntInstance = new LSMSchema();				
		
		
		Access acs = new Access(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		acs.setDescription("access description");
		acs.createOnt_Access();
		
		System.out.println(myOntInstance.exportToTriples("TURTLE"));
	}
	
	public static void insertUserAndAccess()
	{
		//Push data into LSM		
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);
		
		LSMSchema myOnt  =  new  LSMSchema("files\\savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
		LSMSchema myOntInstance = new LSMSchema();				
		
		com.lsm.testschema.model.User usr = new com.lsm.testschema.model.User(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		usr.setName("b user");
		usr.setEmail("a email");
		usr.setDescription("a description");
//		usr.setServiceList(serviceList);			
		
		Access acs = new Access("nodeID://b41082",myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		//acs.setDescription("access description");
		acs.addAccessOfUserList(usr);		
		
		usr.setAccess(acs);

		usr.createOnt_USer();
		acs.createOnt_Access();
		
		
		System.out.println(myOntInstance.exportToTriples("TURTLE"));
	}
	public static void insertUser()
	{
		//Push data into LSM		
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);
		
		LSMSchema myOnt  =  new  LSMSchema("files\\savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
		LSMSchema myOntInstance = new LSMSchema();				
				
		com.lsm.testschema.model.User usr = new com.lsm.testschema.model.User(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		usr.setName("b user");
		usr.setEmail("a email");
		usr.setDescription("a description");
//		usr.setServiceList(serviceList);
		
		usr.createOnt_USer();
		
		System.out.println(myOntInstance.exportToTriples("TURTLE"));
	}
			
	public static void main(String[] args) 
	{
		runGUI();
//		delete();
//		test1();
//		insertAccessAndUser();
//		insertAccess();
//		insertUserAndAccess();
//		insertUser();
//		insertUserService();
		
		//SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		//TupleQueryResult qres = sparqlCl.sparqlToQResult(Access.Queries.selectAccessByDescription("desc"));				
		//List<Access> acs = Access.Queries.parseAccess(qres);
		//System.out.println();
	}
}//class
