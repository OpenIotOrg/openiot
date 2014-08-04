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

package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.StreamElement;

import org.apache.log4j.Logger;

import java.util.TreeMap;

public class StatsBridgeVirtualSensor extends AbstractVirtualSensor {

    private static final String PARAM_LOGGING_INTERVAL = "logging-interval";
    private boolean logging_timestamps = false;
    private long logging_interval;
    private long logging_counter = 0;
    private String vsname;

    private static final transient Logger logger = Logger.getLogger(BridgeVirtualSensor.class);

    public boolean initialize() {

        TreeMap<String, String> params = getVirtualSensorConfiguration().getMainClassInitialParams();

        String logging_interval_str = params.get(PARAM_LOGGING_INTERVAL);
        if (logging_interval_str != null) {
            logging_timestamps = true;
            try {
                logging_interval = Integer.parseInt(logging_interval_str.trim());
            } catch (NumberFormatException e) {
                logger.warn("Parameter \"" + PARAM_LOGGING_INTERVAL + "\" incorrect in Virtual Sensor file");
                logging_timestamps = false;
            }
        }

        vsname = getVirtualSensorConfiguration().getName();

        return true;
    }

    public void dataAvailable(String inputStreamName, StreamElement data) {


        if (logging_counter % logging_interval == 0) {
            logger.warn( vsname + " , " + logging_counter + " , " + System.currentTimeMillis());
        }

        logging_counter++;

        dataProduced(data);
        if (logger.isDebugEnabled()) logger.debug("Data received under the name: " + inputStreamName);
    }

    public void dispose() {

    }

}
