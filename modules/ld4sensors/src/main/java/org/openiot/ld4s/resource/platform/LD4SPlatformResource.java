package org.openiot.ld4s.resource.platform;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.CorelfVocab;
import org.openiot.ld4s.vocabulary.SptVocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Construct an oobservation value resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4SPlatformResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Observation Value";

	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;

	/** Resource provided by this Service resource. */
	protected Platform ov = null;


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
		
		String item = ov.getBase_name();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(CorelfVocab.BASE_NAME, 
						rdfData.createResource(item));	
			}else{
				resource.addProperty(CorelfVocab.BASE_NAME, item);
			}
		}
		item = ov.getStatus();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(SptVocab.STATUS, 
						rdfData.createResource(item));	
			}else{
				resource.addProperty(SptVocab.STATUS, item);
			}
		}
		String[] tprops = ov.getTpproperties();
		if (tprops != null){
			for (int i=0; i<tprops.length ;i++){
				if (tprops[i] != null){
					if (tprops[i].startsWith("http://")){
						resource.addProperty(SptVocab.TEMPORAL, 
								rdfData.createResource(tprops[i]));	
					}else{
						resource.addProperty(SptVocab.TEMPORAL, tprops[i]);
					}
				}
			}			
		}
		String[] vals = ov.getFeeds();
		if (vals != null){
			for (int i=0; i<vals.length ;i++){
				if (vals[i] != null){
					if (vals[i].startsWith("http://")){
						resource.addProperty(SptVocab.FEED, 
								rdfData.createResource(vals[i]));	
					}else{
						resource.addProperty(SptVocab.FEED, vals[i]);
					}	
				}
			}			
		}
		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}




}
