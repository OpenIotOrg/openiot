package org.openiot.scheduler.core.api.impl.UserRegister;

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

import javax.ws.rs.QueryParam;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.scheduler.core.api.impl.RegisterService.RegisterServiceImpl;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import lsm.beans.User;
import org.openiot.lsm.schema.LSMSchema;
import org.openiot.lsm.server.LSMTripleStore;

import com.hp.hpl.jena.ontology.OntModelSpec;

/**
 * 
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 *
 */

public class UserRegisterImpl 
{	
	final static Logger logger = LoggerFactory.getLogger(UserRegisterImpl.class);
	
	private String SchedulerLsmFunctionalGraph;
	private String schedulerLsmUserName;
	private String schedulerLsmPassword;
	private String lsmDeriServer;
	//
	private String name;
	private String mail;
	private String description;
	private String passwd;
	
	private String replyMessage= "";

	//constructor
	public UserRegisterImpl(String userName,String userMail,String userDesc,String passwd)
	{		
		PropertyManagement propertyManagement = new PropertyManagement();
		SchedulerLsmFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
		schedulerLsmUserName = propertyManagement.getSchedulerLsmUserName();
		schedulerLsmPassword = propertyManagement.getSchedulerLsmPassword();
		lsmDeriServer = propertyManagement.getSchedulerLsmRemoteServer();
		
		this.name=userName;
		this.mail=userMail;
		this.description=userDesc;
		this.passwd=passwd;
		
		//logger.debug("String userName,String userMail,String userDesc,String passwd);		

		registerUser();
	}
	
	
	public String getReplyMessage()
	{	
		return replyMessage;
	}
	
	private void registerUser()
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. Error checking if mail already exists",e);
			replyMessage= "error checking if mail already exists";
			return;
		}
		
		
		
		//check if user with same mail exists
		TupleQueryResult qres = sparqlCl.sparqlToQResult(
				org.openiot.scheduler.core.utils.lsmpa.entities.User.Queries.selectUserByEmail(SchedulerLsmFunctionalGraph,this.mail));
		ArrayList<org.openiot.scheduler.core.utils.lsmpa.entities.User> usrEnt =  
				org.openiot.scheduler.core.utils.lsmpa.entities.User.Queries.parseUserData(qres);
		
		if (usrEnt.size()==0)
		{
//			User user = new User();
//			user.setUsername(schedulerLsmUserName);
//			user.setPass(schedulerLsmPassword);
			
			LSMTripleStore lsmStore = new LSMTripleStore(lsmDeriServer);
//			lsmStore.setUser(user);		
			
			LSMSchema myOnt  =  new  LSMSchema (OntModelSpec.OWL_DL_MEM);
			LSMSchema myOntInstance = new LSMSchema();
			
			org.openiot.scheduler.core.utils.lsmpa.entities.User userEnt = new org.openiot.scheduler.core.utils.lsmpa.entities.User(myOnt, myOntInstance,SchedulerLsmFunctionalGraph,lsmStore);
			userEnt.setName(this.name);
			userEnt.setEmail(this.mail);
			userEnt.setDescription(this.description);
			userEnt.setPasswd(this.passwd);
			//
			userEnt.createClassIdv();
			userEnt.createPName();
			userEnt.createPemail();
			userEnt.createPdescription();
			userEnt.createPpasswd();
			
			logger.debug(myOntInstance.exportToTriples("TURTLE"));
//			boolean ok = 
			lsmStore.pushRDF(SchedulerLsmFunctionalGraph,myOntInstance.exportToTriples("N-TRIPLE"));

			
//			if(ok){
				qres = sparqlCl.sparqlToQResult(
						org.openiot.scheduler.core.utils.lsmpa.entities.User.Queries.selectUserByEmail(SchedulerLsmFunctionalGraph,this.mail));
				//parse userdata list should always contain one element
				org.openiot.scheduler.core.utils.lsmpa.entities.User usrEntity =  
						org.openiot.scheduler.core.utils.lsmpa.entities.User.Queries.parseUserData(qres).get(0);
				
				replyMessage= usrEntity.getId();
//			}
//			else{
//				replyMessage= "register user error";
//			}
		}
		else
		{
			replyMessage= "mail already exists";
		}		
	}	
}
