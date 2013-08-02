package org.openiot.gsn.wrappers;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GridDataWrapper extends AbstractWrapper {
    private static final transient Logger logger = Logger.getLogger(GridDataWrapper.class);
    private static int threadCounter;

    private String directory;
    private String fileExtension;
    private String timeFormat;
    private String fileMask;
    private long latestProcessedTimestamp;

    private static final String PARAM_DIRECTORY = "directory";
    private static final String PARAM_FILE_MASK = "file-mask";
    private static final String PARAM_TIME_FORMAT = "time-format";
    private static final String PARAM_EXTENSION = "extension";
    private static final String PARAM_RATE = "rate";

    private static final String[] ESRI_Format = {"ncols",
            "nrows",
            "xllcorner",
            "yllcorner",
            "cellsize",
            "NODATA_value"};

    private String header[] = new String[6];

    private int ncols;
    private int nrows;
    private double xllcorner;
    private double yllcorner;
    private double cellsize;
    private double NODATA_value;
    private Double[][] rawData;

    private long rate;

    public boolean initialize() {
        setName(getWrapperName() + "-" + (++threadCounter));

        AddressBean addressBean = getActiveAddressBean();

        fileExtension = addressBean.getPredicateValue(PARAM_EXTENSION);
        if (fileExtension == null) {
            logger.warn("The > " + PARAM_EXTENSION + " < parameter is missing from the wrapper for VS " + this.getActiveAddressBean().getVirtualSensorName());
            return false;
        }

        timeFormat = addressBean.getPredicateValue(PARAM_TIME_FORMAT);
        if (timeFormat == null) {
            logger.warn("The > " + PARAM_TIME_FORMAT + " < parameter is missing from the wrapper for VS " + this.getActiveAddressBean().getVirtualSensorName());
            return false;
        }

        fileMask = addressBean.getPredicateValue(PARAM_FILE_MASK);
        if (fileMask == null) {
            logger.warn("The > " + PARAM_FILE_MASK + " < parameter is missing from the wrapper for VS " + this.getActiveAddressBean().getVirtualSensorName());
            return false;
        }

        directory = addressBean.getPredicateValue(PARAM_DIRECTORY);
        if (directory == null) {
            logger.warn("The > " + PARAM_DIRECTORY + " < parameter is missing from the wrapper for VS " + this.getActiveAddressBean().getVirtualSensorName());
            return false;
        }

        String rateStr = addressBean.getPredicateValue(PARAM_RATE);
        if (rateStr != null) {

            try {
                rate = Integer.parseInt(rateStr);
            } catch (NumberFormatException e) {
                logger.warn("The > " + PARAM_RATE + " < parameter is invalid for wrapper in VS " + this.getActiveAddressBean().getVirtualSensorName());
                return false;
            }
        } else {
            logger.warn("The > " + PARAM_RATE + " < parameter is missing from the wrapper in VS " + this.getActiveAddressBean().getVirtualSensorName());
            return false;
        }

        latestProcessedTimestamp = -1;

        return true;
    }

    public DataField[] getOutputFormat() {
        return new DataField[]{
                new DataField("ncols", "int", "number of columns"),
                new DataField("nrows", "int", "number of rows"),
                new DataField("xllcorner", "double", "xll corner"),
                new DataField("yllcorner", "double", "yll corner"),
                new DataField("cellsize", "double", "cell size"),
                new DataField("nodata_value", "double", "no data value"),
                new DataField("grid", "binary:image/raw", "raw raster data")};
    }

    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        while (isActive()) {
            try {

                listOfNewFiles(directory, fileMask);
                Thread.sleep(rate);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public boolean parseFile(String fileName) {
        boolean success = true;
        String line;
        BufferedReader reader = null;
        List<String> lines = new ArrayList<String>();
        try {

            reader = new BufferedReader(new FileReader(fileName));

            line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }

            //System.out.println(lines);

        } catch (FileNotFoundException e) {
            success = false;
            logger.warn("File not found: " + fileName);
            logger.warn(e);
        } catch (IOException e) {
            success = false;
            logger.warn("IO exception on opening of file: " + fileName);
            logger.warn(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logger.warn("IO exception on closing of file: " + fileName);
                logger.warn(e);
            }
        }

        if ((success) && (lines != null)) {

            // trim white spaces, replace tabs and multiple spaces with a single space
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, lines.get(i).trim().replaceAll("[ \t]+", " "));
                //System.out.println(lines.get(i));
            }

            logger.debug("size " + lines.size());

            try {
                for (int i = 0; i < 6; i++) {
                    String[] split = lines.get(i).split(" ");

                    header[i] = split[1];
                    logger.debug(split[0] + " <=> " + ESRI_Format[i]);
                    if (!split[0].equals(ESRI_Format[i])) {
                        logger.debug("=> inCorrect");
                        success = false;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                success = false;
                logger.warn("Badly formatted file: " + fileName);
                logger.warn(e);
            }

            if (success) {
                System.out.println("format is correct");
                ncols = Integer.parseInt(header[0]);
                nrows = Integer.parseInt(header[1]);
                xllcorner = Double.parseDouble(header[2]);
                yllcorner = Double.parseDouble(header[3]);
                cellsize = Double.parseDouble(header[4]);
                NODATA_value = Double.parseDouble(header[5]);

                logger.debug("ncols " + ncols);
                logger.debug("nrows " + nrows);
                logger.debug("xllcorner " + xllcorner);
                logger.debug("yllcorner " + yllcorner);
                logger.debug("cellsize " + cellsize);
                logger.debug("NODATA_value " + NODATA_value);
            }

            //parse raw data
            if (success) {
                List raw = new ArrayList<Double>();

                for (int i = 6; i < lines.size(); i++) {
                    String[] aLine = lines.get(i).split(" ");
                    for (int j = 0; j < aLine.length; j++) {

                        try {
                            Double d = Double.parseDouble(aLine[j]);
                            if (d != null)
                                raw.add(d);
                            else
                                raw.add(NODATA_value);
                        } catch (java.lang.NumberFormatException e) {
                            logger.warn(j + ": \"" + aLine[j] + "\"");
                            logger.warn(e);
                            logger.warn(e.getMessage());
                        }
                        //System.out.println(i + "," + j + " : " + d);
                    }

                }

                logger.debug("Size of list => " + raw.size() + " ? " + ncols * nrows);
                logger.debug(raw);

                if (raw.size() == nrows * ncols) {
                    rawData = new Double[nrows][ncols];
                    for (int i = 0; i < nrows; i++)
                        for (int j = 0; j < ncols; j++) {
                            rawData[i][j] = (Double) raw.get(i * ncols + j);
                            //System.out.println(i + "," + j + " : " + rawData[i][j]);
                        }
                    logger.debug("rawData.length " + rawData.length);
                    logger.debug("rawData[0].length " + rawData[0].length);
                } else {
                    success = false;
                }
            }


        }

        return success;
    }

    public void dispose() {
        threadCounter--;
    }

    private Vector<String> listOfNewFiles(String dir, String regexFileMask) {

        File f = new File(dir);
        String[] files = f.list();

        Arrays.sort(files);

        Vector<String> v = new Vector<String>();
        logger.warn("*** found " + files.length + " files ***");
        for (int i = 0; i < files.length; i++) {
            String file = files[i];
            Pattern pattern = Pattern.compile(regexFileMask);
            Matcher matcher = pattern.matcher(file);
            logger.warn("(" + i + ") Testing... " + file);
            if (matcher.find()) {
                String date = getTimeStampFromFileName(file, regexFileMask);
                long epoch = strTime2Long(date, timeFormat);
                logger.warn("Matching => " + file + " date = " + date + " epoch = " + epoch);
                if (epoch > latestProcessedTimestamp) {
                    logger.warn("New file => " + epoch);
                    latestProcessedTimestamp = epoch;
                    v.add(file);
                    postData(dir + "/" + file, epoch);
                }
            }
        }

        return v;
    }

    /*
    * Posting data to database
    * */
    private boolean postData(String filePath, long timed) {


        parseFile(filePath);

        boolean success = true;

        Serializable[] stream = new Serializable[7];

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(rawData);
            oos.flush();
            oos.close();
            bos.close();

            System.out.println("size => " + bos.toByteArray().length);

            stream[0] = new Integer(ncols);
            stream[1] = new Integer(nrows);
            stream[2] = new Double(xllcorner);
            stream[3] = new Double(yllcorner);
            stream[4] = new Double(cellsize);
            stream[5] = new Double(NODATA_value);
            stream[6] = bos.toByteArray();

            logger.debug("size => " + bos.toByteArray().length);

            //testDeserialize(bos.toByteArray());


        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            success = false;
        }

        StreamElement se = new StreamElement(getOutputFormat(), stream, timed);

        if (success) {
            success = postStreamElement(se);
        }

        return success;
    }

    /*
    * Test deserialization
    * */
    public static void testDeserialize(byte[] bytes) {

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
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < deserial[0].length; j++) {
                    sb.append(deserial[i][j]).append(" ");
                }
                System.out.println(sb.toString());
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getWrapperName() {
        return "GridDataWrapper";
    }

    private long strTime2Long(String s, String timeFormat) {

        long l = -1;
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(timeFormat);
            l = fmt.parseDateTime(s).getMillis();
        } catch (java.lang.IllegalArgumentException e) {
            logger.warn(e.getMessage(), e);
        }
        return l;
    }

    private String getTimeStampFromFileName(String fileName, String regexMask) {

        Pattern pattern = Pattern.compile(regexMask);
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            logger.debug("Date => " + matcher.group(1));
            return matcher.group(1);
        } else {
            logger.debug("Date => null");
            return null;
        }
    }
}
