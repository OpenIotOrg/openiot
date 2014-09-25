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

import org.openiot.commons.util.PropertyManagement;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class OpeniotVocab {

	//read data from the properties file
	  private static PropertyManagement propertyManagement = new PropertyManagement();
	  
	  //construct the sensor schema editor URI
	  public final static String SCHEMAEDITOR_URI = 
			 propertyManagement.getSchemaEditorUrl().replace("/navigator.xhtml", "");
	  
	/**
	   * <p>
	   * The ontology model that holds the vocabulary terms
	   * </p>
	   */
	  public static OntModel m_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

	  

	  /**
	   * <p>
	   * Namespace for x-gsn sensor schema creator
	   * </p>
	   */
	  
	  private final static String rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	  private final static String rdfs="http://www.w3.org/2000/01/rdf-schema#";
	  private final static String qu="http://purl.oclc.org/NET/ssnx/qu/qu#";
	  private final static String rr="http://www.w3.org/ns/r2rml#";
	  private final static String prov="http://purl.org/net/provenance/ns#";
	  
	  private final static String dul="http://www.loa-cnr.it/ontologies/DUL.owl#";
	  private final static String wgs84="http://www.w3.org/2003/01/geo/wgs84_pos#";
	  
	  public final static String lsm="http://lsm.deri.ie.ont.lsm.owl#";
	  public final static String lgdata="http://linkedgeodata.org/property/";
	  
	  /**
	   * <p>
	   * Properties and Resources for x-gsn sensor schema creator
	   * </p>
	   */
	  
	  public final static Resource ssnSensor=ResourceFactory.createResource(SsnVocab.SSN_NS + "Sensor");
	  public final static Resource dulPlace=ResourceFactory.createResource(dul+"Place");
	  public final static Property rdfType=ResourceFactory.createProperty(rdf+"type");
	  public final static Property rdfsLabel=ResourceFactory.createProperty(rdfs+"label");
	  public final static Property ssnObserves=ResourceFactory.createProperty(SsnVocab.SSN_NS +"observes");
	  public final static Property ssnOfFeature=ResourceFactory.createProperty(SsnVocab.SSN_NS +"ofFeature");
	  public final static Property quUnit=ResourceFactory.createProperty(qu+"unit");
	  public final static Property rrColumnName=ResourceFactory.createProperty(rr+"columnName");
	  public final static Property provPerformedAt=ResourceFactory.createProperty(prov+"PerformedAt");
	  public final static Property provPerformedBy=ResourceFactory.createProperty(prov+"PerformedBy");
	  public final static Property lsmHasSourceType=ResourceFactory.createProperty(lsm+"hasSourceType");
	  public final static Property lsmHasSensorType=ResourceFactory.createProperty(lsm+"hasSensorType");
	  public final static Property dulHasLocation=ResourceFactory.createProperty(dul+"hasLocation");
	  public final static Property wgs84Lat=ResourceFactory.createProperty(wgs84+"lat");
	  public final static Property wgs84Long=ResourceFactory.createProperty(wgs84+"long");
	  public final static Property provWasGeneratedBy=ResourceFactory.createProperty(prov+"wasGeneratedBy");
	  
	  
	  
	  /**
	   * <p>
	   * The namespace of the vocabulary as a string
	   * </p>
	   */
	  
	  public static final String NS = propertyManagement.getOpeniotOntologyNamespace();
	  public static final String TITLE = propertyManagement.getSchemaEditorTitle();
	  public static final String URL = propertyManagement.getSchemaEditorUrl();
	  
	  public static final String LSM_SERVER_URL = propertyManagement.getSchedulerLsmRemoteServer();
	  public static final String LSM_METAGRAPH = propertyManagement.getSchedulerLsmMetaGraph();
	  public static final String LSM_DATAGRAPH = propertyManagement.getSchedulerLsmDataGraph();
	  public static final String LSM_SPARQL_ENDPOINT = propertyManagement.getSchedulerLsmSparqlEndPoint();
	  
	  
	  public static final String RDF_SUBCLASS = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	  

	  /**
	   * <p>
	   * defined for SensorSchema
	   * </p> 
	   */

	  public static final ObjectProperty HAS_VALUE = m_model.
			  createObjectProperty(NS+"hasValue");
	  
	  public static final String LANG_N3 = "N3";
	  public static final String LANG_TURTLE = "TURTLE";
	  public static final String LANG_RDFXML = "RDF/XML";
	  public static final String LANG_RDFJSON = "RDF/JSON";
	  public static final String LANG_NTRIPLE = "N-TRIPLE";
	  public static final String LANG_RDFXML_ABBREV = "RDF/XML-ABBREV";
}
