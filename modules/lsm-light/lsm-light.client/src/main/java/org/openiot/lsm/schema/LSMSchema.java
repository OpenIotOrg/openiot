package org.openiot.lsm.schema;
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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.openiot.lsm.beans.User;
import org.openiot.lsm.server.LSMTripleStore;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;


/**
 * @author hoan
 *
 */
public class LSMSchema {
	private OntModel base;
	private String namespace;
	
	public LSMSchema(){	
		base = ModelFactory.createOntologyModel();
	}	
	
	/**
	 * 
	 * @param filePath
	 * @param spec. Encapsulates a description of the components of an ontology model, including the storage scheme, reasoner and language profile
	 * @param ontLanguage. "RDF/XML", "N-TRIPLE" or "N3", default is RDF/XML
	 * @return Ontology Model instance
	 */
	public LSMSchema(String filePath,OntModelSpec spec,String ontLanguage){		
		BufferedReader reader;
		try {			 
			reader = new BufferedReader(new FileReader(filePath));
			base = ModelFactory.createOntologyModel(spec);						
			base.read(reader,null,ontLanguage);
			reader.close();
		}catch(Exception e){
			System.out.println("File not found");
		}
	}
	
	/**
	 * load ontology from URL
	 * @param ontURL
	 * @param spec. Encapsulates a description of the components of an ontology model, including the storage scheme, reasoner and language profile
	 * @return
	 */
	public LSMSchema(String ontURL,OntModelSpec spec){
		try{
			base = ModelFactory.createOntologyModel(spec);
			base.read(ontURL);			
		}catch(Exception e){
			System.out.println("File not found");
		}
	}
	
	public LSMSchema(OntModelSpec spec){
		try{
			base = ModelFactory.createOntologyModel(spec);	
		}catch(Exception e){
			System.out.println("File not found");
		}
	}
	
	
	public OntModel getBase() {
		return base;
	}

	public void setBase(OntModel base) {
		this.base = base;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void addPrefix(String prefix,String url){
		this.base.setNsPrefix(prefix, url);	
	}
	
	public OntClass getClass(String classNameURL){
		OntClass cl = null;
		try{
			cl = base.getOntClass(classNameURL);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Class not found");
		}
		return cl;
	}
	
	public OntClass createClass(String classNameURL){
		OntClass cl = null;
		try{
			cl = base.createClass(classNameURL);
		}catch(Exception e){
			e.printStackTrace();
		}
		return cl;
	}
	
	public OntProperty createProperty(String propertyNameURL){
		return base.createOntProperty(propertyNameURL);
	}
	
	public OntProperty getProperty(String propertyNameURL){
		return base.getOntProperty(propertyNameURL);
	}

	public void addProperty(String propertyNameURL){
		base.createProperty(propertyNameURL);
	}
	
	public Individual createIndividual(String indvURL,OntClass cl){
		return base.createIndividual(indvURL, cl);
	}
	
	public Individual getIndividual(String indvURL){
		return base.getIndividual(indvURL);
	}
	
	public Individual createIndividual(OntClass cl){
		return base.createIndividual(cl);
	}
	
	public void addValue(Individual indv,OntProperty opt,Object value){
		indv. addLiteral(opt, value);
	}
	
	public String exportToTriples(String lang){
		OutputStream out = new ByteArrayOutputStream();
		base.write(out,lang);	
//		System.out.println(out.toString());
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toString();
	}
	
	public void exportOntologyToFile(String filePath){
		OutputStream out;
		try {
			out = new FileOutputStream(filePath);
			base.write(out,"RDF/XML");	
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void uploadOntology(String fileName){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LSMSchema schema = new LSMSchema("http://localhost:8080/lsmservlet/download?project=openiot&filename=example.owl",OntModelSpec.OWL_DL_MEM);		
		
//		LSMSchema schema = new LSMSchema(OntModelSpec.OWL_DL_MEM);		
//		schema.addPrefix("openIoT","http://schema.deri.ie/OpenIoT/");
//		schema.addPrefix("resources","http://schema.deri.ie/resources/");
//		schema.addPrefix("prorperty","http://schema.deri.ie/OpenIoT/Property/");			
//		OntClass cl = schema.createClass("openIoT:queryRequest");
//		OntProperty op = schema.createProperty("property:queryRequestID");		
//		System.out.println(schema.exportToTriples());
//		schema.exportOntologyToFile("example.owl");
		
//		OntClass cl = schema.getClass("openIoT:queryRequest");
//		OntProperty op = schema.getProperty("property:queryRequestID");		
		
//		Individual idv = schema.createIndividual("resources:request1", cl);
//		Literal li = schema.getBase().createTypedLiteral("1234");
//		idv.setPropertyValue(op, li);
//		System.out.println(schema.exportToTriples("N-TRIPLE"));
		
//		LSMSchema data = new LSMSchema();
//		Individual idv = data.createIndividual(cl);
//		Literal li = data.getBase().createTypedLiteral("1234");
//		idv.addLiteral(op,"hoan");
//		idv.setPropertyValue(op, li);
//		System.out.println(data.exportToTriples("N-TRIPLE"));	
		
		User user = new User();
        user.setUsername("admin");
        user.setPass("admin");        
        
        LSMTripleStore lsmStore = new LSMTripleStore();
        lsmStore.setUser(user);
//        lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/sensordata", schema.exportToTriples("N-TRIPLE"));
        lsmStore.uploadSchema(schema, "example.owl");
	}

}
