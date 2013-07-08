package org.openiot.ui.request.commons.providers.exceptions;

public class APICommunicationException extends APIException{

	private static final long serialVersionUID = 1L;
	
	public APICommunicationException(String message) {
		super(message);
	}
	
	public APICommunicationException(Throwable cause) {
		super(cause);
	}
}
