package org.openiot.gsn.http;

import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.VSensorConfig;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.openiot.gsn.http.accesscontrol.User;
import org.openiot.gsn.http.ac.User;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class AddressingReqHandler implements RequestHandler {

    private static transient Logger logger = Logger.getLogger(AddressingReqHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String vsName = request.getParameter("name");
        VSensorConfig sensorConfig = Mappings.getVSensorConfig(vsName);
        if (logger.isInfoEnabled())
            logger.info(new StringBuilder().append("Structure request for *").append(vsName).append("* received.").toString());
        StringBuilder sb = new StringBuilder("<virtual-sensor name=\"").append(vsName).append("\" last-modified=\"").append(new File(sensorConfig.getFileName()).lastModified()).append("\">\n");
        for (KeyValue df : sensorConfig.getAddressing())
            sb.append("<predicate key=\"").append(StringEscapeUtils.escapeXml(df.getKey().toString())).append("\">").append(StringEscapeUtils.escapeXml(df.getValue().toString()))
                    .append("</predicate>\n");
        sb.append("</virtual-sensor>");
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

        //Added by Behnaz.
        if (Main.getContainerConfig().isAcEnabled() == true) {
            if (user != null) // meaning, that a login session is active, otherwise we couldn't get there
                if (user.hasReadAccessRight(vsName) == false && user.isAdmin() == false)  // ACCESS_DENIED
                {
                    response.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified virtual sensor .");
                    return false;
                }

        }
        return true;
    }
}
