package com.shpp.sv.notificationservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by SV on 23.02.2016.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, PushNotificationService.class));
        //Toast.makeText(context, "Receiver started", Toast.LENGTH_LONG).show();
    }

}
