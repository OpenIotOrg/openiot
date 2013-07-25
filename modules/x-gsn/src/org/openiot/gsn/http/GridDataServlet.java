package org.openiot.gsn.http;

import org.openiot.gsn.utils.Helpers;
import org.openiot.gsn.utils.geo.GridTools;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.*;


public class GridDataServlet extends HttpServlet {

    private static transient Logger logger = Logger.getLogger(GridDataServlet.class);
    private static final String DEFAULT_TIMEFORMAT = "yyyyMMddHHmmss";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
        User user = null;
        if (Main.getContainerConfig().isAcEnabled()) {
            HttpSession session = request.getSession();
            user = (User) session.getAttribute("user");
            response.setHeader("Cache-Control", "no-store");
            response.setDateHeader("Expires", 0);
            response.setHeader("Pragma", "no-cache");
        }
        */

        String sensor = HttpRequestUtils.getStringParameter("sensor", null, request);
        String from = HttpRequestUtils.getStringParameter("from", null, request);
        String to = HttpRequestUtils.getStringParameter("to", null, request);
        String xcol = HttpRequestUtils.getStringParameter("xcol", null, request);
        String ycol = HttpRequestUtils.getStringParameter("ycol", null, request);
        String timeformat = HttpRequestUtils.getStringParameter("timeformat", null, request);
        String view = HttpRequestUtils.getStringParameter("view", null, request); // files or stream
        String debug = HttpRequestUtils.getStringParameter("debug", "false", request); // show debug information or not

        String timeBounds = (from != null && to != null) ? " where timed >= " + from + " and timed <= " + to : "";

        logger.warn("from: " + from);
        logger.warn("to:" + to);
        logger.warn("from != null && to != null =>" + from != null && to != null);
        logger.warn("timeBounds: \"" + timeBounds + "\"");

        String query = "select * from " + sensor + timeBounds;

        StringBuilder debugInformation = new StringBuilder();

        if (debug.equalsIgnoreCase("true")) {
            debugInformation.append("# sensor: " + sensor + "\n")
                    .append("# from: " + from + "\n")
                    .append("# to: " + to + "\n")
                    .append("# xcol: " + to + "\n")
                    .append("# ycol: " + to + "\n")
                    .append("# timeformat: " + to + "\n")
                    .append("# view: " + to + "\n")
                    .append("# Query: " + query + "\n");

            response.getWriter().write(debugInformation.toString());
            response.getWriter().flush();
        }


        response.getWriter().write(GridTools.executeQueryForGridAsString(query));

        /*
        for (String vsName : sensors) {
            if (!Main.getContainerConfig().isAcEnabled() || (user != null && (user.hasReadAccessRight(vsName) || user.isAdmin()))) {
                matchingSensors.append(vsName);
                matchingSensors.append(GetSensorDataWithGeo.SEPARATOR);
            }
        }
        */

    }

    public void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {
        doGet(request, res);
    }

    private String generateASCIIFileName(String sensor, long timestamp, String timeFormat) {
        StringBuilder sb = new StringBuilder();
        sb.append(sensor).append("_").append(Helpers.convertTimeFromLongToIso(timestamp, timeFormat));
        return sb.toString();
    }

    private String generaACIIFIleName(String sensor, long timestamp) {
        return generateASCIIFileName(sensor, timestamp, DEFAULT_TIMEFORMAT);
    }

    private void writeASCIIFile(String fileName, String folder, String content) {
        try {
            FileWriter outFile = new FileWriter(folder + "/" + fileName);
            PrintWriter out = new PrintWriter(outFile);
            out.print(content);
            out.close();
        } catch (IOException e) {
            logger.warn(e);
        }
    }

    private void writeZipFile(String folder, String[] filenames, String outFilename) {

        byte[] buf = new byte[1024];

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(folder + "/" + outFilename));

            for (int i = 0; i < filenames.length; i++) {
                FileInputStream fileInputStream = new FileInputStream(filenames[i]);

                zipOutputStream.putNextEntry(new ZipEntry(filenames[i]));

                int len;
                while ((len = fileInputStream.read(buf)) > 0) {
                    zipOutputStream.write(buf, 0, len);
                }

                zipOutputStream.closeEntry();
                fileInputStream.close();
            }

            zipOutputStream.close();
        } catch (IOException e) {
        }
    }
}
