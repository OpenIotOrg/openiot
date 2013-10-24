package org.openiot.ld4s.resource.owlclass;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.server.ServerProperties;
import org.openiot.ld4s.vocabulary.SsnVocab;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Construct a new client-defined OWL Type resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4SOwlClassResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "New OWL Type";

	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;

	/** Resource provided by this Service resource. */
	protected OwlClass ov = null;

	/** An OpenIoT ontology module dedicate to the definition of new client-defined types. */
	private static String ONTOLOGY_FOR_NEW_TYPES_NS = null;

	private static OntModel ontologyBaseModel = null; 
	/**
	 * Creates main resources and additional related information
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
		resource = addToOntology(resource);
		return resource;
		
	}

	


	private Resource addToOntology(Resource resource) {
		if (ONTOLOGY_FOR_NEW_TYPES_NS == null){
			ONTOLOGY_FOR_NEW_TYPES_NS = this.ld4sServer.getServerProperties().get(ServerProperties.NEW_TYPES_ONTOLOGY);
		}
		if (ontologyBaseModel == null) {
			ontologyBaseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			if (isUriAccessible(ONTOLOGY_FOR_NEW_TYPES_NS)) {
				ontologyBaseModel.read(ONTOLOGY_FOR_NEW_TYPES_NS, "RDF/XML");
			}
		}
		ontologyBaseModel.add(resource.getModel());
		storeHandler(ontologyBaseModel);
		return null;
	}
	
	private static boolean isUriAccessible(String uri) {
		HttpURLConnection connection = null;
		int code = -1;
		URL myurl;
		try {
			myurl = new URL(uri);

			connection = (HttpURLConnection) myurl.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(1000);
			code = connection.getResponseCode();
		} catch (MalformedURLException e) {
			System.err.println(uri + " is not accessible.");
		} catch (ProtocolException e) {
			System.err.println(uri + " is not accessible.");
		} catch (IOException e) {
			System.err.println(uri + " is not accessible.");
		}
		return (code == 200) ? true : false;
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
			//			subjuri = this.uristr;	
			//		}else{
			subjuri = ov.getRemote_uri();
		}
		resource = rdfData.createResource(subjuri);

		String item = ov.getPredicate(), item1 = ov.getValue();
		if (item != null && item.trim().compareTo("")!=0
				&& item1 != null && item1.trim().compareTo("")!=0){
			resource.addProperty(rdfData.createProperty(item), rdfData.createResource(item1));
		}

		OwlClassRestriction[] conditions = ov.getConditions();
		Node bnode = null;
		Resource bnode_resource = null;
		if (conditions != null){
			for (int ind=0; ind<conditions.length ;ind++){
				bnode = Node.createAnon();
				bnode_resource = rdfData.createResource(bnode.getBlankNodeId());
				resource.addProperty(SsnVocab.IN_CONDITION, bnode_resource);
				if (conditions[ind].onPredicate != null){
					bnode_resource.addProperty(OWL.onProperty, 
							conditions[ind].onPredicate);
				}
				if (conditions[ind].restrictionPredicate != null
						&& conditions[ind].value != null){
					resource.addProperty(
							rdfData.createProperty(conditions[ind].restrictionPredicate), 
							rdfData.createTypedLiteral(conditions[ind].value,
									conditions[ind].uom));

				}

			}
		}
		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}

}
