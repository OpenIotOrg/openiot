package eu.openiot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.AuthorizationManager;
import org.openiot.security.client.OAuthorizationCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceRequestTestServlet extends HttpServlet {

	private static final long serialVersionUID = -2061899001560057007L;

	private static Logger log = LoggerFactory.getLogger(ServiceRequestTestServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Subject subject = SecurityUtils.getSubject();
		AccessControlUtil accessControlUtil = AccessControlUtil.getInstance();
		if (!subject.isAuthenticated()) {
			accessControlUtil.redirectToLogin(req, resp);
		} else {
			String accessToken = req.getParameter("access_token");
			String clientId = req.getParameter("client_id");
			
			AuthorizationManager authorizationManager = accessControlUtil.getAuthorizationManager();
			OAuthorizationCredentials myCredentials = accessControlUtil.getOAuthorizationCredentials();
			OAuthorizationCredentials credentials = new OAuthorizationCredentials(accessToken, clientId, myCredentials);
			boolean hasPermission = authorizationManager.hasPermission("stream:query:s1", credentials);
			
			req.setAttribute("access_token", myCredentials.getAccessToken());
			req.setAttribute("client_id", myCredentials.getClientId());
			req.setAttribute("hasPermission", hasPermission);
			req.getRequestDispatcher("/account/service_request.jsp").forward(req, resp);
		}

	}

}
