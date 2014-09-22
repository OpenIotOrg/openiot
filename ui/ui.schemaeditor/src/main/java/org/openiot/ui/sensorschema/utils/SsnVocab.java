/**
 *    Copyright (c) 2011-2014, OpenIoT
 *   
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 * 
 * 	   @author Prem Jayaraman
 */
package org.openiot.ui.sensorschema.utils;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

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
	  public static final String SSN_NS = "http://purl.oclc.org/NET/ssnx/ssn#";

	  /**
	   * <p>
	   * The namespace of the vocabulary as a string
	   * </p>
	   *
	   * @see #SSN_NS
	   */
	  public static String getURI() {
	    return SSN_NS;
	  }

	  /**
	   * <p>
	   * The namespace of the vocabulary as a resource
	   * </p>
	   */
	  public static final Resource NAMESPACE = m_model.createResource(SSN_NS);

	  /** PREFIX. */
	  public static final String PREFIX = "ssn";

	  ////////////////////////////
	  // Vocabulary properties  //
	  // /////////////////////////
	  	  
	  public static final ObjectProperty IS_PROPERTY_OF = m_model
		      .createObjectProperty(SSN_NS+"isPropertyOf");
	  public static final ObjectProperty OBSERVED_PROPERTY = m_model
		      .createObjectProperty(SSN_NS+"observedProperty");
	  public static final ObjectProperty OBSERVATION_RESULT_TIME = m_model
		      .createObjectProperty(SSN_NS+"observationResultTime");
	  public static final ObjectProperty OBSERVATION_RESULT = m_model
		      .createObjectProperty(SSN_NS+"observationResult");
	  public static final ObjectProperty OBSERVED_BY = m_model
		      .createObjectProperty(SSN_NS+"observedBy");
	  public static final ObjectProperty OBSERVES = m_model
		      .createObjectProperty(SSN_NS+"observes");
	  public static final ObjectProperty IN_CONDITION = m_model
      .createObjectProperty(SSN_NS+"inCondition");
	  public static final ObjectProperty FOR_PROPERTY = m_model
      .createObjectProperty(SSN_NS+"forProperty");
	  public static final ObjectProperty HAS_MEASUREMENT_PROPERTY = m_model
      .createObjectProperty(SSN_NS+"hasMeasurementProperty");
	  public static final ObjectProperty HAS_MEASUREMENT_CAPABILITIES = m_model
      .createObjectProperty(SSN_NS+"hasMeasurementCapabilities");
	  public static final ObjectProperty ATTACHED_SYSTEM = m_model
      .createObjectProperty(SSN_NS+"attachedSystem");
	  public static final ObjectProperty MEASUREMENT_CAPAB = m_model
      .createObjectProperty(SSN_NS+"hasMeasurementCapability");
	  public static final ObjectProperty IMPLEMENTS = m_model
      .createObjectProperty(SSN_NS+"implements");
	  public static final ObjectProperty IN_DEPLOYMENT = m_model
      .createObjectProperty(SSN_NS+"inDeploment");
	  public static final ObjectProperty FEATURE_OF_INTEREST = m_model
      .createObjectProperty(SSN_NS+"featureOfInterest");
	  
	  // /////////////////// //
	  // Vocabulary classes //
	  // ///////////////// //

	  public static final OntClass OBSERVATION = m_model
		      .createClass(SsnVocab.SSN_NS+"Observation");
	  public static final OntClass OBSERVATION_VALUE = m_model
		      .createClass(SsnVocab.SSN_NS+"ObservationValue");
	  public static final OntClass SENSITIVITY = m_model
      .createClass(SsnVocab.SSN_NS+"Sensitivity");
	  public static final OntClass RESPONSE_TIME = m_model
      .createClass(SsnVocab.SSN_NS+"ResponseTime");
	  public static final OntClass RESOLUTION = m_model
      .createClass(SsnVocab.SSN_NS+"Resolution");
	  public static final OntClass PRECISION = m_model
      .createClass(SsnVocab.SSN_NS+"Precision");
	  public static final OntClass LATENCY = m_model
      .createClass(SsnVocab.SSN_NS+"Latency");
	  public static final OntClass FREQUENCY = m_model
      .createClass(SsnVocab.SSN_NS+"Frequency");
	  
	  public static final OntClass DRIFT = m_model
      .createClass(SsnVocab.SSN_NS+"Drift");
	  public static final OntClass DETECTION_LIMIT = m_model
      .createClass(SsnVocab.SSN_NS+"DetectionLimit");
	  public static final OntClass ACCURACY = m_model
      .createClass(SsnVocab.SSN_NS+"Accuracy");
	  public static final OntClass SELECTIVITY  = m_model
      .createClass(SsnVocab.SSN_NS+"Selectivity");
	  public static final OntClass MEASUREMENT_PROPERTY = m_model
      .createClass(SsnVocab.SSN_NS+"MeasurementProperty");
	  public static final OntClass MEASUREMENT_CAPABILITY= m_model
      .createClass(SsnVocab.SSN_NS+"MeasurementCapability");
	  public static final OntClass DEVICE = m_model
      .createClass(SsnVocab.SSN_NS+"Device");
	  public static final OntClass PROPERTY = m_model
      .createClass(SsnVocab.SSN_NS+"Property");
	  public static final OntClass PLATFORM = m_model
      .createClass(SsnVocab.SSN_NS+"Platform");
	  public static final OntClass SENSING_DEVICE = m_model
	      .createClass(SsnVocab.SSN_NS+"SensingDevice");
	  // Vocabulary individuals
	  // /////////////////////////

	public static final OntClass SENSOR = m_model
		      .createClass(SsnVocab.SSN_NS+"Sensor");
	
	public static final String SSN_SENSOR="http://purl.oclc.org/NET/ssnx/ssn#Sensor";
}
