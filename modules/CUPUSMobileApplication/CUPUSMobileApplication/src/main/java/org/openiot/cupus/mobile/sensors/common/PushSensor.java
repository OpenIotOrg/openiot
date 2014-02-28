package org.openiot.cupus.mobile.sensors.common;

/**
 * Created by kpripuzic on 1/14/14.
 */

public abstract class PushSensor {
	public abstract void pushReading(int readingType, SensorReading reading);
}
