package org.openiot.cupus.mobile.sensors.service;

/**
 * Created by Kristijan on 30.01.14..
 */
public interface SensorEventListener {
    void sendReading();
    void newSensor();
}
