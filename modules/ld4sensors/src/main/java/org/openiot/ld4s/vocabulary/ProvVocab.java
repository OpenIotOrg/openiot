package org.openiot.ld4s.vocabulary;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ProvVocab {
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
	  public static final String NS = "http://www.w3.org/ns/prov#";

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
	  public static final String PREFIX = "prov";

	  // Vocabulary properties
	  // /////////////////////////

	  public static final ObjectProperty ACTED_ON_BEHALF_OF = m_model
	    .createObjectProperty(NS+"actedOnBehalfOf");
	  public static final ObjectProperty WAS_GENERATED_BY = m_model
    .createObjectProperty(NS+"wasGeneratedBy");
	  public static final ObjectProperty PERFORMED_BY = m_model
			    .createObjectProperty(NS+"performedBy");
	  
	  // Vocabulary classes
	  // /////////////////////////

	  // Vocabulary individuals
	  // /////////////////////////
}
