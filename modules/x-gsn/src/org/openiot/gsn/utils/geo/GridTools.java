package org.openiot.gsn.utils.geo;

import org.openiot.gsn.Main;
import org.openiot.gsn.beans.DataTypes;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

public class GridTools {

    private static transient Logger logger = Logger.getLogger(GridTools.class);

    public static String deSerializeToString(byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream in = null;

            in = new ObjectInputStream(bis);

            Double deserial[][] = new Double[0][];

            deserial = (Double[][]) in.readObject();
            in.close();

            logger.debug("deserial.length" + deserial.length);
            logger.debug("deserial[0].length" + deserial[0].length);

            for (int i = 0; i < deserial.length; i++) {

                for (int j = 0; j < deserial[0].length; j++) {
                    sb.append(deserial[i][j]).append(" ");
                }
                sb.append("\n");
            }

        } catch (IOException e) {
            logger.warn(e);
        } catch (ClassNotFoundException e) {
            logger.warn(e);
        }

        return sb.toString();
    }

    /*
    * deserialization
    * */
    public static Double[][] deSerialize(byte[] bytes) {

        Double deserial[][] = new Double[0][];

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream in = null;

            in = new ObjectInputStream(bis);

            deserial = (Double[][]) in.readObject();
            in.close();

            logger.debug("deserial.length" + deserial.length);
            logger.debug("deserial[0].length" + deserial[0].length);

            for (int i = 0; i < deserial.length; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < deserial[0].length; j++) {
                    sb.append(deserial[i][j]).append(" ");
                }
                logger.debug(sb.toString());
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }

        return deserial;
    }

    public static String executeQueryForGridAsString(String query) {

        Connection connection = null;
        StringBuilder sb = new StringBuilder();
        ResultSet results = null;

        try {
            connection = Main.getDefaultStorage().getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            results = statement.executeQuery(query);
            ResultSetMetaData metaData;    // Additional information about the results
            int numCols, numRows;          // How many rows and columns in the table
            metaData = results.getMetaData();       // Get metadata on them
            numCols = metaData.getColumnCount();    // How many columns?
            results.last();                         // Move to last row
            numRows = results.getRow();             // How many rows?

            String s;

            // headers
            sb.append("# Query: " + query + "\n");
            sb.append("# ");

            byte typ[] = new byte[numCols];
            String columnLabel[] = new String[numCols];

            for (int col = 0; col < numCols; col++) {
                columnLabel[col] = metaData.getColumnLabel(col + 1);
                typ[col] = Main.getDefaultStorage().convertLocalTypeToGSN(metaData.getColumnType(col + 1));
            }

            for (int row = 0; row < numRows; row++) {
                results.absolute(row + 1);                // Go to the specified row
                for (int col = 0; col < numCols; col++) {
                    Object o = results.getObject(col + 1); // Get value of the column
                    if (o == null)
                        s = "null";
                    else
                        s = o.toString();
                    if (typ[col] == DataTypes.BINARY) {
                        byte[] bin = (byte[]) o;
                        sb.append(GridTools.deSerializeToString(bin));
                    } else {
                        sb.append(columnLabel[col] + " " + s + "\n");
                    }
                }
                sb.append("\n");
            }
        } catch (SQLException e) {
            sb.append("ERROR in execution of query: " + e.getMessage());
        } finally {
            if (results != null)
                try {
                    results.close();
                } catch (SQLException e) {
                    logger.warn(e.getMessage(), e);
                }
            Main.getDefaultStorage().close(connection);
        }

        return sb.toString();
    }

    public static String executeQueryForGridAsJSON(String sensor, long timestamp) {

        String query = new StringBuilder("select * from ").append(sensor).append(" where timed=").append(timestamp).toString();
        Connection connection = null;
        StringBuilder sb = new StringBuilder();
        ResultSet results = null;

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("sensor", sensor);
        jsonResponse.put("epoch", timestamp);

        try {
            connection = Main.getDefaultStorage().getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            results = statement.executeQuery(query);
            ResultSetMetaData metaData;    // Additional information about the results
            int numCols, numRows;          // How many rows and columns in the table
            metaData = results.getMetaData();       // Get metadata on them
            numCols = metaData.getColumnCount();    // How many columns?
            results.last();                         // Move to last row
            numRows = results.getRow();             // How many rows?

            byte typ[] = new byte[numCols];
            String columnLabel[] = new String[numCols];

            for (int col = 0; col < numCols; col++) {
                columnLabel[col] = metaData.getColumnLabel(col + 1);
                typ[col] = Main.getDefaultStorage().convertLocalTypeToGSN(metaData.getColumnType(col + 1));
            }

            for (int row = 0; row < numRows; row++) {
                results.absolute(row + 1);                // Go to the specified row
                for (int col = 0; col < numCols; col++) {
                    Object o = results.getObject(col + 1); // Get value of the column

                    if (typ[col] == DataTypes.BINARY) {
                        byte[] bin = (byte[]) o;
                        Double[][] array = GridTools.deSerialize(bin);
                        JSONArray jsonArray = new JSONArray();
                        for (int i=0;i<array.length;i++) {
                            JSONArray anArray = new JSONArray();
                            for (int j=0;j<array[i].length;j++) {
                                anArray.put(array[i][j]);
                            }
                            jsonArray.put(anArray);
                        }
                        jsonResponse.put(columnLabel[col], jsonArray);
                    } else {
                        jsonResponse.put(columnLabel[col], o);
                    }
                }
            }
        } catch (SQLException e) {
            sb.append("ERROR in execution of query: " + e.getMessage());
        } finally {
            if (results != null)
                try {
                    results.close();
                } catch (SQLException e) {
                    logger.warn(e.getMessage(), e);
                }
            Main.getDefaultStorage().close(connection);
        }

        return jsonResponse.toJSONString();
    }
}
