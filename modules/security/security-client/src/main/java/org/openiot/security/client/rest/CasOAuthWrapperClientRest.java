package org.openiot.security.client.rest;

import java.util.Iterator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.scribe.builder.api.CasOAuthWrapperApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ExtendedOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * Instead implement a Simple Realm that does the authentication!
 * 
 * @author Mehdi Riahi
 * 
 */
public class CasOAuthWrapperClientRest extends BaseOAuth20Client<CasOAuthWrapperProfile> {

	private String casOAuthUrl;
	private String casOAuthRestUrl;

	public CasOAuthWrapperClientRest() {
	}

	public CasOAuthWrapperClientRest(final String key, final String secret, final String casOAuthUrl) {
		setKey(key);
		setSecret(secret);
		this.casOAuthUrl = casOAuthUrl;
	}

	@Override
	protected CasOAuthWrapperClient newClient() {
		final CasOAuthWrapperClient newClient = new CasOAuthWrapperClient();
		newClient.setCasOAuthUrl(this.casOAuthUrl);
		return newClient;
	}

	@Override
	protected void internalInit() {
		super.internalInit();
		CommonHelper.assertNotBlank("casOAuthUrl", this.casOAuthUrl);
		this.service = new ExtendedOAuth20ServiceImpl(new CasOAuthWrapperApi20(this.casOAuthUrl, false), new OAuthConfig(this.key, this.secret,
				this.callbackUrl, SignatureType.Header, null, null), this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort);
	}

	@Override
	protected String getProfileUrl() {
		return this.casOAuthUrl + "/profile";
	}

	@Override
	protected CasOAuthWrapperProfile extractUserProfile(final String body) {
		final CasOAuthWrapperProfile userProfile = new CasOAuthWrapperProfile();
		JsonNode json = JsonHelper.getFirstNode(body);
		if (json != null) {
			userProfile.setId(JsonHelper.get(json, "id"));
			json = json.get("attributes");
			if (json != null) {
				final Iterator<JsonNode> nodes = json.iterator();
				while (nodes.hasNext()) {
					json = nodes.next();
					final String attribute = json.fieldNames().next();
					userProfile.addAttribute(attribute, JsonHelper.get(json, attribute));
				}
			}
		}
		return userProfile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Token getAccessToken(final OAuthCredentials credentials) {
		RestfulOAuthService service = new RestfulOAuthService(casOAuthRestUrl);
		final Token accessToken = service.getAccessToken((OAuthCredentialsRest) credentials);
		logger.debug("accessToken : {}", accessToken);
		return accessToken;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OAuthCredentials getOAuthCredentials(final WebContext context) {
		return new OAuthCredentials("RESTful-no-code!", getName());
	}

	public String getCasOAuthUrl() {
		return this.casOAuthUrl;
	}

	public void setCasOAuthUrl(final String casOAuthUrl) {
		this.casOAuthUrl = casOAuthUrl;
	}

	public String getCasOAuthRestUrl() {
		return casOAuthRestUrl;
	}

	public void setCasOAuthRestUrl(String casOAuthRestUrl) {
		this.casOAuthRestUrl = casOAuthRestUrl;
	}

	@Override
	protected boolean requiresStateParameter() {
		return false;
	}

	@Override
	protected boolean hasBeenCancelled(final WebContext context) {
		return false;
	}
}
