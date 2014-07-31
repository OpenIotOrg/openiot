package org.openiot.cupus.mobile.application;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.openiot.cupus.mobile.data.ServiceBounded;
import org.openiot.cupus.mobile.sensors.implementations.LocationPullSensor;
import org.openiot.cupus.mobile.sensors.implementations.PressurePullSensor;
import org.openiot.cupus.mobile.sensors.service.MultiSensorService;
import org.openiot.cupus.mobile.sensors.service.SensorService;

public class SensorServiceActivity extends Activity {

    PressurePullSensor barometer;
    LocationPullSensor locationSensor;

    private SensorService serviceReference;
    private MultiSensorService serviceReference2;
    private long period;

    boolean mBound = false;
    boolean mBound2 = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            serviceReference = ((SensorService.SSBinder) service)
                    .getService();
            if (!mBound) {
                serviceReference.getSensorManager().setPullSensor(barometer);
                serviceReference.getSensorManager().setPeriod(period);
                serviceReference.getSensorManager().getSensorEventListener().newSensor();
                mBound = true;

                ServiceBounded.setSensorServiceBounded(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceReference = null;
            mBound = false;
        }
    };

    private ServiceConnection serviceConnection2 = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            serviceReference2 = ((MultiSensorService.MSSBinder) service)
                    .getService();
            if (!mBound2) {
                serviceReference2.getSensorManager().setPullMultiSensor(locationSensor);
                serviceReference2.getSensorManager().setPeriod(period);
                serviceReference2.getSensorManager().getSensorEventListener().newSensor();
                mBound2 = true;

                ServiceBounded.setMultiSensorServiceBounded(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceReference2 = null;
            mBound2 = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_service);

        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        barometer = new PressurePullSensor(sensorManager, SensorManager.SENSOR_DELAY_FASTEST);

        Button startServiceButton = (Button) findViewById(R.id.startSensorService);
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barometer.initialize();

                EditText sensorPeriod = (EditText) findViewById(R.id.sensorPeriod);
                // sec to msec
                period = Long.valueOf(sensorPeriod.getText().toString()) * 1000;

                Intent intent = new Intent(SensorServiceActivity.this, SensorService.class);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

                startService(intent);

                findViewById(R.id.stopSensorService).setEnabled(true);
                findViewById(R.id.startSensorService).setEnabled(false);
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        locationSensor = new LocationPullSensor(locationManager, 1000l, telephonyManager);

        Button startService2 = (Button) findViewById(R.id.startMultiSensorService);
        startService2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationSensor.initialize();

                EditText sensorPeriod = (EditText) findViewById(R.id.multiSensorPeriod);
                // sec to msec
                period = Long.valueOf(sensorPeriod.getText().toString()) * 1000;

                Intent intent = new Intent(SensorServiceActivity.this, MultiSensorService.class);
                startService(intent);
                bindService(intent, serviceConnection2, Context.BIND_AUTO_CREATE);

                findViewById(R.id.stopMultiSensorService).setEnabled(true);
                findViewById(R.id.startMultiSensorService).setEnabled(false);
            }
        });

        Button stopService = (Button) findViewById(R.id.stopSensorService);
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceReference.getSensorManager().terminate();
                ServiceBounded.setSensorServiceBounded(false);

                findViewById(R.id.stopSensorService).setEnabled(false);
                findViewById(R.id.startSensorService).setEnabled(true);
            }
        });

        Button stopService2 = (Button) findViewById(R.id.stopMultiSensorService);
        stopService2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceReference2.getSensorManager().terminate();
                ServiceBounded.setMultiSensorServiceBounded(false);

                findViewById(R.id.stopMultiSensorService).setEnabled(false);
                findViewById(R.id.startMultiSensorService).setEnabled(true);
            }
        });

        if (ServiceBounded.isSensorServiceBounded()) {
            startServiceButton.setEnabled(false);

            Intent intent = new Intent(SensorServiceActivity.this, SensorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }
        else {
            stopService.setEnabled(false);
        }

        if (ServiceBounded.isMultiSensorServiceBounded()) {
            startService2.setEnabled(false);

            Intent intent = new Intent(SensorServiceActivity.this, MultiSensorService.class);
            bindService(intent, serviceConnection2, Context.BIND_AUTO_CREATE);
            mBound2 = true;
        }
        else {
            stopService2.setEnabled(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBound) {
            unbindService(serviceConnection);
        }

        if (mBound2) {
            unbindService(serviceConnection2);
        }

    }

}
