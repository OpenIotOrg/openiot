package org.openiot.ld4s.resource.measurement_capab;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
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
public class LD4SMCResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Measurement Capability";
	
	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;
	
	/** Resource provided by this Service resource. */
	protected MC ov = null;


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
			subjuri = ov.getResource_id();
		}
		resource = rdfData.createResource(subjuri);
		
		String item = ov.getObserved_property();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(SsnVocab.FOR_PROPERTY, 
						rdfData.createResource(item));	
			}else{
				resource = addObsProp(resource, item, SsnVocab.FOR_PROPERTY, ov.getFoi(), ov.getConDate(), ov.getConTime(), ov.getConCompany(), ov.getConCountry());
			}
		}	
		String[] props = ov.getMeasurement_prop_uris();
		if (props != null){
			for (int ind=0; ind<props.length ;ind++){
				if (props[ind] != null){
					resource.addProperty(SsnVocab.HAS_MEASUREMENT_PROPERTY, 
							rdfData.createResource(props[ind]));
				}
			}
		}
		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}
	
}
