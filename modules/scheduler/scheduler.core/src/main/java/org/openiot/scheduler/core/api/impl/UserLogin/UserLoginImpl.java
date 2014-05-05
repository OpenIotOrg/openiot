package org.openiot.scheduler.core.api.impl.UserLogin;

import java.util.ArrayList;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.scheduler.core.api.impl.UserRegister.UserRegisterImpl;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginImpl 
{
	final static Logger logger = LoggerFactory.getLogger(UserLoginImpl.class);
	
	private String schedulerLsmFunctionalGraph;
	//
	private String userMail;
	private String userPasw;
	
	private String replyMessage= "";
	
	public UserLoginImpl(String userMail,String pasw)
	{
		PropertyManagement propertyManagement = new PropertyManagement();
		schedulerLsmFunctionalGraph = propertyManagement.getSchedulerLsmFunctionalGraph();
		
		this.userMail = userMail;
		this.userPasw = pasw;
				
//		logger.debug("received: "+this.userMail);		

		loginUser();
	}
	
	public String getReplyMessage()
	{	
		return replyMessage;
	}
	
	private void loginUser()
	{
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. Error checking if mail exists",e);
			replyMessage= "error checking if mail exists, cannot init repository";
			return;
		}
		
		
		
		//check that user entered correct email
		TupleQueryResult qres = sparqlCl.sparqlToQResult(
				org.openiot.scheduler.core.utils.lsmpa.entities.User.Queries.selectUserByEmail(schedulerLsmFunctionalGraph,this.userMail));
		ArrayList<org.openiot.scheduler.core.utils.lsmpa.entities.User> usrEnt =  
				org.openiot.scheduler.core.utils.lsmpa.entities.User.Queries.parseUserData(qres);
		
		if (usrEnt.size()==0)
		{		
			replyMessage="user mail not found";
		}
		else
		{
			if (usrEnt.get(0).getPasswd().equals(this.userPasw))
				replyMessage=usrEnt.get(0).getId();				
			else
				replyMessage="wrong password";			
		}
	}
}
