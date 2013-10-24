package org.openiot.ld4s.lod_cloud;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.security.User;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class SearchRouter {

	protected String baseHost = null;

	protected Context context = null;

	protected User author = null;
	
	protected Resource authorResource = null;
	
	protected Resource from_resource = null;

	public SearchRouter(String baseHost, Context context, User author,
			Resource from_resource){
		this.baseHost = baseHost;
		this.context = context;
		this.author = author;
		this.from_resource = from_resource;
	}	

	
	public abstract Model start() throws Exception;
	
	protected static String makeRequest(String query, MediaType mediatype){
		String response = null;
		try{
			Response resp = getRequestResponseFollowRedirects(query, mediatype, 1);
			if (resp != null){
				Status status = resp.getStatus();
				if (status.getCode() == 200) {
					if (resp != null && resp.getEntity() != null) {
						response = resp.getEntity().getText();
						System.out.println("  => " + status.getCode() + " " + status.getDescription());
					}
				}else {
					System.err.println("Request failed:"+status.getCode()+" - "+status.getDescription());
				}
			}
		}catch (Exception e) {
			System.err.println("Unable to perform search\n"+e.getMessage());
			e.printStackTrace();
			return null;
		}
		return response;
	}
	
	protected static Response getRequestResponseFollowRedirects(String query, MediaType mediatype,
			int times){
		Response resp = null;
		if (times > 0){
			resp = getRequestResponse(query, mediatype);
			if (resp != null && resp.getStatus().equals(Status.REDIRECTION_SEE_OTHER)){
				resp = getRequestResponseFollowRedirects(resp.getLocationRef().toUrl().toExternalForm(), 
						mediatype, --times);
			}
		}
		return resp;
	}
	
	protected static Response getRequestResponse(String query, MediaType mediatype){
		Response resp = null;
		System.out.println("LD4S Tracing: " + Method.GET + " " + query);
		Reference reference = new Reference(query);
		Request request = null;
		request = new Request(Method.GET, reference);
		request.getClientInfo().getAcceptedMediaTypes().clear();
		request.getClientInfo().getAcceptedMediaTypes().add(
				new Preference<MediaType>(mediatype));
		try{
			Client client = new Client(Protocol.HTTP);
			//------------------------------
			client.setConnectTimeout(10000);
			//------------------------------
			resp = client.handle(request);
			Status status = resp.getStatus();
			if (status.getCode() != 200) {
				System.err.println("Request failed:"+status.getCode()+" - "+status.getDescription());
			}
		}catch (Exception e) {
			System.err.println("Unable to perform search\n"+e.getMessage());
			e.printStackTrace();
			return null;
		}
		return resp;
	}

	/**
	 * Foaf Search for an author URI
	 * @param author
	 */
	public void setAuthor(User author) {
		this.author = author;
	}

	/**
	 * Returns a resource of the author's foaf (temporarily returns just a literal)
	 * @return
	 */
	public Literal getAuthor() {
		if (author.getIdentifier() != null){
			return from_resource.getModel().createLiteral(author.getIdentifier());
		}
		return null;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public void setBaseHost(String baseHost) {
		this.baseHost = baseHost;
	}

	public String getBaseHost() {
		return baseHost;
	}
}
