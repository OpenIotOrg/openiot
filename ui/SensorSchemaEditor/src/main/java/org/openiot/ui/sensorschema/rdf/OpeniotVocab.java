package org.openiot.ui.sensorschema.rdf;

import org.openiot.commons.util.PropertyManagement;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OpeniotVocab {

	//read data from the properties file
	  private static PropertyManagement propertyManagement = new PropertyManagement();
	  
	
	/**
	   * <p>
	   * The ontology model that holds the vocabulary terms
	   * </p>
	   */
	  public static OntModel m_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

	  /**
	   * <p>
	   * The namespace of the vocabulary as a string
	   * </p>
	   */
	  
	  
	  
	  public static final String NS = propertyManagement.getSensorSchemaEditorNS();
	  public static final String TITLE = propertyManagement.getSensorSchemaEditorTITLE();
	  public static final String URL = propertyManagement.getSensorSchemaEditorURL();
	  
	  public static final String LSM_URL = propertyManagement.getSensorSchemaEditorLSMSERVER();
	  public static final String LSM_METAGRAPH = propertyManagement.getSensorSchemaEditorLSMMETAGRAPH();
	  public static final String LSM_SPARQL_ENDPOINT = propertyManagement.getSensorSchemaEditorSPARQLENDPOINT();
	  
	  
	  
	  
	  //defined for SensorSchema
	  public static final ObjectProperty HAS_VALUE = m_model.
			  createObjectProperty(NS+"hasValue");
	  
	  public static final String LANG_N3 = "N3";
	  public static final String LANG_TURTLE = "TURTLE";
	  public static final String LANG_RDFXML = "RDF/XML";
	  public static final String LANG_RDFJSON = "RDF/JSON";
	  public static final String LANG_NTRIPLE = "N-TRIPLE";
	  public static final String LANG_RDFXML_ABBREV = "RDF/XML-ABBREV";
}
