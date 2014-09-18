package org.openiot.cupus.mobile.sensors.common;

/**
 * Created by kpripuzic on 1/14/14.
 */
public class SensorReading<T> {
    private T value;

    public SensorReading(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorReading reading = (SensorReading) o;

        if (value != null ? !value.equals(reading.value) : reading.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
