package org.openiot.ld4s.client;

import org.restlet.data.Status;

public class LD4SClientException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6748613448986317777L;

	/**
	   * Thrown when an unsuccessful status code is returned from the Server.
	   *
	   * @param status The Status instance indicating the problem.
	   */
	  public LD4SClientException(Status status) {
	    super(status.getCode() + ": " + status.getDescription());
	  }

	  /**
	   * Thrown when an unsuccessful status code is returned from the Server.
	   *
	   * @param status The status instance indicating the problem.
	   * @param error The previous error.
	   */
	  public LD4SClientException(Status status, Throwable error) {
	    super(status.getCode() + ": " + status.getDescription(), error);
	  }

	  /**
	   * Thrown when some problem occurs with Client not involving the server.
	   *
	   * @param description The problem description.
	   * @param error The previous error.
	   */
	  public LD4SClientException(String description, Throwable error) {
	    super(description, error);
	  }

	  /**
	   * Thrown when some problem occurs with Client not involving the server.
	   *
	   * @param description The problem description.
	   */
	  public LD4SClientException(String description) {
	    super(description);
	  }
}
