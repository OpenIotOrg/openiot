package org.openiot.cupus.mobile.sensors.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class MultiSensorService extends Service {

    private final IBinder binder = new MSSBinder();
    private MultiSensorManager sensorManager;

    public class MSSBinder extends Binder {
        public MultiSensorService getService() {
            return MultiSensorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        sensorManager = new MultiSensorManager(localBroadcastManager);

        super.onCreate();
    }

    public MultiSensorManager getSensorManager() {
        return sensorManager;
    }
}
