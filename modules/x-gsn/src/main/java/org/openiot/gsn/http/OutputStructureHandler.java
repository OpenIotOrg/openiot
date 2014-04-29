/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/

package org.openiot.gsn.http;

import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.VSensorConfig;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.openiot.gsn.http.accesscontrol.User;
import org.openiot.gsn.http.ac.User;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class OutputStructureHandler implements RequestHandler {
    private static transient Logger logger = Logger.getLogger(OutputStructureHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String vsName = request.getParameter("name");
        VSensorConfig sensorConfig = Mappings.getVSensorConfig(vsName);
        if (logger.isInfoEnabled())
            logger.info(new StringBuilder().append("Structure request for *").append(vsName).append("* received.").toString());
        StringBuilder sb = new StringBuilder("<virtual-sensor name=\"").append(vsName).append("\">\n");
        for (DataField df : sensorConfig.getOutputStructure())
            sb.append("<field name=\"").append(df.getName()).append("\" ").append("type=\"").append(df.getType()).append("\" ").append("description=\"").append(
                    StringEscapeUtils.escapeXml(df.getDescription())).append("\" />\n");
        sb.append("<field name=\"timed\" type=\"string\" description=\"The timestamp associated with the stream element\" />\n");
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
        if (user != null) // meaning, that a login session is active, otherwise we couldn't get there
            if (Main.getContainerConfig().isAcEnabled() == true) {
                if (user.hasReadAccessRight(vsName) == false && user.isAdmin() == false)  // ACCESS_DENIED
                {
                    response.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified virtual sensor .");
                    return false;
                }
            }

        return true;
    }

}
