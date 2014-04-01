package org.openiot.ld4s.vocabulary;

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

	// Vocabulary classes
	// /////////////////////////


	public static final OntClass MOBILE_CONTEXT = m_model
			.createClass(OpenIoTVocab.NS+"MobileContext");


	// Vocabulary individuals
	// /////////////////////////

	public OpenIoTVocab(){

	}

}