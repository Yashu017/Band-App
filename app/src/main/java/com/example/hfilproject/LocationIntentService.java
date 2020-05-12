package com.example.hfilproject;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.example.hfilproject.App.CHANNEL_ID;

public class LocationIntentService extends IntentService {


    private static final String TAG = "LocationIntentService";
    private PowerManager.WakeLock wakeLock;

    public LocationIntentService() {
        super("LocationIntentService");
        setIntentRedelivery(false);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PostLocation: Wakelock");
        wakeLock.acquire();         //timeout time should be filled here
        Log.d(TAG, "Wakelock acquired");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new android.app.Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Sending Temperature")
                    .setContentText("Sending...")
                    .setSmallIcon(R.drawable.ic_launch_black_24dp)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");

        String input = intent.getStringExtra("inputExtra");
        Log.d(TAG, input);

        for (int i = 0; i < 10; i++) {
            Log.d(TAG, input + "-" + i);
            SystemClock.sleep(1000);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        wakeLock.release();
        Log.d(TAG, "Wakelock released");
    }
}

