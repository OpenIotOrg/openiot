package org.openiot.cupus.mobile.entity.mobilebroker;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.mobile.application.MobileBrokerActivity;
import org.openiot.cupus.mobile.application.notification.MyNotificationListener;
import org.openiot.cupus.mobile.data.IntentObjectHolder;
import org.openiot.cupus.mobile.data.Parameters;
import org.openiot.cupus.mobile.sensors.common.ReadingDefinition;
import org.openiot.cupus.mobile.sensors.common.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MobileBrokerService extends Service {

    private BrokerServiceBroadcastReceiver broadcastReceiver;
    private AbstractMobileBroker mobileBroker;
    private LocalBroadcastManager localBroadcastManager;

    private List<ReadingDefinition> availableSensors = new ArrayList<ReadingDefinition>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastReceiver = new BrokerServiceBroadcastReceiver();

        IntentFilter filterPublication = new IntentFilter(Parameters.PUBLICATION);
        IntentFilter filterAnnouncement = new IntentFilter(Parameters.ANNOUNCEMENT);
        IntentFilter filterMultiPublication = new IntentFilter(Parameters.MULTI_PUBLICATION);
        IntentFilter filterMultiAnnouncement = new IntentFilter(Parameters.MULTI_ANNOUNCEMENT);
        IntentFilter filterSubscription = new IntentFilter(Parameters.SUBSCRIPTION);
        IntentFilter filterCancelSubscription = new IntentFilter(Parameters.CANCEL_SUBSCRIPTION);
        IntentFilter filterTerminate = new IntentFilter(Parameters.TERMINATE);
        IntentFilter filterMultiTerminate = new IntentFilter(Parameters.MULTI_TERMINATE);

        localBroadcastManager.registerReceiver(broadcastReceiver, filterPublication);
        localBroadcastManager.registerReceiver(broadcastReceiver, filterAnnouncement);
        localBroadcastManager.registerReceiver(broadcastReceiver, filterMultiPublication);
        localBroadcastManager.registerReceiver(broadcastReceiver, filterMultiAnnouncement);
        localBroadcastManager.registerReceiver(broadcastReceiver, filterSubscription);
        localBroadcastManager.registerReceiver(broadcastReceiver, filterCancelSubscription);
        localBroadcastManager.registerReceiver(broadcastReceiver, filterTerminate);
        localBroadcastManager.registerReceiver(broadcastReceiver, filterMultiTerminate);

        super.onCreate();
    }

    private void connect() {
        mobileBroker.connect();
    }

    private void disconnect() {
        mobileBroker.disconnectFromBroker();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        String myName = extras.getString("mobileBrokerName");
        String myBrokerIP = extras.getString("brokerIP");
        int myBrokerPort = extras.getInt("brokerPort");
        String brokerType = extras.getString("brokerType");

        if(brokerType.equals("TCP")){
            mobileBroker = new TCPMobileBroker(myName, myBrokerIP, myBrokerPort, this.getApplicationContext());
        }else if(brokerType.equals("GCM")){
            mobileBroker = new GCMMobileBroker(myName,myBrokerIP,myBrokerPort,this.getApplicationContext(), MobileBrokerActivity.activity);
        }else {
            throw new UnsupportedOperationException("That broker type don't exist");
        }


        connect();

        final Context context = this;

        //set notification listener
        mobileBroker.setNotificationListener(new MyNotificationListener(context));

        //create announcement listener and set it
        mobileBroker.setAnnouncementListener(new AnnouncementListener() {
            @Override
            public void announcement(Set<String> subscriptionAttributes, boolean unsubscribe) {
                // send message to sensor listener to start/stop sensor
                Intent intent = new Intent(Parameters.START_STOP_SENSOR);
                intent.putStringArrayListExtra("subscriptions", new ArrayList<String>(subscriptionAttributes));
                localBroadcastManager.sendBroadcast(intent);

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    private class BrokerServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Parameters.PUBLICATION)) {
                // send publication to mobile broker
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String readingDefinition = extras.getString("readingDefinition");
                    int mapKey = extras.getInt("mapKey");
                    SensorReading<?> reading = IntentObjectHolder.intentObjectMap.get(mapKey);
                    Object value = reading.getValue();
                    HashtablePublication publication = new HashtablePublication(-1, System.currentTimeMillis());
                    publication.setProperty(readingDefinition, value);
                    mobileBroker.publish(publication);
                    IntentObjectHolder.intentObjectMap.remove(mapKey);
                }


            }
            else if (intent.getAction().equals(Parameters.ANNOUNCEMENT)) {
                // send announcement to mobile broker
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int tripletAnnouncementKey = extras.getInt("tripletAnnouncement");
                    String readingDefinition = extras.getString("readingDefinition");
                    TripletAnnouncement tripletAnnouncement = IntentObjectHolder.readingDefinitionMap.get(tripletAnnouncementKey);
                    mobileBroker.announce(tripletAnnouncement);
                    availableSensors.add(new ReadingDefinition(readingDefinition));
                }

            }
            else if (intent.getAction().equals(Parameters.MULTI_PUBLICATION)) {
                // send multi publication to mobile broker
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int numOfReadings = extras.getInt("successfulReadings");
                    HashtablePublication publication = new HashtablePublication(-1, System.currentTimeMillis());
                    for (int i = 0; i < numOfReadings; i++) {
                        String readingDefinition = extras.getString("readingDefinition" + i);
                        int mapKey = extras.getInt("mapKey" + i);
                        SensorReading<?> reading = IntentObjectHolder.intentObjectMap.get(mapKey);
                        if (reading != null) {
                            Object value = reading.getValue();
                            publication.setProperty(readingDefinition, value);
                        }
                        IntentObjectHolder.intentObjectMap.remove(mapKey);
                    }

                    mobileBroker.publish(publication);
                }
            }
            else if (intent.getAction().equals(Parameters.MULTI_ANNOUNCEMENT)) {
                // send multi announcement to mobile broker
                List<String> readingDefinitions = intent.getStringArrayListExtra("readingDefinitions");
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int index = 0;
                    for (String readingDefinition : readingDefinitions) {
                        int tripletAnnouncementKey = extras.getInt("tripletAnnouncement" + index);
                        TripletAnnouncement tripletAnnouncement = IntentObjectHolder.readingDefinitionMap.get(tripletAnnouncementKey);
                        mobileBroker.announce(tripletAnnouncement);
                        availableSensors.add(new ReadingDefinition(readingDefinition));
                        index++;
                    }

                }
            }
            else if (intent.getAction().equals(Parameters.SUBSCRIPTION)) {
                // send subscription to mobile broker
                mobileBroker.subscribe(IntentObjectHolder.getSubscription());
            }
            else if (intent.getAction().equals(Parameters.CANCEL_SUBSCRIPTION)) {
                // cancel subscription
                mobileBroker.unsubscribe(IntentObjectHolder.getSubscription());
                IntentObjectHolder.setSubscription(null);
            }
            else if (intent.getAction().equals(Parameters.TERMINATE)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int tripletAnnouncementKey = extras.getInt("tripletAnnouncement");
                    String readingDefinition = extras.getString("readingDefinition");
                    mobileBroker.revokeAnnouncement(IntentObjectHolder.readingDefinitionMap.get(tripletAnnouncementKey));
                    IntentObjectHolder.intentObjectMap.remove(tripletAnnouncementKey);
                    availableSensors.remove(new ReadingDefinition(readingDefinition));
                }
            }
            else if (intent.getAction().equals(Parameters.MULTI_TERMINATE)) {
                List<String> readingDefinitions = intent.getStringArrayListExtra("readingDefinitions");
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int index = 0;
                    for (String readingDefinition : readingDefinitions) {
                        int tripletAnnouncementKey = extras.getInt("tripletAnnouncement" + index);
                        mobileBroker.revokeAnnouncement(IntentObjectHolder.readingDefinitionMap.get(tripletAnnouncementKey));
                        IntentObjectHolder.intentObjectMap.remove(tripletAnnouncementKey);
                        availableSensors.add(new ReadingDefinition(readingDefinition));
                        availableSensors.remove(new ReadingDefinition(readingDefinition));
                        index++;
                    }

                }
            }
        }
    }
}
