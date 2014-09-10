package org.openiot.cupus.mobile.sensors.implementations;

/**
 * Created by kpripuzic on 1/14/14.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.common.enums.Operator;
import org.openiot.cupus.mobile.sensors.common.PullSensor;
import org.openiot.cupus.mobile.sensors.common.ReadingDefinition;
import org.openiot.cupus.mobile.sensors.common.SensorReading;

public class PressurePullSensor extends PullSensor {

    private SensorReading<Float> currentPressure;
    private boolean supported;

    private BarometerListener barometerListener;
    private SensorManager sensorManager;
    private int rate;

    public PressurePullSensor(SensorManager sensorManager, int rate) {
        this.sensorManager = sensorManager;
        this.rate = rate;
        this.readingDefinition = readingDefinition;
    }

    @Override
    public boolean initialize() {
        if (sensorManager != null && !sensorManager.getSensorList(Sensor.TYPE_PRESSURE).isEmpty()) {
            Sensor barometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            sensorManager.registerListener(barometerListener = new BarometerListener(), barometer, rate);

            readingDefinition = new ReadingDefinition("pressure");
            tripletAnnouncement = new TripletAnnouncement(-1, System.currentTimeMillis());
            tripletAnnouncement.addNumericalPdredicate(readingDefinition.getName(), 0, Operator.GREATER_OR_EQUAL);

            //initialization finished
            super.setInitialized(true);
            return true;
        } else {
            //initialization failed
            super.setInitialized(false);
            return false;
        }
    }

    @Override
    public boolean terminate() {
        sensorManager.unregisterListener(barometerListener);
        return true;
    }

    @Override
    public int getId() {
        return hashCode();
    }

    public final SensorReading pullReading() {
        return currentPressure;
    }

    @Override
    public boolean isSupported() {
        return supported;
    }

    private class BarometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            currentPressure = new SensorReading<Float>(event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
