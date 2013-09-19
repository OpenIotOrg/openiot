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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.http.ac.UserUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class DynamicGeoDataServlet extends HttpServlet {

    private static GeometryFactory geometryFactory;
    private static STRtree geoIndex;

    private static transient Logger logger = Logger.getLogger(DynamicGeoDataServlet.class);
    private static final String SEPARATOR = ",";
    private static final String NEWLINE = "\n";

    private List<SensorGeoReading> sensorReadingsList = new Vector<SensorGeoReading>();
    private HashMap<String, SensorGeoReading> sensorReadingsHash = new HashMap<String, SensorGeoReading>();
    private List<String> sensorsWithinEnvelope = new ArrayList<String>();

    private boolean debugMode = false;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String env = HttpRequestUtils.getStringParameter("env", null, request); // e.g. "POLYGON ((0 0, 0 100, 100 100, 100 0, 0 0))";
        String field = HttpRequestUtils.getStringParameter("field", null, request);
        String timed = HttpRequestUtils.getStringParameter("timed", null, request);
        String query = HttpRequestUtils.getStringParameter("query", "value", request);
        String username = HttpRequestUtils.getStringParameter("username", null, request);
        String password = HttpRequestUtils.getStringParameter("password", null, request);
        String debugModeStr = HttpRequestUtils.getStringParameter("debug", "false", request);
        String format = HttpRequestUtils.getStringParameter("format", "csv", request);

        if (debugModeStr.equalsIgnoreCase("true"))
            debugMode = true;
        else
            debugMode = false;

        List<String> allowedSensors = new Vector<String>();

        if (Main.getContainerConfig().isAcEnabled()) {
            if (username != null && password != null) {
                if (UserUtils.allowUserToLogin(username, password) == null) {
                    response.getWriter().write("ERROR: incorrect login for user '" + username + "'. Check your credentials.");
                    return;
                } else { // user authenticated correctly
                    allowedSensors = UserUtils.getAllowedVirtualSensorsForUser(username, password, getAllSensors());
                }

            } else { // username or password is null
                response.getWriter().write("ERROR: username and password required.");
                return;
            }
        } else { // No access control
            allowedSensors = getAllSensors();
        }

        StringBuilder sb = new StringBuilder();

        StringBuilder sqlQueryStr = new StringBuilder();

        if (timed.equalsIgnoreCase("latest")) {

            for (int i = 0; i < allowedSensors.size(); i++) {
                sqlQueryStr.append("select '" + allowedSensors.get(i) + "'")
                        .append(" as name, timed, ")
                        .append(field)
                        .append(", latitude, longitude, altitude ")
                        .append(" from ")
                        .append(allowedSensors.get(i))
                        .append(" where timed = ( select max(timed) from ")
                        .append(allowedSensors.get(i))
                        .append(" )");
                if (i < allowedSensors.size() - 1)
                    sqlQueryStr.append("\n union \n");
            }

        } else { // timed explicitly specified
            for (int i = 0; i < allowedSensors.size(); i++) {
                sqlQueryStr.append("select '" + allowedSensors.get(i) + "'")
                        .append(" as name, timed, ")
                        .append(field)
                        .append(", latitude, longitude, altitude ")
                        .append(" from ")
                        .append(allowedSensors.get(i))
                        .append(" where timed = ")
                        .append(timed);
                if (i < allowedSensors.size() - 1)
                    sqlQueryStr.append("\n union \n");
            }
        }


        sb.append("env = " + env)
                .append("\ndebug = " + debugMode)
                .append("\n")
                .append("field = " + field)
                .append("\n")
                .append("timed = " + timed)
                .append("\n")
                .append("query = " + query)
                .append("\n")
                .append("all_sensors = " + sensorsToString(allowedSensors))
                .append("\n")
                .append(sqlQueryStr)
                .append("\n# -------\n")
                .append(executeQuery(sqlQueryStr.toString(), field));


        if (debugMode)
            response.getWriter().write(sb.toString());

        logger.warn(sb.toString());

        buildGeoIndex();


        try {
            sensorsWithinEnvelope = getListOfSensorsWithinEnvelope(env);
        } catch (ParseException e) {
            logger.warn(e.getMessage(), e);
            response.getWriter().write("ERROR: cannot create geographic index");
            return;
        }


        if (debugMode) {
            response.getWriter().write("\nSensors within envelope: ");
            response.getWriter().write(sensorsToString(sensorsWithinEnvelope));
            response.getWriter().write("\n");
        }

        //response.getWriter().write(formatter("default", field));
        response.getWriter().write(formatter(format, field));
    }

    public String formatter(String format, String field) {
        StringBuilder sb = new StringBuilder();
        if (format.equalsIgnoreCase("json")) {
            JSONArray sensorsReadings = new JSONArray();
            for (String aSensor : sensorsWithinEnvelope) {
                JSONObject aSensorReading = new JSONObject();
                aSensorReading.put("name", sensorReadingsHash.get(aSensor).sensorName);
                aSensorReading.put("latitude", sensorReadingsHash.get(aSensor).coordinates.getY());
                aSensorReading.put("longitude", sensorReadingsHash.get(aSensor).coordinates.getX());
                aSensorReading.put("timed", sensorReadingsHash.get(aSensor).timestamp);
                aSensorReading.put(field, sensorReadingsHash.get(aSensor).value);
                sensorsReadings.add(aSensorReading);
            }
            return sensorsReadings.toJSONString();
        } else if (format.equalsIgnoreCase("xml")) {
            sb.append("<geodata>\n");
            for (String aSensor : sensorsWithinEnvelope) {
                sb.append("\t<sensor name=\"")
                        .append(sensorReadingsHash.get(aSensor).sensorName)
                        .append("\" latitude=\"")
                        .append(sensorReadingsHash.get(aSensor).coordinates.getY())
                        .append("\" longitude=\"")
                        .append(sensorReadingsHash.get(aSensor).coordinates.getX())
                        .append("\" timed=\"")
                        .append(sensorReadingsHash.get(aSensor).timestamp)
                        .append("\" field=\"")
                        .append(field)
                        .append("\">")
                        .append(sensorReadingsHash.get(aSensor).value)
                        .append("</sensor>\n");
            }
            sb.append("</geodata>");
            return sb.toString();
        } else if (format.equalsIgnoreCase("native")) {
            for (String aSensor : sensorsWithinEnvelope) {
                sb.append(sensorReadingsHash.get(aSensor).toString() + "\n");
            }
            return sb.toString();
        } else if (format.equalsIgnoreCase("csv")) {
            sb.append("# name, latitude, longitude, timed, " + field + "\n");
            for (String aSensor : sensorsWithinEnvelope) {
                sb.append(sensorReadingsHash.get(aSensor).sensorName + ", " + sensorReadingsHash.get(aSensor).coordinates.getY() + ", " + sensorReadingsHash.get(aSensor).coordinates.getX() + ", " + sensorReadingsHash.get(aSensor).timestamp + ", " + +sensorReadingsHash.get(aSensor).value + "\n");
            }
            return sb.toString();
        } else { //default is CSV
            sb.append("# name, latitude, longitude, timed, " + field + "\n");
            for (String aSensor : sensorsWithinEnvelope) {
                sb.append(sensorReadingsHash.get(aSensor).sensorName + ", " + sensorReadingsHash.get(aSensor).coordinates.getY() + ", " + sensorReadingsHash.get(aSensor).coordinates.getX() + ", " + sensorReadingsHash.get(aSensor).timestamp + ", " + +sensorReadingsHash.get(aSensor).value + "\n");
            }
            return sb.toString();
        }
    }

    public List<String> getAllSensors() {

        Iterator iter = Mappings.getAllVSensorConfigs();
        List<String> sensors = new Vector<String>();

        while (iter.hasNext()) {
            VSensorConfig sensorConfig = (VSensorConfig) iter.next();
            //Double longitude = sensorConfig.getLongitude();
            //Double latitude = sensorConfig.getLatitude();
            //Double altitude = sensorConfig.getAltitude();
            String sensor = sensorConfig.getName();
            sensors.add(sensor);
        }

        return sensors;
    }

    public String sensorsToString(List<String> sensors) {
        StringBuilder sensorsAsString = new StringBuilder();
        for (String sensor : sensors) {
            sensorsAsString.append(sensor);
            sensorsAsString.append(SEPARATOR);
        }
        if (sensorsAsString.length() > 0)
            sensorsAsString.setLength(sensorsAsString.length() - 1);  // remove the last SEPARATOR

        return sensorsAsString.toString();
    }


    public String executeQuery(String query, String fieldName) {

        sensorReadingsList.clear(); // reset global sensor readings
        geometryFactory = new GeometryFactory();

        StringBuilder sb = new StringBuilder();
        Connection connection = null;

        try {
            connection = Main.getDefaultStorage().getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = statement.executeQuery(query);
            ResultSetMetaData metaData;    // Additional information about the results
            int numCols, numRows;          // How many rows and columns in the table
            metaData = results.getMetaData();       // Get metadata on them
            numCols = metaData.getColumnCount();    // How many columns?
            results.last();                         // Move to last row
            numRows = results.getRow();             // How many rows?

            String s;

            sb.append("# ");

            for (int col = 0; col < numCols; col++) {
                sb.append(metaData.getColumnLabel(col + 1));
                if (col < numCols - 1)
                    sb.append(SEPARATOR);
            }
            sb.append(NEWLINE);

            for (int row = 0; row < numRows; row++) {
                results.absolute(row + 1);                // Go to the specified row

                Double latitude = results.getDouble("latitude");
                Double longitude = results.getDouble("longitude");
                Double altitude = results.getDouble("altitude");

                Double value = results.getDouble(fieldName);
                String sensorName = results.getString("name");
                Long timeStamp = results.getLong("timed");

                logger.warn("longitude = " + longitude + " , latitude = " + latitude);

                Point coordinates = geometryFactory.createPoint(new Coordinate(longitude, latitude));

                SensorGeoReading sensorReadings = new SensorGeoReading(sensorName, coordinates, timeStamp, value, fieldName);
                sensorReadingsList.add(sensorReadings);
                sensorReadingsHash.put(sensorName, sensorReadings);

                //String
                logger.warn(sensorReadings);

                for (int col = 0; col < numCols; col++) {
                    Object o = results.getObject(col + 1); // Get value of the column
                    //logger.warn(row + " , "+col+" : "+ o.toString());
                    if (o == null)
                        s = "null";
                    else
                        s = o.toString();
                    if (col < numCols - 1)
                        sb.append(s).append(SEPARATOR);
                    else
                        sb.append(s);
                }
                sb.append(NEWLINE);
            }
        } catch (SQLException e) {
            sb.append("ERROR in execution of query: " + e.getMessage());
        } finally {
            Main.getDefaultStorage().close(connection);
        }

        return sb.toString();
    }

    /*
   * Builds geographic geoIndex from list of sensors currently loaded in the system
   * */

    public void buildGeoIndex() {

        geoIndex = new STRtree();
        //geometryFactory = new GeometryFactory();

        for (int i = 0; i < sensorReadingsList.size(); i++) {
            geoIndex.insert(sensorReadingsList.get(i).coordinates.getEnvelopeInternal(), sensorReadingsList.get(i).coordinates);
            //logger.warn(sensors.get(i) + " : " + coordinates.get(i) + " : " + searchForSensors_String(coordinates.get(i)));
        }
        geoIndex.build();
    }

    public List<String> getListOfSensorsWithinEnvelope(String envelope) throws ParseException {
        Geometry geom = new WKTReader().read(envelope);
        List listEnvelope = geoIndex.query(geom.getEnvelopeInternal());
        List<String> sensors = new ArrayList<String>();
        for (int i = 0; i < listEnvelope.size(); i++) {
            sensors.add(searchForSensors_String((Point) listEnvelope.get(i)));
        }
        return sensors;
    }

    /*
    * Searches for the list of sensors which are located at the given point (comma separated)
    * */
    public String searchForSensors_String(Point p) {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < sensorReadingsList.size(); i++) {
            if (sensorReadingsList.get(i).coordinates == p) {
                return sensorReadingsList.get(i).sensorName;
            }
        }
        return "";
    }


    public class SensorGeoReading {
        public SensorGeoReading(String name, Point coords, Long ts, double v, String field) {
            sensorName = name;
            coordinates = coords;
            timestamp = ts;
            value = v;
            fieldName = field;
        }

        String sensorName;
        Point coordinates;
        Long timestamp;
        Double value;
        String fieldName;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[")
                    .append(sensorName)
                    .append("] (lat:")
                    .append(coordinates.getY())
                    .append(",lon:")
                    .append(coordinates.getX())
                    .append(") @ ")
                    .append(timestamp)
                    .append(" => ")
                    .append(fieldName)
                    .append(" = ")
                    .append(value);
            return sb.toString();
        }
    }
}
