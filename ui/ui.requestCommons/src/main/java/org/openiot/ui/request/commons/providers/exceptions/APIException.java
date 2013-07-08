package org.openiot.ui.request.commons.providers.exceptions;

public class APIException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public APIException(String message) {
		super(message);
	}
	
	public APIException(Throwable cause) {
		super(cause);
	}
}
