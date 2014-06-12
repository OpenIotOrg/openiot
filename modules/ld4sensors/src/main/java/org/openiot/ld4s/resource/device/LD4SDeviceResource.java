package org.openiot.ld4s.resource.device;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.SsnVocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Construct an oobservation value resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4SDeviceResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Sensor";

	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;

	/** Resource provided by this Service resource. */
	protected Device ov = null;


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
			subjuri = getResourceUri(null, null, ov.getResource_id());
		}
		resource = rdfData.createResource(subjuri);

		
		if (ov.getType() != null){
			resource.addProperty(RDF.type, rdfData.createResource(ov.getType()));
		}else{
			resource.addProperty(RDF.type, SsnVocab.SENSOR);
		}
		
		
		String item = ov.getLocation_name();
		String[] item1 = ov.getCoords();
		if (item != null && item.startsWith("http://")){
			resource.addProperty(
					resource.getModel().createProperty(
							"http://www.ontologydesignpatterns.org/ont/dul/DUL.owl/hasLocation"), 
							resource.getModel().createResource(item));	
		}else {
			resource = addLocation(resource, item, item1);

		}
		
		
		String[] obsprops = ov.getObservedProperties();
		if (obsprops != null){
			for (int i=0; i<obsprops.length ;i++){
				if (obsprops[i] != null){
					if (obsprops[i].startsWith("http://")){
						resource.addProperty(SsnVocab.OBSERVES, 
								rdfData.createResource(obsprops[i]));	
					}else{
						addObsProp(resource, obsprops[i], SsnVocab.OBSERVES, ov.getFoi(), null, null, null, null);
					}
					
				}
			}			
		}
		
//		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}




}
