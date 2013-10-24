package org.openiot.ld4s.resource.other;

import org.openiot.ld4s.resource.LD4SDataResource;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Resource representing the description of the published Hackystat dataset.
 *
 * @author Myriam Leggieri.
 *
 */
public class OtherResource extends LD4SDataResource {

	private static final String resourceName = "LD4S Resource Description";

	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;
	
  @Get
  public Representation get() {
			Representation ret = null;
		logger.fine(resourceName + " as Linked Data: Starting");

		try {
			//check cache
			//get all the resource information from the Triple DB
			logger.fine(resourceName + " LD4S: Requesting data");
			logRequest(resourceName, resourceId);
			rdfData = retrieve(uristr, namedModel);
			if (rdfData.isEmpty()){
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			}else{
				ret = serializeAccordingToReqMediaType(rdfData);
			}
		}
		catch (Exception e) {
			setStatusError("Error creating " + resourceName + "  LD4S.", e);
			ret = null;
		}

		return ret;
  }

}
