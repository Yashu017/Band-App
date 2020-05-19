package com.example.hfilproject;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UpdateBackgroundLocation extends Service {

    private static final String CHANNEL_ID = "my_channel";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.example.hfilproject" + "started from notification";
    private static final String TAG = "Post Location";
    private final IBinder mBinder = new LocalBinder();
    private static long UPDATE_INTERVAL_IN_MIL = 10000;
    private static long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL_IN_MIL / 2;
    private final int NOTI_ID = 1223;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    String token;
    int temp;
    String sendToken;
    String location;
    Retrofit retrofit;

    public UpdateBackgroundLocation() {

    }

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        sharedPrefs = getSharedPreferences("app", Context.MODE_PRIVATE);

        token = sharedPrefs.getString("token", "");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());

            }
        };
        createLocationRequest();
        getLastknownLocation();

        HandlerThread handlerThread = new HandlerThread("HFIL");
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

    }

    Timer timer = new Timer();

    Timer timer2 = new Timer();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SendLocation();
            }
        }, 0, 1 * 60000);


        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SendTemperature();
            }
        }, 0, 30000);
        return START_NOT_STICKY;
    }

    private void SendLocation() {

        location = sharedPrefs.getString("Updated Location", "");
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        for_login login = retrofit.create(for_login.class);
        Map<String, Object> params = new HashMap<>();
        params.put("location", location);
        Call<UserLocation> call = login.userLocation(token, params);
        call.enqueue(new Callback<UserLocation>() {
            @Override
            public void onResponse(Call<UserLocation> call, Response<UserLocation> response) {
                String error;
                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body().getErrorCode() != null) {
                        error = response.body().getErrorCode();
                        if (error.equals("2")) {
                            Toast.makeText(UpdateBackgroundLocation.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    sendToken = response.body().getToken();
                    Toast.makeText(UpdateBackgroundLocation.this, "Success", Toast.LENGTH_SHORT).show();
                    Log.e("Result", "Success");
                }
            }

            @Override
            public void onFailure(Call<UserLocation> call, Throwable t) {
                Toast.makeText(UpdateBackgroundLocation.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });

    }


    private void SendTemperature() {
        temp = sharedPrefs.getInt("temperature", 0);

        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        for_login login = retrofit.create(for_login.class);
        Map<String, Object> params = new HashMap<>();
        params.put("temperature", temp);

        Call<UserTemp> tempCall = login.userTemp(token, params);
        tempCall.enqueue(new Callback<UserTemp>() {
            @Override
            public void onResponse(Call<UserTemp> call, Response<UserTemp> response) {
                String error;
                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body().getErrorCode() != null) {
                        error = response.body().getErrorCode();
                        if (error.equals("2")) {
                            Toast.makeText(UpdateBackgroundLocation.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    sendToken = response.body().getToken();
                    Toast.makeText(UpdateBackgroundLocation.this, "Temp Success", Toast.LENGTH_SHORT).show();
                    Log.e("Result", " Temp Success");
                }
            }

            @Override
            public void onFailure(Call<UserTemp> call, Throwable t) {
                Toast.makeText(UpdateBackgroundLocation.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    private void removeLocationUpdates() {

        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            Common.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException ecx) {
            Common.setRequestingLocationUpdates(this, true);
            Log.e("Failed", "cannot remove updates.Lost Location permission");
        }
    }

    private void getLastknownLocation() {

        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.e("Message", "Failed to get location");

                            }
                        }
                    });
        } catch (SecurityException ex) {
            Log.e("message1", "lost location permission" + ex);
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MIL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void onNewLocation(Location lastLocation) {
        mLocation = lastLocation;
        EventBus.getDefault().postSticky(new SendLocation(mLocation));
        if (serviceIsRunningINForeground(this)) {
            mNotificationManager.notify(NOTI_ID, getNotification());
        }

    }

    private Notification getNotification() {

        Intent intent = new Intent(this, UpdateBackgroundLocation.class);
        String text = Common.getLocationText(mLocation);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        PendingIntent servicePendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SecondFragment.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch_black_24dp, "Launch", activityPendingIntent)
                .addAction(R.drawable.ic_launch_black_24dp, "Remove", servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Common.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, BottomNavActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
       builder.setContentIntent(contentIntent);
        builder.setChannelId(CHANNEL_ID);
        return builder.build();
    }

    private boolean serviceIsRunningINForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (getClass().getName().equals(service.service.getClassName()))
                if (service.foreground)
                    return true;


        return false;
    }

    public void requestLocationUpdates() {

        Common.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), UpdateBackgroundLocation.class));
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException ec) {
            Log.e("failed", "lost permission request" + ec);

        }
    }

    public class LocalBinder extends Binder {
        UpdateBackgroundLocation getService() {
            return UpdateBackgroundLocation.this;
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!mChangingConfiguration && Common.requestLoctionUpdates(this))
            startForeground(NOTI_ID, getNotification());
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}
