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
 * @author Sofiane Sarni
*/

package org.openiot.gsn.wrappers;

import org.openiot.gsn.Main;
import org.openiot.gsn.storage.StorageManagerFactory;
import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.storage.StorageManager;
import org.openiot.gsn.storage.DataEnumerator;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TetraedreNMCWrapper extends AbstractWrapper {

    private static long DEFAULT_RATE = 1000;   // 1 second in milliseconds
    private static long DEFAULT_BUFFER_SIZE = 100;

    private transient Logger logger = Logger.getLogger(this.getClass());
    private DataField[] outputFormat;
    private int threadCounter = 0;
    private String table_name;
    private long start_time;
    private long rate = DEFAULT_RATE;
    private long buffer_size = DEFAULT_BUFFER_SIZE;
    private long latest_timed;
    private String checkPointDir;

    private String driver;
    private String username;
    private String password;
    private String databaseURL;

    String checkPointFile;
    StorageManager sm = null;

    String[] dataFieldNames;
    Byte[] dataFieldTypes;
    int dataFieldsLength;

    boolean useDefaultStorageManager = true;

    public String getWrapperName() {
        return "TetraedreNMCWrapper";
    }

    public void dispose() {
        threadCounter--;
    }

    public DataField[] getOutputFormat() {
        return outputFormat;
    }

    public boolean initialize() {
        setName(getWrapperName() + "-" + (++threadCounter));
        AddressBean addressBean = getActiveAddressBean();

        table_name = addressBean.getPredicateValue("table-name");
        databaseURL = addressBean.getPredicateValue("jdbc-url");
        username = addressBean.getPredicateValue("username");
        password = addressBean.getPredicateValue("password");
        driver = addressBean.getPredicateValue("driver");

        if ((databaseURL != null) && (username != null) && (password != null) && (driver != null)) {
            useDefaultStorageManager = false;
            sm = StorageManagerFactory.getInstance(driver, username, password, databaseURL, 8);
            logger.warn("Using specified storage manager: " + databaseURL);
        } else {
            sm = Main.getDefaultStorage();
            logger.warn("Using default storage manager");
        }

        if (table_name == null) {
            logger.warn("The > table-name < parameter is missing from the wrapper for VS " + this.getActiveAddressBean().getVirtualSensorName());
            return false;
        }

        //////////////////
        boolean usePreviousCheckPoint = true;
        String time = addressBean.getPredicateValue("start-time");
        if (time == null) {
            logger.warn("The > start-time < parameter is missing from the wrapper for VS " + this.getActiveAddressBean().getVirtualSensorName());
            return false;
        }

        if (time.equalsIgnoreCase("continue")) {
            latest_timed = getLatestProcessed();
            usePreviousCheckPoint = false;
            logger.warn("Mode: continue => " + latest_timed);
        } else if (isISOFormat(time)) {

            try {
                DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                start_time = fmt.parseDateTime(time).getMillis();
                latest_timed = start_time;
                logger.warn("Mode: ISO => " + latest_timed);
            }
            catch (IllegalArgumentException e) {
                logger.warn("The > start-time < parameter is malformed (looks like ISO8601) for VS " + this.getActiveAddressBean().getVirtualSensorName());
                return false;
            }
        } else if (isLong(time)) {
            try {
                latest_timed = Long.parseLong(time);
                logger.warn("Mode: epoch => " + latest_timed);
            }
            catch (NumberFormatException e) {
                logger.warn("The > start-time < parameter is malformed (looks like epoch) for VS " + this.getActiveAddressBean().getVirtualSensorName());
                return false;
            }
        } else {
            logger.warn("Incorrectly formatted > start-time < accepted values are: 'continue' (from latest element in destination table), iso-date (e.g. 2009-11-02T00:00:00.000+00:00), or epoch (e.g. 1257946505000)");
            return false;
        }

        //////////////////

        checkPointDir = addressBean.getPredicateValueWithDefault("check-point-directory", "jdbc-check-points");
        checkPointFile = checkPointDir + "/" + table_name + "-" + this.getActiveAddressBean().getVirtualSensorName();
        new File(checkPointDir).mkdirs();

        if (usePreviousCheckPoint) {
            logger.warn("trying to read latest timestamp from chekpoint file ... " + checkPointFile);
            try {
                if (getLatestTimeStampFromCheckPoint() != 0) {
                    latest_timed = getLatestTimeStampFromCheckPoint();
                    logger.warn("latest ts => " + latest_timed);
                } else
                    logger.warn("wrong value for latest ts (" + getLatestTimeStampFromCheckPoint() + "), ignored");
            } catch (IOException e) {
                logger.warn("Checkpoints couldn't be used due to IO exception.");
                logger.warn(e.getMessage(), e);
            }
        }

        //////////////////


        Connection connection = null;
        try {
            logger.info("Initializing the structure of JDBCWrapper with : " + table_name);
            connection = sm.getConnection();

            outputFormat = sm.tableToStructureByString(table_name, connection);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            sm.close(connection);
        }

        dataFieldsLength = outputFormat.length;
        dataFieldNames = new String[dataFieldsLength];
        dataFieldTypes = new Byte[dataFieldsLength];

        for (int i = 0; i < outputFormat.length; i++) {
            dataFieldNames[i] = outputFormat[i].getName();
            dataFieldTypes[i] = outputFormat[i].getDataTypeID();
        }

        return true;
    }

    public long getLatestTimeStampFromCheckPoint() throws IOException {
        String val = FileUtils.readFileToString(new File(checkPointFile), "UTF-8");
        long lastItem = 0;
        if (val != null && val.trim().length() > 0)
            lastItem = Long.parseLong(val.trim());
        return lastItem;
    }

    public void run() {
        DataEnumerator data;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        Connection conn = null;
        ResultSet resultSet = null;

        while (isActive()) {
            try {
                conn = sm.getConnection();
                StringBuilder query = new StringBuilder("select * from ").append(table_name).append(" where timestamp*1000 > " + latest_timed + "  order by timestamp limit 0," + buffer_size);

                
                resultSet = sm.executeQueryWithResultSet(query, conn);

                //logger.debug(query);

                while (resultSet.next()) {
                    Serializable[] output = new Serializable[this.getOutputFormat().length];

                    //long pk = resultSet.getLong(1);
                    long timed = resultSet.getLong(1)*1000;

                    //logger.warn("pk => "+ pk);
                    //logger.warn("timed => "+ timed);

                    for (int i = 0; i < dataFieldsLength; i++) {

                        switch (dataFieldTypes[i]) {
                            case DataTypes.VARCHAR:
                            case DataTypes.CHAR:
                                output[i] = resultSet.getString(i + 1);
                                break;
                            case DataTypes.INTEGER:
                                output[i] = resultSet.getInt(i + 1);
                                break;
                            case DataTypes.TINYINT:
                                output[i] = resultSet.getByte(i + 1);
                                break;
                            case DataTypes.SMALLINT:
                                output[i] = resultSet.getShort(i + 1);
                                break;
                            case DataTypes.DOUBLE:
                                output[i] = resultSet.getDouble(i + 1);
                                break;
                            case DataTypes.BIGINT:
                                output[i] = resultSet.getLong(i + 1);
                                break;
                            case DataTypes.BINARY:
                                output[i] = resultSet.getBytes(i + 1);
                                break;
                        }
                        //logger.warn(i+" (type: "+dataFieldTypes[i]+" ) => "+output[i]);
                    }

                    StreamElement se = new StreamElement(dataFieldNames, dataFieldTypes, output, timed);
                    latest_timed = se.getTimeStamp();

                    //logger.warn(" Latest => " + latest_timed);

                    this.postStreamElement(se);

                    updateCheckPointFile(latest_timed);

                    //logger.warn(se);
                }

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                sm.close(resultSet);
                sm.close(conn);
            }

            try {
                Thread.sleep(rate);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void updateCheckPointFile(long timestamp) throws IOException {
        FileUtils.writeStringToFile(new File(checkPointFile), Long.toString(timestamp), "UTF-8");
    }

    public long getLatestProcessed() {
        DataEnumerator data;
        long latest = -1;
        StringBuilder query = new StringBuilder("select max(timed) from ").append(this.getActiveAddressBean().getVirtualSensorName());
        try {
            data = sm.executeQuery(query, false);
            logger.warn("Running query " + query);

            while (data.hasMoreElements()) {
                StreamElement se = data.nextElement();
                if (se.getData("max(timed)") != null)
                    latest = (Long) se.getData("max(timed)");
                logger.warn(" MAX ts = " + latest);
                logger.warn(se);

            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
        }
        return latest;
    }


    public boolean isISOFormat(String time) {
        //Example: 2009-11-02T00:00:00.000+00:00
        String regexMask = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}[+-]\\d{2}:\\d{2}$";
        Pattern pattern = Pattern.compile(regexMask);
        Matcher matcher = pattern.matcher(time);
        logger.debug("Testing... " + time + " <==> " + regexMask);
        if (matcher.find()) {
            logger.debug(">>>>>    ISO FORMAT");
            return true;
        } else
            return false;
    }

    public boolean isLong(String time) {

        String regexMask = "^\\d+$";
        Pattern pattern = Pattern.compile(regexMask);
        Matcher matcher = pattern.matcher(time);
        logger.debug("Testing... " + time + " <==> " + regexMask);
        if (matcher.find()) {
            logger.debug(">>>>>    LONG number");
            return true;
        } else
            return false;
    }
}

