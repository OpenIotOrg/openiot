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
import java.util.Set;

import org.openiot.commons.util.PropertyManagement;
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

public class Access {
	
	public static class Queries {
		public static ArrayList<Access> parseAccess(TupleQueryResult qres) {
			ArrayList<Access> accessList = new ArrayList<Access>();
			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					Access acs = new Access();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("accessID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							acs.setId(str);
							System.out.print("acs id: " + acs.getId() + " ");
						}
					}
					accessList.add(acs);
				}// while
				return accessList;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		private static String getNamespaceDeclarations() {
			StringBuilder declarations = new StringBuilder();
			declarations.append("PREFIX : <" + "http://openiot.eu/ontology/ns/" + "> \n");
			// declarations.append("PREFIX spt: <" +
			// "http://spitfire-project.eu/ontology/ns/" + "> \n");
			declarations.append("PREFIX rdf: <" + RDF.getURI() + "> \n");// http://www.w3.org/1999/02/22-rdf-syntax-ns#
			declarations.append("PREFIX rdfs: <" + RDFS.getURI() + "> \n");// http://www.w3.org/2000/01/rdf-schema#
			declarations.append("PREFIX xsd: <" + XSD.getURI() + "> \n");
			// declarations.append("PREFIX owl: <" + OWL.getURI() + "> \n");
			// declarations.append("PREFIX ssn: <" +
			// "http://purl.oclc.org/NET/ssnx/ssn#" + "> \n");
			// declarations.append("PREFIX dul: <" +
			// "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#" +
			// "> \n");
			// declarations.append("PREFIX oiot: <" +
			// "http://openiot.eu/ontology/ns/" + "> \n");
			// declarations.append("base oiot: <" +
			// "http://openiot.eu/ontology/ns/clouddb" + "> \n");
			declarations.append("\n");

			return declarations.toString();
		}

		public static String selectAllAccess(String graph) {
			StringBuilder update = new StringBuilder();
			update.append(getNamespaceDeclarations());

			String str = ("SELECT ?accessID from <" + graph + "> " 
							+ "WHERE " 
							+ "{"
							+ "?accessID rdf:type <http://openiot.eu/ontology/ns/Access> . " 
							+ "}");

			update.append(str);
			return update.toString();
		}

		public static String selectAccessByDescription(String graph,String desc) {
			StringBuilder update = new StringBuilder();
			update.append(getNamespaceDeclarations());

			String str = ("SELECT ?accessID from <"	+ graph	+ "> "
					+ "WHERE "
					+ "{"
					+ "?accessID <http://openiot.eu/ontology/ns/accessDescription> ?desc FILTER regex(?desc, \""+ desc + "\" )  . " 
					+ "}");

			update.append(str);
			return update.toString();
		}

		public static String selectAccessByUser(String graph,ArrayList<User> accessOfUserlist) {
			StringBuilder update = new StringBuilder();
			update.append(getNamespaceDeclarations());

			update.append("SELECT ?accessID from <" + graph + "> " + "WHERE " 
					+ "{");			
					for (int i = 0; i < accessOfUserlist.size(); i++) 
					{
						update.append("?userID <http://openiot.eu/ontology/ns/userOf> <"+ accessOfUserlist.get(i).getId() + "> )  . ");
					}
			update.append("}");

			return update.toString();
		}
	}// class

	private LSMSchema myOnt;
	private LSMSchema ontInstance;
	private String graph;
	private LSMTripleStore lsmStore;

	private Individual accessClassIdv;

	private OntClass ontClsAccessClass;
	private OntProperty ontPdescription;
	private OntProperty ontPaccessOf;
	// private OntProperty ontPAccessRights;

	private String id;
	private String description;
	// private String accessRights;
	private ArrayList<User> accessOfUserlist = new ArrayList<User>();

	public Access() {
	}

	public Access(LSMSchema myOnt, LSMSchema ontInstance, String graph, LSMTripleStore lsmStore) {
		
		this.myOnt = myOnt;
		this.ontInstance = ontInstance;
		this.graph = graph;
		this.lsmStore = lsmStore;

		initOnt_Access();
		createClassIdv();
	}

	public Access(String classIdvURL, LSMSchema myOnt, LSMSchema ontInstance, String graph,
			LSMTripleStore lsmStore) {
		
		this.myOnt = myOnt;
		this.ontInstance = ontInstance;
		this.graph = graph;
		this.lsmStore = lsmStore;

		id = classIdvURL;

		initOnt_Access();
		createClassIdv();
	}

	private void initOnt_Access() {
		ontClsAccessClass = myOnt.getClass("http://openiot.eu/ontology/ns/Access");
		ontPdescription = myOnt.createProperty("http://openiot.eu/ontology/ns/accessDescription");
		// ontPAccessRights =
		// myOnt.createProperty("http://openiot.eu/ontology/ns/accessRights");
		ontPaccessOf = myOnt.getProperty("http://openiot.eu/ontology/ns/accessOf");
	}

	private void createClassIdv() {
		if (id == null)
			accessClassIdv = ontInstance.createIndividual(ontClsAccessClass);
		else
			accessClassIdv = ontInstance.createIndividual(id, ontClsAccessClass);
	}

	public void createOnt_Access() {

		if (description != null)
			accessClassIdv.setPropertyValue(ontPdescription,
					ontInstance.getBase().createTypedLiteral(description));
		// if(accessRights!=null)
		// accessClassIdv.setPropertyValue(ontPAccessRights,
		// ontInstance.getBase().createTypedLiteral(accessRights));

		for (int i = 0; i < accessOfUserlist.size(); i++) {
			accessClassIdv.addProperty(ontPaccessOf, accessOfUserlist.get(i).getClassIndividual());
		}
	}

	public void updateOnt_Access() {
		accessClassIdv = ontInstance.createIndividual("nodeID://b41082", ontClsAccessClass);

		if (description != null) {
			accessClassIdv.setPropertyValue(ontPdescription,
					ontInstance.getBase().createTypedLiteral(description));
		}
		// accessClassIdv.setPropertyValue(ontPAccessRights,
		// ontInstance.getBase().createTypedLiteral(accessRights));

		for (int i = 0; i < accessOfUserlist.size(); i++) {
			accessClassIdv.addProperty(ontPaccessOf, accessOfUserlist.get(i).getClassIndividual());
		}
	}

	// //

	public LSMSchema getOnt() {
		return myOnt;
	}

	public LSMSchema getOntInstance() {
		return ontInstance;
	}

	public Individual getClassIndividual() {
		return accessClassIdv;
	}

	// //

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

	// public String getAccessRights() {
	// return accessRights;
	// }
	// public void setAccessRights(String accessRights) {
	// this.accessRights = accessRights;
	// }

	public ArrayList<User> getAccessOfUserList() {
		return accessOfUserlist;
	}

	public void setAccessOfUserList(ArrayList<User> accessOfUserlist) {
		this.accessOfUserlist = accessOfUserlist;
	}

	public void addAccessOfUserList(User accessOfUser) {
		accessOfUserlist.add(accessOfUser);
	}

}// class
