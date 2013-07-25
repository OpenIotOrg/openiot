package org.openiot.gsn.http.restapi;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class RestServlet extends HttpServlet {

    private static transient Logger logger = Logger.getLogger(RestServlet.class);

    private static final int REQUEST_UNKNOWN = -1;
    private static final int REQUEST_GET_ALL_SENSORS = 0;
    private static final int REQUEST_GET_MEASUREMENTS_FOR_SENSOR = 1;
    private static final int REQUEST_GET_MEASUREMENTS_FOR_SENSOR_FIELD = 2;
    private static final int REQUEST_GET_GEO_DATA_FOR_SENSOR = 3;
    private static final int REQUEST_GET_PREVIEW_MEASUREMENTS_FOR_SENSOR_FIELD = 4;
    private static final int REQUEST_GET_GRID = 5;


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GetRequestHandler getRequestHandler = new GetRequestHandler();

        //response.getWriter().write(debugRequest(request, response));
        RestResponse restResponse;

        String sensor = null;
        String field = null;
        String str_from = null;
        String str_to = null;
        String str_size = null;
        String str_date = null;

        switch (determineRequest(request.getRequestURI())) {
            case REQUEST_GET_ALL_SENSORS:
                restResponse = getRequestHandler.getSensors();
                break;
            case REQUEST_GET_MEASUREMENTS_FOR_SENSOR_FIELD:
                sensor = parseURI(request.getRequestURI())[3];
                field = parseURI(request.getRequestURI())[4];
                str_from = request.getParameter("from");
                str_to = request.getParameter("to");
                restResponse = getRequestHandler.getMeasurementsForSensorField(sensor, field, str_from, str_to);
                break;
            case REQUEST_GET_PREVIEW_MEASUREMENTS_FOR_SENSOR_FIELD:
                sensor = parseURI(request.getRequestURI())[3];
                field = parseURI(request.getRequestURI())[4];
                str_from = request.getParameter("from");
                str_to = request.getParameter("to");
                str_size = request.getParameter("size");
                restResponse = getRequestHandler.getPreviewMeasurementsForSensorField(sensor, field, str_from, str_to, str_size);
                break;
            case REQUEST_GET_GRID:
                sensor = parseURI(request.getRequestURI())[3];
                str_date = request.getParameter("date");
                restResponse = getRequestHandler.getGridData(sensor, str_date);
                break;
            default:
                restResponse = RestResponse.CreateErrorResponse(RestResponse.HTTP_STATUS_BAD_REQUEST, "Cannot interpret request.");
                break;
        }
        response.setContentType(restResponse.getType());
        response.setStatus(restResponse.getHttpStatus());
        response.getWriter().write(restResponse.getResponse());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("REST POST" + "\n" + request.getRequestURI());
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("REST PUT" + "\n" + request.getRequestURI());
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("REST DELETE" + "\n" + request.getRequestURI());
    }

    public String formatURI(String URI) {
        StringBuilder sb = new StringBuilder();
        String[] parsedURI = parseURI(URI);
        for (int i = 0; i < parsedURI.length; i++) {
            for (int j = 1; j <= i; j++)
                sb.append("..");
            sb.append("'").append(parsedURI[i]).append("'").append("\n");
        }
        return sb.toString();
    }

    public String[] parseURI(String URI) {
        return URI.split("/");
    }

    public int determineRequest(String URI) {
        String[] parsedURI = parseURI(URI);
        logger.warn(parsedURI.length);
        logger.warn(parsedURI[1]);
        if (parsedURI.length == 5 && parsedURI[2].equalsIgnoreCase("preview"))
            return REQUEST_GET_PREVIEW_MEASUREMENTS_FOR_SENSOR_FIELD;
        if (parsedURI.length == 3 && parsedURI[2].equalsIgnoreCase("sensors"))
            return REQUEST_GET_ALL_SENSORS;
        if (parsedURI.length == 5 && parsedURI[2].equalsIgnoreCase("sensors"))
            return REQUEST_GET_MEASUREMENTS_FOR_SENSOR_FIELD;
        if (parsedURI.length == 4 && parsedURI[2].equalsIgnoreCase("grid"))
            return REQUEST_GET_GRID;

        return REQUEST_UNKNOWN;
    }

    public String debugRequest(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("REST GET"
                + "\n"
                + "URI : "
                + formatURI(request.getRequestURI())
                + "\n");

        List<String> requestParameterNames = Collections.list((Enumeration<String>) request.getParameterNames());

        for (String parameterName : requestParameterNames) {
            sb.append(parameterName + " : ");
            for (int i = 0; i < request.getParameterValues(parameterName).length; i++)
                sb.append(request.getParameterValues(parameterName)[i] + "\n");
        }
        return sb.toString();
    }

}
