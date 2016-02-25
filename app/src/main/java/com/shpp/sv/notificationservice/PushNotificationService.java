package com.shpp.sv.notificationservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class PushNotificationService extends Service {
    private String notification;
    private final IBinder binder = new ServiceBinder();
    private static String LOG_TAG = "svcom";
    private static long MINUTE_IN_MILISEC = 60000;
    private int interval = 1;
    private Timer timer;
    private TimerTask timerTask;

    public PushNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        //Toast.makeText(this, "Service onBind()", Toast.LENGTH_LONG).show();
        log();
        return binder;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        notification = intent.getStringExtra(MainActivity.NOTIF_TEXT);
//        if (TextUtils.isEmpty(notification)){
//            notification = getString(R.string.TimeToDrinkTea);
//        }
        //sendNotification(notification);
        scheduleNotification(getString(R.string.TimeToDrinkTea));
        log();
        return START_STICKY;
    }
    public void scheduleNotification(final String text){
        notification = text;
        if (timerTask!= null){
            timerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                sendNotification(text);
            }
        };
        //timer.schedule(timerTask, interval * MINUTE_IN_MILISEC, interval * MINUTE_IN_MILISEC);
        timer.schedule(timerTask, 10000, 10000);
    }
    public void sendNotification(String text) {

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra("text", notification);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        Notification notif = new Notification.Builder(this)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notif.defaults = Notification.DEFAULT_SOUND;

        notificationManager.notify(1, notif);


    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer= new Timer();
        log();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerTask!= null){
            timerTask.cancel();
        }
        log();
    }

    private void log(){
        Throwable t = new Throwable();
        StackTraceElement trace[] = t.getStackTrace();

        if (trace.length > 1){
            StackTraceElement element = trace[1];
            Log.d(LOG_TAG, this.getClass().getSimpleName() +
                    "  " + element.getMethodName() +
                    "() - executed!!!");
        }

    }

    public class ServiceBinder extends Binder {
        PushNotificationService getService(){
            return PushNotificationService.this;
        }

    }

}


