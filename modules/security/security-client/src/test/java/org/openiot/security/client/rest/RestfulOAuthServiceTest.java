package org.openiot.security.client.rest;

import org.junit.Test;
import org.scribe.model.Token;
import static org.junit.Assert.*;

public class RestfulOAuthServiceTest {

	@Test
	public void testGetAccessToken() {
		String casOAuthUrl = "https://localhost:8443/openiot-cas/openiot1/tickets";
		RestfulOAuthService service = new RestfulOAuthService(casOAuthUrl);

		OAuthCredentialsRest credentials = new OAuthCredentialsRest("admin", "secret", "dummyClientName", "testservice1", "testsecret1");
		Token accessToken = service.getAccessToken(credentials);

		assertNotNull(accessToken);
	}
}
