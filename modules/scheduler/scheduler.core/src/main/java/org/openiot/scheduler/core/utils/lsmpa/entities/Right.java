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

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

import lsm.schema.LSMSchema;

public class Right 
{
	private LSMSchema  myOnt;	
	private LSMSchema  ontInstance;
	
	private Individual rightClassIdv;
	
	private OntClass ontClsRightClass;	
	private OntProperty ontPdescription;
	
	private String id;
	private String description;
	
	public Right(LSMSchema  myOnt,LSMSchema  ontInstance)
	{
		this.myOnt=myOnt;
		this.ontInstance=ontInstance;
		
		initOnt_Right();
	}
	
	private void initOnt_Right()
	{
		ontClsRightClass = myOnt.getClass("http://openiot.eu/ontology/ns/Right");
		ontPdescription = myOnt.createProperty("http://openiot.eu/ontology/ns/rightDescription");					
	}

		
	public void createOnt_Right()
	{		
		rightClassIdv = ontInstance.createIndividual(ontClsRightClass);		
		rightClassIdv.setPropertyValue(ontPdescription, ontInstance.getBase().createTypedLiteral(description));		
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}//class
