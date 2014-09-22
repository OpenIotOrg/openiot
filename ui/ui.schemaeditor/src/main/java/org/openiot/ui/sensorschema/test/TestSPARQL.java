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
package org.openiot.ui.sensorschema.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

import org.openiot.ui.sensorschema.utils.SesameSPARQLClient;
import org.openiot.ui.sensorschema.utils.SsnVocab;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class TestSPARQL {
	
	Properties prop = new Properties();
	OutputStream output = null;
	
	public static void main(String[] str){
		TestSPARQL test = new TestSPARQL();
		try {
			test.checkSensorTypeRegistration("Test");
			System.exit(0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean checkSensorTypeRegistration(String sensorTypeName) throws FileNotFoundException{
		
		String sensorURI =null, metagraphURI = null, lsmURl = null;
		
		output = new FileOutputStream("openiot.properties");
		 
		// set the properties value
		sensorURI = prop.getProperty("ide.core.navigation.sensorSchemaEditor.ns", "http://services.openiot.eu/resources#");
		metagraphURI = prop.getProperty("ide.core.nvaigation.lsm.openiotMetaGraph", "http://lsm.deri.ie/OpenIoT/guest/sensormeta#");
		lsmURl = prop.getProperty("ide.core.navigation.lsm.sparql.endpoint","http://lsm.deri.ie/sparql");
 		
		sensorURI = sensorURI + sensorTypeName;				
		
		StringBuilder query = new StringBuilder();		
		query.append("SELECT * from");
		query.append(" <" + metagraphURI + ">"); //sensor metagraph from configuration file
		query.append(" WHERE" );
		query.append(" {");
		query.append(" <" + sensorURI + ">"); //subject
		query.append(" ?o"); //object
		query.append(" ?p"); //predicate
		query.append(" }");
		
		//System.out.println("SPARQL Query" + query.toString());
		SesameSPARQLClient sparqlclient;
		try {
			sparqlclient = new SesameSPARQLClient(lsmURl);
			TupleQueryResult qres = sparqlclient.sparqlToQResult(query.toString());
			while (qres.hasNext()){
				
				BindingSet b = qres.next();				
				Set names = b.getBindingNames();				
				String text = b.getValue("o").stringValue();				
				//System.out.println((SsnVocab.MEASUREMENT_CAPAB).toString());
				if (text.contains((SsnVocab.MEASUREMENT_CAPAB).toString()))
					System.out.println(b.getValue((String) "p"));
				//System.out.println(b.getValue((String) "o"));
				
					
					
			}
			return true;
			
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		
		
		return false;
		
	}
	
	public void sparqlQuery(){
//		String queryString = " select ?s ?property ?freqval where { ?s ?o <http://purl.oclc.org/NET/ssnx/ssn#Sensor> ." +
//								"?s  <http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementCapability> ?mc ." +
//								"?mc <http://purl.oclc.org/NET/ssnx/ssn#forProperty> ?property ." +
//								"?mc <http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementProperty> ?mpa ." +
//								"?mpa a <http://purl.oclc.org/NET/ssnx/ssn#Frequency> ." +
//								"?mpa  <http://services.openiot.eu/resources#hasValue>  ?freqval} " ;

		
		String queryString = " select ?s ?property ?freqval where { ?s ?o <http://purl.oclc.org/NET/ssnx/ssn#Sensor> ." +
				"?s  <http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementCapability> ?mc ." +
				"?mc <http://purl.oclc.org/NET/ssnx/ssn#forProperty> ?property ." +
				"?mc <http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementProperty> ?mpa ." +
				"?mpa a <http://purl.oclc.org/NET/ssnx/ssn#Frequency> ." +				
				"?mpa  <http://services.openiot.eu/resources#hasValue>  ?freqval " +	
				"} " ;
		//"
		//"?out  <http://services.openiot.eu/resources#hasValue>  ?accurval " +
		//"?s   <http://purl.oclc.org/NET/ssnx/ssn#observes>  ?property} " ;
		//String queryString = " select * where { ?s <http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementCapability> ?p}" ;
		
		
//		Query query = QueryFactory.create(queryString);
//		QueryExecution qexec = QueryExecutionFactory.create(query, ontModel) ;
//		  try {
//		    ResultSet results = qexec.execSelect() ;
//		    ResultSetFormatter.out(System.out, results, query) ;
//		    for ( ; results.hasNext() ; )
//		    {
////		      System.out.println(results.toString());
//		    	QuerySolution soln = results.nextSolution() ;
//		      RDFNode x = soln.get("varName") ;       // Get a result variable by name.
//		      Resource r = soln.getResource("VarR") ; // Get a result variable - must be a resource
//		      Literal l = soln.getLiteral("VarL") ;   // Get a result variable - must be a literal
//		    }
//		  } finally { qexec.close() ; }
		
	}
}
