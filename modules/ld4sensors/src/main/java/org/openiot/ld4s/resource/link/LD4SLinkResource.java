package org.openiot.ld4s.resource.link;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LD4SLinkResource extends LD4SDataResource {
	
	/** Service resource name. */
	protected String resourceName = "Data Link";
	
	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;
	
	/** Resource provided by this Service resource. */
	protected Link ov = null;

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
		Resource[] resource = null;
		String subjuri = null;
		if (resourceId != null){
			subjuri = this.uristr;	
		}else{
			subjuri = ov.getResource_id();
		}
//		resource = rdfData.createResource(subjuri);
//		resource = crossResourcesAnnotation(ov, resource);
		
		resource = createDataLinkResource(
				rdfData.createResource(ov.getFrom()), this.ld4sServer.getHostName(), ov, RDFS.seeAlso, subjuri);
		return resource!=null&&resource.length==2?resource[0]:rdfData.createResource();
	}

	
	


		  

	
}

