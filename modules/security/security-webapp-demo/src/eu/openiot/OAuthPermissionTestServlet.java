package eu.openiot;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.openiot.util.AccessControlUtil;

public class OAuthPermissionTestServlet extends HttpServlet {

	private static final long serialVersionUID = -2061899001560057007L;

	private static Logger log = LoggerFactory.getLogger(OAuthPermissionTestServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			AccessControlUtil.redirectToLogin(req, resp);
		} else {
			Map<String, String> allPermissions = new HashMap<String, String>();

			String[] permissions = new String[] { "stream:view:s1", "stream:query:s1", "stream:view:s2", "stream:query:s2", 
					"admin:create_user", "admin:delete_user", "admin:delete_stream:s1", "admin:delete_stream:s2,s3" };

			for (String permission : permissions)
				allPermissions.put(permission, "--");

			req.setAttribute("permissions", allPermissions);
			req.getRequestDispatcher("/account/permissions.jsp").forward(req, resp);
		}

	}

}
