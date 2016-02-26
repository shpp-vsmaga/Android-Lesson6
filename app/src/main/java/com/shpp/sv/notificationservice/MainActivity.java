package com.shpp.sv.notificationservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.shpp.sv.notificationservice.PushNotificationService.ServiceBinder;

public class MainActivity extends AppCompatActivity {
    private static String LOG_TAG = "svcom";
    private Button btnStartService;
    private Button btnStopService;
    private Button btnChangeText;
    private EditText edtNotification;
    private EditText edtInterval;
    private PushNotificationService notifService;
    private ServiceConnection connection;
    boolean isBound = false;
    public final static String NOTIF_TEXT = "notif";
    public final static String NOTIF_INTERVAL = "inteval";
    public final static String SAVED_NOTIF_TEXT = "savedText";
    public final static String SAVED_INTERVAL = "savedInterval";
    public final static int DEFAULT_INTERVAL = 30;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        createServiceConnection();
        serviceIntent = new Intent(this, PushNotificationService.class);
        bindNotifService();
        updateBtnState();

        Intent intent = getIntent();
            String savedText = (intent.getStringExtra(SAVED_NOTIF_TEXT));
            int savedInterval = (intent.getIntExtra(SAVED_INTERVAL, DEFAULT_INTERVAL));
            if (savedText != null && savedInterval != 0) {
                edtNotification.setText(savedText);
                edtInterval.setText(Integer.toString(savedInterval));
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
        edtInterval = (EditText)findViewById(R.id.edtInterval);

        edtNotification.setText(getString(R.string.TimeToDrinkTea));
        edtInterval.setText(Integer.toString(DEFAULT_INTERVAL));
    }

    public void onClickChangeText(View view) {
        if (isBound) {
            notifService.scheduleNotification(edtNotification.getText().toString(),
                    Integer.parseInt(String.valueOf(edtInterval.getText())));
        }
    }

    public void onClickStartService(View view) {
        Intent intent = new Intent(this, PushNotificationService.class);
        intent.putExtra(NOTIF_TEXT, edtNotification.getText().toString());
        intent.putExtra(NOTIF_INTERVAL, Integer.parseInt(String.valueOf(edtInterval.getText())));
        startService(intent);
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
