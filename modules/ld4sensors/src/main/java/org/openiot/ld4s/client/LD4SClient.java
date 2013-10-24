package org.openiot.ld4s.client;

import org.openiot.ld4s.server.Server;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

public class LD4SClient {
	private static String HOST = "http://localhost:8182/ld4s/";
//	private static final String CONTEXT_ROOT = "ld4s";
//	private static final String AUTHENTICATION_MESS = "LD4Sensors - Authenticated";
	
	private String user = null;
	private String passw = null;
	
	public LD4SClient(String user, String passw, String host){
//		this.HOST = host;
		this.user = user;
		this.passw = passw;
	}

	public LD4SClient(String user, String passw){
		this(user, passw, LD4SClient.HOST);
	}
	
	
	
	public static void main(String[] args) throws Exception {
		Server.newInstance();		
//		String requestString = "/test";
//		LD4SClient.makeRequest(LD4SClient.HOST, Method.GET, requestString, null, 
//				"scott", "tiger", MediaType.ALL, ChallengeScheme.HTTP_BASIC, true);
	
    }
	
	  /**
	   * Does the housekeeping for making HTTP requests to the specified service using the
	   * specified method and asking for the specified media type. It's also possible to send an object
	   * entity to host and to log on it using the specified user and password.
	   *
	   * @param host host.
	   * @param method method.
	   * @param requestString request.
	   * @param entity object entity to send.
	   * @param user user to log.
	   * @param passw password.
	   * @return The Response instance returned from the specified server.
	   */
	  public static final Response makeRequest(final String host, final Method method,
	      final String requestString, final Representation entity, final String user, 
	      final String passw, MediaType media, ChallengeScheme scheme, boolean isTraceEnabled) {
	    Reference reference = new Reference(host + requestString);
	    Request request = (entity == null) ? new Request(method, reference) : new Request(method,
	        reference, entity);
	    request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
	    if (user != null && passw != null){
	    	ChallengeResponse authentication = new ChallengeResponse(scheme, user, passw);
	    	request.setChallengeResponse(authentication);
	    }
	    if (isTraceEnabled) {
	      System.out.println("LD4Sensors " + "Tracing: " + method + " " + reference);
	      if (entity != null) {
	        try {
	          System.out.println(entity.getText());
	        }
	        catch (Exception e) {
	          System.out.println("  Problems with " + "getText() on entity.");
	        }
	      }
	    }
	    Client client = new Client(Protocol.HTTP);
	    Response response = client.handle(request);
	    if (isTraceEnabled) {
	      Status status = response.getStatus();
	      System.out.println("  => " + status.getCode() + " " + status.getDescription());
	    }
	    return response;
	  }
	  
	  
	 
	  
	  /**
	   * Ping this instance of the LD4S service, eventually checking the authentication
	   * whenever user and password had been initialized for this instance of the clien
	   * and throwing an LD4SClientException
	   * if these user and password are not valid credentials. 
	   *
	   * @return LD4SClient instance
	   * @throws LD4SClientException If authentication is not successful.
	   */
	  public final synchronized Response makeRequest(String requestStrEnd, Method method,
			  MediaType mt, Representation entity)
	      throws LD4SClientException {
	    
		  Response response = LD4SClient.makeRequest(LD4SClient.HOST, method, requestStrEnd, entity, 
					this.user, this.passw, mt, ChallengeScheme.HTTP_BASIC, true);
		if (!response.getStatus().isSuccess()) {
	      throw new LD4SClientException(response.getStatus());
	    }
	    try {
	     
	    }
	    catch (Exception e) {
	      throw new LD4SClientException("Bad response", e);
	    }
	    
	    return response;
	  }
	  
}
