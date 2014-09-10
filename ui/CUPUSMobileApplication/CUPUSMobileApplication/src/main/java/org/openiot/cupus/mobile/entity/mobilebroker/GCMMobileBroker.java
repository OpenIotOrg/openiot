/**
 *    Copyright (c) 2011-2014, OpenIoT
 *
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

package org.openiot.cupus.mobile.entity.mobilebroker;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.MobileBrokerRegisterGCMMessage;
import org.openiot.cupus.message.external.NotifyMessage;
import org.openiot.cupus.message.external.NotifySubscriptionMessage;
import org.openiot.cupus.mobile.application.MobileBrokerActivity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

/**
 * @author Marko
 */
public class GCMMobileBroker extends MobileBrokerOutgoingTCP {

    private static GCMMobileBroker gcmMobileBroker;

    public static final String PROPERTY_REG_ID = "";// "registration_id";
    private static final String PROPERTY_APP_VERSION = "1";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    private String SENDER_ID = "362337850870";

    private Context context;
    private Activity activity;

    private String regID;

    private GoogleCloudMessaging gcm;

    public GCMMobileBroker(String myName, String myBrokerIP, int myBrokerPort, Context context, Activity activity) {
        super(myName, myBrokerIP, myBrokerPort, context);
        this.context = context;
        this.activity = activity;
        gcmMobileBroker = this;
    }

    /**
     * Constructor - subscriber can be created via configuration file or
     * directly
     */
    public GCMMobileBroker(File configFile, Context context, Activity activity) {
        super(configFile, context);
        this.context = context;
        this.activity = activity;
        gcmMobileBroker = this;
    }

    @Override
    protected void startIncomingConnection() {
        String registrationID = null;

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            registrationID = getRegistrationId(context);

            if (registrationID.isEmpty()) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    registrationID = gcm.register(SENDER_ID);
                    System.out.println("Device registered, registration ID=" + registrationID);
                } catch (IOException e) {

                }
                this.regID = registrationID;

            }
        } else {
            Log.i("CUPUS", "No valid Google Play Services APK found.");
        }

        //send the register message
        Message connectMessage = new MobileBrokerRegisterGCMMessage(myName, this.getId(), this.regID);
        this.sendMessageInBackGround(connectMessage);

        this.connected = true;
        log.writeToLog("Connected to GCM Broker " + registrationID);


        IntentFilter filter = new IntentFilter();
        filter.addAction("com.google.android.c2dm.intent.RECEIVE");
        filter.addCategory("org.openiot.cupus.mobile.application");

        GcmBroadcastReceiver receiver = new GcmBroadcastReceiver();
        if (activity instanceof MobileBrokerActivity) {

            activity.registerReceiver(receiver, filter);
            ((MobileBrokerActivity) activity).setBroadcastReceiver(receiver);
            ((MobileBrokerActivity) activity).setGcmReceiver(true);
        }
    }

    @Override
    protected void terminateIncomingConnectionInBackground() {
        try {
            gcm.unregister();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("CUPUS", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = this.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("CUPUS", "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return activity.getSharedPreferences(activity.getClass().getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("CUPUS", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Explicitly specify that GcmIntentService will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GcmIntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }
    }


    public static class GcmIntentService extends IntentService {
        public static final int NOTIFICATION_ID = 1;
        private NotificationManager mNotificationManager;
        NotificationCompat.Builder builder;
        GCMMobileBroker broker;

        public GcmIntentService() {
            super("GcmIntentService");
            this.broker = GCMMobileBroker.gcmMobileBroker;
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            // The getMessageType() intent parameter must be the intent you received
            // in your BroadcastReceiver.
            String messageType = gcm.getMessageType(intent);

            if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
                if (GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

                    //TODO za error msg
                    // sendNotification("Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                    //TODO za deleted msg
                    // sendNotification("Deleted messages on server: " + extras.toString());
                    // If it's a regular GCM message, do some work.
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                    String message = extras.getString("message", "");
                    System.out.println("Received msg: " + message);
                    broker.log.writeToLog("Received msg: " + message);
                    if (message.isEmpty()) {
                        System.out.println("Received empty msg");
                        broker.log.writeToLog("Received empty msg");
                        return;
                    }

                    Object objIn = null;

                    byte[] byteMsg = android.util.Base64.decode(message, android.util.Base64.DEFAULT);
                    ByteArrayInputStream bis = new ByteArrayInputStream(byteMsg);
                    ObjectInput in = null;
                    try {
                        try {
                            in = new ObjectInputStream(bis);
                            objIn = in.readObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } finally {
                        try {
                            bis.close();
                        } catch (IOException ex) {
                            // ignore close exception
                        }
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException ex) {
                            // ignore close exception
                        }
                    }

                    if (objIn instanceof NotifyMessage) {
                        NotifyMessage msg = (NotifyMessage) objIn;
                        broker.notify(msg.getPublication(), msg.isUnpublish());
                    } else if (objIn instanceof NotifySubscriptionMessage) {
                        NotifySubscriptionMessage msg = (NotifySubscriptionMessage) objIn;
                        broker.announcement(msg.getSubscription(), msg.isRevoke());
                    } else {
                        broker.log.writeToLog("Unkown request/response received from broker (type = "
                                + objIn.getClass().getName() + "). Ignoring...");
                    }

                }
            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}