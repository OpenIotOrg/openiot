package org.openiot.ld4s.vocabulary;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.TransitiveProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

	/**
	 * Vocabulary definitions from
	 * spitfire.rdf
	 *
	 * @author Myriam Leggieri
	 */
	public class SptVocab {
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
	  public static final String NS = "http://spitfire-project.eu/ontology/ns/";

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
	  public static final String PREFIX = "spt";

	  // Vocabulary properties
	  // /////////////////////////

	  public static final ObjectProperty HAS_VALUE_RANGE = m_model
      .createObjectProperty(NS+"valueRange");
	  public static final ObjectProperty HAS_VALUE = m_model
      .createObjectProperty(NS+"value");
	  public static final ObjectProperty HAS_AVG_VALUE = m_model
      .createObjectProperty(NS+"avgValue");
	  public static final ObjectProperty HAS_MIN_VALUE = m_model
      .createObjectProperty(NS+"minValue");
	  public static final ObjectProperty HAS_MAX_VALUE = m_model
      .createObjectProperty(NS+"maxValue");
	  public static final ObjectProperty STATUS = m_model
      .createObjectProperty(NS+"status");
	  public static final ObjectProperty FEED = m_model
      .createObjectProperty(NS+"feed");
	  public static final ObjectProperty WEATHER_FORECAST = m_model
      .createObjectProperty(NS+"weatherForecast");
	  public static final ObjectProperty TS_MAP_OF = m_model
      .createObjectProperty(NS+"tsMapOf");
	  public static final ObjectProperty TS_MAP = m_model
      .createObjectProperty(NS+"tsMap");
	  public static final TransitiveProperty WORN_BY = m_model
      .createTransitiveProperty(NS+"wornBy");
	  public static final ObjectProperty OWNS = m_model
      .createObjectProperty(NS+"owns");
	  public static final ObjectProperty OWNED_BY = m_model
      .createObjectProperty(NS+"ownedBy");
	  public static final ObjectProperty LINK_TO = m_model
      .createObjectProperty(NS+"to");
	  public static final ObjectProperty LINK_TO_OF = m_model
      .createObjectProperty(NS+"toOf");
	  public static final ObjectProperty LINK_FROM = m_model
      .createObjectProperty(NS+"from");
	  public static final ObjectProperty SAME_AS_LINK = m_model
      .createObjectProperty(NS+"sameAsLink");
	  public static final ObjectProperty SEE_ALSO_LINK = m_model
      .createObjectProperty(NS+"seeAlsoLink");
	  public static final ObjectProperty NET_LINK = m_model
      .createObjectProperty(NS+"netLink");
	  public static final ObjectProperty NET_ROLE = m_model
      .createObjectProperty(NS+"netRole");
	  public static final ObjectProperty TEMPORAL = m_model
      .createObjectProperty(NS+"temporal");
	  public static final ObjectProperty TEMPORAL_OF = m_model
		      .createObjectProperty(NS+"temporalOf");
	  public static final ObjectProperty OUT = m_model
      .createObjectProperty(NS+"out");
	  public static final ObjectProperty OUT_OF = m_model
		      .createObjectProperty(NS+"outOf");
	  public static final ObjectProperty OBSERVED_PROPERTY = m_model
      .createObjectProperty(NS+"obs");
	  public static final ObjectProperty UOM = m_model
      .createObjectProperty(NS+"uom");
	  public static final ObjectProperty VALUE = m_model
	      .createObjectProperty(NS+"value");
	  public static TransitiveProperty IN = m_model
      .createTransitiveProperty(NS+"containedIn");
	  public static final TransitiveProperty UNDER = m_model
      .createTransitiveProperty(NS+"under");
	  public static final TransitiveProperty OVER = m_model
      .createTransitiveProperty(NS+"over");
	  public static final TransitiveProperty NEAR = m_model
      .createTransitiveProperty(NS+"nearby");
	  public static final ObjectProperty DOMAIN = m_model
      .createObjectProperty(NS+"domain");
	  public static final ObjectProperty CONFIDENCE = m_model
      .createObjectProperty(NS+"confidence");
	  public static final ObjectProperty BYTES = m_model
      .createObjectProperty(NS+"bytes");
	  
	  public static final ObjectProperty GEOGRAPHY = m_model
      .createObjectProperty(NS+"geography");
	  public static final ObjectProperty PUBLICATION = m_model
      .createObjectProperty(NS+"publication");
	  public static final ObjectProperty CROSS = m_model
      .createObjectProperty(NS+"topic");
	  public static final ObjectProperty LIFESCIENCE = m_model
      .createObjectProperty(NS+"lifeScience");
	  public static final ObjectProperty MEDIA = m_model
      .createObjectProperty(NS+"media");
	  public static final ObjectProperty GOV = m_model
      .createObjectProperty(NS+"government");
	  public static final ObjectProperty QUANTITY = m_model
      .createObjectProperty(NS+"quantity");
	  public static final ObjectProperty USER_GEN = m_model
      .createObjectProperty(NS+"userGeneratedContent");
	  
	  
	  public static final ObjectProperty IS_MEDIA = m_model
      .createObjectProperty(NS+"isMediaFor");
	  public static final ObjectProperty IS_GOV = m_model
      .createObjectProperty(NS+"isGovernmentFor");
	  public static final ObjectProperty IS_USER_GEN = m_model
      .createObjectProperty(NS+"isUserGeneratedContentFor");
	  public static final ObjectProperty IS_LIFE_SCIENCE= m_model
      .createObjectProperty(NS+"isLifeScienceFor");
	  public static final ObjectProperty IS_GEOGRAPHY = m_model
      .createObjectProperty(NS+"isGeographyFor");
	  public static final ObjectProperty IS_PUBLICATION = m_model
      .createObjectProperty(NS+"isPublicationFor");
	  public static final ObjectProperty IS_QUANTITY = m_model
      .createObjectProperty(NS+"isQuantityFor");
	  public static final ObjectProperty IS_CROSS = m_model
      .createObjectProperty(NS+"isTopicFor");
	  
	  public static final ObjectProperty TITLE = m_model
      .createObjectProperty(NS+"title");
	  public static final ObjectProperty START_TIME = m_model
      .createObjectProperty(NS+"tStart");
	  public static final ObjectProperty END_TIME = m_model
      .createObjectProperty(NS+"tEnd");
	  
	  public static OntProperty getInverse(OntProperty prop){
		  return prop.getInverse();
	  }
	  
	  // Vocabulary classes
	  // /////////////////////////

	  
	  public static final OntClass PLATFORM_TEMPORAL_PROPERTY = m_model
      .createClass(SptVocab.NS+"PlatformTemporalProperty");
	  public static final OntClass SENSOR_TEMPORAL_PROPERTY = m_model
      .createClass(SptVocab.NS+"SensorTemporalProperty");
	  public static final OntClass OV = m_model
	      .createClass(SptVocab.NS+"OV");
	  public static final OntClass DATALINKING = m_model
      .createClass(SptVocab.NS+"DataLink");
	  public static final OntClass LINKREVIEW = m_model
      .createClass(NS+"LinkReview");
	  // Vocabulary individuals
	  // /////////////////////////
	  
	  public SptVocab(){
		  IN = IN.asTransitiveProperty();
		  
		  IN.setSuperProperty(
m_model.createOntProperty("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl/hasLocation"));
		  
		  TEMPORAL.setInverseOf(TEMPORAL_OF);
		  OUT.setInverseOf(OUT_OF);
		  OWNS.setInverseOf(OWNED_BY);
		  
		  
		  CROSS.setInverseOf(IS_CROSS);
		  QUANTITY.setInverseOf(IS_QUANTITY);
		  PUBLICATION.setInverseOf(IS_PUBLICATION);
		  GEOGRAPHY.setInverseOf(IS_GEOGRAPHY);
		  GOV.setInverseOf(IS_GOV);
		  MEDIA.setInverseOf(IS_MEDIA);
		  IS_USER_GEN.setInverseOf(IS_USER_GEN);
		  IS_LIFE_SCIENCE.setInverseOf(LIFESCIENCE);
		  
	  }

}