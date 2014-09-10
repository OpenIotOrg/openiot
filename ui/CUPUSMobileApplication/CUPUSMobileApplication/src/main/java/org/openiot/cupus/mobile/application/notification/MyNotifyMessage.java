package org.openiot.cupus.mobile.application.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


import org.openiot.cupus.mobile.application.R;

public class MyNotifyMessage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_message);

        onNewIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("NotificationMessage"))
            {
                //setContentView(R);
                // extract the extra-data in the Notification
                String msg = extras.getString("NotificationMessage");
                TextView txtView = (TextView) findViewById(R.id.notificationText);
                txtView.setText(msg);
            }
        }
    }
}
