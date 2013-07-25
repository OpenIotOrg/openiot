package org.openiot.gsn.utils.graph;

public class EdgeExistsException extends Exception {

	private static final long serialVersionUID = 6890337360223725923L;

	public EdgeExistsException() {
		super();
	}

	public EdgeExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public EdgeExistsException(String message) {
		super(message);
	}

	public EdgeExistsException(Throwable cause) {
		super(cause);
	}
	
}
