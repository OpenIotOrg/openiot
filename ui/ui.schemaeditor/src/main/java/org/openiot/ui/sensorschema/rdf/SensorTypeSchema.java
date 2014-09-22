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
 * 
 * 	   @author Prem Jayaraman
 */
package org.openiot.ui.sensorschema.rdf;

import java.io.StringWriter;
import java.util.Date;
import java.util.Set;

import org.openiot.lsm.server.LSMTripleStore;
import org.openiot.ui.sensorschema.register.AbstractSensorRegistrarFactory;
import org.openiot.ui.sensorschema.utils.OpeniotVocab;
import org.openiot.ui.sensorschema.utils.SesameSPARQLClient;
import org.openiot.ui.sensorschema.utils.SsnVocab;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Hello world!
 *
 */
public class SensorTypeSchema
{
	private static final transient Logger logger = LoggerFactory.getLogger(SensorTypeSchema.class);
	
	private OntModel ontModel = null;
    private OntClass sensortype;    
    
 // ============READING PROPERIES=========================

 		
	public SensorTypeSchema(){
		
		initialise();
		
	}
	
	public void initialise(){
        
		//code to read proertyies files and add OpenIoT schema information
		
	  	// Create an empty ontology model
	  	ontModel = ModelFactory.createOntologyModel();
	  	ontModel.setNsPrefix("ssn", SsnVocab.SSN_NS);
	  	
	  	//openiot namespace	  	
	  	//Ontology onto = ontModel.createOntology(BASEURI);	  		  	
  	}
	
	public void defineSensorType(String sensorTypeName)
    {
		// Create The new SensorType Class
    	sensortype = ontModel.createClass(OpeniotVocab.NS + sensorTypeName);
    	
    	//add this to be a subclass of SSN Sensor
    	SsnVocab.SENSOR.addSubClass(sensortype);
    }
	public void addObservedProperty(String propertyName, String accuracy, String frequency){
    	
    	//create a property and the corresponding MeasurementCapability 		
		//using default openiot uri --> but if properties are defined else where???
		//String proertyURI = OpeniotVocab.NS.replace("#", "/") + propertyName;
		
		//user is responsible to define properties as URI. Interface has a URI checker
		String proertyURI = propertyName;
    	sensortype.addProperty(SsnVocab.OBSERVES, proertyURI);
    	Individual mct = measurementCapability(proertyURI ,accuracy, frequency);
    	sensortype.addProperty(SsnVocab.MEASUREMENT_CAPAB, mct);
    }
    
	
	public String printRDF(){
		  
    	String str_rdfData = serializeRDF("RDF/XML");
    	System.out.println(str_rdfData);
    	return str_rdfData;
    	//sparqlQuery();

	}
   
	public String serializeRDF(String lang){
		StringWriter sw=new StringWriter();
		ontModel.write(sw, lang);
		return sw.toString();
	 }
	
	public Individual measurementCapability(String propertyURI, String accur, String freq){
		long timestamp = generateTimeStamp();
		
		Individual mct1 = ontModel.createIndividual(OpeniotVocab.NS + "mct" + timestamp, SsnVocab.MEASUREMENT_CAPABILITY);
		Individual accuracy = ontModel.createIndividual(OpeniotVocab.NS + "accuracy" + timestamp, SsnVocab.ACCURACY);
		Individual frequency = ontModel.createIndividual(OpeniotVocab.NS + "frequency" + timestamp, SsnVocab.FREQUENCY);
				
    	mct1.addProperty(SsnVocab.HAS_MEASUREMENT_PROPERTY, accuracy);
    	mct1.addProperty(SsnVocab.HAS_MEASUREMENT_PROPERTY, frequency);    
    	mct1.addProperty(SsnVocab.FOR_PROPERTY, propertyURI);
    	
    	//add the value to freq and accuracy
    	accuracy.addLiteral(OpeniotVocab.HAS_VALUE, accur);
    	frequency.addLiteral(OpeniotVocab.HAS_VALUE, freq);

    	return mct1;
	}
	
	
	private long generateTimeStamp(){
		long lDateTime = new Date().getTime();
		return lDateTime;
	}

}
