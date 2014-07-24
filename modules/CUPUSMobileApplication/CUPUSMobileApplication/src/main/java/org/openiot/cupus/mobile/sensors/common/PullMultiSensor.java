package org.openiot.cupus.mobile.sensors.common;

/**
 * Created by kpripuzic on 1/14/14.
 */

import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.common.Triplet;

import java.util.List;

public abstract class PullMultiSensor extends AbstractSensor {

    protected List<ReadingDefinition> readingDefinitions;
    protected List<TripletAnnouncement> tripletAnnouncements;

	public abstract SensorReading pullReading(String readingDefinition);

    public abstract List<SensorReading> pullReadings();

    public List<ReadingDefinition> getReadingDefinitions() {
        return readingDefinitions;
    }

    public List<TripletAnnouncement> getTripletAnnouncements() {
        return tripletAnnouncements;
    }
}
