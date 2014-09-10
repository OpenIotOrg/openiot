package org.openiot.cupus.mobile.sensors.implementations;

/**
 * Created by kpripuzic on 1/14/14.
 */

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;

import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.mobile.sensors.common.PullMultiSensor;
import org.openiot.cupus.mobile.sensors.common.ReadingDefinition;
import org.openiot.cupus.mobile.sensors.common.SensorReading;

import java.util.ArrayList;
import java.util.List;

public class LocationPullSensor extends PullMultiSensor {

    private Location currentLocation;

    private GPSListener gpsListener;
    private LocationManager locationManager;
    private long locationUpdateRate;

    private TelephonyManager telephonyManager;

    public LocationPullSensor(LocationManager locationManager, long locationUpdateRate, TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
        this.locationManager = locationManager;
        this.locationUpdateRate = locationUpdateRate;
        this.readingDefinitions = new ArrayList<ReadingDefinition>();
        this.tripletAnnouncements = new ArrayList<TripletAnnouncement>();
    }

    @Override
    public boolean initialize() {

        //GPS initialization
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationUpdateRate, 0, gpsListener = new GPSListener());

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                currentLocation = location;
            }

            readingDefinitions.add(new ReadingDefinition("latitude"));
            TripletAnnouncement tripletAnnouncementLat = new TripletAnnouncement(-1, System.currentTimeMillis());
            tripletAnnouncementLat.addNumericalPredicate("latitude");
            tripletAnnouncements.add(tripletAnnouncementLat);

            readingDefinitions.add(new ReadingDefinition("longitude"));
            TripletAnnouncement tripletAnnouncementLong = new TripletAnnouncement(-1, System.currentTimeMillis());
            tripletAnnouncementLong.addNumericalPredicate("longitude");
            tripletAnnouncements.add(tripletAnnouncementLong);
        }

        //CellId initialization
        if (telephonyManager != null) {
            readingDefinitions.add(new ReadingDefinition("cellid"));
            TripletAnnouncement tripletAnnouncementCellId = new TripletAnnouncement(-1, System.currentTimeMillis());
            tripletAnnouncementCellId.addTextualPdredicate("cellid");
            tripletAnnouncements.add(tripletAnnouncementCellId);
        }


        if (!readingDefinitions.isEmpty()) {
            super.setInitialized(true);
            return true;
        }

        //initialization failed
        super.setInitialized(false);
        return false;
    }

    private String getCellIds() {
        CellLocation cellLocation = telephonyManager.getCellLocation();
        return cellLocation.toString();
    }

    @Override
    public boolean terminate() {
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && gpsListener != null) {
            //stop listening location changes
            locationManager.removeUpdates(gpsListener);
        }
        return true;
    }

    @Override
    public int getId() {
        return hashCode();
    }

    public final SensorReading pullReading(String readingDefinition) {
        if (readingDefinition.equals("latitude") && currentLocation != null)
            return new SensorReading(currentLocation.getLatitude());
        else if (readingDefinition.equals("longitude") && currentLocation != null)
            return new SensorReading(currentLocation.getLongitude());
        else if (readingDefinition.equals("cellid") && telephonyManager != null) {
            return new SensorReading<String>(getCellIds());
        } else
            return null;
    }

    public final List<SensorReading> pullReadings() {
        List<SensorReading> readings = new ArrayList<SensorReading>();

        for (ReadingDefinition readingDefinition : readingDefinitions) {
            readings.add(pullReading(readingDefinition.getName()));
        }

        return readings;
    }

    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            currentLocation = location;
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}
