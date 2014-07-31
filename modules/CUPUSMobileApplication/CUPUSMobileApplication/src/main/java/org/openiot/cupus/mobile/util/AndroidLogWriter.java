package org.openiot.cupus.mobile.util;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Kristijan on 03.02.14..
 */
public class AndroidLogWriter {

    private BufferedWriter log;
    public static final String DATE_FORMAT_NOW = "dd.MM.yyyy HH:mm:ss";

    private boolean logging;
    private boolean testing;

    /**
     * Constructor
     *
     * @param filename
     *            name of file where to write log.
     */
    public AndroidLogWriter(String filename, boolean logging, boolean testing, Context context) {
        this.logging = logging;
        this.testing = testing;
        if (logging) {
            try {
                File folder = new File(context.getFilesDir() + "log");
                folder.mkdir();
                File logFile = new File(folder.getPath() + filename);
                System.out.println("File: " + logFile.getPath());
                log = new BufferedWriter(new FileWriter(logFile));
                writeToLog("Log file: " + folder.getCanonicalPath()
                        + File.separator + filename);
            } catch (Exception e) {
                throw new RuntimeException("Failed to make the log file...", e);
            }
        }
    }

    /**
     * Writes string to log.
     *
     * @param text
     *            Input text for writing
     */
    public void writeToLog(String text, boolean forceToScreen) {
        if (logging) {
            try {
                log.write(now() + " " + text + "\n");
                log.flush();
            } catch (IOException e) {
                // logging error... so what xD
            }
        }
        if (testing || forceToScreen) {
            System.out.println(text);
        }
    }

    public void writeToLog(String text) {
        writeToLog(text, false);
    }

    /**
     * Adds "ERROR: " in front of the given string and forces the output to
     * screen [calls writeToLog("ERROR: "+error, true)]
     *
     * @param error
     *            Msn to print/log
     */
    public void error(String error) {
        writeToLog("ERROR: " + error, true);
    }

    /**
     *
     * This method returns current time in format set by DATE_FORMAT_NOW
     */
    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }

    /**
     * Closes log
     */
    public void close() {
        try {
            log.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
