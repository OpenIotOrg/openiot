package org.openiot.gsn.wrappers.general;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.utils.CaseInsensitiveComparator;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import au.com.bytecode.opencsv.CSVReader;

/**
 * possible formats for the timestamp fields are available @ http://joda-time.sourceforge.net/api-release/org/joda/time/format/DateTimeFormat.html
 * Possible timezone : http://joda-time.sourceforge.net/timezones.html
 */
public class CSVHandler {


    public static final String LOCAL_TIMEZONE_ID = DateTimeZone.getDefault().getID();

    private static Logger logger = Logger.getLogger(CSVHandler.class);

    private static final String TIMESTAMP = "timed";

    public static DateTime parseTimeStamp(String format, String value) throws IllegalArgumentException {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
        return fmt.parseDateTime(value);
    }

    private char stringSeparator, separator;
    private String dataFile;
    private DateTimeZone timeZone;
    private int skipFirstXLines;
    private String[] fields, formats, nulls;

    private String checkPointFile;

    public boolean initialize(String dataFile, String inFields, String inFormats, char separator, char stringSeparator, int skipFirstXLines, String nullValues) {
        return initialize(dataFile, inFields, inFormats, separator, stringSeparator, skipFirstXLines, nullValues, LOCAL_TIMEZONE_ID, "check-poin/" + (new File(dataFile).getName() + ".chk-point"));
    }

    public boolean initialize(String dataFile, String inFields, String inFormats, char separator, char stringSeparator, int skipFirstXLines, String nullValues, String timeZone, String checkpointFile) {

        this.stringSeparator = stringSeparator; // default to ,
        this.skipFirstXLines = skipFirstXLines;// default to 0
        this.dataFile = dataFile; // check if it exist.
        this.separator = separator;
        this.timeZone = DateTimeZone.forID(timeZone);
        this.checkPointFile = checkpointFile;
        File file = new File(dataFile);

        if (!file.isFile()) {
            logger.error("The specified CSV data file: " + dataFile + " doesn't exists.");
            return false;
        }

        try {
            setupCheckPointFileIfNeeded();
            this.fields = generateFieldIdx(inFields, true);
            this.formats = generateFieldIdx(inFormats, false);
            this.nulls = generateFieldIdx(nullValues, true);
            ////////////////////////
            // TODO: Check that the lengths are the same
            ////////////////////////

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        if (!validateFormats(this.formats))
            return false;
        if (fields.length != formats.length) {
            logger.error("loading the csv-wrapper failed as the length of fields(" + fields.length + ") doesn't match the length of formats(" + formats.length + ")");
            return false;
        }
        return true;

    }

    public void setupCheckPointFileIfNeeded() throws IOException {
        String chkPointDir = new File(new File(getCheckPointFile()).getParent()).getAbsolutePath();
        new File(chkPointDir).mkdirs();
        new File(getCheckPointFile()).createNewFile();
    }

    public static boolean validateFormats(String[] formats) {
        for (int i = 0; i < formats.length; i++) {
            if (formats[i].equalsIgnoreCase("numeric") || formats[i].equalsIgnoreCase("string"))
                continue;
            else if (isTimeStampFormat(formats[i])) {
                try {
                    String tmp = DateTimeFormat.forPattern(getTimeStampFormat(formats[i])).print(System.currentTimeMillis());
                } catch (IllegalArgumentException e) {
                    logger.error("Validating the time-format(" + formats[i] + ") used by the CSV-wrapper is failed. ");
                    return false;
                }
            } else {
                logger.error("The format (" + formats[i] + ") used by the CSV-Wrapper doesn't exist.");
                return false;
            }
        }
        return true;

    }

    /**
     * Removes the space from the fields.
     * Split the rawFields using comma as the separator.
     *
     * @param rawFields
     * @param toLowerCase, if false, the case is preserved. if true, the actual outputs will be in lower-case.
     * @return
     * @throws IOException
     */
    public static String[] generateFieldIdx(String rawFields, boolean toLowerCase) throws IOException {
        String[] toReturn = new CSVReader(new StringReader(rawFields)).readNext();
        if (toReturn == null)
            return new String[0];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = toReturn[i].trim();
            if (toLowerCase)
                toReturn[i] = toReturn[i].toLowerCase();
        }
        return toReturn;
    }

    public ArrayList<TreeMap<String, Serializable>> work(Reader dataFile, String checkpointDir) throws IOException {
        ArrayList<TreeMap<String, Serializable>> items = null;
        setupCheckPointFileIfNeeded();
        String val = FileUtils.readFileToString(new File(checkPointFile), "UTF-8");
        long lastItem = 0;
        if (val != null && val.trim().length() > 0)
            lastItem = Long.parseLong(val.trim());
        items = parseValues(dataFile, lastItem);

        return items;
    }

    public void updateCheckPointFile(long timestamp) throws IOException {
        FileUtils.writeStringToFile(new File(checkPointFile), Long.toString(timestamp), "UTF-8");
    }

    private boolean loggedNoChange = false; // to avoid duplicate logging messages when there is no change

    public ArrayList<TreeMap<String, Serializable>> parseValues(Reader datainput, long previousCheckPoint) throws IOException {
        ArrayList<TreeMap<String, Serializable>> toReturn = new ArrayList<TreeMap<String, Serializable>>();
        CSVReader reader = new CSVReader(datainput, getSeparator(), getStringSeparator(), getSkipFirstXLines());
        String[] values = null;
        long currentLine = 0;
        while ((values = reader.readNext()) != null) {
            TreeMap<String, Serializable> se = convertTo(formats, fields, getNulls(), values, getSeparator());
            if (isEmpty(se))
                continue;
            if (se.containsKey(TIMESTAMP)) {
                if (((Long) se.get(TIMESTAMP)) <= previousCheckPoint)
                    continue;
            } else {// assuming useCounterForCheckPoint = true

                if (logger.isDebugEnabled()) {
                    String symbol = (currentLine < previousCheckPoint) ? " < " : " >= ";
                    logger.debug("currentLine=" + currentLine + symbol + "checkpoint=" + previousCheckPoint);
                }

                if (currentLine < previousCheckPoint) {// skipping already read lines, based on line count
                    if (logger.isDebugEnabled()) logger.debug("skipping");
                    currentLine++;
                    continue;
                }

            }
            toReturn.add(se);
            currentLine++;
            loggedNoChange = false;
            if (toReturn.size() > 250)
                break; // Move outside the loop as in each call we only read 250 values;
        }
        if (logger.isDebugEnabled() && toReturn.size() == 0 && loggedNoChange == false) {
            logger.debug("There is no new item after most recent checkpoint(previousCheckPoint:" + new DateTime(previousCheckPoint) + ").");
            loggedNoChange = true;
        }

        reader.close();
        return toReturn;
    }

    private boolean isEmpty(Map<String, Serializable> se) {
        for (Object o : se.values())
            if (o != null)
                return false;
        return true;
    }

    public TreeMap<String, Serializable> convertTo(String[] formats, String[] fields, String nullValues[], String[] values, char separator) {
        TreeMap<String, Serializable> streamElement = new TreeMap<String, Serializable>(new CaseInsensitiveComparator());
        for (String field : fields)
            streamElement.put(field, null);
        HashMap<String, String> timeStampFormats = new HashMap<String, String>();
        for (int i = 0; i < Math.min(fields.length, values.length); i++) {
            if (isNull(nullValues, values[i])) {
                continue;
            } else if (formats[i].equalsIgnoreCase("numeric")) {
                try {
                    streamElement.put(fields[i], Double.parseDouble(values[i]));
                } catch (java.lang.NumberFormatException e) {
                    logger.error("Parsing to Numeric fails: Value to parse=" + values[i]);
                    throw e;
                }
            } else if (formats[i].equalsIgnoreCase("string"))
                streamElement.put(fields[i], values[i]);
            else if (isTimeStampFormat(formats[i])) {
                String value = "";
                String format = "";
                if (streamElement.get(fields[i]) != null) {
                    value = (String) streamElement.get(fields[i]);
                    format = timeStampFormats.get(fields[i]);
                    value += separator;
                    format += separator;
                }
                if (isTimeStampLeftPaddedFormat(formats[i]))
                    values[i] = StringUtils.leftPad(values[i], getTimeStampFormat(formats[i]).length(), '0');

                value += values[i];
                format += getTimeStampFormat(formats[i]);
                streamElement.put(fields[i], value);
                timeStampFormats.put(fields[i], format);
            }
        }
        for (String timeField : timeStampFormats.keySet()) {
            String timeFormat = timeStampFormats.get(timeField);
            String timeValue = (String) streamElement.get(timeField);
            try {
                DateTime x = DateTimeFormat.forPattern(timeFormat).withZone(getTimeZone()).parseDateTime(timeValue);
                streamElement.put(timeField, x.getMillis());
            } catch (IllegalArgumentException e) {
                logger.error("Parsing error: TimeFormat=" + timeFormat + " , TimeValue=" + timeValue);
                logger.error(e.getMessage(), e);
                throw e;
            }
        }

        return streamElement;
    }

    public static String getTimeStampFormat(String input) {
        if (input.indexOf("timestampl(") >= 0)
            return input.substring("timestampl(".length(), input.indexOf(")")).trim();
        else
            return input.substring("timestamp(".length(), input.indexOf(")")).trim();
    }

    public static boolean isTimeStampFormat(String input) {
        return (input.toLowerCase().startsWith("timestamp(") || input.toLowerCase().startsWith("timestampl(")) && input.endsWith(")");
    }

    public static boolean isTimeStampLeftPaddedFormat(String input) {
        return input.toLowerCase().startsWith("timestampl(") && input.endsWith(")");
    }


    public char getSeparator() {
        return separator;
    }

    public char getStringSeparator() {
        return stringSeparator;
    }

    public int getSkipFirstXLines() {
        return skipFirstXLines;
    }

    public static boolean isNull(String[] possibleNullValues, String value) {
        if (value == null || value.length() == 0)
            return true;
        for (int i = 0; i < possibleNullValues.length; i++)
            if (possibleNullValues[i].equalsIgnoreCase(value.trim()))
                return true;
        return false;
    }

    public String[] getFields() {
        return fields;
    }

    public DataField[] getDataFields() {
        HashMap<String, String> fields = new HashMap<String, String>();
        for (int i = 0; i < getFields().length; i++) {
            String field = getFields()[i];
            String type = getFormats()[i];
            if (isTimeStampFormat(type)) {
                //GSN doesn't support timestamp data type, all timestamp values are supposed to be bigint.
                fields.put(field, "bigint");
            } else if (type.equalsIgnoreCase("numeric"))
                fields.put(field, "numeric");
            else
                fields.put(field, "string");
        }
        DataField[] toReturn = new DataField[fields.size()];
        int i = 0;
        for (String key : fields.keySet())
            toReturn[i++] = new DataField(key, fields.get(key));
        return toReturn;
    }

    public String[] getFormats() {
        return formats;
    }

    public String getDataFile() {
        return dataFile;
    }

    public String[] getNulls() {
        return nulls;
    }

    public void setSkipFirstXLines(int skipFirstXLines) {
        this.skipFirstXLines = skipFirstXLines;
    }

    public DateTimeZone getTimeZone() {
        return timeZone;
    }

    public String getCheckPointFile() {
        return checkPointFile;
    }

}
