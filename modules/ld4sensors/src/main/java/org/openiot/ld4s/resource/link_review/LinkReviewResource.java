package org.openiot.ld4s.resource.link_review;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SApiInterface;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Resource representing the users' authored data link review.
 *
 * @author Myriam Leggieri.
 *
 */
public class LinkReviewResource extends LD4SLinkReviewResource implements LD4SApiInterface{


	
	/**
	 * Returns a serialized RDF Model 
	 * that contains the linked data associated with the
	 * specified path
	 *
	 * @return The resource representation.
	 */
	@Override
	public Representation get() {
		if (resourceId == null || resourceId.trim().compareTo("") == 0){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Please request only a resource stored in this LD4S TDB");
			return null;
		}

		Representation ret = null;
		logger.fine(resourceName + " as Linked Data: Starting");

		try {
			//check cache
			//get all the resource information from the Triple DB
			logger.fine(resourceName + " LD4S: Requesting data");
			logRequest(resourceName, resourceId);
			//get the resource uri by cutting off the eventual appended query string
			int query = uristr.indexOf("?");
			if (query != -1){
				uristr = this.uristr.substring(0,query-1);
			}
			rdfData = retrieve(this.uristr, this.namedModel);
			//how it is: for now, if links are requested, then search for new ones 
			//and filter out all the stored ones.
			if (!this.context.isEmpty()){
				//how it should be: add the already existing links iff their context 
				//matches with the requested one search for new links
				rdfData = addLinkedData(rdfData.getResource(uristr), Domain.ALL, this.context).getModel();
			}
			ret = serializeAccordingToReqMediaType(rdfData);
		}
		catch (Exception e) {
			setStatusError("Error creating " + resourceName + "  LD4S.", e);
			ret = null;
		}

		logger.info("REQUEST "+ this.uristr +" PROCESSING END - "+LD4SDataResource.getCurrentTime()); return ret;
	}


//	/**
//	 * Create and store a new Observation Value resource as Linked Data
//	 * from the submitted content. 
//	 * This resource MUST be stored in the LD4S TDB.
//	 * This resource MUST not be enriched with Linked Data since this would modify the initial
//	 * submitted content significantly (use POST instead).
//	 *
//	 *@param obj information to be semantically annotated and stored
//	 */
//	@Put
//	public Representation put(OV ldobj){
//		if (resourceId == null || resourceId.trim().compareTo("") == 0){
//			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//			return null;
//		}
//		this.ov = ldobj;
//
//		if (ov.getRemote_uri() != null){
//			//if the preferred resource hosting is a remote one, PUT can not be used
//			//(use POST instead) 
//			if (this.ov.isStoredRemotely(ld4sServer.getHostName())){
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return null;
//			}			
//		}
//
//		Representation ret = null;
//		rdfData = ModelFactory.createDefaultModel();
//		super.initModel(rdfData,"spitfire.rdf");
//		logger.fine(resourceName + " LD4S: Now building LD4S.");
//		try {
//			rdfData = makeOVData().getModel();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
//		} catch (JSONException e) {
//			e.printStackTrace();
//			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
//		}		
//		// create a new resource in the database
//		if (store(rdfData, this.namedModel)){
//			setStatus(Status.SUCCESS_CREATED);	
//			ret = serializeAccordingToReqMediaType(rdfData);
//		}else{
//			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to store in the Trple DB");
//		}
//		logger.info("REQUEST "+ this.uristr +" PROCESSING END - "+LD4SDataResource.getCurrentTime()); return ret;
//	}

	// PUT req: resource stored locally + no Linked Data enrichment
	/**
	 * Create and store a new Observation Value resource as Linked Data
	 * from the submitted content. 
	 * This resource MUST be stored in the LD4S TDB.
	 * This resource MUST not be enriched with Linked Data since this would modify the initial
	 * submitted content significantly (use POST instead).
	 *
	 *@param obj information to be semantically annotated and stored
	 */
	@Override
	public Representation put(Form obj){
		if (resourceId == null || resourceId.trim().compareTo("") == 0){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		Representation ret = null;
		rdfData = ModelFactory.createDefaultModel();
		super.initModel(rdfData,"spitfire.rdf");
		logger.fine(resourceName + " LD4S: Now building LD4S.");
//		try {
//			this.ov = new LinkReview(obj, this.ld4sServer.getHostName());
//
//			if (ov.getRemote_uri() != null){
//				//if the preferred resource hosting is a remote one, PUT can not be used
//				//(use POST instead) 
//				if (this.ov.isStoredRemotely(ld4sServer.getHostName())){
//					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//					return null;
//				}			
//			}
//			try {
//				rdfData = makeOVData().getModel();
//			}  catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//				setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
//			}catch (JSONException e) {
//				e.printStackTrace();
//				setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
//			}		
//			// create a new resource in the database
//			if (store(rdfData, this.namedModel)){
//				setStatus(Status.SUCCESS_CREATED);
//				ret = serializeAccordingToReqMediaType(rdfData);
//			}else{
//				setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to store in the Trple DB");
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to instantiate the requested resource\n"
//					+e1.getMessage());
//			return null;
//		}
		logger.info("REQUEST "+ this.uristr +" PROCESSING END - "+LD4SDataResource.getCurrentTime()); return ret;
	}

	/**
	 * Create and store a new Observation Value resource as Linked Data
	 * from the submitted content. 
	 * This resource MUST be stored in the LD4S TDB.
	 * This resource MUST not be enriched with Linked Data since this would modify the initial
	 * submitted content significantly (use POST instead).
	 *
	 *@param obj information to be semantically annotated and stored
	 */
	@Override
	public Representation put(JSONObject obj){
		if (resourceId == null || resourceId.trim().compareTo("") == 0){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		Representation ret = null;
		rdfData = ModelFactory.createDefaultModel();
		super.initModel(rdfData,"spitfire.rdf");
		logger.fine(resourceName + " LD4S: Now building LD4S.");
		try {
			this.ov = new LinkReview(obj, this.ld4sServer.getHostName());
			if (ov.getRemote_uri() != null){
				//if the preferred resource hosting server is a remote one, PUT can not be used
				//(use POST instead) 
				if (this.ov.isStoredRemotely(ld4sServer.getHostName())){
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return null;
				}			
			}
			try {
				rdfData = makeOVData().getModel();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to instantiate the requested resource\n"
					+e.getMessage());
			return null;
		}

		// create a new resource in the database
		if (storeHandler(rdfData)){
			setStatus(Status.SUCCESS_CREATED);	 
			ret = serializeAccordingToReqMediaType(rdfData);
		}else{
			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to store in the Trple DB");
		}
		logger.info("REQUEST "+ this.uristr +" PROCESSING END - "+LD4SDataResource.getCurrentTime()); return ret;
	}

//	/**
//	 * Update a stored Observation Value resource
//	 * with the information sent 
//	 *
//	 *@param obj information to store
//	 */
//	@Post
//	public Representation post(OV ldobj){
//		this.ov = ldobj;
//		Representation ret = null;
//		rdfData = ModelFactory.createDefaultModel();
//		super.initModel(rdfData,"spitfire.rdf");
//		logger.fine(resourceName + " LD4S: Now updating.");
//		try {
//			rdfData = makeOVLinkedData().getModel();
//
//			// create a new resource in the database only if the preferred resource hosting server is
//			// the LD4S one
//			if (resourceId != null || !this.ov.isStoredRemotely(ld4sServer.getHostName())){
//				if (update(rdfData, this.namedModel)){
//					setStatus(Status.SUCCESS_OK);	 
//					ret = serializeAccordingToReqMediaType(rdfData);
//				}else{
//					setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to update in the Trple DB");
//				}
//			}else{
//				setStatus(Status.SUCCESS_OK);	 
//				ret = serializeAccordingToReqMediaType(rdfData);
//			}
//		}  catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
//		}catch (JSONException e) {
//			e.printStackTrace();
//			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
//		}		
//
//		logger.info("REQUEST "+ this.uristr +" PROCESSING END - "+LD4SDataResource.getCurrentTime()); return ret;
//	}

	/**
	 * Update a stored Observation Value resource
	 * with the information sent 
	 *
	 *@param obj information to store
	 */
	@Override
	public Representation post(Form obj){
		//if an host has not been set then the LD4S service one has to be assigned
		Representation ret = null;
		rdfData = ModelFactory.createDefaultModel();
		super.initModel(rdfData,"spitfire.rdf");
		logger.fine(resourceName + " LD4S: Now updating.");
//		try {
//			this.ov = new LinkReview(obj, this.ld4sServer.getHostName());
//			rdfData = makeOVLinkedData().getModel();
//
//			// create a new resource in the database only if the preferred resource hosting server is
//			// the LD4S one
//			if (resourceId != null || !this.ov.isStoredRemotely(ld4sServer.getHostName())){
//				if (update(rdfData, this.namedModel)){
//					setStatus(Status.SUCCESS_OK);	 
//					ret = serializeAccordingToReqMediaType(rdfData);
//				}else{
//					setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to update in the Trple DB");
//				}
//			}else{
//				setStatus(Status.SUCCESS_OK);	 
//				ret = serializeAccordingToReqMediaType(rdfData);
//			}
//		}  catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
//		}	catch (JSONException e) {
//			e.printStackTrace();
//			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
//		} catch (Exception e) {
//			e.printStackTrace();
//			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to instantiate the requested resource\n"
//					+e.getMessage());
//			return null;
//		}		
//
//
		logger.info("REQUEST "+ this.uristr +" PROCESSING END - "+LD4SDataResource.getCurrentTime()); return ret;
	}

	/**
	 * Update a stored resource
	 * with the information sent 
	 *
	 *@param obj information to store
	 */
	@Override
	public Representation post(JSONObject obj){
		Representation ret = null;
		rdfData = ModelFactory.createDefaultModel();
		super.initModel(rdfData,"spitfire.rdf");
		logger.fine(resourceName + " LD4S: Now updating.");
		try {
			this.ov = new LinkReview(obj, this.ld4sServer.getHostName());
			rdfData = makeOVLinkedData().getModel();

			// create a new resource in the database only if the preferred resource hosting server is
			// the LD4S one
			if (resourceId != null || !this.ov.isStoredRemotely(ld4sServer.getHostName())){
				if (update(rdfData, this.namedModel)){
					setStatus(Status.SUCCESS_OK);	 
					ret = serializeAccordingToReqMediaType(rdfData);
				}else{
					setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to update in the Trple DB");
				}
			}else{
				setStatus(Status.SUCCESS_OK);	 
				ret = serializeAccordingToReqMediaType(rdfData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}  catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to instantiate the requested resource\n"
					+e.getMessage());
			return null;
		}


		logger.info("REQUEST "+ this.uristr +" PROCESSING END - "+LD4SDataResource.getCurrentTime()); return ret;
	}

	// DELETE req: resource stored locally
	/**
	 * Delete an already store Observation Value resource 
	 *
	 */
	@Override
	public void remove(){
		if (resourceId == null || resourceId.trim().compareTo("") == 0){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
		}
		logger.fine(resourceName + " LD4S: Now deleting "+this.uristr);		
		// create a new resource in the database
		if (delete(this.uristr, this.namedModel)){
			setStatus(Status.SUCCESS_OK);	 
		}else{
			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to delete from the Trple DB");
		}
	}

}
