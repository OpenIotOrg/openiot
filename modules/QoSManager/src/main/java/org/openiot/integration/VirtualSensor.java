/*    Copyright (c) 2011-2014, OpenIoT
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

package org.openiot.integration;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Aleksandar
 */
public class VirtualSensor {
    
    String gsnAddress;
    
    List<String> sensorParameters;
    List<String> sensorTypes;
    List<String> lsmProperty;
    List<String> lsmUnit;
    String virtualSensorID;
    int virtualSensorPort;
    double latitude;
    double longitude;
    
    public VirtualSensor (String id, int port, double lat, double lng, List<String> param, List<String> paramTypes, List<String>lsmProp, List<String> lsmUnits, String gsn){
        this.gsnAddress = gsn;
        this.virtualSensorID = id;
        this.virtualSensorPort = port;
        this.latitude = lat;
        this.longitude = lng;
        this.sensorParameters = param;
        this.sensorTypes = paramTypes;
        this.lsmProperty = lsmProp;
        this.lsmUnit = lsmUnits;
      
    }

    public void createAndRegister () throws IOException, InterruptedException {
        
        HttpClient client = HttpClientBuilder.create().build();
        String name = "FER"+virtualSensorID;

        WriteXMLFile xmlFile = new WriteXMLFile(name, virtualSensorPort, sensorParameters, sensorTypes);
        String virtualSensor = xmlFile.createXML();

        String url = "http://"+gsnAddress+"/vs/vsensor/" + name + "/create";
        HttpPost request = new HttpPost(url);
        StringEntity input = new StringEntity(virtualSensor);
        input.setContentType("text/xml");
        request.setEntity(input);
        HttpResponse response = client.execute(request);
        StatusLine statusLine = response.getStatusLine();
        boolean result = statusLine.getStatusCode() == 200;

        EntityUtils.toString(response.getEntity());
        response.getEntity().getContent().close();
        
        WriteMetadataFile metaFile = new WriteMetadataFile(name, virtualSensorID, latitude, longitude, sensorParameters, lsmProperty, lsmUnit);
        String sensorInstance = metaFile.createMetadata();
        
        url = "http://"+gsnAddress+"/vs/vsensor/" + name + "/register";
        request = new HttpPost(url);
        input = new StringEntity(sensorInstance);

        input.setContentType("text/xml");
        request.setEntity(input);
        response = client.execute(request);
         statusLine = response.getStatusLine();
         result = statusLine.getStatusCode() == 200;
        EntityUtils.toString(response.getEntity());
        response.getEntity().getContent().close(); 
    }
        
        
    
}
