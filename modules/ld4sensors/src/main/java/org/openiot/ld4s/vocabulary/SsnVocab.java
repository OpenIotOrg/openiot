package org.openiot.ld4s.vocabulary;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class SsnVocab {
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
	  public static final String NS = "http://purl.oclc.org/NET/ssnx/ssn#";

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
	  public static final String PREFIX = "ssn";

	  // Vocabulary properties
	  // /////////////////////////

	  public static final ObjectProperty IN_CONDITION = m_model
      .createObjectProperty(NS+"inCondition");
	  public static final ObjectProperty FOR_PROPERTY = m_model
      .createObjectProperty(NS+"forProperty");
	  public static final ObjectProperty HAS_MEASUREMENT_PROPERTY = m_model
      .createObjectProperty(NS+"hasMeasurementProperty");
	  public static final ObjectProperty HAS_MEASUREMENT_CAPABILITIES = m_model
      .createObjectProperty(NS+"hasMeasurementCapabilities");
	  public static final ObjectProperty ATTACHED_SYSTEM = m_model
      .createObjectProperty(NS+"attachedSystem");
	  public static final ObjectProperty MEASUREMENT_CAPAB = m_model
      .createObjectProperty(NS+"hasMeasurementCapability");
	  public static final ObjectProperty IMPLEMENTS = m_model
      .createObjectProperty(NS+"implements");
	  public static final ObjectProperty IN_DEPLOYMENT = m_model
      .createObjectProperty(NS+"inDeploment");
	  public static final ObjectProperty FEATURE_OF_INTEREST = m_model
      .createObjectProperty(NS+"featureOfInterest");
	  
	  // Vocabulary classes
	  // /////////////////////////

	  public static final OntClass SENSITIVITY = m_model
      .createClass(SsnVocab.NS+"Sensitivity");
	  public static final OntClass RESPONSE_TIME = m_model
      .createClass(SsnVocab.NS+"ResponseTime");
	  public static final OntClass RESOLUTION = m_model
      .createClass(SsnVocab.NS+"Resolution");
	  public static final OntClass PRECISION = m_model
      .createClass(SsnVocab.NS+"Precision");
	  public static final OntClass LATENCY = m_model
      .createClass(SsnVocab.NS+"Latency");
	  public static final OntClass FREQUENCY = m_model
      .createClass(SsnVocab.NS+"Frequency");
	  
	  public static final OntClass DRIFT = m_model
      .createClass(SsnVocab.NS+"Drift");
	  public static final OntClass DETECTION_LIMIT = m_model
      .createClass(SsnVocab.NS+"DetectionLimit");
	  public static final OntClass ACCURACY = m_model
      .createClass(SsnVocab.NS+"Accuracy");
	  public static final OntClass SELECTIVITY  = m_model
      .createClass(SsnVocab.NS+"Selectivity");
	  public static final OntClass MEASUREMENT_PROPERTY = m_model
      .createClass(SsnVocab.NS+"MeasurementProperty");
	  public static final OntClass MEASUREMENT_CAPABILITY= m_model
      .createClass(SsnVocab.NS+"MeasurementCapability");
	  public static final OntClass DEVICE = m_model
      .createClass(SsnVocab.NS+"Device");
	  public static final OntClass PROPERTY = m_model
      .createClass(SsnVocab.NS+"Property");
	  public static final OntClass PLATFORM = m_model
      .createClass(SsnVocab.NS+"Platform");
	  public static final OntClass SENSING_DEVICE = m_model
	      .createClass(SsnVocab.NS+"SensingDevice");
	  // Vocabulary individuals
	  // /////////////////////////

	public static final OntClass SENSOR = m_model
		      .createClass(SsnVocab.NS+"Sensor");
}
