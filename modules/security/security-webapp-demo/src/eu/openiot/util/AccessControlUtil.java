package eu.openiot.util;

import static eu.openiot.util.Globals.gLogger;
import io.buji.pac4j.ShiroWebContext;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.util.WebUtils;
import org.openiot.security.client.CasOAuthClientRealm;
import org.pac4j.core.client.BaseClient;

public class AccessControlUtil {

	public static boolean hasPermission(String perm) {
		return SecurityUtils.getSubject().isPermitted(perm);
	}

	public static String getLoginUrl(HttpServletRequest req, HttpServletResponse resp) {
		SecurityManager securityManager = SecurityUtils.getSecurityManager();
		RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securityManager;
		Collection<Realm> realms = realmSecurityManager.getRealms();
		CasOAuthClientRealm casOAuthClientRealm = null;
		for (Realm realm : realms) {
			if (realm instanceof CasOAuthClientRealm) {
				casOAuthClientRealm = (CasOAuthClientRealm) realm;
				break;
			}
		}

		BaseClient<?, ?> client = (BaseClient<?, ?>) casOAuthClientRealm.getClients().findClient("CasOAuthWrapperClient");
		return client.getRedirectionUrl(new ShiroWebContext(req, resp), true);
	}

	public static void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String url = AccessControlUtil.getLoginUrl(req, resp);
		gLogger.debug("redirecting to loginUrl: {} ", url);

		WebUtils.saveRequest(req);
		WebUtils.issueRedirect(req, resp, url);
	}
}
