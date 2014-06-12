package org.openiot.ld4s.resource.ontoProperty;

import org.openiot.ld4s.resource.LD4SDataResource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Construct an Ontology Property resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4SOntoPropertyResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Ontology Property";

	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;

	/** Resource provided by this Service resource. */
	protected OntoProperty ov = null;


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
		//		resource = addLinkedData(resource, Domain.ALL, this.context);
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

		OntoProperty.Type[] types = ov.getOntoPropTypes();
		if (types != null){
			for (int ind=0; ind<types.length ;ind++){
				switch (types[ind]){
				case SubProperty:
					if (ov.getSuperProperty() != null){
						resource.addProperty(RDFS.subPropertyOf, 
								rdfData.createProperty(ov.getSuperProperty()));
					}
					break;
				case AnnotationProperty:
				case DataTypeProperty:
				case FunctionalProperty:
				case InverseFunctionalProperty:
				case ObjectProperty:
				case Property:
				case SymmetricProperty:
				case TransitiveProperty:				
				default:
					resource.addProperty(RDF.type, types[ind].name());
					break;

				}
			}
		}

		String[] item = ov.getInverseProperties();
		if (item != null){
			for (int ind=0; ind<item.length ;ind++){
				resource.addProperty(OWL.inverseOf, 
						rdfData.createProperty(item[ind]));
			}
		}
		item = ov.getEquivalentProperties();
		if (item != null){
			for (int ind=0; ind<item.length ;ind++){
				resource.addProperty(OWL.equivalentProperty, 
						rdfData.createProperty(item[ind]));
			}
		}
		String str = ov.getDomain();
		if (str != null){
			resource.addProperty(RDFS.domain, 
					rdfData.createResource(str));
		}
		str = ov.getRange();
		if (str != null){
			resource.addProperty(RDFS.range, 
					rdfData.createResource(str));
		}
		//		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}


}
