package org.openiot.gsn.http.restapi;

import org.json.simple.JSONObject;

public class RestResponse {

    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_BAD_REQUEST = 400;

    public static final String JSON_CONTENT_TYPE = "application/json";

    String Response;

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    String Type;     // json, xml, csv, image
    int httpStatus;

    public static RestResponse CreateErrorResponse(int httpStatus, String errorMessage) {
        RestResponse restResponse = new RestResponse();
        JSONObject jsonObject = new JSONObject();
        restResponse.setHttpStatus(httpStatus);
        restResponse.setType(JSON_CONTENT_TYPE);
        jsonObject.put("error", errorMessage);
        restResponse.setResponse(jsonObject.toJSONString());
        return restResponse;
    }

    @Override
    public String toString() {
        return "RestResponse{\n" +
                "Response='" + Response + '\'' +
                ",\n Type='" + Type + '\'' +
                ",\n httpStatus=" + httpStatus +
                "\n}";
    }
}
