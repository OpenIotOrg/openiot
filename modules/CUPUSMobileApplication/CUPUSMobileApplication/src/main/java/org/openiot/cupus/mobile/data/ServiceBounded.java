package org.openiot.cupus.mobile.data;



/**
 * Created by Kristijan on 07.02.14..
 */
public class ServiceBounded {
    private static boolean sensorServiceBounded = false;
    private static boolean multiSensorServiceBounded = false;

    public static boolean isSensorServiceBounded() {
        return sensorServiceBounded;
    }

    public static void setSensorServiceBounded(boolean sensorServiceBoundedValue) {
        sensorServiceBounded = sensorServiceBoundedValue;
    }

    public static boolean isMultiSensorServiceBounded() {
        return multiSensorServiceBounded;
    }

    public static void setMultiSensorServiceBounded(boolean multiSensorServiceBounded) {
        ServiceBounded.multiSensorServiceBounded = multiSensorServiceBounded;
    }
}
