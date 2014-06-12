package org.openiot.ld4s.resource.ov;

import java.util.Random;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.OpenIoTVocab;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.openiot.ld4s.vocabulary.SsnVocab;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Construct an observation value resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4SOVResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Observation Value";
	
	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;
	
	/** Resource provided by this Service resource. */
	protected OV ov = null;


	/**
	 * Creates main resources and additional related information
	 * including linked data
	 *
	 * @param m_returned model which the resources to be created should be attached to
	 * @param obj object containing the information to be semantically annotate
	 * @param id resource identification
	 * @return model 
	 * @throws Exception
	 */
	protected Resource makeOVLinkedData() throws Exception {
		Resource resource = makeOVData();
		//set the linking criteria
		this.context = ov.getLink_criteria();
		resource = addLinkedData(resource, Domain.ALL, this.context);
		return resource;
	}
	
	/**
	 * Creates main resources and additional related information
	 * excluding linked data
	 *
	 * @param m_returned model which the resources to be created should be attached to
	 * @param obj object containing the information to be semantically annotate
	 * @param id resource identification
	 * @return model 
	 * @throws Exception
	 */
	protected Resource makeOVData() throws Exception {
		Resource resource = createOVResource();
		resource.addProperty(DCTerms.isPartOf,
				this.ld4sServer.getHostName()+"void");
		return resource;
	}

	/**
	 * Creates the main resource
	 * @param model
	 * @param value
	 * @return
	 * @throws Exception 
	 */
	protected  Resource createOVResource() throws Exception {
		Resource observation_value_resource = null;
		String subjuri = null;
		if (resourceId != null){
			subjuri = this.uristr;	
		}else{
			subjuri = getResourceUri(null, null, ov.getResource_id());
		}
		observation_value_resource = rdfData.createResource(subjuri);
		
		observation_value_resource.addProperty(RDF.type, SsnVocab.OBSERVATION_VALUE);
		
		String[] vals = ov.getValues();
		if (vals != null){
			for (int i=0; i<vals.length ;i++){
				if (vals[i] != null){
					observation_value_resource.addProperty(OpenIoTVocab.VALUE, 
							rdfData.createTypedLiteral(vals[i], XSDDatatype.XSDdouble));
				}
			}			
		}
		
		/** Observation Resource Creation - Start. */
		//create observation, if not given. link it with the sensor, if given.
		String item = ov.getObservation();
		Resource observation_resource = null;
		if (item != null && item.trim().compareTo("")!=0){
			observation_resource = rdfData.createResource(getResourceUri(null,  null,  item));
		}else{
			String observation_uri = getResourceUri(this.ld4sServer.getHostName(), "/observation", 
					String.valueOf((new Random()).nextInt()));		
			observation_resource = rdfData.createResource(observation_uri);
		}
		observation_value_resource.addProperty(OpenIoTVocab.IS_OBSERVED_VALUE_OF, observation_resource);
		observation_resource.addProperty(SsnVocab.OBSERVATION_RESULT, observation_value_resource);
		observation_resource.addProperty(RDF.type, SsnVocab.OBSERVATION);
				
		item = ov.getSensor_id();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				observation_resource.addProperty(SsnVocab.OBSERVED_BY,
						rdfData.createResource(item));

			}else{
//				resource.addProperty(SptVocab.OUT_OF, item);
				observation_resource.addProperty(SsnVocab.OBSERVED_BY, item);
			}
		}	
		
		item = ov.getFoi();
		if (item != null && item.trim().compareTo("") != 0){
			if (item.startsWith("http://")){
				observation_resource.addProperty(SsnVocab.FEATURE_OF_INTEREST, 
						rdfData.createResource(item));
			}else{
				observation_resource = addFoi(observation_resource, item);
			}
		}
		
		item = ov.getResult_time();
		if (item != null && item.trim().compareTo("")!=0){
				observation_resource.addProperty(SsnVocab.OBSERVATION_RESULT_TIME, 
						rdfData.createTypedLiteral(item, XSDDatatype.XSDdateTime));	
		}
		/** Observation Resource Creation - End. */
		
		
		item = ov.getStart_range();
		if (item != null && item.trim().compareTo("")!=0){
				observation_value_resource.addProperty(SptVocab.START_TIME, 
						rdfData.createTypedLiteral(item, XSDDatatype.XSDdateTime));	
		}
		item = ov.getEnd_range();
		if (item != null && item.trim().compareTo("")!=0){
				observation_value_resource.addProperty(SptVocab.END_TIME, 
						rdfData.createTypedLiteral(item, XSDDatatype.XSDdateTime));	
		}
		item = ov.getUnit_of_measurement();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				observation_value_resource.addProperty(OpenIoTVocab.UNIT, 
						rdfData.createResource(item));	
			}else{
				observation_value_resource = addUom(observation_value_resource, item);
			}
		}
		item = ov.getObserved_property();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				observation_value_resource.addProperty(SsnVocab.OBSERVED_PROPERTY, 
						rdfData.createResource(item));	
			}else{
				observation_value_resource = addObsProp(observation_value_resource, 
						item, SsnVocab.OBSERVED_PROPERTY, ov.getFoi(),
						ov.getConDate(), ov.getConTime(),
						ov.getConCompany(), ov.getConCountry());
			}
		}	
		observation_value_resource = crossResourcesAnnotation(ov, observation_value_resource);
		return observation_value_resource;
	}

		  

	
}
