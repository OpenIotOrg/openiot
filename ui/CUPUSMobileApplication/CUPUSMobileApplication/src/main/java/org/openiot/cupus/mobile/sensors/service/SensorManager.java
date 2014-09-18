package org.openiot.cupus.mobile.sensors.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.mobile.data.IntentObjectHolder;
import org.openiot.cupus.mobile.data.Parameters;
import org.openiot.cupus.mobile.sensors.common.PullSensor;
import org.openiot.cupus.mobile.sensors.common.SensorReading;

import java.util.List;

/**
 * Created by Kristijan on 22.01.14..
 */
public class SensorManager extends AbstractSensorManager implements Runnable{

    private PullSensor pullSensor;

    private SensorServiceBroadcastReceiver sensorServiceBroadcastReceiver;

    public SensorManager(LocalBroadcastManager localBroadcastManager) {
        super(localBroadcastManager);
        this.sensorEventListener = new SensorEventListenerImplement();

        IntentFilter sensorControl = new IntentFilter(Parameters.START_STOP_SENSOR);
        sensorServiceBroadcastReceiver = new SensorServiceBroadcastReceiver();
        localBroadcastManager.registerReceiver(sensorServiceBroadcastReceiver, sensorControl);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sensorEventListener.sendReading();
        }
    }

    @Override
    public void startIt() {
        if (!running) {
            Thread thread = new Thread(this);
            thread.start();
            running = true;
        }
    }


    @Override
    public void terminate() {
        stopIt();
        pullSensor.terminate();

        Intent intent = new Intent(Parameters.TERMINATE);
        intent.putExtra("readingDefinition", pullSensor.getReadingDefinition().getName());
        intent.putExtra("tripletAnnouncement", pullSensor.getTripletAnnouncement().hashCode());
        localBroadcastManager.sendBroadcast(intent);
    }


    private class SensorEventListenerImplement implements SensorEventListener {

        @Override
            public void sendReading() {
            Intent intent = new Intent(Parameters.PUBLICATION);
            // sending values ​​through the map
            SensorReading reading = pullSensor.pullReading();
            IntentObjectHolder.intentObjectMap.put(reading.hashCode(), reading);
            intent.putExtra("mapKey", reading.hashCode());
            intent.putExtra("readingDefinition", pullSensor.getReadingDefinition().getName());
            localBroadcastManager.sendBroadcast(intent);
        }

        @Override
        public void newSensor() {
            Intent intent = new Intent(Parameters.ANNOUNCEMENT);
            TripletAnnouncement tripletAnnouncement = pullSensor.getTripletAnnouncement();
            IntentObjectHolder.readingDefinitionMap.put(tripletAnnouncement.hashCode(), tripletAnnouncement);
            intent.putExtra("readingDefinition", pullSensor.getReadingDefinition().getName());
            intent.putExtra("tripletAnnouncement", tripletAnnouncement.hashCode());
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    public void setPullSensor(PullSensor pullSensor) {
        this.pullSensor = pullSensor;
    }

    public PullSensor getPullSensor() {
        return pullSensor;
    }

    private class SensorServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Parameters.START_STOP_SENSOR)) {
                List<String> subscriptions = intent.getStringArrayListExtra("subscriptions");
                if (subscriptions.contains(getPullSensor().getReadingDefinition().getName())) {
                    if (!isRunning()) {
                        startIt();
                    }
                }
                else {
                    if (isRunning()) {
                        stopIt();
                    }
                }
            }
        }
    }
}
