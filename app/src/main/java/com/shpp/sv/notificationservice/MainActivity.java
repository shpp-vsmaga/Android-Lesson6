package com.shpp.sv.notificationservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.shpp.sv.notificationservice.PushNotificationService.ServiceBinder;

public class MainActivity extends AppCompatActivity {
    private static String LOG_TAG = "svcom";
    Button btnStartService;
    Button btnStopService;
    Button btnChangeText;
    EditText edtNotification;
    PushNotificationService notifService;
    ServiceConnection connection;
    boolean isBound = false;
    public final static String NOTIF_TEXT = "notif";

    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        createServiceConnection();
        serviceIntent = new Intent(this, PushNotificationService.class);
        bindNotifService();
        updateBtnState();
        String nnn = (getIntent().getStringExtra("text"));
        if (!TextUtils.isEmpty(nnn)) {
            Log.d(LOG_TAG, nnn);
        }
    }

    private void bindNotifService() {
        bindService(serviceIntent, connection, 0);
    }

    private void createServiceConnection() {

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ServiceBinder binder = (ServiceBinder)service;
                notifService = binder.getService();
                isBound = true;
                updateBtnState();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
                updateBtnState();
            }
        };
    }

    private void findViews() {
        btnStartService = (Button)findViewById(R.id.btnStartService);
        btnStopService = (Button)findViewById(R.id.btnStopService);
        btnChangeText = (Button)findViewById(R.id.btnChangeText);
        edtNotification = (EditText)findViewById(R.id.edtNotification);
    }

    public void onClickChangeText(View view) {
        if (isBound) {
            notifService.scheduleNotification(edtNotification.getText().toString());
        }
    }

    public void onClickStartService(View view) {
        startService(new Intent(this, PushNotificationService.class)
                .putExtra(NOTIF_TEXT, edtNotification.getText().toString()));
        updateBtnState();
        bindNotifService();
    }

    public void oncCLickStopService(View view) {
        stopService(new Intent(this, PushNotificationService.class));
        updateBtnState();
    }

    public void updateBtnState(){
        if (isBound){
            btnStartService.setEnabled(false);
            btnStopService.setEnabled(true);
            btnChangeText.setEnabled(true);

        } else {
            btnStartService.setEnabled(true);
            btnStopService.setEnabled(false);
            btnChangeText.setEnabled(false);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound){
            unbindService(connection);
        }
    }
}
