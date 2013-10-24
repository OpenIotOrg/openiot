package org.openiot.ld4s.resource.sparql;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Test;
import org.openiot.ld4s.test.LD4STestHelper;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class TestSparqlEndpoint extends LD4STestHelper{
	
	/**
	 * Test that  PUT {host}/resource/ov/{resource_id}
	 *
	 * @throws Exception If problems occur.
	 */
	@Test
	public void testSelect() throws Exception {
		String filters = "PREFIX spt: <http://spitfire-project.eu/ontology/ns/> SELECT * {GRAPH ?g {?x a ?t}}"; 
//	+ "SELECT * { GRAPH ?g {?x ?y ?z} } LIMIT 10";
//		filters = URLEncoder.encode(filters, "utf-8");
		ClientResource cr = new ClientResource(
				"http://localhost:8182/ld4s/sparql");
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		List<Preference<MediaType>> accepted = new LinkedList<Preference<MediaType>>();
		accepted.add(new Preference<MediaType>(MediaType.APPLICATION_RDF_XML));
		cr.getClientInfo().setAcceptedMediaTypes(accepted);
		JSONObject json = new JSONObject();
		json.append("query", filters);
		Representation resp = cr.post(json);
		System.out.println("RESPONSE to the SPARQL QUERY***\n"+resp.getText());
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());            
		assertTrue(status.isSuccess());
	}

	


}
