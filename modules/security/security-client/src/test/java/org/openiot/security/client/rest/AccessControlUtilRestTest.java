package org.openiot.security.client.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;

public class AccessControlUtilRestTest {

	@Test
	public void testLogin() {
		AccessControlUtil accessControlUtil = AccessControlUtil.getRestInstance();
		OAuthorizationCredentials credentials = accessControlUtil.login("admin", "secret");
		assertNotNull(credentials);
	}

	@Test
	public void testUserId() {
		AccessControlUtil accessControlUtil = AccessControlUtil.getRestInstance();
		OAuthorizationCredentials credentials = accessControlUtil.login("admin", "secret");
		assertEquals("admin", credentials.getUserId());
	}

	@Test
	public void testLogout() {
		AccessControlUtil accessControlUtil = AccessControlUtil.getRestInstance();
		accessControlUtil.login("admin", "secret");
		accessControlUtil.logout();
		assertFalse(SecurityUtils.getSubject().isAuthenticated());
	}

	@Test
	public void testHasPermission() {
		AccessControlUtil accessControlUtil = AccessControlUtil.getRestInstance();
		accessControlUtil.login("admin", "secret");
		assertTrue("Admin must have all permissions", accessControlUtil.hasPermission("*"));
	}

	@Test
	public void testHasPermissionForTarget() {
		AccessControlUtil accessControlUtil = AccessControlUtil.getRestInstance();
		accessControlUtil.login("admin", "secret");
		OAuthorizationCredentials credentials = accessControlUtil.getOAuthorizationCredentials();
		assertTrue("Admin must have all permissions on testservice2", accessControlUtil.hasPermission("*", "testservice2", credentials));
	}

}
