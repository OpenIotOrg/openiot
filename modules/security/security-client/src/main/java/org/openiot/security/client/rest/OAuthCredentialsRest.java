package org.openiot.security.client.rest;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.credentials.OAuthCredentials;

public class OAuthCredentialsRest extends OAuthCredentials {

	private static final long serialVersionUID = 3072246886362233086L;
	private String key;
	private String secret;

	public OAuthCredentialsRest(String username, String password, String clientName, String key, String secret) {
		super(null, username, password, clientName);
		this.secret = secret;
		this.key = key;
	}

	public String getUsername() {
		return getToken();
	}

	public String getPassword() {
		return getVerifier();
	}

	public String getSecret() {
		return secret;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return CommonHelper.toString(this.getClass(), "username", getUsername(), "password", "*****", "clientName", getClientName(), "key", getKey(), "secret",
				getSecret());
	}

}
