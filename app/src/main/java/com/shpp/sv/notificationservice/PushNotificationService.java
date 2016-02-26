package com.shpp.sv.notificationservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class PushNotificationService extends Service {
    private String notification;
    private final IBinder binder = new ServiceBinder();
    private static String LOG_TAG = "svcom";
    private static long MINUTE_IN_MILISEC = 60000;
    private int id = 1;
    private int interval = MainActivity.DEFAULT_INTERVAL;
    private Timer timer;
    private TimerTask timerTask;

    public PushNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        log();
        return binder;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log();
        notification = getString(R.string.TimeToDrinkTea);
        if (intent != null) {
            String receivedNotif = intent.getStringExtra(MainActivity.NOTIF_TEXT);
            int receivedInterval = intent.getIntExtra(MainActivity.NOTIF_INTERVAL, MainActivity.DEFAULT_INTERVAL);
            if (receivedNotif != null && receivedInterval != 0) {
                notification = receivedNotif;
                interval = receivedInterval;
            }
        }
        scheduleNotification(notification, interval);

        return START_STICKY;
    }

    public void scheduleNotification(final String text, final int receivedInterval){
        notification = text;
        interval = receivedInterval;
        if (timerTask != null){
            timerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                sendNotification(text);
            }
        };
        timer.schedule(timerTask, interval * MINUTE_IN_MILISEC, interval * MINUTE_IN_MILISEC);
        //timer.schedule(timerTask, 10000, 10000);
    }

    public void sendNotification(String text) {

        Intent mainActivityIntent = new Intent(this, MainActivity.class);

        mainActivityIntent.putExtra(MainActivity.SAVED_NOTIF_TEXT, notification);
        mainActivityIntent.putExtra(MainActivity.SAVED_INTERVAL, interval);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notif = new Notification.Builder(this)
                .setContentText(text)
                .setContentTitle(getString(R.string.Notification))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notif.defaults = Notification.DEFAULT_SOUND;

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(this);
        notifManager.notify(id++, notif);

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


