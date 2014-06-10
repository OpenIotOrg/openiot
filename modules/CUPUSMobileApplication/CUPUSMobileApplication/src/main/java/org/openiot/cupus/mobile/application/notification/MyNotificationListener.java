package org.openiot.cupus.mobile.application.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.entity.subscriber.NotificationListener;
import org.openiot.cupus.mobile.application.R;
import org.openiot.cupus.mobile.data.Parameters;

import java.util.UUID;

/**
 * Created by Kristijan on 27.02.14..
 */
public class MyNotificationListener implements NotificationListener {

    private Context context;

    public MyNotificationListener(Context context) {
        this.context = context;
    }

    @Override
    public void notify(UUID subscriberId, String subscriberName, Publication publication) {
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MyNotifyMessage.class);
        notificationIntent.putExtra("NotificationMessage", publication.toString());
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // This pending intent will open after notification click
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification not = new NotificationCompat.Builder(context)
                .setContentTitle("New notification")
                .setContentText(publication.toString())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .build();

        nManager.notify(Parameters.NOTIFY_ME_ID, not);
    }
}
