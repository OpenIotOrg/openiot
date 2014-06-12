package org.openiot.ld4s.resource.measurement_prop;

import java.util.IllegalFormatException;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.openiot.ld4s.vocabulary.SsnVocab;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Construct a measurement property resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4SMPResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Measurement Property";

	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;

	/** Resource provided by this Service resource. */
	protected MP ov = null;


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

		String item = ov.getPredicate();
		if (item != null && item.trim().compareTo("")!=0){
			resource.addProperty(OWL.onProperty, 
					rdfData.createProperty(item));
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
		try{
			item = ov.getValue();
			if (item != null && item.trim().compareTo("")!=0){
				if (item.contains("_")){
					String[] range = item.split("_");
					if (range.length >=2 
							&& Double.valueOf(range[0]) < Double.valueOf(range[1])){
						resource.addProperty(SptVocab.HAS_MIN_VALUE, 
								rdfData.createTypedLiteral(range[0], XSDDatatype.XSDdouble));
						resource.addProperty(SptVocab.HAS_MAX_VALUE, 
								rdfData.createTypedLiteral(range[1], XSDDatatype.XSDdateTime));
					}
				}else{
					resource.addProperty(SptVocab.HAS_VALUE, 
							rdfData.createTypedLiteral(item, XSDDatatype.XSDdateTime));
				}
			}
		}catch (IllegalFormatException e){
			e.printStackTrace();
		}
		MPCondition[] conditions = ov.getConditions();
		Node bnode = null;
		Resource bnode_resource = null;
		String[] range = null;
		if (conditions != null){
			for (int ind=0; ind<conditions.length ;ind++){
				bnode = Node.createAnon();
				bnode_resource = rdfData.createResource(bnode.getBlankNodeId());
				resource.addProperty(SsnVocab.IN_CONDITION, bnode_resource);
				if (conditions[ind].observed_property != null){
					bnode_resource.addProperty(SsnVocab.FOR_PROPERTY, 
							conditions[ind].observed_property);
				}
				if (conditions[ind].predicate != null){
					bnode_resource.addProperty(OWL.onProperty, 
							conditions[ind].predicate);
				}
				if (conditions[ind].uom != null){
					bnode_resource = addUom(bnode_resource, conditions[ind].uom);
				}
				try{
					if (conditions[ind].value != null){
						if (conditions[ind].value.contains("_")){
							range = conditions[ind].value.split("_");
							if (range.length >=2 
									&& Double.valueOf(range[0]) < Double.valueOf(range[1])){
								resource.addProperty(SptVocab.HAS_MIN_VALUE, 
										range[0]);
								resource.addProperty(SptVocab.HAS_MAX_VALUE, 
										range[1]);
							}
						}else{
							resource.addProperty(SptVocab.HAS_VALUE, conditions[ind].value);
						}
					}
				}catch (IllegalFormatException e){
					e.printStackTrace();
				}
			}
		}
		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}

}
