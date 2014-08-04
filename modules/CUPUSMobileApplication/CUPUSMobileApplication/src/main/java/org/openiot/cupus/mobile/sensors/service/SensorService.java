package org.openiot.cupus.mobile.sensors.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;


public class SensorService extends Service {

    private final IBinder binder = new SSBinder();
    private SensorManager sensorManager;

    public class SSBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        sensorManager = new SensorManager(localBroadcastManager);

        super.onCreate();
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

}
