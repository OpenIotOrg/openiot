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

package org.openiot.ws;

import java.io.File;
import java.util.List;
import java.util.Set;
import javax.jws.Oneway;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.openiot.qos.QoSManager;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.TripletSubscription;

/**
 *
 * @author Martina
 */
@Startup
@WebService(serviceName = "QoSManagerWS")
public class QoSManagerWS {

    QoSManager manager;
    private static final String PROPERTIES_FILE = "openiot.properties";
    String jbosServerConfigDir;
    String openIotConfigFile;

    public QoSManagerWS() {
        this.jbosServerConfigDir = System.getProperty("jboss.server.config.dir");
        this.openIotConfigFile = jbosServerConfigDir + File.separator + PROPERTIES_FILE;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "initialize")
    @Oneway
    @PostConstruct
    public void initialize() {
        this.manager = new QoSManager(new File(openIotConfigFile));
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAllSensors")
    public Set<String> getAllSensors() {
        //TODO write your implementation code here:
        return this.manager.getAllAvailableSensors();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getLatLongFromArea")
    public List<Float> getLatLongFromArea(@WebParam(name = "area") String area) {
        //TODO write your implementation code here:
        return this.manager.getLatLongFromArea(area);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAreaFromLatLng")
    public String getAreaFromLatLng(@WebParam(name = "lat") double lat, @WebParam(name = "lng") double lng, @WebParam(name = "accuracy") int accuracy) {
        //TODO write your implementation code here:
        return this.manager.getAreaFromLatLong(lat, lng, accuracy);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getActiveSensorsInArea")
    public Set<String> getActiveSensorsInArea(@WebParam(name = "area") String area) {
        //TODO write your implementation code here:
        return this.manager.getActiveSensorsInArea(area);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAllSensorsInArea")
    public Set<String> getAllSensorsInArea(@WebParam(name = "area") String area) {
        //TODO write your implementation code here:
        return this.manager.getAllSensorsInArea(area);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "setNumberOfActiveSensors")
    @Oneway
    public void setNumberOfActiveSensors(@WebParam(name = "maxNumberOfActiveSensors") int maxNumberOfActiveSensors) {
        this.manager.setNumberOfActiveSensors(maxNumberOfActiveSensors);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "setBatteryLevels")
    @Oneway
    public void setBatteryLevels(@WebParam(name = "highBatteryLevel") double highBatteryLevel, @WebParam(name = "lowBatteryLevel") double lowBatteryLevel) {
        this.manager.setBatteryLevels(highBatteryLevel, lowBatteryLevel);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "shutdown")
    @Oneway
    @PreDestroy
    public void shutdown() {
        this.manager.shutdown();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "average")
    public String average(@WebParam(name = "area") String area) {
        return this.manager.getAverageSensorReadingsInArea(area).toString();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "defineNewSubscriptionInArea")
    public void defineNewSubscriptionInArea(@WebParam(name = "area") String area) {
        this.manager.defineNewSubscriptionInArea(area);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAverageSensorReadingsInArea")
    public HashtablePublication getAverageSensorReadingsInArea(@WebParam(name = "area") String area) {
        return this.manager.getAverageSensorReadingsInArea(area);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAllSubscriptionsInArea")
    public List<TripletSubscription> getAllSubscriptionsInArea(@WebParam(name = "area") String area) {
       return this.manager.getAllSubscriptionsInArea(area);
    }

   

}

