package org.openiot.ld4s.resource.measurement_prop;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.openiot.ld4s.resource.LD4SApiInterface;
import org.openiot.ld4s.test.LD4STestHelper;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class TestMPRestApi extends LD4STestHelper {
	/** Resource ID necessary to store locally. */
	protected String resourceId = "speed";
	
	/** Measurement Property type. */
	protected String type = "Latency";

	/** LD4S currently running server host. */
	protected String local_uri = "http://localhost:8182/ld4s/meas_prop/";

	/** Resource URI necessary in case of remote resource hosting server. */
	protected String remote_uri = "http://www.example.org/meas_prop/latency";

	/** Milliseconds shift from the base time as a resource creation time point. */
	protected String resource_time = "23846";

	/** User-defined criteria for linking. */
	protected String filters = "d=crossdomain%20OR%20geography" +
	"&s=NEAR(OR(shop1, shop2,shop3))UNDER(OR(home,d'avanzo,AND(italy, OR(palace, building), bari),south-italy))" +
	"OVER(AND(floor,garden,OR(metro,train),sky))" +
	"&th=OR(red,AND(cotton,tshirt),tissue,dress)";

	/** JSONObject contatining the above data. */
	protected JSONObject json = null;

	/** Form contatining the above data. */
	protected Form form = null;

	private String observed_property2 = "pressure";

	private String predicate2 = SptVocab.HAS_MAX_VALUE.getURI();
	
//	private String predicate3 = SptVocab.HAS_MIN_VALUE.getURI();

	private String value2 = "0.9";
	
//	private String value3 = "0.4";
	
	private String uom2 = "bar";

	private String predicate1 = SptVocab.HAS_VALUE_RANGE.getURI();

	private String value1 = "0.8_2";

	private String uom1 = "g/m3";
	
	private String observed_property1 = "humidity";

	
	private String uom0 = "sec";
	
	private String predicate0 = SptVocab.HAS_VALUE_RANGE.getURI();
	
	private String value0 = "0.5_1";

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
			json.append("conditions", initConditions());
			json.append("predicate", predicate0);
			json.append("value", value0);
			json.append("uom", uom0 );
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}



	/**
	 * Init Example: 
	 * This measurement property (e.g., speed) hasMinValue 0.6 s/t 
	 * whenever humidity hasValueRange between 0.8 and 2 g/m3 
	 * or temperature hasMaxValue 20 celsius degrees
	 * 
	 * considering g/m3 as the unit of measurement; 
	 * @return
	 * @throws JSONException
	 */
	private JSONArray initConditions() throws JSONException {
		JSONArray jarr = new JSONArray();
		JSONObject jobj = initCondition(observed_property1, predicate1, value1, uom1);
		jarr.put(jobj);
		jobj = initCondition(observed_property2, predicate2, value2, uom2);
		jarr.put(jobj);
		return jarr;
	}
	
	private JSONObject initCondition(String op, String pred, String val, String uom) throws JSONException{
		JSONObject jobj = new JSONObject();
		jobj.append("oncondition_property", op);
		jobj.append("oncondition_predicate", pred);
		jobj.append("oncondition_value", val);
		jobj.append("oncondition_uom", uom);
		return jobj;
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
		form.set("resource_time", resource_time);
		form.set("start_range", start_range);
		form.set("end_range", end_range);
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
		System.out.println("RESPONSE to GET REQUEST***\n"+resp.getText());
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
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
		System.out.println("RESPONSE to GET REQUEST***\n"+resp.getText());
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
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
		System.out.println(resp.getText());
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
		assertTrue(status.isSuccess());
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
		System.out.println(resp.getText());
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
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
		System.out.println(resp.getText());
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
		assertTrue(status.isSuccess());
	}
}
