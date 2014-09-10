package org.openiot.cupus.mobile.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import org.openiot.cupus.mobile.entity.mobilebroker.MobileBrokerService;

public class MobileBrokerActivity extends Activity {

    private Intent serviceIntent;

    private String mobileBrokerName;
    private String brokerIpAddress;
    private int brokerPort;
    private String brokerType;

    private boolean brokerConected = false;
    private boolean gcmReceiver = false;

    public static BroadcastReceiver receiver = null;
    public static Activity activity;


    private String CLIENT_KEY = "client";
    private String SERVER_KEY = "server";
    private String PORT_KEY = "port";
    private String MBA_STATE_SP = "mba_state";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_broker);

        this.activity = this;

        Button connectButton = (Button) findViewById(R.id.connect_button);
        Button disconnectButton = (Button) findViewById(R.id.disconnect_button);

        // test: ispis id-a
        /*TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();
        System.out.println(uuid);
        System.out.println(SensorUniqueID.deviceID);*/

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText myNameText = (EditText) findViewById(R.id.client_name);
                String myName = myNameText.getText().toString();

                EditText brokerIPText = (EditText) findViewById(R.id.broker_ip);
                String brokerIP = brokerIPText.getText().toString();

                EditText brokerPortText = (EditText) findViewById(R.id.broker_port);
                String stringBrokerPort = brokerPortText.getText().toString();

                RadioButton radioButtonTCP = (RadioButton) findViewById(R.id.radioButtonTCP);

                if(radioButtonTCP.isChecked()){
                    brokerType = "TCP";
                }else {
                    brokerType = "GCM";
                }

                if (myName.trim().length() == 0 || brokerIP.trim().length() == 0 || stringBrokerPort.trim().length() == 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MobileBrokerActivity.this);
                    alertDialog.setTitle("Message");
                    alertDialog.setMessage("You must enter all values!");
                    alertDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    // mobile broker service
                    mobileBrokerName = myName;
                    brokerIpAddress = brokerIP;
                    brokerPort = Integer.valueOf(stringBrokerPort);

                    serviceIntent = new Intent(MobileBrokerActivity.this, MobileBrokerService.class);

                    serviceIntent.putExtra("mobileBrokerName", mobileBrokerName);
                    serviceIntent.putExtra("brokerIP", brokerIpAddress);
                    serviceIntent.putExtra("brokerPort", brokerPort);
                    serviceIntent.putExtra("brokerType", brokerType);

                    startService(serviceIntent);
                }
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(serviceIntent);
            }
        });


        SharedPreferences prefs = getSharedPreferences(MBA_STATE_SP, Activity.MODE_PRIVATE);

        if (prefs != null) {
            //Retreive the View
            EditText myNameText = (EditText) findViewById(R.id.client_name);
            EditText brokerIPText = (EditText) findViewById(R.id.broker_ip);
            EditText brokerPortText = (EditText) findViewById(R.id.broker_port);

            //Load its state
            String text = prefs.getString(CLIENT_KEY, "");
            myNameText.setText(text);

            text = prefs.getString(SERVER_KEY, "");
            brokerIPText.setText(text);

            text = prefs.getString(PORT_KEY, "");
            brokerPortText.setText(text);

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Retreive the View
        EditText myNameText = (EditText) findViewById(R.id.client_name);
        EditText brokerIPText = (EditText) findViewById(R.id.broker_ip);
        EditText brokerPortText = (EditText) findViewById(R.id.broker_port);

        //Load its state
        String text;
        if (savedInstanceState.containsKey(CLIENT_KEY)) {
            text = savedInstanceState.getString(CLIENT_KEY);
            myNameText.setText(text);
        }
        if (savedInstanceState.containsKey(SERVER_KEY)) {
            text = savedInstanceState.getString(SERVER_KEY);
            brokerIPText.setText(text);
        }
        if (savedInstanceState.containsKey(PORT_KEY)) {
            text = savedInstanceState.getString(PORT_KEY);
            brokerPortText.setText(text);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        // Retrieve the View
        EditText myNameText = (EditText) findViewById(R.id.client_name);
        EditText brokerIPText = (EditText) findViewById(R.id.broker_ip);
        EditText brokerPortText = (EditText) findViewById(R.id.broker_port);

        // Save its state
        saveInstanceState.putString(CLIENT_KEY, myNameText.getText().toString());
        saveInstanceState.putString(SERVER_KEY, brokerIPText.getText().toString());
        saveInstanceState.putString(PORT_KEY, brokerPortText.getText().toString());

        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    public void finish() {
        super.finish();

        SharedPreferences prefs = getSharedPreferences(MBA_STATE_SP, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Retrieve the View
        EditText myNameText = (EditText) findViewById(R.id.client_name);
        EditText brokerIPText = (EditText) findViewById(R.id.broker_ip);
        EditText brokerPortText = (EditText) findViewById(R.id.broker_port);

        // Save its state
        editor.putString(CLIENT_KEY, myNameText.getText().toString());
        editor.putString(SERVER_KEY, brokerIPText.getText().toString());
        editor.putString(PORT_KEY, brokerPortText.getText().toString());
        editor.apply();

    }

    public void setGcmReceiver(boolean gcmReceiver) {
        this.gcmReceiver = gcmReceiver;
    }

    public void setBroadcastReceiver(BroadcastReceiver receiver) {
        this.receiver = receiver;
    }

//    @Override
//    protected void onDestroy() {
////        if(brokerConected) {
////            disconnectFromBroker();
////        }
//        if(gcmReceiver){
//            unregisterReceiver(receiver);
//        }
//        super.onDestroy();
//    }

}
