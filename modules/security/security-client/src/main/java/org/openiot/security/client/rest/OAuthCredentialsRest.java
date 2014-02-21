package org.openiot.security.client.rest;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.credentials.OAuthCredentials;

public class OAuthCredentialsRest extends OAuthCredentials {

	private static final long serialVersionUID = 3072246886362233086L;

	public OAuthCredentialsRest(String username, String password, final String clientName) {
		super(null, username, password, clientName);
	}

	public String getUsername() {
		return getToken();
	}

	public String getPassword() {
		return getVerifier();
	}

	@Override
	public String toString() {
		return CommonHelper.toString(this.getClass(), "username", getUsername(), "password", "*****", "clientName", getClientName());
	}

}
