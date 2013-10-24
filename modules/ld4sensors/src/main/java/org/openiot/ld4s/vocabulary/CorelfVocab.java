package org.openiot.ld4s.vocabulary;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class CorelfVocab {

	  /**
	   * <p>
	   * The ontology model that holds the vocabulary terms
	   * </p>
	   */
	  private static OntModel m_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

	  /**
	   * <p>
	   * The namespace of the vocabulary as a string
	   * </p>
	   */
	  public static final String NS = "http://purl.org/NET/corelf#";

	  public static final String PREFIX = "corelf";

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
	  
	
	  public static final ObjectProperty TIME = m_model
	      .createObjectProperty(NS+"t");
	  public static final ObjectProperty BASE_TIME = m_model
      .createObjectProperty(NS+"bt");
	  public static final ObjectProperty RESOURCE_TIME = m_model
      .createObjectProperty(NS+"rt");
	  public static final ObjectProperty BASE_NAME = m_model
      .createObjectProperty(NS+"bn");
	  public static final ObjectProperty BASE_OV_NAME = m_model
      .createObjectProperty(NS+"bovn");

}
