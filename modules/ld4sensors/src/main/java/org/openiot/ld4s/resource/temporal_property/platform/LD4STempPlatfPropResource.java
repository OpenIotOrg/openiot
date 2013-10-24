package org.openiot.ld4s.resource.temporal_property.platform;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.lod_cloud.Person;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.openiot.ld4s.vocabulary.SsnVocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Construct an oobservation value resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4STempPlatfPropResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Observation Value";

	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;

	/** Resource provided by this Service resource. */
	protected TempPlatfProp ov = null;


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
		Resource resource = null;
		String subjuri = null;
		if (resourceId != null){
			subjuri = this.uristr;	
		}else{
			subjuri = ov.getRemote_uri();
		}
		resource = rdfData.createResource(subjuri);

		String item = ov.getDeployment();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(SsnVocab.IN_DEPLOYMENT, 
						rdfData.createResource(item));	
			}else{
				resource.addProperty(SsnVocab.IN_DEPLOYMENT, item);
			}
		}
		item = ov.getPlatform_id();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(SptVocab.TEMPORAL, 
						rdfData.createResource(item));	
			}else{
				resource.addProperty(SptVocab.TEMPORAL, item);
			}
		}	
		item = ov.getMeasurement_capability();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(SsnVocab.HAS_MEASUREMENT_CAPABILITIES, 
						rdfData.createResource(item));	
			}else{
				resource.addProperty(SsnVocab.HAS_MEASUREMENT_CAPABILITIES, item);
			}
		}	
		String[] vals = ov.getAlgorithms();
		if (vals != null){
			for (int i=0; i<vals.length ;i++){
				if (vals[i] != null){
					if (vals[i].startsWith("http://")){
						resource.addProperty(SsnVocab.IMPLEMENTS, 
								rdfData.createResource(vals[i]));	
					}else{
						resource.addProperty(SsnVocab.IMPLEMENTS, vals[i]);
					}	
				}
			}			
		}
		Person[] people = ov.getPerson_owners();
		Person p = null;
		if (people != null){
			for (int i=0; i<people.length ;i++){
				p = people[i];
				if (p.getUri() != null){
					resource.addProperty(SptVocab.OWNED_BY, 
							rdfData.createResource(p.getUri()));
				}else{
					resource = 
						addPerson(resource, p, SptVocab.OWNED_BY);
				}
			}
		}
		vals = ov.getSystems();
		if (vals != null){
			for (int i=0; i<vals.length ;i++){
				if (vals[i] != null){
					if (vals[i].startsWith("http://")){
						resource.addProperty(SsnVocab.ATTACHED_SYSTEM, 
								rdfData.createResource(vals[i]));	
					}else{
						resource.addProperty(SsnVocab.ATTACHED_SYSTEM, vals[i]);
					}	
				}
			}			
		}
		vals = ov.getWornby();
		if (vals != null){
			for (int i=0; i<vals.length ;i++){
				if (vals[i] != null){
					if (vals[i].startsWith("http://")){
						resource.addProperty(SptVocab.WORN_BY, 
								rdfData.createResource(vals[i]));	
					}else{
						resource.addProperty(SptVocab.WORN_BY, vals[i]);
					}	
				}
			}			
		}else{
			Person[] persons = ov.getPerson_wornby();
			if (persons != null){
				for (int ind=0; ind<persons.length ;ind++){
					resource = 
						addPerson(resource, persons[ind], SptVocab.WORN_BY);
				}
			}
		}
		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}




}
