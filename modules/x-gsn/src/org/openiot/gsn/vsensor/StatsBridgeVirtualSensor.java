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
