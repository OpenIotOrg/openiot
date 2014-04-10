package org.openiot.ld4s.resource.device;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.CorelfVocab;
import org.openiot.ld4s.vocabulary.OpenIoTVocab;
import org.openiot.ld4s.vocabulary.ProvVocab;
import org.openiot.ld4s.vocabulary.SptVocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

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
			subjuri = ov.getRemote_uri();
		}
		resource = rdfData.createResource(subjuri);

		String item = ov.getBase_name();
//		if (item != null && item.trim().compareTo("")!=0){
//			if (item.startsWith("http://")){
//				resource.addProperty(CorelfVocab.BASE_NAME, 
//						rdfData.createResource(item));	
//			}else{
//				resource.addProperty(CorelfVocab.BASE_NAME, 
//						rdfData.createTypedLiteral(item));
//			}
//		}
//		item = ov.getBase_ov_name();
//		if (item != null && item.trim().compareTo("")!=0){
//			if (item.startsWith("http://")){
//				resource.addProperty(CorelfVocab.BASE_OV_NAME, 
//						rdfData.createResource(item));	
//			}else{
//				resource.addProperty(CorelfVocab.BASE_OV_NAME, 
//						rdfData.createTypedLiteral(item));
//			}
//		}
		item = ov.getSource();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(ProvVocab.PERFORMED_BY, 
						rdfData.createResource(item));	
			}else{
				resource.addProperty(ProvVocab.PERFORMED_BY, 
						rdfData.createTypedLiteral(item));
			}
		}
		
		item = ov.getUnit_of_measurement();
		if (item != null && item.trim().compareTo("")!=0){
			if (item.startsWith("http://")){
				resource.addProperty(SptVocab.UOM, 
						rdfData.createResource(item));	
			}else{
				resource = addUom(resource, item);
			}
		}
		String[] tprops = ov.getTsproperties();
		if (tprops != null){
			for (int i=0; i<tprops.length ;i++){
				if (tprops[i].startsWith("http://")){
					resource.addProperty(OpenIoTVocab.MOBILITY, 
							rdfData.createResource(tprops[i]));	
				}else{
					resource.addProperty(OpenIoTVocab.MOBILITY, 
							rdfData.createTypedLiteral(tprops[i]));
				}
			}			
		}
		
		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}




}
