package org.openiot.cupus.mobile.sensors.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.mobile.data.IntentObjectHolder;
import org.openiot.cupus.mobile.data.Parameters;
import org.openiot.cupus.mobile.sensors.common.PullMultiSensor;
import org.openiot.cupus.mobile.sensors.common.ReadingDefinition;
import org.openiot.cupus.mobile.sensors.common.SensorReading;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristijan on 30.01.14..
 */
public class MultiSensorManager extends AbstractSensorManager implements Runnable{

    private PullMultiSensor pullMultiSensor;

    private MultiSensorServiceBroadcastReceiver sensorServiceBroadcastReceiver;

    public MultiSensorManager(LocalBroadcastManager localBroadcastManager) {
        super(localBroadcastManager);
        this.sensorEventListener = new MultiSensorEventListenerImplement();

        IntentFilter sensorControl = new IntentFilter(Parameters.START_STOP_SENSOR);
        sensorServiceBroadcastReceiver = new MultiSensorServiceBroadcastReceiver();
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
        pullMultiSensor.terminate();

        Intent intent = new Intent(Parameters.MULTI_TERMINATE);
        ArrayList<String> stringReadingDefinitions = new ArrayList<String>();
        List<ReadingDefinition> readingDefinitions = pullMultiSensor.getReadingDefinitions();
        int index = 0;
        for (TripletAnnouncement tripletAnnouncement : pullMultiSensor.getTripletAnnouncements()) {
            stringReadingDefinitions.add(readingDefinitions.get(index).getName());
            intent.putExtra("tripletAnnouncement" + index, tripletAnnouncement.hashCode());
            index++;
        }
        intent.putStringArrayListExtra("readingDefinitions", stringReadingDefinitions);
        localBroadcastManager.sendBroadcast(intent);
    }

    private class MultiSensorEventListenerImplement implements SensorEventListener {
        @Override
        public void sendReading() {
            Intent intent = new Intent(Parameters.MULTI_PUBLICATION);
            List<SensorReading> readings = pullMultiSensor.pullReadings();

            int index = 0;
            int numOfSuccReadings = 0;
            for (SensorReading reading : readings) {
                if (reading != null) {
                    String readingDefinition = pullMultiSensor.getReadingDefinitions().get(index).getName();
                    IntentObjectHolder.intentObjectMap.put(reading.hashCode(), reading);
                    intent.putExtra("mapKey" + numOfSuccReadings, reading.hashCode());
                    intent.putExtra("readingDefinition" + numOfSuccReadings, readingDefinition);
                    numOfSuccReadings++;
                }
                index++;
            }
            intent.putExtra("successfulReadings", numOfSuccReadings);

            localBroadcastManager.sendBroadcast(intent);
        }

        @Override
        public void newSensor() {
            Intent intent = new Intent(Parameters.MULTI_ANNOUNCEMENT);
            ArrayList<String> stringReadingDefinitions = new ArrayList<String>();
            List<ReadingDefinition> readingDefinitions = pullMultiSensor.getReadingDefinitions();
            int index = 0;
            for (TripletAnnouncement tripletAnnouncement : pullMultiSensor.getTripletAnnouncements()) {
                stringReadingDefinitions.add(readingDefinitions.get(index).getName());
                IntentObjectHolder.readingDefinitionMap.put(tripletAnnouncement.hashCode(), tripletAnnouncement);
                intent.putExtra("tripletAnnouncement" + index, tripletAnnouncement.hashCode());
                index++;
            }
            intent.putStringArrayListExtra("readingDefinitions", stringReadingDefinitions);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    public PullMultiSensor getPullMultiSensor() {
        return pullMultiSensor;
    }

    public void setPullMultiSensor(PullMultiSensor pullMultiSensor) {
        this.pullMultiSensor = pullMultiSensor;
    }

    private class MultiSensorServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Parameters.START_STOP_SENSOR)) {
                List<String> subscriptions = intent.getStringArrayListExtra("subscriptions");

                boolean contains = false;

                for (ReadingDefinition readingDefinition : getPullMultiSensor().getReadingDefinitions()) {
                    if (subscriptions.contains(readingDefinition.getName())) {
                        contains = true;
                    }
                }

                if (contains) {
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
