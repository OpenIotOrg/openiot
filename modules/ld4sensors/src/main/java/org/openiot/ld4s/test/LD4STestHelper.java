package org.openiot.ld4s.test;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.openiot.ld4s.lod_cloud.Person;
import org.openiot.ld4s.server.Server;
import org.openiot.ld4s.server.ServerProperties;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;
import org.restlet.security.Role;

/**
 * Provides a helper class to facilitate JUnit testing.
 *
 * @author Myriam Leggieri
 */
public class LD4STestHelper {
	/** The admin username. */
	protected static final String admin = "admin";
	/** The admin password. */
	protected static final String admin_password = "admin";
	/** The testing user. */
	protected static final String user = "scott";
	/** The testing user password. */
	protected static final String user_password = "tiger";
	/** The testing user role. */
	protected static final Role user_role = ServerProperties.PUBLISHER;
	/** The LD4S server used in these tests. */
	private static Server ld4sServer;

	/** Milliseconds shift from the base time as a starting point of a time range. */
	protected String start_range = "5800";

	/** Milliseconds shift from the base time as an ending point of a time range. */
	protected String end_range = "10321";
	
	/** Milliseconds shift from the base time. */
	protected String base_datetime = "12-08-23T19:03Z";

	/** Locations in the form <spacerel # <name | lat_long>>. */
	protected String[] locations = new String[]{" # madrid", "near # 12.009_24.500"
			, "near # 19.489_23.52", "in # spain"};
	
	protected String location_name = "Patras";
	
	protected String location_coords = "38.24444_21.73444";
	
	protected Person author = new Person(
//			"Ioannis", "Chatzigiannakis", null, null, null, null, null);
			"Manfred", "Hauswirth", null, null, null, null, null);
	
	protected MediaType media = new MediaType("application/rdf+json");
	
	
	protected ClientResource initClient(Protocol protocol, int timeout, String uri){
		Client client = new Client(protocol);

		client.setConnectTimeout(timeout);

		ClientResource cr = new ClientResource(uri);

		cr.setNext(client);

		return cr;
	}
	
	

	/**
	 * Constructor.
	 */
	public LD4STestHelper() {
		// Does nothing.
	}

	/**
	 * Starts the server going for these tests.
	 * @throws Exception If problems occur setting up the server.
	 */
	@BeforeClass public static void setupServer() throws Exception {
		// Create a testing version of the Ld4S.
		LD4STestHelper.ld4sServer = Server.newInstance();
	}

	/**
	 * Returns the hostname associated with this LD4S test server.
	 *
	 * @return The host name, including the context root.
	 */
	protected String getLD4SHostName() {
		return LD4STestHelper.ld4sServer.getHostName();
	}


	/**
	 * Returns the LD4S server instance.
	 *
	 * @return The LD4S server instance.
	 */
	protected Server getLD4SServer() {
		return LD4STestHelper.ld4sServer;
	}

	protected JSONObject getAuthor(Person author) throws JSONException{
		JSONObject obj = new JSONObject();
		if (author.getFirstname() != null){
			obj.append("firstname", author.getFirstname());
		}
		if (author.getSurname() != null){
			obj.append("surname", author.getSurname());
		}
		if (author.getEmail() != null){
			obj.append("email", author.getEmail());
		}
		if (author.getHomepage() != null){
			obj.append("homepage", author.getHomepage());
		}
		if (author.getNickname() != null){
			obj.append("nickname", author.getNickname());
		}
		if (author.getWeblog() != null){
			obj.append("weblog", author.getWeblog());
		}
		return obj;
	}
	
}
