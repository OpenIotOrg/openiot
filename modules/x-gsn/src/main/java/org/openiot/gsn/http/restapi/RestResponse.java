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
