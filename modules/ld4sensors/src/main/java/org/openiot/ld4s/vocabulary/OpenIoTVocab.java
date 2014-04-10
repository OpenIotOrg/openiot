package org.openiot.ld4s.vocabulary;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.TransitiveProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Vocabulary definitions from
 * http://openiot.eu/ontology/ns
 *
 * @author Myriam Leggieri
 */
public class OpenIoTVocab {
	/**
	 * <p>
	 * The ontology model that holds the vocabulary terms
	 * </p>
	 */
	public static OntModel m_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);

	/**
	 * <p>
	 * The namespace of the vocabulary as a string
	 * </p>
	 */
	public static final String NS = "http://http://openiot.eu/ontology/ns/";

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
	public static final String PREFIX = "oiot";

	// Vocabulary properties
	// /////////////////////////

	public static final TransitiveProperty CONTEXT_OF = m_model
			.createTransitiveProperty(NS+"contextOf");
	public static final TransitiveProperty CONTEXT = m_model
			.createTransitiveProperty(NS+"context");

	public static final TransitiveProperty MOBILITY_OF = m_model
			.createTransitiveProperty(NS+"mobilityOf");
	public static final TransitiveProperty MOBILITY = m_model
			.createTransitiveProperty(NS+"mobility");
	
	
	 public static final ObjectProperty UNIT = m_model
		      .createObjectProperty(NS+"unit");
	 public static final ObjectProperty IS_OBSERVED_VALUE_OF = m_model
		      .createObjectProperty(NS+"isObservedValueOf");
	 public static final ObjectProperty FOI = m_model
		      .createObjectProperty(NS+"foi");
	 
	 
	 public static final DatatypeProperty VALUE = m_model
		      .createDatatypeProperty(NS+"value");
	 public static final DatatypeProperty MOBILITY_TIME = m_model
		      .createDatatypeProperty(NS+"mobilityTime");
	 public static final DatatypeProperty MOBILITY_START= m_model
		      .createDatatypeProperty(NS+"mobilityStart");
	 public static final DatatypeProperty MOBILITY_END = m_model
		      .createDatatypeProperty(NS+"mobilityEnd");
	
	

	// Vocabulary classes
	// /////////////////////////


		public static final OntClass PLACE = m_model
				.createClass(OpenIoTVocab.NS+"Place");
		
	public static final OntClass MOBILITY_CONTEXT = m_model
			.createClass(OpenIoTVocab.NS+"MobilityContext");
	
		public static final OntClass TEMPERATURE = m_model
		.createClass(OpenIoTVocab.NS+"Temperature");
	public static final OntClass AIR_TEMPERATURE = m_model
			.createClass(OpenIoTVocab.NS+"AirTemperature");
	public static final OntClass ATMOSPHERE_HUMIDITY = m_model
			.createClass(OpenIoTVocab.NS+"AtmosphereHumidity");
	public static final OntClass ATMOSPHERE_PRESSURE = m_model
			.createClass(OpenIoTVocab.NS+"AtmospherePressure");
	public static final OntClass ATMOSPHERE_VISIBILITY = m_model
			.createClass(OpenIoTVocab.NS+"AtmosphereVisibility");
	public static final OntClass WIND_CHILL = m_model
			.createClass(OpenIoTVocab.NS+"WindChill");
	public static final OntClass WIND_SPEED = m_model
			.createClass(OpenIoTVocab.NS+"WindSpeed");
	public static final OntClass STATUS = m_model
			.createClass(OpenIoTVocab.NS+"Status");
	public static final OntClass WEBCAM_SNAPSHOT = m_model
			.createClass(OpenIoTVocab.NS+"WebcamSnapShot");
	
	


	// Vocabulary individuals
	// /////////////////////////

	public OpenIoTVocab(){

	}

}