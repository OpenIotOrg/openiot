package org.openiot.ld4s.vocabulary;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class SptSnVocab {
	/**
	   * <p>
	   * The ontology model that holds the vocabulary terms
	   * </p>
	   */
	  public static OntModel m_model = ModelFactory.createOntologyModel(
			  OntModelSpec.OWL_MEM, null);

	  /**
	   * <p>
	   * The namespace of the vocabulary as a string
	   * </p>
	   */
	  public static final String NS = "http://spitfire-project.eu/ontology/ns/sn/";

	  /**
	   * <p>
	   * The namespace of the vocabulary as a string
	   * </p>
	   *
	   * @see #NS
	   */
	  public static String getURI() {
	    return NS;
	  }

	  /**
	   * <p>
	   * The namespace of the vocabulary as a resource
	   * </p>
	   */
	  public static final Resource NAMESPACE = m_model.createResource(NS);

	  /** PREFIX. */
	  public static final String PREFIX = "spt-sn";

	  // Vocabulary properties
	  // /////////////////////////


	  
	  // Vocabulary classes
	  // /////////////////////////

	  public static final OntClass ACTUATOR = m_model
	    .createClass(SptSnVocab.NS+"Actuator");
	  public static final OntClass TRANSDUCER = m_model
	    .createClass(SptSnVocab.NS+"Testbed");
	  public static final OntClass TESTBED = m_model
	    .createClass(SptSnVocab.NS+"Testbed");
	  public static final OntClass MIDDLEWARE = m_model
    .createClass(SptSnVocab.NS+"Middleware");
	  public static final OntClass CLUSTERHEAD = m_model
	    .createClass(SptSnVocab.NS+"ClusterHead");
	  public static final OntClass ROUTER = m_model
	    .createClass(SptSnVocab.NS+"Router");
	  public static final OntClass RFID = m_model
	    .createClass(SptSnVocab.NS+"Middleware");
	  public static final OntClass GPS = m_model
	    .createClass(SptSnVocab.NS+"Gps");
	  public static final OntClass ACCELEROMETER = m_model
	    .createClass(SptSnVocab.NS+"Accelerometer");
	  public static final OntClass MOTION_SENSOR = m_model
	    .createClass(SptSnVocab.NS+"MotionSensor");
	  public static final OntClass STATUS_LOGGER = m_model
	    .createClass(SptSnVocab.NS+"StatusLogger");
	  public static final OntClass TEMPERATURE_SENSOR = m_model
	    .createClass(SptSnVocab.NS+"TemperatureSensor");
	  public static final OntClass HUMIDITY_SENSOR = m_model
	    .createClass(SptSnVocab.NS+"HumiditySensor");
	  public static final OntClass LIGHT_SENSOR = m_model
	    .createClass(SptSnVocab.NS+"LightSensor");
	  public static final OntClass NOISE_SENSOR = m_model
	    .createClass(SptSnVocab.NS+"NoiseSensor");
	  public static final OntClass PRESSURE_SENSOR = m_model
	    .createClass(SptSnVocab.NS+"PressureSensor");
	  
	  // Vocabulary individuals
	  // /////////////////////////


}
