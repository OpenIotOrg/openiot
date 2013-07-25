package org.openiot.gsn.vsensor;

import org.apache.log4j.Logger;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;

import java.util.TreeMap;
import java.text.NumberFormat;

public class SensorScope2VS extends AbstractVirtualSensor {

    private static final transient Logger logger = Logger.getLogger(SensorScope2VS.class);

    private Double[] buffer;

    private int length = -1;
    private NumberFormat nf = NumberFormat.getInstance();

    private boolean allowNulls = false;

    public boolean initialize() {
        VSensorConfig vsensor = getVirtualSensorConfiguration();
        TreeMap<String, String> params = vsensor.getMainClassInitialParams();

        String allow_nulls_str = params.get("allow-nulls");

        if (allow_nulls_str != null)
            if (allow_nulls_str.trim().equalsIgnoreCase("true"))
                allowNulls = true;


        logger.warn("Allow nulls => " + allowNulls);

        length = vsensor.getOutputStructure().length;

        buffer = new Double[length];

        return true;
    }

    public void dataAvailable(String inputStreamName, StreamElement data) {


        logger.debug("Data => " + data.toString());

        // verify if all tuples are null, avoids duplicating data using buffer
        boolean nullpacket = true;
        for (int i = 0; i < length; i++) {
            if ((Double) data.getData()[i] != null)
                nullpacket = false;
        }
        if (nullpacket) {
            logger.debug("Completely empty packet (all nulls). Discarded" + "\nData: " + data);
            return;
        }

        // if nulls are allowed
        if (allowNulls) {
            dataProduced(data);
            if (logger.isDebugEnabled())
                logger.debug("Data received under the name: " + inputStreamName + "\nData: " + data);
            return;
        }

        // if nulls are not allowed, using buffer

        for (int i = 0; i < length; i++) {
            Double d = (Double) data.getData()[i];
            if (d != null)
                buffer[i] = d;
        }

        /*
        * check if buffer contains any null values
        * */

        boolean publish = true;

        for (int i = 0; i < length; i++) {
            data.setData(i, buffer[i]);
            if (buffer[i] == null) publish = false;
        }

        //logger.debug("Pub => " + data.toString());

        if (publish) {
            dataProduced(data);
            if (logger.isDebugEnabled())
                logger.debug("Data received under the name: " + inputStreamName + "\nData: " + data);
        } else {
            logger.debug("null values, not published (" + this.getVirtualSensorConfiguration().getName() + ")");
        }
    }

    public void dispose() {

    }

}
