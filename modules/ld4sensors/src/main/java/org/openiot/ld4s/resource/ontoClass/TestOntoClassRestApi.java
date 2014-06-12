package org.openiot.ld4s.resource.ontoClass;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.openiot.ld4s.test.LD4STestHelper;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class TestOntoClassRestApi extends LD4STestHelper {
	
	/** LD4S currently running server host. */
	protected String local_uri = "http://localhost:8182/ld4s/ontology_class/";
	
	private String id = "MyWindSensorType";
	
	private String superClass = "http://purl.oclc.org/NET/ssnx/ssn#Sensor";
		
	// alternatives are: 
	// Class (when there's no specific super-class), 
	// Datatype (when its instances are literals)
	private String classType = "Subclass";   
	
	private JSONObject initJSON(){
		JSONObject payload = new JSONObject();
		try {
			payload.append("id", id);
			
			JSONArray allTypes = new JSONArray(); 			
			JSONObject currType = new JSONObject();
			currType.append("type_choice", classType);
			currType.append("type_super", superClass);			
			allTypes.put(currType); //in this case we only assign 1 type (as a super-class)
			
			payload.append("types", allTypes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return payload;
	}
	
	
	/**
	 * equivalent curl command:
	 * curl --request post --data '{"id":"MyWindSensorType","types":[[{"type_choice":"Subclass", "type_super":"http://purl.oclc.org/NET/ssnx/ssn#Sensor"}]]}' --header "Accept: text/n-triples" --header "Content-type: application/json" http://localhost:8182/ld4s/ontology_class
	 * @throws IOException
	 */	
	@Test
	public void testNewTypeDefinitionRequest () throws IOException{
		JSONObject payload = initJSON();
		ClientResource cr = new ClientResource(local_uri);
		//ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, 
				//user_password);
		//cr.setChallengeResponse(authentication);
		List<Preference<MediaType>> accepted = new LinkedList<Preference<MediaType>>();
		accepted.add(new Preference<MediaType>(MediaType.TEXT_RDF_NTRIPLES));
		cr.getClientInfo().setAcceptedMediaTypes(accepted);
		Representation response = cr.post(payload); 
		
		Status status = cr.getStatus();
		System.out.println(status.getCode()+ " - "+cr.getStatus().getDescription());
		assertTrue(status.isSuccess());
		
		String rdf = response.getText();
		System.out.println("\n\n\n==============\nTesting Ontology_Class JSON POST- " 
				+"(annotation to be stored locally) "
				+ "sent : "+payload
				+local_uri+"==============\n"+rdf);
		
		response.release();	
		
		
	}
	
}
