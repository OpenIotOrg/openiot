package org.openiot.ld4s.resource.device;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.openiot.ld4s.resource.LD4SApiInterface;
import org.openiot.ld4s.test.LD4STestHelper;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class TestDeviceRestApi extends LD4STestHelper {
	/** Resource ID necessary to store locally. */
	protected String resourceId = "a12b";

	/** LD4S currently running server host. */
	protected String local_uri = 
			"http://myrdebby.myrdebby.com:8182/ld4s/device/";
//			"http://spitfire-project.eu:8182/ld4s/device/";

	/** Resource URI necessary in case of remote resource hosting server. */
	protected String remote_uri = "http://www.example.org/device/remotea12b";

	/** Milliseconds shift from the base time. */
	protected String base_datetime = "12-08-28T19:03Z";

	/** Base OV host name. */
	protected String base_ov_name = "http://www.example1.org/ov/";

	/** Base host name. */
	protected String base_name = "http://www.example2.org/device/";

	/** Observed Property. */
	protected String observed_property = "powerconsumption" ; 
//		"http://www.example3.org/prop/temperature12";
//		"area";
//		"temperature";
//			"light";

	/** Temporarily: to enhance the link search for the observed property. */
	protected String foi = "room";
	
	/** Preferred type. */
	protected String type = "temperature sensor";
	
	/** Unit of Measurement. */
	protected String uom = 
//		"section";
//		"centigrade";
			"lux";
	
	/** Observed Value IDs. */
	protected String[] values = new String[]{"a12b", "x12y", "c23d", "e45f"};
	
	/** Temporal Sensor Property IDs. */
	protected String[] tsprops = new String[]{"id123", "id456", "id789", "id101"};

	/** User-defined criteria for linking. */
	protected String filters = "d=crossdomain%20OR%20geography" +
	"&s=NEAR(OR(shop1, shop2,shop3))UNDER(OR(home,d'avanzo,AND(italy, OR(palace, building), bari),south-italy))" +
	"OVER(AND(floor,garden,OR(metro,train),sky))" +
	"&th=OR(red,AND(cotton,tshirt),tissue,dress)";

	/** JSONObject contatining the above data. */
	protected JSONObject json = null;

	/** Form contatining the above data. */
	protected Form form = null;

//	/** Java object contatining the above data. */
//	protected OV ov = null;


	private void initJson(boolean isRemote, boolean isEnriched){
		this.json = new JSONObject();
		try {
			if (isRemote){
				json.append("uri", remote_uri);
			}else{
				json.append("uri", null);
			}
			if (isEnriched){
				json.append("context", filters);	
			}else{
				json.append("context", null);
			}
			json.append("base_datetime", base_datetime);
			json.append("base_name", base_name);
			json.append("base_ov_name", base_ov_name);
			json.append("observed_property", observed_property);
			json.append("foi", foi);
			
			json.append("con_company", "http://spitfire-project.eu/ontology/ns/en/electricitySupplyBoard");
			json.append("con_date", "2013-10-15");
			json.append("con_time", "04:00:34");
			json.append("con_country", "http://spitfire-project.eu/ontology/ns/en/greece");
			
			json.append("uom", uom);
			json.append("type", type);
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
			json.append("author", obj);
			JSONArray vals = new JSONArray();
			for (int i=0; i<values.length ;i++){
				vals.put(values[i]);
			}
			vals = new JSONArray();
			for (int i=0; i<tsprops.length ;i++){
				vals.put(tsprops[i]);
			}
			json.append("tsproperties", vals);
			vals = new JSONArray();
			for (int i=0; i<locations.length ;i++){
				vals.put(locations[i]);
			}
			json.append("locations", vals);
			json.append("location-name", location_name);
			json.append("location-coords", location_coords);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	private void initForm(boolean isRemote, boolean isEnriched){
		this.form = new Form();
		if (isRemote){
			form.set("uri", remote_uri);
		}else{
			form.set("uri", null);
		}
		if (isEnriched){
			form.set("context", filters);	
		}else{
			form.set("context", null);
		}
		form.set("base_datetime", base_datetime);
		form.set("base_name", base_name);
		form.set("base_ov_name", base_ov_name);
		form.set("observed_property", observed_property);
		form.set("uom", uom);
		form.set("type", type);
		for (int i=0; i<locations.length ;i++){
			form.set("locations", locations[i]);
		}
		for (int i=0; i<values.length ;i++){
			form.set("observation_values", values[i]);
		}
		for (int i=0; i<tsprops.length ;i++){
			form.set("tsproperties", tsprops[i]);
		}
	}
	
//	private void initOV(boolean isRemote, boolean isEnriched){
//		String remote = null, filters = null;
//		try {
//			if (isRemote){
//				remote = this.remote_uri;
//			}
//			if (isEnriched){
//				filters = this.filters;
//			}
//			this.ov = new OV(remote, values, base_datetime, resource_time, start_range, end_range, filters, local_uri);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//	}
	

	/**
	 * Test PUT {host}/ov/{id}
	 * requirement: resource stored locally + no Linked Data enrichment
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testPut() throws Exception {
		System.out.println("Test Put - java object payload");
		initJson(false, false); 
		ClientResource cr = new ClientResource(local_uri+resourceId);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		Representation response = cr.put(json);
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
		assertTrue(status.isSuccess());
		
		String rdf = response.getText();
		System.out.println("\n\n\n==============\nTesting DEVICE JSON PUT- " 
				+"(annotation to be stored locally) "
				+ "sent : "+json
				+local_uri+resourceId+"==============\n"+rdf);
		
		response.release();	
	}
	
	/**
	 * Test PUT {host}/ov/{id}
	 * requirement: resource stored locally + no Linked Data enrichment
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testFormPut() throws Exception {
		System.out.println("Test Put - form payload");
		initForm(false, false); 
		ClientResource cr = new ClientResource(local_uri+resourceId);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		Representation response = cr.put(form); 
		System.out.println(response.getText());
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
		assertTrue(status.isSuccess());
		
		response.release();	
	}
	
	/**
	 * Test PUT {host}/ov/{id}
	 * requirement: resource stored locally + no Linked Data enrichment
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testJSONPut() throws Exception {
		System.out.println("Test Put - JSON payload");
		initJson(false, false); 
		ClientResource cr = new ClientResource(local_uri+resourceId);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		Representation response = cr.put(json); 
		System.out.println(response.getText());
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
		assertTrue(status.isSuccess());
		
		String rdf = response.getText();
		System.out.println("\n\n\n==============\nTesting DEVICE JSON PUT " +
				"(annotation to be soterd locally)\n"
				+ "sent : "+json
				+local_uri+resourceId+"==============\n"+rdf);
		
		response.release();	
	}

	/**
	 * Test GET {host}/ov/{id}
	 * requirement: resource stored locally
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testGet() throws Exception {
		System.out.println("Test Get");
		ClientResource cr = new ClientResource(local_uri+resourceId);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		List<Preference<MediaType>> accepted = new LinkedList<Preference<MediaType>>();
		accepted.add(new Preference<MediaType>(MediaType.APPLICATION_RDF_TURTLE));
		cr.getClientInfo().setAcceptedMediaTypes(accepted);
		Representation resp = cr.get();
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
		
		String rdf = resp.getText();
		System.out.println("\n\n\n==============\nTesting DEVICE GET - "
				+local_uri+resourceId+"==============\n"+rdf);
	}

	/**
	 * Test GET {host}/ov/{id}?d=&s=&th=&trange=
	 * requirement: resource stored locally
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testLDGet() throws Exception {
		System.out.println("Test Get with query string appended");
		ClientResource cr = new ClientResource(local_uri+resourceId+"?"+filters);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		List<Preference<MediaType>> accepted = new LinkedList<Preference<MediaType>>();
		accepted.add(new Preference<MediaType>(MediaType.APPLICATION_RDF_TURTLE));
		cr.getClientInfo().setAcceptedMediaTypes(accepted);
		Representation resp = cr.get();

		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
		
		String rdf = resp.getText();
		System.out.println("\n\n\n==============\nTesting DEVICE LD GET - " 
				+ "sent : "+json
				+local_uri+resourceId+"?"+filters+"==============\n"+rdf);
	}

	/**
	 * Test DELETE {host}/ov/{id}
	 * requirement: resource stored locally
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testDelete() throws Exception {
		System.out.println("Test Delete");
		ClientResource cr = new ClientResource(local_uri+resourceId);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		LD4SApiInterface resource = cr.wrap(LD4SApiInterface.class);
		resource.remove();

		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
		
		System.out.println("\n\n\n==============\nTesting DEVICE DELETE - "
				+local_uri+resourceId+"==============");
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
	}

	/**
	 * Test POST {host}/ov/{id}
	 * requirement: resource stored locally
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testFormPostLocalResource() throws Exception {
		System.out.println("Test POST local and with no external links - Form payload");
		initForm(false, false);
		System.out.println(form.toString());		 
		ClientResource cr = new ClientResource(local_uri+resourceId);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		cr.post(form);
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
	}

	/**
	 * Test POST {host}/ov/{id}
	 * requirement: resource stored locally + Linked Data enrichment
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testLDPostLocalResource() throws Exception {
		System.out.println("Test POST local and with external links - Java object payload");
		initJson(false, true);
		ClientResource cr = new ClientResource(local_uri+resourceId);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		Representation resp = cr.post(json);
		
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
		assertTrue(status.isSuccess());
		
		String rdf = resp.getText();
		System.out.println("\n\n\n==============\nTesting DEVICE LD JSON POST - " 
				+"(annotation to be stored locally) "
				+ "sent : "+json
				+local_uri+resourceId+"==============\n"+rdf);
	}

	/**
	 * Test POST {host}/ov
	 * requirement: resource stored remotely
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testJSONPostRemoteResource() throws Exception {
		System.out.println("Test POST remote and with no external links - JSON payload");
		initJson(true, false);
		System.out.println(json.toString());		 
		ClientResource cr = new ClientResource(local_uri);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		Representation resp = cr.post(json);
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
		
		String rdf = resp.getText();
		System.out.println("\n\n\n==============\nTesting DEVICE JSON POST " +
				"(annotation to be soterd remotely)\n"
				+ "sent : "+json
				+local_uri+"==============\n"+rdf);
	}

	/**
	 * Test POST {host}/ov
	 * requirement: resource stored remotely + Linked Data enrichment
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testLDPostRemoteResource() throws Exception {
		System.out.println("Test POST remote and with external links - Java object payload");
		initJson(true, true);
		ClientResource cr = new ClientResource(local_uri);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		Representation resp = cr.post(json);
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
		assertTrue(status.isSuccess());
		
		String rdf = resp.getText();
		System.out.println("\n\n\n==============\nTesting DEVICE LD JSON POST - " 
				+"(annotation to be stored remotely) "
				+ "sent : "+json
				+local_uri+"==============\n"+rdf);
	}
}
