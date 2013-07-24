package eu.openiot;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

import eu.openiot.util.Globals;

public class PermissionTestServlet extends HttpServlet {

	private static final long serialVersionUID = -2061899001560057007L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated())
			resp.sendRedirect("/login.jsp");
		else {
			SecurityManager securityManager = SecurityUtils.getSecurityManager();
			Map<String, String> allPermissions = Collections.<String, String> emptyMap();
			if (securityManager instanceof RealmSecurityManager) {
				ExtendedJDBCRealm realm = (ExtendedJDBCRealm) ((RealmSecurityManager) securityManager).getRealms().iterator().next();
				try {
					allPermissions = realm.getAllPermissions();
				} catch (SQLException e) {
					e.printStackTrace();
					Globals.gLogger.error("SQL Exception while retrieving permissions", e);
				}
			}
			req.setAttribute("permissions", allPermissions);
			req.getRequestDispatcher("/account/permissions.jsp").forward(req, resp);
		}

	}

}
