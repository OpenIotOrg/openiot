package org.openiot.security.client.rest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestfulOAuthService {

	private static Logger log = LoggerFactory.getLogger(RestfulOAuthService.class);
	private static final int STATUS_SUCCESS = 201;
	private static final int STATUS_DELETE_SUCCESS = 200;

	private String casOAuthURL;

	public RestfulOAuthService(String casOAuthUrl) {
		this.casOAuthURL = casOAuthUrl;
	}

	public Token getAccessToken(OAuthCredentialsRest credentials) {
		Token token = null;
		ClientRequest request = new ClientRequest(casOAuthURL);
		// String params = "username=" + credentials.getUsername() + "&password=" +
		// credentials.getPassword() + "&clientId=" + credentials.getKey() + "&secret="
		// + credentials.getSecret();
		request.formParameter("username", credentials.getUsername());
		request.formParameter("password", credentials.getPassword());
		request.formParameter("clientId", credentials.getKey());
		request.formParameter("secret", credentials.getSecret());
		try {
			ClientResponse<String> response = request.post(String.class);
			// Read output in string format
			log.debug("Status code: {}", response.getStatus());

			if (response.getStatus() == STATUS_SUCCESS) {
				Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(response.getEntity());
				if (matcher.matches())
					token = new Token(matcher.group(1), "");
			} else {
				log.warn("Invalid response code {} from CAS server!", response.getStatus());
				log.info("Response: {}", response.getEntity());
			}

			response.releaseConnection();
		} catch (Exception e) {
			log.error("Error while retrieving access token", e);
		}
		return token;
	}

	public boolean removeAccessToken(String token) {
		boolean deleted = false;
		ClientRequest request = new ClientRequest(casOAuthURL + "/" + token);
		log.debug("sending request to delete token {}", token);
		try {
			ClientResponse<String> response = request.delete(String.class);
			log.debug("Delete request sent for token {}", token);
			log.debug("Status code: {}", response.getStatus());
			if (response.getStatus() == STATUS_DELETE_SUCCESS) {
				deleted = true;
			} else {
				log.warn("Invalid response code {} from CAS server!", response.getStatus());
				log.info("Response: {}", response.getEntity());
			}
		} catch (Exception e) {
			log.error("Delete request error", e);
		}

		return deleted;
	}

	class UsernamePassword {
		private String username;
		private String password;

		public UsernamePassword(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		@Override
		public String toString() {
			return "username=" + username + "&password=" + password;
		}
	}

}
