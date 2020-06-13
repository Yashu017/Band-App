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
import android.location.Address;
import android.location.Geocoder;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private final int NOTI_ID = 1223;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;
    private LocationRequest locationRequest;
    Retrofit retrofit;
    String token1;
    String sendTokenBle;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    double distaceGeo;
    private Location mLocation;

    NotificationHelper notificationHelper;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    String token;
    int temp;
    String sendToken;
    String location;
    int geoStatus;
    long time = 1 * 30000;
    ;
    String fullAddress;
    public static final int notify = 300000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling


    public UpdateBackgroundLocation() {

    }

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        sharedPrefs = getSharedPreferences("app", Context.MODE_PRIVATE);

        token = sharedPrefs.getString("token", "");
        geoStatus = sharedPrefs.getInt("geoStatus", 0);

        notificationHelper = new NotificationHelper(this);
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

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
                    NotificationManager.IMPORTANCE_MIN);
            mNotificationManager.createNotificationChannel(mChannel);
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();

        }

        if (sharedPrefs.getBoolean("firstTimeMap", false) != true) {

            Handler handler = new Handler();
// Define the code block to be executed
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    // Do something here on the main thread
                    if (mLocation != null) {
                        comparedifference(mLocation.getLatitude(), mLocation.getLongitude());
                    }
                    // Repeat this the same runnable code block again another 2 seconds
                    // 'this' is referencing the Runnable object
                    handler.postDelayed(this, time);
                }
            };
// Start the initial runnable task by posting through the handler
            handler.post(runnableCode);


        }

        sendNotification();
        return START_NOT_STICKY;
    }

    private void sendNotification() {

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
                                double latitude = mLocation.getLatitude();
                                double longitude = mLocation.getLongitude();
                                Log.e("getM", latitude + "" + longitude);


                                Geocoder geocoder = new Geocoder(UpdateBackgroundLocation.this, Locale.getDefault());
                                try {
                                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 3);
                                    String address1 = addressList.get(0).getAddressLine(0);
                                    String area = addressList.get(0).getLocality();
                                    String city = addressList.get(0).getAdminArea();
                                    String country = addressList.get(0).getCountryName();
                                    String postalCode = addressList.get(0).getPostalCode();
                                    fullAddress = address1 + ", " + area + ", " + city + ", " + country + ", " + postalCode;

                                } catch (Exception e) {
                                    e.printStackTrace();

                                }

                            } else {
                                Log.e("Message", "Failed to get location");

                            }
                        }
                    });
        } catch (SecurityException ex) {
            Log.e("message1", "lost location permission" + ex);
        }
    }

    private void comparedifference(double latii, double longii) {

        double lat1 = latii;//end
        double lon1 = longii;//start
        double lat2 = Double.parseDouble(sharedPrefs.getString("latGeo", ""));
        double lon2 = Double.parseDouble(sharedPrefs.getString("longGeo", ""));
        // Haversine formula
        double dlon = Math.toRadians(lon1 - lon2);
        double dlat = Math.toRadians(lat1 - lat2);
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        float r = 6371;

        // calculate the result
        distaceGeo = c * r;
        double kmres = distaceGeo / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(kmres));
        double meter = distaceGeo % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        Log.i("Radius Value", "" + distaceGeo + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        if (meterInDec > 40) {

            String exit = "You exited your geofence";
            //putNotificationGeofence(exit);
            notificationHelper.SendNotification("Alert", "You exited geofence area.", MapsActivity.class);
            sendLocation();
            Toast.makeText(this, "You exited geofence", Toast.LENGTH_LONG).show();
            postNotificationToServer();
            time = 30000 * 1;
        } else {
            Toast.makeText(this, "enter", Toast.LENGTH_LONG).show();
            time = 60000 * 1;
        }


    }

    private void postNotificationToServer() {

        int categoryType = 0;
        sharedPrefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        token1 = sharedPrefs.getString("token", "");


        String geofenceStatus = "Geo fence breached.";
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
        params.put("notification", geofenceStatus);
        params.put("category", categoryType);
        Call<UserNotification> call = login.userNotify(token1, params);
        call.enqueue(new Callback<UserNotification>() {
            @Override
            public void onResponse(Call<UserNotification> call, Response<UserNotification> response) {
                String error;
                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body().getErrorCode() != null) {
                        error = response.body().getErrorCode();
                        if (error.equals("2")) {
                            Toast.makeText(UpdateBackgroundLocation.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    sendTokenBle = response.body().getToken();
                    //  Toast.makeText(UpdateBackgroundLocation.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserNotification> call, Throwable t) {
                Toast.makeText(UpdateBackgroundLocation.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("GeofenceTransition", "" + t.getMessage());
            }
        });


    }

    private void sendLocation() {
        location = sharedPrefs.getString("updated Location", "");
        if (location != null) {
            Log.e("token", "" + location);
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
            params.put("location", fullAddress);
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
                        //   Toast.makeText(UpdateBackgroundLocation.this, "Location sent to server.", Toast.LENGTH_SHORT).show();
                        Log.e("Location Posted", "Success");
                    }
                }

                @Override
                public void onFailure(Call<UserLocation> call, Throwable t) {
                    Toast.makeText(UpdateBackgroundLocation.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("error", "" + t.getMessage());
                }
            });

        }

    }


    private Notification putNotificationGeofence(String exit) {


        Intent intent = new Intent(this, UpdateBackgroundLocation.class);
        // String text = Common.getLocationText(mLocation);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        PendingIntent servicePendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SecondFragment.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // .setContentText(text)
                .setContentText(exit)
                .setContentTitle("Alert")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.sqlogo)
                // .setTicker(text)
                .setWhen(System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, BottomNavActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);


        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, builder.build());
        // builder.setChannelId(CHANNEL_ID);
        return builder.build();
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setFastestInterval(5000);
        locationRequest.setFastestInterval(10000);
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
                // .setContentText(text)
                .setContentText("Your own KAWACH in your protection.")
                //.setContentTitle(Common.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.sqlogo)
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

    private class TimeDisplay extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("service is ", "running");
                }
            });
        }
    }
}

