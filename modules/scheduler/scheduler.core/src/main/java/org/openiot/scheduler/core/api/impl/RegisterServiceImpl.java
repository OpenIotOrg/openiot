package org.openiot.scheduler.core.api.impl;


/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */




import lsm.beans.User;
import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.OSMO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModelSpec;


/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
public class RegisterServiceImpl {
	
	
	private OSDSpec osdSpec;
	
	final static Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);
	
	private String replyMessage= "unsuccessfuly";
	
	
	
	public RegisterServiceImpl (OSDSpec osdSpec){
		

		
		this.osdSpec = osdSpec;
		
		logger.debug("Recieved OSDSpec from User with userID: " + osdSpec.getUserID());
		
		
		//uncoment after test
		registerService();
//		insertUser();
		
	}

	
	
	/**
	 * 
	 */
	private void registerService() {
		
		
		
		for (OAMO oamo: osdSpec.getOAMO()){
			
			logger.debug("OAMO Description: {}  ID: {}",oamo.getDescription(), oamo.getId());
			logger.debug("OAMO Name: {}",oamo.getName());
			
			
			
			for (OSMO osmo : oamo.getOSMO()){
				
				
				logger.debug("OSMO Description: {}  ID: {}",osmo.getDescription(), osmo.getId());
				logger.debug("OSMO Name: {}",osmo.getName());
				
				
				logger.debug("OSMO Query: {}",osmo.getQueryRequest().getQuery());
					
				
				//keep going in....
				
				
				
				
			}
			
		}

		
		replyMessage= "successfuly";
	}
	
	
	//helper methods
	private void insertUser()
	{
		//Push data into LSM		
		User user = new User();
		user.setUsername("spet");
		user.setPass("spetlsm");
		
		LSMTripleStore lsmStore = new LSMTripleStore();
		lsmStore.setUser(user);
		
		LSMSchema myOnt  =  new  LSMSchema("savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
		LSMSchema myOntInstance = new LSMSchema();				
				
		org.openiot.scheduler.core.utils.lsmpa.entities.User usr = new org.openiot.scheduler.core.utils.lsmpa.entities.User(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
		usr.setName("a user");
		usr.setEmail("user@email");
		usr.setDescription("a user description");
//		usr.setServiceList(serviceList);
		
		usr.createClassIdv();
		usr.createPName();
		usr.createPemail();
		usr.createPdescription();
		
		logger.debug(myOntInstance.exportToTriples("TURTLE"));
	}
	
	/**
	 * @return String
	 */
	public String replyMessage(){
		
		
		return replyMessage;
		
		
	}
	
	

}
