package com.kelmory.goodtogo.running;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.kelmory.goodtogo.R;
import com.kelmory.goodtogo.utils.AndroidLocationService;

import java.util.Arrays;

public class RunningIntentService extends IntentService {

    private static final String TAG = RunningIntentService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 975;

    private static final String ACTION_START_RUN =
            "com.kelmory.goodtogo.running.action.ACTION_START_RUN";
    private static final String ACTION_STOP_RUN =
            "com.kelmory.goodtogo.running.action.ACTION_STOP_RUN";

    private static final String CHANNEL_ID = "com.kelmory.goodtogo.running.notify.RUN_CHANNEL";

    private NotificationManager notificationManager;

    // Extra parameters names

    public RunningIntentService() {
        super("RunningIntentService");
    }


    public static void startActionStart(Context context) {
        Toast.makeText(context,
                "Start running recording.",
                Toast.LENGTH_LONG)
                .show();

        Intent intent = new Intent(context, RunningIntentService.class);
        intent.setAction(ACTION_START_RUN);
        context.startService(intent);
    }

    public static void startActionStop(Context context) {

        Toast.makeText(context,
                "Running stopped, recording...",
                Toast.LENGTH_LONG)
                .show();

        Intent intent = new Intent(context, RunningIntentService.class);
        intent.setAction(ACTION_STOP_RUN);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // When intent passed in, set action by it.
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_RUN.equals(action)) {
                handleActionStart();
            } else if (ACTION_STOP_RUN.equals(action)) {
                handleActionStop();
            }
        }
    }

    private void handleActionStart() {
        final AndroidLocationService locationService = new AndroidLocationService(this);
        locationService.setLocationListener(new AndroidLocationService.OnLocationGetCallback() {
            @Override
            public void onLocationGet(Location location) {
                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                RunningManager.addRoutePoint(position);
            }
        });
        locationService.startService();
        RunningManager.startRunning();

        Log.d(TAG, "running manager started");

        createNotificationChannel();

        Log.d(TAG, "channel created");

        final RemoteViews contentView = new RemoteViews(
                getPackageName(), R.layout.notification_small);
        contentView.setTextViewText(R.id.textview_notify_dist, RunningManager.getDistance());
        contentView.setTextViewText(R.id.textview_notify_time, RunningManager.getRunTime());

        Log.d(TAG, "contentview set");

        final NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this, CHANNEL_ID);


        final Notification notification = builder
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("You are running with GoodToGo.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(CHANNEL_ID)
                .build();

        Log.d(TAG, "notification built");

        notificationManager.notify(NOTIFICATION_ID, notification);

        Log.d(TAG, "notification sent");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(RunningManager.isStarted()){
                    Log.d(TAG, "running thread for updating");

                    contentView.setTextViewText(
                            R.id.textview_notify_dist, RunningManager.getDistance());
                    contentView.setTextViewText(
                            R.id.textview_notify_time, RunningManager.getRunTime());

                    notificationManager.notify(NOTIFICATION_ID, notification);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                }

                locationService.stopService();
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }).start();

    }

    private void handleActionStop() {
        RunningManager.stopRunning(this);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}