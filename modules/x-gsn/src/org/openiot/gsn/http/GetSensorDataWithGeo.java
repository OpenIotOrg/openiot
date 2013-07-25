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
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

public class GetSensorDataWithGeo {

    private static transient Logger logger = Logger.getLogger(GetSensorDataWithGeo.class);

    private static GetSensorDataWithGeo instance = null;
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

    protected GetSensorDataWithGeo() {

    }

    public static GetSensorDataWithGeo getInstance() { // Singleton
        if (instance == null) {
            instance = new GetSensorDataWithGeo();
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
        while (iter.hasNext()) {
            VSensorConfig sensorConfig = (VSensorConfig) iter.next();
            Double longitude = sensorConfig.getLongitude();
            Double latitude = sensorConfig.getLatitude();
            String sensor = sensorConfig.getName();

            if ((latitude != null) && (longitude != null)) {
                Point point = geometryFactory.createPoint(new Coordinate(longitude.doubleValue(), latitude.doubleValue()));
                coordinates.add(point);
                sensors.add(sensor);
                s.append(sensor)
                        .append(" => ")
                        .append(longitude)
                        .append(" : ")
                        .append(latitude)
                        .append(NEWLINE);
            }
        }
        return s.toString();
    }

    /*
    * Builds geographic geoIndex from list of sensors currently loaded in the system
    * */
    public static void buildGeoIndex() {

        geoIndex = new STRtree();
        geometryFactory = new GeometryFactory();
        sensors = new Vector<String>();
        coordinates = new Vector<Point>();

        getListOfSensors();

        for (int i = 0; i < sensors.size(); i++) {
            geoIndex.insert(coordinates.get(i).getEnvelopeInternal(), coordinates.get(i));
            logger.warn(sensors.get(i) + " : " + coordinates.get(i) + " : " + searchForSensors_String(coordinates.get(i)));
        }
        geoIndex.build();
    }

    /*
    *  Returns the list of sensors whose locations are within the given envelope
    * */
    /*
    public static String getListOfSensorsAsString(String envelope) throws ParseException {
        Geometry geom = new WKTReader().read(envelope);
        List listEnvelope = geoIndex.query(geom.getEnvelopeInternal());

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < listEnvelope.size(); i++) {
            sb.append(searchForSensors_String((Point) listEnvelope.get(i))).append(" "); // use space as temporary separator
        }
        return sb.toString()
                .trim()               // remove last trailing space
                .replace(" ", SEPARATOR);   // replace other spaces with commas
    }
    */

    public static ArrayList<String> getListOfSensors(String envelope) throws ParseException {
        Geometry geom = new WKTReader().read(envelope);
        List listEnvelope = geoIndex.query(geom.getEnvelopeInternal());
        ArrayList<String> sensors = new ArrayList<String>();
        for (int i = 0; i < listEnvelope.size(); i++) {
            sensors.add(searchForSensors_String((Point) listEnvelope.get(i)));
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
            //System.out.println("unionofAll => " + unionOfAll);
            //System.out.println("union keyword => " + UNION_RESERVED_WORD_REGEX);
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

            //System.out.println("* Executing query *\n" + reformattedQuery + "\n***");

            // headers
            //sb.append("# Query: " + query + NEWLINE);
            sb.append("# Query: " + reformattedQuery.replaceAll("\n","\n# ") + NEWLINE);

            sb.append("# ");
            //System.out.println("ncols: " + numCols);
            //System.out.println("nrows: " + numRows);
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

        /*
        GetSensorDataWithGeo g = GetSensorDataWithGeo.getInstance();
        g.buildGeoIndex();
        //String env = "POLYGON ((10 5, 40.5 20.7, 11.5 23.2, 32.5 41.8, 17.78 15.9, 29.67 2.876, 10 5))";
        String env = "POLYGON ((0 0, 0 100, 100 100, 100 0, 0 0))";
        System.out.println(g.getListOfSensorsAsString(env));
        System.out.println(getListOfSensorsAsString());
        System.out.println(executeQuery(env, "select * from sensors"));
        */
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
