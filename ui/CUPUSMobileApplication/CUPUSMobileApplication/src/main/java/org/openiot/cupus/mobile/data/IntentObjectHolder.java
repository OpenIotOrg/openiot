package org.openiot.cupus.mobile.data;

import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.mobile.sensors.common.SensorReading;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Kristijan on 22.01.14..
 */
public class IntentObjectHolder {
    public static final ConcurrentMap<Integer, SensorReading> intentObjectMap = new ConcurrentHashMap<Integer, SensorReading>();
    public static final Map<Integer, TripletAnnouncement> readingDefinitionMap = new HashMap<Integer, TripletAnnouncement>();

    private static TripletSubscription subscription;

    public static TripletSubscription getSubscription() {
        return subscription;
    }

    public static void setSubscription(TripletSubscription tripletSubscription) {
        subscription = tripletSubscription;
    }
}
