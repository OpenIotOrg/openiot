package org.openiot.gsn.http;

import com.vividsolutions.jts.geom.GeometryFactory;

import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.io.ParseException;
import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.VSensorConfig;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.postgis.PGgeometry;
import org.postgis.Point;

public class GetSensorDataWithGeoPostGIS {


    private static transient Logger logger = Logger.getLogger(GetSensorDataWithGeoPostGIS.class);

    private static GetSensorDataWithGeoPostGIS instance = null;
    private static STRtree geoIndex;
    private static GeometryFactory geometryFactory;
    private static List<String> sensors;
    private static List<Point> coordinates;

    public static final String LIST_SENSORS_RESERVED_WORD = "$sensors";
    public static final String LIST_SENSORS_RESERVED_WORD_REGEX = "\\$sensors";
    public static final String UNION_RESERVED_WORD = "$union";
    public static final String UNION_RESERVED_WORD_REGEX = "\\$union";
    public static final String SENSOR_RESERVED_WORD = "$sensor";
    public static final String SENSOR_RESERVED_WORD_REGEX = "\\$sensor";

    public static final String CSV_FORMAT = "csv";
    public static final String XML_FORMAT = "xml";

    private static final String NEWLINE = "\n";
    public static final String SEPARATOR = ",";
    private static String dburl;
    private static String dbuser;
    private static String dbpass;
    public static final String CONF_SPATIAL_PROPERTIES_FILE = "conf/spatial.properties";

    protected GetSensorDataWithGeoPostGIS() {

    }

    public static GetSensorDataWithGeoPostGIS getInstance() { // Singleton
        if (instance == null) {
            instance = new GetSensorDataWithGeoPostGIS();
        }
        return instance;
    }

    /*
    * Searches for the list of sensors which are located at the given point (as list of Strings)
    * */
    public static List<String> searchForSensors(Point p) {
        List l = new Vector<String>();
        for (int i = 0; i < coordinates.size(); i++) {
            if (coordinates.get(i) == p) {
                l.add(sensors.get(i));
            }
        }
        return l;
    }

    /*
    * Searches for the list of sensors which are located at the given point (comma separated)
    * */
    public static String searchForSensors_String(Point p) {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < coordinates.size(); i++) {
            if (coordinates.get(i) == p) {
                s.append(sensors.get(i)).append(" ");
            }
        }
        return s.toString().trim().replace(" ", SEPARATOR);
    }

    /*
    * Returns list of sensors currently loaded in the system
    * */
    public static String getListOfSensors() {
        StringBuilder s = new StringBuilder();
        Iterator iter = Mappings.getAllVSensorConfigs();

        sensors.clear();
        coordinates.clear();

        while (iter.hasNext()) {
            VSensorConfig sensorConfig = (VSensorConfig) iter.next();
            Double longitude = sensorConfig.getLongitude();
            Double latitude = sensorConfig.getLatitude();
            Double altitude = sensorConfig.getAltitude();
            String sensor = sensorConfig.getName();

            if ((latitude != null) && (longitude != null) && (altitude != null)) {
                Point point = new Point(latitude, longitude, altitude);
                coordinates.add(point);
                sensors.add(sensor);
                s.append(sensor)
                        .append(" => ")
                        .append(longitude)
                        .append(" : ")
                        .append(latitude)
                        .append("\n");
            }
        }
        return s.toString();
    }

    public static Connection connect(String url, String dbuser, String dbpass) throws SQLException, ClassNotFoundException {
        Connection conn;
        Class.forName("org.postgis.DriverWrapper");
        conn = DriverManager.getConnection(url, dbuser, dbpass);
        return conn;
    }

    /*
    * Builds sensors table from list of sensors currently loaded in the system
    * */
    public static boolean buildGeoIndex() {

        boolean success = true;

        sensors = new Vector<String>();
        coordinates = new Vector<Point>();

        getListOfSensors();

        Properties properties = loadProperties();

        if (properties != null) {
            try {
                dburl = properties.getProperty("dburl");
                dbuser = properties.getProperty("dbuser");
                dbpass = properties.getProperty("dbpass");

                Connection conn = connect(dburl, dbuser, dbpass);

                //((org.postgresql.PGConnection) conn).addDataType("geometry", "org.postgis.PGgeometry");
                //((org.postgresql.PGConnection) conn).addDataType("box3d", "org.postgis.PGbox3d");

                String st_create_table = "DROP INDEX IF EXISTS gist_sensors;"
                        + " DROP TABLE IF EXISTS sensors;"
                        + " CREATE TABLE sensors ( \"name\" character(255) NOT NULL, \"location\" geometry NOT NULL );"
                        + " CREATE INDEX gist_sensors ON sensors USING GIST ( location ); ";

                logger.warn("Running query: " + st_create_table);

                PreparedStatement prepareStatement = conn.prepareStatement(st_create_table);
                prepareStatement.execute();
                prepareStatement.close();

                for (int i = 0; i < coordinates.size(); i++) {
                    String insert = "insert into sensors values ( '" + sensors.get(i) + "', ST_MakePoint(" + coordinates.get(i).getX() + " , " + coordinates.get(i).getY() + " , " + coordinates.get(i).getZ() + ") );";
                    PreparedStatement ps = conn.prepareStatement(insert);
                    ps.execute();
                    ps.close();
                    logger.warn(insert);
                }

                Statement s = conn.createStatement();
                ResultSet r = s.executeQuery("select location, name from sensors");
                while (r.next()) {
                    PGgeometry geom = (PGgeometry) r.getObject(1);
                    String name = r.getString(2);
                    logger.warn("Geometry " + geom.toString() + " : " + name);
                }
                s.close();
                conn.close();

            }
            catch (SQLException e) {
                logger.warn(e.getMessage(), e);
                success = false;
            }
            catch (ClassNotFoundException e) {
                logger.warn(e.getMessage(), e);
                success = false;
            }
        } else {
            logger.warn("Couldn't load properties files for PostGIS");
            success = false;
        }

        return success;

    }

    public static Properties loadProperties() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(CONF_SPATIAL_PROPERTIES_FILE));
        }
        catch (IOException e) {
            p = null;
            logger.warn(e.getMessage(), e);
        }
        return p;
    }


    /*
    * Searches for the sensors, which are contained within the specified envelope
    * */
    public static ArrayList<String> getListOfSensors(String envelope) throws ParseException {

        String spatial_query = "select location, name from sensors\n" +
                "where ST_CONTAINS(ST_GeomFromText('" + envelope + "'), location)";

        ArrayList<String> sensors = new ArrayList<String>();

        try {

            Connection conn = connect(dburl, dbuser, dbpass);

            Statement s2 = conn.createStatement();
            ResultSet r2 = s2.executeQuery(spatial_query);
            int count = 0;
            while (r2.next()) {
                PGgeometry geom = (PGgeometry) r2.getObject(1);
                String name = r2.getString(2);
                sensors.add(name);
                logger.warn("Matching Geometry " + geom.toString()+ " : " + name);
                count++;
            }
            logger.warn("count = " + count);
            s2.close();
            conn.close();
        }
        catch (SQLException e) {
            logger.warn(e.getMessage(), e);

        }
        catch (ClassNotFoundException e) {
            logger.warn(e.getMessage(), e);

        }

        return sensors;
    }


    public static String reformatQuery(String query, String matchingSensors, String unionElement) {

        String lower_query = query.toLowerCase();

        String listSensors[] = matchingSensors.split(",");
        for (int i = 0; i < listSensors.length; i++)
            logger.warn(i + " : " + listSensors[i]);

        //replace "sensors"
        String ref_query = new StringBuilder(lower_query.replaceAll(LIST_SENSORS_RESERVED_WORD_REGEX, matchingSensors)).toString();

        //check for aggregates, containing reserved word $union
        if (ref_query.indexOf(UNION_RESERVED_WORD) > 0) {
            StringBuilder unionOfAll = new StringBuilder();
            if (unionElement != "") {
                System.out.println("what_to_repeat => " + unionElement);
                for (int i = 0; i < listSensors.length; i++) {
                    unionOfAll.append(unionElement.replaceAll(SENSOR_RESERVED_WORD_REGEX, listSensors[i]));
                    if (i < listSensors.length - 1)
                        unionOfAll.append("\n union \n");
                }
            }
            System.out.println("unionofAll => " + unionOfAll);
            ref_query = ref_query.replaceAll(UNION_RESERVED_WORD_REGEX, unionOfAll.toString());
        }

        return ref_query;
    }

    public static String reformatQuery(String query, String matchingSensors) {
        return reformatQuery(query, matchingSensors, "");
    }

    /*
    * Execute query against a list of sensors
    *
    * */
    public static String executeQuery(String envelope, String query, String matchingSensors, String format) throws ParseException {

        //String matchingSensors = getListOfSensorsAsString(envelope);
        String reformattedQuery = reformatQuery(query, matchingSensors);
        StringBuilder sb = new StringBuilder();
        Connection connection = null;

        try {
            connection = Main.getDefaultStorage().getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = statement.executeQuery(reformattedQuery);
            ResultSetMetaData metaData;    // Additional information about the results
            int numCols, numRows;          // How many rows and columns in the table
            metaData = results.getMetaData();       // Get metadata on them
            numCols = metaData.getColumnCount();    // How many columns?
            results.last();                         // Move to last row
            numRows = results.getRow();             // How many rows?

            String s;

            // headers
            //sb.append("# Query: " + query + NEWLINE);
            sb.append("# Query: " + reformattedQuery.replaceAll("\n","\n# ") + NEWLINE);

            sb.append("# ");
            for (int col = 0; col < numCols; col++) {
                sb.append(metaData.getColumnLabel(col + 1));
                if (col < numCols - 1)
                    sb.append(SEPARATOR);
            }
            sb.append(NEWLINE);

            for (int row = 0; row < numRows; row++) {
                results.absolute(row + 1);                // Go to the specified row
                for (int col = 0; col < numCols; col++) {
                    Object o = results.getObject(col + 1); // Get value of the column
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
        }
        catch (SQLException e) {
            sb.append("ERROR in execution of query: " + e.getMessage());
        } finally {
            Main.getDefaultStorage().close(connection);
        }

        return sb.toString();
    }

    public static String executeQuery(String envelope, String matchingSensors, String query) throws ParseException {
        return executeQuery(envelope, query, matchingSensors, CSV_FORMAT);
    }

    public static String executeQueryWithUnion(String envelope, String matchingSensors, String query, String union) throws ParseException {
        String _query = reformatQuery(query, matchingSensors, union);
        return executeQuery(envelope, _query, matchingSensors, CSV_FORMAT);
    }

    public static void main(String[] args) throws ParseException, SQLException {

        String query = "select station, wind_speed, timed\n" +
                "from ($union) as newtable\n" + "where wind_speed>15 \n" +
                "order by wind_speed";
        String matchingsensors = "station1,station2,station3";
        String unionElement = "\"$sensor\" as station, wind_speed, timed as date from $sensor";

        System.out.println("\n=====\n query: \n" + query);
        System.out.println("\n=====\n reformatted: \n" + reformatQuery(query, matchingsensors, unionElement));
    }

    String makeStringFromList(List l) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l.size(); i++) {
            Object o = l.get(i);
            if (o == null)
                sb.append("null");
            else
                sb.append(o.toString());
            if (i < l.size() - 1)
                sb.append(SEPARATOR);
        }
        return sb.toString();
    }

}
