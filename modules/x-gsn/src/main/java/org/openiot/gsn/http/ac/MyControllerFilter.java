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
 * @author Behnaz Bostanipour
 * @author Sofiane Sarni
 * @author Milos Stojanovic
*/

package org.openiot.gsn.http.ac;


import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.http.WebConstants;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class MyControllerFilter implements Filter {

    private FilterConfig config = null;
    private static transient Logger logger = Logger.getLogger(MyControllerFilter.class);
    private boolean logging = false;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        if (config.getInitParameter("logIPs") != null)
            if (config.getInitParameter("logIPs").equalsIgnoreCase("true"))
                logging = true;
    }

    public void destroy() {
        config = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            if (logging)
                if ("/multidata".equals(req.getServletPath()))
                    logger.warn(req.getRemoteAddr() + " => multidata\n" + listRequestParameters(req));


            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            if (Main.getContainerConfig().isAcEnabled() == false) // do as filter does not exist
            {
                chain.doFilter(request, response);
            } else {

                // check of username and password are given in the URL
                String reqUsername = req.getParameter("username");
                String reqPassword = req.getParameter("password");
                String reqVirtualSensorName = req.getParameter("name");
                String rawRequest = req.getParameter(WebConstants.REQUEST);
                int requestType = -1;
                if (rawRequest == null || rawRequest.trim().length() == 0) {
                    requestType = 0;
                } else
                    try {
                        requestType = Integer.parseInt(rawRequest);
                    } catch (Exception e) {
                        logger.debug(e.getMessage(), e);
                        requestType = -1;
                    }

                if ("/data".equals(req.getServletPath())) {   // /data request uses vsname instead of name
                    reqVirtualSensorName = req.getParameter("vsname");
                    if (reqVirtualSensorName == null)               // try the other accepted alternative: vsName
                        reqVirtualSensorName = req.getParameter("vsName");
                }

                if ((reqUsername == null) && (reqPassword == null) && (user == null) && ("/multidata".equals(req.getServletPath()))) { // generally request from web client for plotting
                    List<String> listOfVirtualSensors = createListOfVirtualSensorsFromRequest(req);
                    boolean flag = UserUtils.userHasAccessToAllVirtualSensorsInList(reqUsername, reqPassword, listOfVirtualSensors) || DataSource.allVirtualSensorsInListAreNotManaged(listOfVirtualSensors);
                    if (flag) {
                        chain.doFilter(request, response);
                        return;
                    } else {
                        res.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified resource.");
                        return;
                    }

                }

                if ((reqUsername != null) && (reqPassword != null) && (reqVirtualSensorName != null)) {
                    //logger.debug("Detected URL-based login");
                    //logger.debug("User: " + reqUsername);
                    //logger.debug("Pass: " + reqPassword);
                    //logger.debug("Name: " + reqVirtualSensorName);
                    //logger.debug("Request type: " + requestType);

                    User userByURL = UserUtils.allowUserToLogin(reqUsername, reqPassword);

                    if (userByURL == null) {
                        res.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified user.");
                        return;
                    }

                    boolean flag = false;

                    if ("/multidata".equals(req.getServletPath())) {
                        List<String> listOfVirtualSensors = createListOfVirtualSensorsFromRequest(req);

                        flag = UserUtils.userHasAccessToAllVirtualSensorsInList(reqUsername, reqPassword, listOfVirtualSensors) || DataSource.allVirtualSensorsInListAreNotManaged(listOfVirtualSensors);
                    } else
                        flag = UserUtils.userHasAccessToVirtualSensor(reqUsername, reqPassword, reqVirtualSensorName) || !DataSource.isVSManaged(reqVirtualSensorName);

                    if (flag) {
                        chain.doFilter(request, response);
                        return;
                    } else {
                        res.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified resource.");
                        return;
                    }
                }

                // support for request 114
                if ("/gsn".equals(req.getServletPath()) && (requestType == 114)) {
                    if ((reqUsername != null) && (reqPassword != null) && (reqVirtualSensorName != null)) {
                        User userByURL = UserUtils.allowUserToLogin(reqUsername, reqPassword);

                        if (userByURL == null) {
                            res.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified user.");
                            return;
                        }

                        boolean flag = false;
                        flag = UserUtils.userHasAccessToVirtualSensor(reqUsername, reqPassword, reqVirtualSensorName) || !DataSource.isVSManaged(reqVirtualSensorName);

                        if (flag) {
                            chain.doFilter(request, response);
                            return;
                        } else {
                            res.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified resource.");
                            return;
                        }
                    } else { // if resource is public and no password was provided
                        if (!DataSource.isVSManaged(reqVirtualSensorName)) {
                            chain.doFilter(request, response);
                            return;
                        } else {
                            res.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified resource.");
                            return;
                        }
                    }
                }


                // bypass if servlet is gsn and request is for ContainerInfoHandler

                if (("/gsn".equals(req.getServletPath()) && (requestType == 0 || requestType == 901))
                        || ("/field".equals(req.getServletPath()))
                        ) {
                    chain.doFilter(request, response);
                    return;
                }

                //

                if (user == null)// if user has not already logged-in
                {
                    if (req.getQueryString() == null) // if there is no query string in uri, we suppose that target is GSN home
                    {
                        session.setAttribute("login.target", null);

                    } else {

                        /* if there is query string, store it as a target, so to go back to it once logged-in */
                        session.setAttribute("login.target", req.getRequestURL() + "?" + req.getQueryString());
                    }
                    res.setHeader("Cache-Control", "no-cache");
                    res.setHeader("Pragma", "no-cache");
                    res.setHeader("Expires", "0");

                    // redirect to login
                    res.sendRedirect("/gsn/MyLoginHandlerServlet");

                    return;
                } else {
                    //if logged-in, go to the target directly
                    chain.doFilter(request, response);

                }
            }
        }
    }

    private String listRequestParameters(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        Enumeration e = req.getParameterNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            sb.append("\t").append(key).append(":").append(req.getParameter(key)).append("\n");
        }
        return sb.toString();
    }

    private List<String> createListOfVirtualSensorsFromRequest(HttpServletRequest req) {
        Enumeration e = req.getParameterNames();
        List<String> l = new ArrayList<String>();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith("vs[")) {
                if (req.getParameter(key).equals("All"))
                    l.addAll(getAllVirtualSensors());
                else
                    l.add(req.getParameter(key));
            }
        }
        return l;
    }

    private List<String> getAllVirtualSensors() {
        List<String> l = new ArrayList<String>();
        Iterator<VSensorConfig> iter = Mappings.getAllVSensorConfigs();
        VSensorConfig vsc;
        while (iter.hasNext()) {
            vsc = (VSensorConfig) iter.next();
            l.add(vsc.getName());
        }
        return l;
    }
}
