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

package org.openiot.gsn.wrappers.general;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Timezones: http://joda-time.sourceforge.net/timezones.html
 * Formatting: http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html
 */
public class CSVWrapper extends AbstractWrapper {

    private final transient Logger logger = Logger.getLogger(CSVWrapper.class);

    private static int threadCounter = 0;

    private DataField[] dataField;

    private CSVHandler handler = new CSVHandler();

    private int samplingPeriodInMsc;

    private String checkPointDir;

    private String dataFile;

    boolean useCounterForCheckPoint = false;
    long processedLineCounter = 0; // counts lines processed when checkpoint use counter to track changes (instead of timestamp, by default)

    public boolean initialize() {
        setName("CSVWrapper-Thread" + (++threadCounter));
        AddressBean addressBean = getActiveAddressBean();
        dataFile = addressBean.getPredicateValueWithException("file");
        String csvFields = addressBean.getPredicateValueWithException("fields");
        String csvFormats = addressBean.getPredicateValueWithException("formats");
        //String csvSeparator = addressBean.getPredicateValueWithDefault("separator",",");
        String value = addressBean.getPredicateValue("separator");
        String csvSeparator = (value == null || value.length() == 0) ? "," : value;
        checkPointDir = addressBean.getPredicateValueWithDefault("check-point-directory", "./csv-check-points");
        String csvStringQuote = addressBean.getPredicateValueWithDefault("quote", "\"");
        int skipFirstXLine = addressBean.getPredicateValueAsInt("skip-first-lines", 0);
        String timezone = addressBean.getPredicateValueWithDefault("timezone", handler.LOCAL_TIMEZONE_ID);
        String nullValues = addressBean.getPredicateValueWithDefault("bad-values", "");
        String strUseCounterForCheckPoint = addressBean.getPredicateValueWithDefault("use-counter-for-check-point", "false");
        samplingPeriodInMsc = addressBean.getPredicateValueAsInt("sampling", 10000);

        /*
        DEBUG_INFO(dataFile);
        */

        if (csvSeparator != null && csvSeparator.length() != 1) {
            logger.warn("The provided CSV separator:>" + csvSeparator + "< should only have  1 character, thus ignored and instead \",\" is used.");
            csvSeparator = ",";
        }

        if (csvStringQuote.length() != 1) {
            logger.warn("The provided CSV quote:>" + csvSeparator + "< should only have 1 character, thus ignored and instead '\"' is used.");
            csvStringQuote = "\"";
        }

        try {
            if (strUseCounterForCheckPoint.equalsIgnoreCase("true")) {
                useCounterForCheckPoint = true;
                logger.warn("Using counter-based check points");
            }
            //String checkPointFile = new File(checkPointDir).getAbsolutePath()+"/"+(new File(dataFile).getName())+"-"+addressBean.hashCode();
            StringBuilder checkPointFile = new StringBuilder()
                    .append(new File(checkPointDir).getAbsolutePath())
                    .append("/")
                    .append(addressBean.getVirtualSensorName())
                    .append("_")
                    .append(addressBean.getInputStreamName())
                    .append("_")
                    .append(addressBean.getWrapper())
                    .append("_")
                    .append(new File(dataFile).getName());
            if (!handler.initialize(dataFile.trim(), csvFields, csvFormats, csvSeparator.toCharArray()[0], csvStringQuote.toCharArray()[0], skipFirstXLine, nullValues, timezone, checkPointFile.toString()))
                return false;

            String val = FileUtils.readFileToString(new File(checkPointFile.toString()), "UTF-8");
            long lastItem = 0;
            if (val != null && val.trim().length() > 0)
                lastItem = Long.parseLong(val.trim());
            logger.warn("Latest item: "+lastItem);

            if (useCounterForCheckPoint) {
                processedLineCounter = lastItem;
            }

        } catch (Exception e) {
            logger.error("Loading the csv-wrapper failed:" + e.getMessage(), e);
            return false;
        }

        dataField = handler.getDataFields();

        logger.warn("Reading from: " + dataFile);

        return true;
    }


    public void run() {
        Exception preivousError = null;
        long previousModTime = -1;
        long previousCheckModTime = -1;
        while (isActive()) {
            File dataFile = new File(handler.getDataFile());
            File chkPointFile = new File(handler.getCheckPointFile());
            long lastModified = -1;
            long lastModifiedCheckPoint = -1;
            if (dataFile.isFile())
                lastModified = dataFile.lastModified();
            if (chkPointFile.isFile())
                lastModifiedCheckPoint = chkPointFile.lastModified();
            FileReader reader = null;

            /*
            DEBUG_INFO("* Entry *");
            DEBUG_INFO(list("lastModified", lastModified));
            DEBUG_INFO(list("lastModifiedCheckPoint", lastModifiedCheckPoint));
            */

            try {
                ArrayList<TreeMap<String, Serializable>> output = null;
                if (preivousError == null || (preivousError != null && ((lastModified != previousModTime || lastModifiedCheckPoint != previousCheckModTime) || useCounterForCheckPoint))) {

                    reader = new FileReader(handler.getDataFile());
                    output = handler.work(reader, checkPointDir);
                    for (TreeMap<String, Serializable> se : output) {
                        StreamElement streamElement = new StreamElement(se, getOutputFormat());
                        processedLineCounter++;
                        //logger.warn(se);
                        boolean insertionSuccess = postStreamElement(streamElement);

                        if (!useCounterForCheckPoint)
                            handler.updateCheckPointFile(streamElement.getTimeStamp()); // write latest processed timestamp
                        else
                            handler.updateCheckPointFile(processedLineCounter); // write latest processed line number
                    }
                }
                //if (output==null || output.size()==0) //More intelligent sleeping, being more proactive once the wrapper receives huge files.
                Thread.sleep(samplingPeriodInMsc);
            } catch (Exception e) {
                if (preivousError != null && preivousError.getMessage().equals(e.getMessage()))
                    continue;
                logger.error(e.getMessage() + " :: " + dataFile, e);
                preivousError = e;
                previousModTime = lastModified;
                previousCheckModTime = lastModifiedCheckPoint;
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        logger.debug(e.getMessage(), e);
                    }
            }
            /*
            DEBUG_INFO("* Exit *");
            */
        }
    }

    public DataField[] getOutputFormat() {
        return dataField;
    }

    public String getWrapperName() {
        return this.getClass().getName();
    }

    public void dispose() {
        threadCounter--;
    }

 /*
   * Convenient function used for debugging
   * */
    public void DEBUG_INFO(String s) {

        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss,SSS").format(new java.util.Date(System.currentTimeMillis()));
        s = "[" + date + "] " + s + "\n";
        try {
            FileUtils.writeStringToFile(new File("DEBUG_INFO_" + threadCounter + ".txt"), s, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String list(String name, long value) {
        return name + " = " + value + " (" + new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss,SSS").format(new java.util.Date(value)) + ")";
    }


}