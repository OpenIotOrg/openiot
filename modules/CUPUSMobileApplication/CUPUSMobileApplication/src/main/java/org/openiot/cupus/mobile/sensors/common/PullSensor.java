package org.openiot.cupus.mobile.sensors.common;

import org.openiot.cupus.artefact.TripletAnnouncement;

/**
 * Created by kpripuzic on 1/16/14.
 */

public abstract class PullSensor extends AbstractSensor{
    protected ReadingDefinition readingDefinition;
    protected TripletAnnouncement tripletAnnouncement;

    public abstract SensorReading pullReading();
    public abstract boolean isSupported();

    public ReadingDefinition getReadingDefinition() {
        return readingDefinition;
    }

    public TripletAnnouncement getTripletAnnouncement() {
        return tripletAnnouncement;
    }
}
