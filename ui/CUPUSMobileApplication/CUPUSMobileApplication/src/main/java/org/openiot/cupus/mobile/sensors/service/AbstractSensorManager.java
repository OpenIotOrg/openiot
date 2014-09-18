package org.openiot.cupus.mobile.sensors.service;

import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Kristijan on 12.02.14..
 */
public abstract class AbstractSensorManager {
    protected long period;
    protected boolean running = false;

    protected SensorEventListener sensorEventListener;
    protected LocalBroadcastManager localBroadcastManager;

    public AbstractSensorManager(LocalBroadcastManager localBroadcastManager) {
        this.localBroadcastManager = localBroadcastManager;
        this.period = 0L;
    }

    public abstract void startIt();

    public void stopIt() {
        running = false;
    }

    public abstract void terminate();

    public void setPeriod(long period) {
        this.period = period;
    }

    public boolean isRunning() {
        return running;
    }

    public SensorEventListener getSensorEventListener() {
        return sensorEventListener;
    }
}
