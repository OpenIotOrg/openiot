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
import org.openiot.security.client.OAuthorizationCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OAuthPermissionTestServlet extends HttpServlet {

	private static final long serialVersionUID = -2061899001560057007L;

	private static Logger log = LoggerFactory.getLogger(OAuthPermissionTestServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Subject subject = SecurityUtils.getSubject();
		AccessControlUtil accessControlUtil = AccessControlUtil.getInstance();
		if (!subject.isAuthenticated()) {
			accessControlUtil.redirectToLogin(req, resp);
		} else {
			Map<String, String> allPermissions = new HashMap<String, String>();

			String[][] permissions = new String[][] { {"sensor:discover:s1", "Discovery of sensor s1"}, 
					{"sensor:query:s1", "Querying sensor s1"}, {"sensor:discover:s2", "Discovery of sensor s2"}, {"sensor:query:s2", "Querying sensor s2"}, 
					{"admin:create_user", "Creating a user"}, {"admin:delete_user", "Deleting a user"}, {"admin:delete_sensor:s1", "Deteling sensor s1"},
					{"admin:delete_sensor:s2,s3", "Deleting sensors s2 and s3"} };

			for (String[] permission : permissions)
				allPermissions.put(permission[0], permission[1]);

			OAuthorizationCredentials myCredentials = accessControlUtil.getOAuthorizationCredentials();
			req.setAttribute("access_token", myCredentials.getAccessToken());
			req.setAttribute("client_id", myCredentials.getClientId());
			req.setAttribute("permissions", allPermissions);
			req.getRequestDispatcher("/account/permissions.jsp").forward(req, resp);
		}

	}

}
