package org.openiot.gsn.http;

import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
//import org.openiot.gsn.http.accesscontrol.User;
import org.openiot.gsn.http.ac.User;
import org.openiot.gsn.storage.DataEnumerator;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class OneShotQueryHandler implements RequestHandler {

    private static transient Logger logger = Logger.getLogger(OneShotQueryHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {


        SimpleDateFormat sdf = new SimpleDateFormat(Main.getInstance().getContainerConfig().getTimeFormat());

        String vsName = request.getParameter("name");
        String vsCondition = request.getParameter("condition");
        if (vsCondition == null || vsCondition.trim().length() == 0)
            vsCondition = " ";
        else
            vsCondition = " where " + vsCondition;
        String vsFields = request.getParameter("fields");
        if (vsFields == null || vsFields.trim().length() == 0 || vsFields.trim().equals("*"))
            vsFields = "*";
        else
            vsFields += " , pk, timed";
        String windowSize = request.getParameter("window");
        if (windowSize == null || windowSize.trim().length() == 0) windowSize = "1";
        StringBuilder query = new StringBuilder("select " + vsFields + " from " + vsName + vsCondition + " order by timed DESC limit " + windowSize + " offset 0");
        DataEnumerator result;
        try {
            result = Main.getStorage(vsName).executeQuery(query, true);
        } catch (SQLException e) {
            logger.error("ERROR IN EXECUTING, query: " + query);
            logger.error(e.getMessage(), e);
            logger.error("Query is from " + request.getRemoteAddr() + "- " + request.getRemoteHost());
            return;
        }
        StringBuilder sb = new StringBuilder("<result>\n");
        while (result.hasMoreElements()) {
            StreamElement se = result.nextElement();
            sb.append("<stream-element>\n");
            for (int i = 0; i < se.getFieldNames().length; i++) {
                sb.append("<field name=\"").append(se.getFieldNames()[i]).append("\" >");
                if (se.getData()[i] != null)
                    if (se.getFieldTypes()[i] == DataTypes.BINARY)
                        sb.append(se.getData()[i].toString());
                    else
                        sb.append(StringEscapeUtils.escapeXml(se.getData()[i].toString()));
                sb.append("</field>\n");
            }
            sb.append("<field name=\"timed\" >").append(sdf.format(new Date(se.getTimeStamp()))).append("</field>\n");
            sb.append("</stream-element>\n");
        }
        result.close();
        sb.append("</result>");

        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        response.getWriter().write(sb.toString());
    }

    public boolean isValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String vsName = request.getParameter("name");

        //Added by Behnaz
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (vsName == null || vsName.trim().length() == 0) {
            response.sendError(WebConstants.MISSING_VSNAME_ERROR, "The virtual sensor name is missing");
            return false;
        }
        VSensorConfig sensorConfig = Mappings.getVSensorConfig(vsName);
        if (sensorConfig == null) {
            response.sendError(WebConstants.ERROR_INVALID_VSNAME, "The specified virtual sensor doesn't exist.");
            return false;
        }
        if (Main.getContainerConfig().isAcEnabled() == true) {
            //Added by Behnaz.
            if (user != null) // meaning, that a login session is active, otherwise we couldn't get there
                if (user.hasReadAccessRight(vsName) == false && user.isAdmin() == false)  // ACCESS_DENIED
                {
                    response.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified virtual sensor .");
                    //response.sendError(response.SC_UNAUTHORIZED);

                    return false;
                }

        }


        return true;
    }

}
