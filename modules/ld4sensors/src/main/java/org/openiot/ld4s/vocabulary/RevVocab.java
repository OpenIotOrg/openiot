package org.openiot.ld4s.vocabulary;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class RevVocab {
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
	  public static final String NS = "http://purl.org/stuff/rev#";

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
	  public static final String PREFIX = "rev";

	  // Vocabulary properties
	  // /////////////////////////

	  public static final ObjectProperty HAS_FEEDBACK = m_model
	    .createObjectProperty(NS+"hasFeedback");
	  public static final ObjectProperty IS_FEEDBACK_OF = m_model
	    .createObjectProperty(NS+"isFeedbackOf");
	  public static final ObjectProperty HAS_COMMENT = m_model
	    .createObjectProperty(NS+"hasComment");
	  public static final ObjectProperty COMMENTER  = m_model
  .createObjectProperty(NS+"commenter");
	  public static final DatatypeProperty RATING = m_model
	  .createDatatypeProperty(NS+"rating");
	  public static final DatatypeProperty POSITIVE_VOTES = m_model
	  .createDatatypeProperty(NS+"positiveVotes");
	  
	  
	  // Vocabulary classes
	  // /////////////////////////
	  public static final OntClass REVIEW = m_model
      .createClass(NS+"Review");
	  public static final OntClass FEEDBACK= m_model
      .createClass(NS+"Feedback");
	  
	  // Vocabulary individuals
	  // /////////////////////////
}
