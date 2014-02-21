package org.openiot.security.client.rest;

import org.junit.Test;
import static org.junit.Assert.*;
import org.openiot.security.client.OAuthorizationCredentials;

public class AccessControlUtilRestTest {

	@Test
	public void testLogin() {
		AccessControlUtilRest accessControlUtil = AccessControlUtilRest.getInstance();
		OAuthorizationCredentials credentials = accessControlUtil.login("admin", "secret");
		assertNotNull(credentials);
	}

}
