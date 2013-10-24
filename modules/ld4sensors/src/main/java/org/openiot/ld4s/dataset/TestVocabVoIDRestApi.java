package org.openiot.ld4s.dataset;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openiot.ld4s.client.LD4SClient;
import org.openiot.ld4s.test.LD4STestHelper;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

public class TestVocabVoIDRestApi  extends LD4STestHelper{
	/**
	   * Test that unauthenticated GET {host}/void returns the dataset void description.
	   *
	   * @throws Exception If problems occur.
	   */
	  @Test
	  public void testUnAuthenticatedVoid() throws Exception {
	    LD4SClient client = new LD4SClient(null, null);
	    Response response = client.makeRequest("void", Method.GET, MediaType.ALL, null);
	    System.out.println(response.getEntityAsText());
	    Status status = response.getStatus();
	    assertTrue(status.isSuccess());
	  }
	  
}
