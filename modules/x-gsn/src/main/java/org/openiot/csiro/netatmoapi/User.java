package org.openiot.csiro.netatmoapi;

import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;

public class User {
	
	private String clientID;
	private String clientSecret;
	private String userName;
	private String password;
	
	
	public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	
	
	public User(String clientID, String clientSecret, String userName,
			String password) {
		super();
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.userName = userName;
		this.password = password;
	}
	public User(){
		this.clientID = "";
		this.clientSecret = "";
		this.userName = "";
		this.password = "";
	}
	
	public OAuthClientRequest getAuthRequest() throws OAuthSystemException{
		OAuthClientRequest request = null;
		
		request = OAuthClientRequest
				.tokenLocation(Constants.REQUEST_TOKEN_URI)
				 .setGrantType(GrantType.PASSWORD)
				.setClientId(this.clientID)
				.setClientSecret(this.clientSecret)
				.setUsername(this.userName)
				.setPassword(this.password)
				.buildBodyMessage();
				
		return request;
	}
	
	

}
