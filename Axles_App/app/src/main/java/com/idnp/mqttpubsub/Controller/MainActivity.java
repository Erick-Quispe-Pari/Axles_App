package com.idnp.mqttpubsub.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.idnp.mqttpubsub.R;
import com.idnp.mqttpubsub.Utilities.ToolHelper;

import java.util.UUID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity
        implements MainActivityListener {

    private final static String TAG = "MainActivity";
    public final static String CLIENT_ID = UUID.randomUUID().toString();
    private MqttBroadcastReceiver mqttBroadcastReceiver;
    private TextView txtDisplay;
    private TextView txtAction;
    private EditText edtTopic;
    private EditText edtQos;
    private EditText edtNumMessage;
    private EditText edtDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttBroadcastReceiver = new MqttBroadcastReceiver(MainActivity.this);

        txtAction = (TextView) findViewById(R.id.txtAction);
        txtDisplay = (TextView) findViewById(R.id.txtDisplay);
        edtTopic = (EditText) findViewById(R.id.edtTopic);
        edtQos = (EditText) findViewById(R.id.edtQos);
        edtNumMessage = (EditText) findViewById(R.id.edtNumMessage);
        edtDelay = (EditText) findViewById(R.id.edtDelay);

        Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnStop = (Button) findViewById(R.id.btnStop);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnPublish = (Button) findViewById(R.id.btnPublish);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d(TAG, "stoping...");
                    initMqttService(MqttIntentService.ACTION_STOP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initMqttService(MqttIntentService.ACTION_START);
                    txtAction.setText("Connected");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initMqttService(MqttIntentService.ACTION_PUBLISH);
                    String datetime2 = ToolHelper.getDateTime();
                    ToolHelper.setPublishBegin(getApplicationContext(), datetime2);
                    txtAction.setText("Started at: " + datetime2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initMqttService(MqttIntentService.ACTION_SAVE);
                    txtAction.setText("Saved");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MqttBroadcastReceiver.ACTION_MESSAGE);
        registerReceiver(mqttBroadcastReceiver, intentFilter);

        String datetime2 = ToolHelper.getPublishBegin(getApplicationContext());
        txtAction.setText(datetime2);

    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mqttBroadcastReceiver);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void display(String data) {
        txtDisplay.setText(data);
    }


    private void initMqttService(String action) {
        String topic = edtTopic.getText().toString();
        int qos = Integer.parseInt(edtQos.getText().toString());
        int delay = Integer.parseInt(edtDelay.getText().toString());
        int size = Integer.parseInt(edtNumMessage.getText().toString());

        Intent intent = new Intent(MainActivity.this, MqttHelperService.class);
        intent.putExtra(MqttIntentService.TOPIC, topic);
        intent.putExtra(MqttIntentService.QOS, qos);
        intent.putExtra(MqttIntentService.DELAY, delay);
        intent.putExtra(MqttIntentService.DATA, size);
        intent.setAction(action);
        startService(intent);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }*/

    }


}
