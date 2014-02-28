package org.openiot.security.oauth;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jasig.cas.authentication.principal.Credentials;

public class RestTGTRequestCredentials implements Credentials {

	private static final long serialVersionUID = -7502633890742130952L;

	/** The username. */
	@NotNull
	@Size(min = 1, message = "required.username")
	private String username;

	/** The password. */
	@NotNull
	@Size(min = 1, message = "required.password")
	private String password;

	@NotNull
	@Size(min = 1, message = "required.clientId")
	private String clientId;

	@NotNull
	@Size(min = 1, message = "required.secret")
	private String secret;

	/**
	 * @return Returns the password.
	 */
	public final String getPassword() {
		return this.password;
	}

	/**
	 * @param password
	 *            The password to set.
	 */
	public final void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return Returns the userName.
	 */
	public final String getUsername() {
		return this.username;
	}

	/**
	 * @param userName
	 *            The userName to set.
	 */
	public final void setUsername(final String userName) {
		this.username = userName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String toString() {
		return "[username: " + this.username + ", clientId: " + clientId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((secret == null) ? 0 : secret.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestTGTRequestCredentials other = (RestTGTRequestCredentials) obj;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (secret == null) {
			if (other.secret != null)
				return false;
		} else if (!secret.equals(other.secret))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
