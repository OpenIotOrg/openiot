package org.openiot.gsn.http;

import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.http.ac.DataSource;
import org.openiot.gsn.http.ac.User;
import org.openiot.gsn.storage.DataEnumerator;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import org.openiot.gsn.http.ac.UserUtils;

public class GMLHandler implements RequestHandler {

    private static transient Logger logger = Logger.getLogger(GMLHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String reqName = request.getParameter("name");
        String reqGroup = request.getParameter("group");
        String reqUsername = request.getParameter("username");
        String reqPassword = request.getParameter("password");
        response.getWriter().write(buildOutput(reqName, reqGroup, reqUsername, reqPassword));
    }

    //return only the requested sensor(s) if specified (otherwise use null)
    public String buildOutput(String reqName, String reqGroup, String reqUsername, String reqPassword) {

        boolean authenticateUserFromURL = false;
        User user = null;

        if (Main.getContainerConfig().isAcEnabled()) {

            if ((reqUsername != null) && (reqPassword != null)) {
                authenticateUserFromURL = true;
                user = UserUtils.allowUserToLogin(reqUsername, reqPassword);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat(Main.getContainerConfig().getTimeFormat());
        StringBuilder outsb = new StringBuilder("<gsn:FeatureCollection xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"./gsn.xsd\" xmlns:gsn=\"http://gsn.ch/\" xmlns:gml=\"http://www.opengis.net/gml\"> \n");
        Iterator<VSensorConfig> vsIterator = Mappings.getAllVSensorConfigs();
        HashSet<String> sensorsSet = new HashSet<String>();
        if (reqName != null && reqName.contains(","))
            sensorsSet = new HashSet<String>(Arrays.asList(reqName.split(",")));
        else sensorsSet.add(reqName);

        while (vsIterator.hasNext()) {
            StringBuilder sb = new StringBuilder();
            String lat = null;
            String lon = null;
            VSensorConfig sensorConfig = vsIterator.next();
            if (reqName != null && !sensorsSet.contains(sensorConfig.getName())) continue;
            if (reqGroup != null && !(sensorConfig.getName().startsWith(reqGroup + "_"))) continue;

            if (Main.getContainerConfig().isAcEnabled()) {
                if (user == null) {
                    if (authenticateUserFromURL)
                        continue; // means that username and password provided are rejected dince they don't map to a correct User object
                    else // no username was provided, show only public sensors
                        if (DataSource.isVSManaged(sensorConfig.getName()))
                            continue; //skip sensor if it is managed by access control
                } else // user authenticated, verify that it has the right credentials
                    if (!user.hasReadAccessRight(sensorConfig.getName()) && !user.isAdmin() && !DataSource.isVSManaged(sensorConfig.getName()))
                        continue;
            }

            for (KeyValue df : sensorConfig.getAddressing()) {
                if (StringEscapeUtils.escapeXml(df.getKey().toString().toLowerCase()).contentEquals("latitude"))
                    lat = new String(StringEscapeUtils.escapeXml(df.getValue().toString()));
                if (StringEscapeUtils.escapeXml(df.getKey().toString().toLowerCase()).contentEquals("longitude"))
                    lon = new String(StringEscapeUtils.escapeXml(df.getValue().toString()));
            }
            if (lat != null && lon != null) {
                sb.append("<gml:featureMember>\n");
                sb.append("<gsn:sensors");
                sb.append(" fid=\"").append(sensorConfig.getName()).append("\"");
                sb.append(">\n");
                sb.append("\t<gsn:geometryProperty><gml:Point><gml:coordinates>").append(lon).append(",").append(lat).append("</gml:coordinates></gml:Point></gsn:geometryProperty>\n");
            } else continue;

            if (lat.isEmpty() || lon.isEmpty()) // skip sensors with empty coordinates
                continue;

            ArrayList<StreamElement> ses = getMostRecentValueFor(sensorConfig.getName());
            int counter = 1;
            if (ses != null) {
                for (StreamElement se : ses) {
                    sb.append("\t<gsn:sensor>").append(sensorConfig.getName()).append("</gsn:sensor>\n");
                    for (KeyValue df : sensorConfig.getAddressing()) {
                        sb.append("\t<gsn:").append(StringEscapeUtils.escapeXml(df.getKey().toString().toLowerCase())).append(">");
                        sb.append(StringEscapeUtils.escapeXml(df.getValue().toString()));
                        sb.append("</gsn:").append(StringEscapeUtils.escapeXml(df.getKey().toString().toLowerCase())).append(">\n");
                    }
                    for (DataField df : sensorConfig.getOutputStructure()) {
                        sb.append("\t<gsn:").append(df.getName().toLowerCase()).append(">");
                        if (se != null)
                            if (df.getType().toLowerCase().trim().indexOf("binary") > 0)
                                sb.append(se.getData(df.getName()));
                            else
                                sb.append(se.getData(StringEscapeUtils.escapeXml(df.getName())));
                        sb.append("</gsn:").append(df.getName().toLowerCase()).append(">\n");
                    }
                    counter++;
                }
            }
            sb.append("</gsn:sensors>\n");
            sb.append("</gml:featureMember>\n");
            outsb.append(sb);
        }
        outsb.append("</gsn:FeatureCollection>\n");
        return outsb.toString();
    }

    public boolean isValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return true;
    }

    /**
     * returns null if there is an error.
     *
     * @param virtual_sensor_name
     * @return
     */
    public static ArrayList<StreamElement> getMostRecentValueFor(String virtual_sensor_name) {
        StringBuilder query = new StringBuilder("select * from ").append(virtual_sensor_name).append(" where timed = (select max(timed) from ").append(virtual_sensor_name).append(")");
        ArrayList<StreamElement> toReturn = new ArrayList<StreamElement>();
        try {
            DataEnumerator result = Main.getStorage(virtual_sensor_name).executeQuery(query, true);
            while (result.hasMoreElements())
                toReturn.add(result.nextElement());
        } catch (SQLException e) {
            logger.error("ERROR IN EXECUTING, query: " + query);
            logger.error(e.getMessage(), e);
            return null;
        }
        return toReturn;
    }
}
