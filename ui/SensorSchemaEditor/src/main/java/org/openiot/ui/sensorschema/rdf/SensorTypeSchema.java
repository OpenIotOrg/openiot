package org.openiot.ui.sensorschema.rdf;

import java.io.StringWriter;
import java.util.Date;
import java.util.Set;

import org.openiot.lsm.server.LSMTripleStore;
import org.openiot.ui.sensorschema.utils.SesameSPARQLClient;
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
	  	ontModel.setNsPrefix("ssn", SsnVocab.NS);
	  	
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
		String proertyURI = OpeniotVocab.NS.replace("#", "/") + propertyName;
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
    
	
	//code to pusht he rdf into LSM using the LSM client
	public boolean pushRdftoLSM(String rdf){
        LSMTripleStore lsmStore = new LSMTripleStore(OpeniotVocab.LSM_URL);
        logger.info("Connecting to LSM and Storing data to graph: "+OpeniotVocab.LSM_URL + ";" + OpeniotVocab.LSM_METAGRAPH);        
        boolean success = lsmStore.pushRDF(OpeniotVocab.LSM_METAGRAPH,rdf);
        return success;
	}
	
	
	public boolean checkSensorTypeRegistration(String sensorTypeName){
		
		String sensorURI =null, metagraphURI = null;
		
		sensorURI = OpeniotVocab.NS + sensorTypeName;
		metagraphURI = OpeniotVocab.LSM_METAGRAPH;
		
		
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
		
		try {
			SesameSPARQLClient sparqlclient = new SesameSPARQLClient();
			TupleQueryResult qres = sparqlclient.sparqlToQResult(query.toString());
			if (qres.hasNext())
				return true;
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		
		return false;
		
	}

	public void checkProperty(){
		//processing the result
//		while (qres.hasNext()) {
//			BindingSet b = qres.next();
//			Set names = b.getBindingNames();				
//
//			for (Object n : names) {
//				System.out.println(b.getValue((String) n));
//			}
//
//		}// while

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
		
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, ontModel) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    ResultSetFormatter.out(System.out, results, query) ;
//		    for ( ; results.hasNext() ; )
//		    {
////		      System.out.println(results.toString());
//		    	QuerySolution soln = results.nextSolution() ;
//		      RDFNode x = soln.get("varName") ;       // Get a result variable by name.
//		      Resource r = soln.getResource("VarR") ; // Get a result variable - must be a resource
//		      Literal l = soln.getLiteral("VarL") ;   // Get a result variable - must be a literal
//		    }
		  } finally { qexec.close() ; }
		
	}
	
	private long generateTimeStamp(){
		long lDateTime = new Date().getTime();
		return lDateTime;
	}

}
