package org.openiot.security.client;

public class AccessTokenExpiredException extends RuntimeException {

	private String token;

	private static final long serialVersionUID = 3766459895858231682L;

	public AccessTokenExpiredException(String token, String message) {
		super(message);
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
