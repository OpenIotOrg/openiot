package org.openiot.gsn.utils.graph;

public class NodeNotExistsExeption extends Exception {

	private static final long serialVersionUID = 2460464270692100205L;

	public NodeNotExistsExeption() {
		super();
	}

	public NodeNotExistsExeption(String message, Throwable cause) {
		super(message, cause);
	}

	public NodeNotExistsExeption(String message) {
		super(message);
	}

	public NodeNotExistsExeption(Throwable cause) {
		super(cause);
	}
	
}
