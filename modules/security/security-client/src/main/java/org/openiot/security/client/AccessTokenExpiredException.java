package org.openiot.security.client;

public class AccessTokenExpiredException extends Exception {

	private static final long serialVersionUID = 3766459895858231682L;

	public AccessTokenExpiredException(String message) {
		super(message);
	}
}
