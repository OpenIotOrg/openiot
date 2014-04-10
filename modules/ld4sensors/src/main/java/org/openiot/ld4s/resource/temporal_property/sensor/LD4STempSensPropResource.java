package org.openiot.ld4s.resource.temporal_property.sensor;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.OpenIoTVocab;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.openiot.ld4s.vocabulary.SsnVocab;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Construct an oobservation value resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4STempSensPropResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Observation Value";
	
	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;
	
	/** Resource provided by this Service resource. */
	protected TempSensProp ov = null;


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
		String item = ov.getFoi();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(SsnVocab.FEATURE_OF_INTEREST, 
						rdfData.createResource(item));	
			}else{
				resource = addFoi(resource, item);
			}
		}		
		item = ov.getSensor_id();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(
						OpenIoTVocab.CONTEXT_OF, 
						rdfData.createResource(item));	
			}else{
				resource.addProperty(OpenIoTVocab.CONTEXT_OF, item);
			}
		}
		item = ov.getNet_role();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(SptVocab.NET_ROLE, 
						rdfData.createResource(item));	
			}else{
				/** @todo: special search on dbpedia for a proper type 
				 * + create a resource of this type and store it locally. */ 
				resource.addProperty(SptVocab.NET_ROLE, item);
			}
		}	
		item = ov.getTime();
		if (item != null && item.trim().compareTo("")!=0){
			resource.addProperty(OpenIoTVocab.MOBILITY_TIME, 
					resource.getModel().createTypedLiteral(item, XSDDatatype.XSDlong));
		}
		item = ov.getStart_range();
		if (item != null && item.trim().compareTo("")!=0){
			resource.addProperty(OpenIoTVocab.MOBILITY_START, 
					resource.getModel().createTypedLiteral(item, XSDDatatype.XSDlong));
		}
		item = ov.getEnd_range();
		if (item != null && item.trim().compareTo("")!=0){
			resource.addProperty(OpenIoTVocab.MOBILITY_END, 
					resource.getModel().createTypedLiteral(item, XSDDatatype.XSDlong));
		}		
		String[] vals = ov.getNet_links();
		if (vals != null){
			for (int i=0; i<vals.length ;i++){
				if (vals[i] != null){
					if (vals[i].startsWith("http://")){
						resource.addProperty(SptVocab.NET_LINK, 
								rdfData.createResource(vals[i]));	
					}else{
						resource.addProperty(SptVocab.NET_LINK, vals[i]);
					}	
				}
			}			
		}
		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}

		  

	
}
