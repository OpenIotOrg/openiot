package org.openiot.security.client;

import io.buji.pac4j.ShiroWebContext;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessControlUtil {

	private static Logger logger = LoggerFactory.getLogger(AccessControlUtil.class);

	private static AccessControlUtil instance;

	private CasOAuthClientRealm casOAuthClientRealm;

	private CasOAuthWrapperClient client;

	private AuthorizationManager authorizationManager;

	public static AccessControlUtil getInstance() {
		if (instance == null)
			instance = new AccessControlUtil();
		return instance;
	}

	private AccessControlUtil() {

	}

	public boolean hasPermission(String perm) {
		return SecurityUtils.getSubject().isPermitted(perm);
	}

	public boolean hasRole(String role) {
		return SecurityUtils.getSubject().hasRole(role);
	}

	public String getLoginUrl(HttpServletRequest req, HttpServletResponse resp) {
		return getClient().getRedirectionUrl(new ShiroWebContext(req, resp), true);
	}

	public AuthorizationManager getAuthorizationManager() {
		if (authorizationManager == null) {
			authorizationManager = new AuthorizationManager();
			authorizationManager.setClient((CasOAuthWrapperClient) getClient());
			authorizationManager.setPermissionsURL(getCasOAuthClientRealm().getPermissionsURL());
		}
		return authorizationManager;
	}

	public OAuthorizationCredentials getOAuthorizationCredentials() {
		final Subject subject = SecurityUtils.getSubject();
		if(!subject.isAuthenticated())
			return null;
		final CasOAuthWrapperProfile profile = subject.getPrincipals().oneByType(CasOAuthWrapperProfile.class);
		String accessToken = profile.getAccessToken();
		String clientId = getClient().getKey();
		return new OAuthorizationCredentials(accessToken, clientId, null);
	}

	public void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String url = getLoginUrl(req, resp);
		logger.debug("redirecting to loginUrl: {} ", url);

		WebUtils.saveRequest(req);
		WebUtils.issueRedirect(req, resp, url);
	}

	private CasOAuthClientRealm getCasOAuthClientRealm() {
		if (casOAuthClientRealm == null) {
			SecurityManager securityManager = SecurityUtils.getSecurityManager();
			RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securityManager;
			Collection<Realm> realms = realmSecurityManager.getRealms();
			for (Realm realm : realms)
				if (realm instanceof CasOAuthClientRealm) {
					casOAuthClientRealm = (CasOAuthClientRealm) realm;
					break;
				}

		}
		return casOAuthClientRealm;
	}

	private CasOAuthWrapperClient getClient() {
		if (client == null)
			client = (CasOAuthWrapperClient) getCasOAuthClientRealm().getClients().findClient("CasOAuthWrapperClient");

		return client;
	}
}
