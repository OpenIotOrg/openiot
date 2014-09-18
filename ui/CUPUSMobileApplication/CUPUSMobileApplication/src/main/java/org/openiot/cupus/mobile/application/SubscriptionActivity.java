package org.openiot.cupus.mobile.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;
import org.openiot.cupus.mobile.data.IntentObjectHolder;
import org.openiot.cupus.mobile.data.Parameters;

public class SubscriptionActivity extends Activity {

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        Button subscribeButton = (Button) findViewById(R.id.pressureSub);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText minPressureValue = (EditText) findViewById(R.id.pressure_value);
                String minPressureText = minPressureValue.getText().toString().trim();

                EditText minLatValue = (EditText) findViewById(R.id.minLat);
                String minLatText = minLatValue.getText().toString().trim();

                EditText maxLatValue = (EditText) findViewById(R.id.maxLat);
                String maxLatText = maxLatValue.getText().toString().trim();

                EditText minLongValue = (EditText) findViewById(R.id.minLong);
                String minLongText = minLongValue.getText().toString().trim();

                EditText maxLongValue = (EditText) findViewById(R.id.maxLong);
                String maxLongText = maxLongValue.getText().toString().trim();

                if ((minPressureText.length() == 0) &&
                        (minLatText.length() == 0 && maxLatText.length() == 0
                                && minLongText.length() == 0 && maxLongText.length() == 0)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SubscriptionActivity.this);
                    alertDialog.setTitle("Message");
                    alertDialog.setMessage("You must enter value!");
                    alertDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    TripletSubscription subscription = new TripletSubscription(-1, System.currentTimeMillis());
                    if (!(minPressureText.length() == 0)) {
                        int minPressure = Integer.valueOf(minPressureText);
                        subscription.addPredicate(new Triplet("pressure", minPressure, Operator.GREATER_OR_EQUAL));
                    }

                    if (!(minLatText.length() == 0 && maxLatText.length() == 0
                            && minLongText.length() == 0 && maxLongText.length() == 0)) {
                        double minLat = Double.valueOf(minLatText);
                        double maxLat = Double.valueOf(maxLatText);
                        double minLong = Double.valueOf(minLongText);
                        double maxLong = Double.valueOf(maxLongText);

                        subscription.addPredicate(new Triplet("latitude", minLat, Operator.GREATER_OR_EQUAL));
                        subscription.addPredicate(new Triplet("latitude", maxLat, Operator.LESS_OR_EQUAL));
                        subscription.addPredicate(new Triplet("longitude", minLong, Operator.GREATER_OR_EQUAL));
                        subscription.addPredicate(new Triplet("longitude", maxLong, Operator.LESS_OR_EQUAL));
                    }

                    IntentObjectHolder.setSubscription(subscription);

                    Intent intent = new Intent(Parameters.SUBSCRIPTION);

                    localBroadcastManager.sendBroadcast(intent);
                }

            }
        });

        Button unsubscribeButton = (Button) findViewById(R.id.unsubscribe);
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Parameters.CANCEL_SUBSCRIPTION);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

}
