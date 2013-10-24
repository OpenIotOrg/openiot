package org.openiot.ld4s.resource.sparql;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.resource.LD4SApiInterface;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 * Resource representing a SPARQL query
 * 
 * @author Myriam Leggieri <iammyr@email.com>
 *
 */
public class SparqlResource extends LD4SDataResource implements LD4SApiInterface{


	private String resourceName = "Sparql Request";
	

	
	/**
	 * Returns a serialized RDF Model 
	 * that contains the linked data associated with the
	 * specified path
	 *
	 * @return The resource representation.
	 */
	@Override
	public Representation post(JSONObject obj) {
		Representation ret = null;
		logger.fine(resourceName + " LD4S: Now querying.");
		try {
			this.query = java.net.URLDecoder.decode(LD4SDataResource.removeBrackets(obj.getString("query")), "UTF-8");
		} catch (JSONException e1) {
			setStatus(new Status(Status.CLIENT_ERROR_BAD_REQUEST, " The SPARQL query must be included in the JSON field named 'query' in a JSON payload."));
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		try {
			logger.fine(resourceName + " : Requesting answer");
			logRequest(resourceName);
			if (this.query == null){
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return null;
			}
			ret = sparqlQueryExec(this.query);
			if (ret != null){
					setStatus(Status.SUCCESS_OK);
			}
			
		}
		catch (Exception e) {
			setStatusError("Error answering the " + resourceName + " - LD4S.", e);
			e.printStackTrace();
			ret = null;
		}

		logger.info("REQUEST "+ this.uristr +" PROCESSING END - "+LD4SDataResource.getCurrentTime()); return ret;
	}

	@Override
	@Get	
	public Representation get(){
		setStatus(new Status(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, " only POST is allowed."));
		return null;
	}


	@Override
	@Put
	public Representation put(Form obj) {
		setStatus(new Status(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, " only POST is allowed."));
		return null;
	}



	@Override
	@Put
	public Representation put(JSONObject obj) {
		setStatus(new Status(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, " only POST is allowed."));
		return null;
	}



	@Override
	@Post
	public Representation post(Form obj) {
		setStatus(new Status(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, " only POST is allowed."));
		return null;
	}


	@Override
	@Delete
	public void remove() {
		setStatus(new Status(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, " only POST is allowed."));
	}
	
}
