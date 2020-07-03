package com.example.hfilproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import no.nordicsemi.android.ble.common.profile.ht.TemperatureMeasurementCallback;
import no.nordicsemi.android.ble.common.profile.ht.TemperatureType;
import no.nordicsemi.android.ble.common.profile.ht.TemperatureUnit;
import no.nordicsemi.android.log.Logger;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HTService extends BleProfileService implements HTManagerCallbacks {
    public static final String BROADCAST_HTS_MEASUREMENT = "no.nordicsemi.android.nrftoolbox.hts.BROADCAST_HTS_MEASUREMENT";
    public static final String EXTRA_TEMPERATURE = "no.nordicsemi.android.nrftoolbox.hts.EXTRA_TEMPERATURE";

    public static final String BROADCAST_BATTERY_LEVEL = "no.nordicsemi.android.nrftoolbox.BROADCAST_BATTERY_LEVEL";
    public static final String EXTRA_BATTERY_LEVEL = "no.nordicsemi.android.nrftoolbox.EXTRA_BATTERY_LEVEL";

    private final static String ACTION_DISCONNECT = "no.nordicsemi.android.nrftoolbox.hts.ACTION_DISCONNECT";

    private final static int NOTIFICATION_ID = 267;
    private final static int OPEN_ACTIVITY_REQ = 0;
    private final static int DISCONNECT_REQ = 1;
    /**
     * The last received temperature value in Celsius degrees.
     */
    private Float temp;

    private float tempData;

    @SuppressWarnings("unused")
    private HTManager manager;

    int categoryType = 0;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    String sendToken;
    String token1;
    Retrofit retrofit;
    String sendTokenBle;
    NotificationHelper notificationHelper;


    private final LocalBinder minder = new HTSBinder();

    /**
     * This local binder is an interface for the bonded activity to operate with the HTS sensor
     */
    class HTSBinder extends LocalBinder {
        /**
         * Returns the last received temperature value.
         *
         * @return Temperature value in Celsius.
         */
        Float getTemperature() {
            return temp;
        }
    }

    @Override
    protected LocalBinder getBinder() {
        return minder;
    }

    @Override
    protected LoggableBleManager<HTManagerCallbacks> initializeManager() {
        return manager = new HTManager(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DISCONNECT);
        registerReceiver(disconnectActionBroadcastReceiver, filter);

        notificationHelper = new NotificationHelper(this);
        sharedPrefs = getSharedPreferences("app", Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        // when user has disconnected from the sensor, we have to cancel the notification that we've created some milliseconds before using unbindService
        cancelNotification();
        unregisterReceiver(disconnectActionBroadcastReceiver);
        notificationHelper.SendNotification("Alert", "Device is disconnected.", HTActivity.class);
        SendNotification("Device is disconnected.");
        super.onDestroy();
    }

    @Override
    protected void onRebind() {

        stopForegroundService();

    }

    @Override
    protected void onUnbind() {
        startForegroundService();
    }

    @Override
    public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
        super.onDeviceDisconnected(device);
        temp = null;
        SendNotification("Device is disconnected.");

    }

    private void SendNotification(String connectionStatus) {
        sharedPrefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        token1 = sharedPrefs.getString("token", "");

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
        params.put("notification", connectionStatus);
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
                            Toast.makeText(HTService.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    sendTokenBle = response.body().getToken();
                    Toast.makeText(HTService.this, " Bluetooth disconnected", Toast.LENGTH_SHORT).show();
                    Log.e("Disconnected", "Bluetooth Service");
                }
            }

            @Override
            public void onFailure(Call<UserNotification> call, Throwable t) {
                Toast.makeText(HTService.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error in HT Service", "" + t.getMessage());
            }
        });

    }


    @Override
    public void onTemperatureMeasurementReceived(@NonNull final BluetoothDevice device,
                                                 final float temperature, @TemperatureUnit final int unit,
                                                 @Nullable final Calendar calendar,
                                                 @Nullable @TemperatureType final Integer type) {
        temp = TemperatureMeasurementCallback.toCelsius(temperature, unit);

        tempData = TemperatureMeasurementCallback.toFahrenheit(temperature, unit);

        // Toast.makeText(this, "temperature value"+ tempData, Toast.LENGTH_SHORT).show();


        SendTemp(tempData, temp);

        SendData(temp, tempData);


        final Intent broadcast = new Intent(BROADCAST_HTS_MEASUREMENT);
        broadcast.putExtra(EXTRA_DEVICE, getBluetoothDevice());
        broadcast.putExtra(EXTRA_TEMPERATURE, temp);
        // ignore the rest
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);


        if (!bound) {
            // Here we may update the notification to display the current temperature.
            // TODO modify the notification here
        }

        editor = sharedPrefs.edit();
        editor.putBoolean("TempReceived",true);
        editor.commit();
        notifyUser();
    }

    private void notifyUser() {
        boolean TempReceived = sharedPrefs.getBoolean("TempReceived",false);
        if (!TempReceived){
            notificationHelper.SendNotification("Notification","CWatch band is in sleep mode.",HTActivity.class);
            SendNotification("CWatch band is in sleep mode.");
        }
    }

    private void SendData(Float temp, float tempData) {

    }


    private void SendTemp(float tempData, float temp) {
        sharedPrefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();


        token1 = sharedPrefs.getString("token", "");
        Log.d("TAG", "" + token1);
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

        params.put("temperature", tempData);

        Call<UserTemp> tempCall = login.userTemp(token1, params);
        tempCall.enqueue(new Callback<UserTemp>() {
            @Override
            public void onResponse(Call<UserTemp> call, Response<UserTemp> response) {
                String error;
                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body().getErrorCode() != null) {
                        error = response.body().getErrorCode();
                        if (error.equals("2")) {
                            Toast.makeText(HTService.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT +5:30"));
                    Date currentTime = cal.getTime();
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    String time = dateFormat.format(currentTime);


                    sendToken = response.body().getToken();
                    //   Toast.makeText(HTService.this, "Temp sent to server.", Toast.LENGTH_SHORT).show();

                    Log.e("Result", " Temp Sent to server.");
                }
            }

            @Override
            public void onFailure(Call<UserTemp> call, Throwable t) {
                Toast.makeText(HTService.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });


    }


    @Override
    public void onBatteryLevelChanged(@NonNull final BluetoothDevice device, final int batteryLevel) {
        final Intent broadcast = new Intent(BROADCAST_BATTERY_LEVEL);
        broadcast.putExtra(EXTRA_DEVICE, getBluetoothDevice());
        broadcast.putExtra(EXTRA_BATTERY_LEVEL, batteryLevel);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    /**
     * Sets the service as a foreground service
     */
    private void startForegroundService() {
        // when the activity closes we need to show the notification that user is connected to the peripheral sensor
        // We start the service as a foreground service as Android 8.0 (Oreo) onwards kills any running background services
        final android.app.Notification notification = createNotification(R.string.uart_notification_connected_message, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification);
        } else {
            final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, notification);
        }
    }

    /**
     * Stops the service as a foreground service
     */
    private void stopForegroundService() {
        // when the activity rebinds to the service, remove the notification and stop the foreground service
        // on devices running Android 8.0 (Oreo) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            cancelNotification();
        }
    }

    /**
     * Creates the notification
     *
     * @param messageResId message resource id. The message must have one String parameter,<br />
     *                     f.e. <code>&lt;string name="name"&gt;%s is connected&lt;/string&gt;</code>
     * @param defaults
     */
    @SuppressWarnings("SameParameterValue")
    private Notification createNotification(final int messageResId, final int defaults) {
        final Intent parentIntent = new Intent(this, FeaturesActivity.class);
        parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Intent targetIntent = new Intent(this, HTActivity.class);

        final Intent disconnect = new Intent(ACTION_DISCONNECT);
        final PendingIntent disconnectAction = PendingIntent.getBroadcast(this, DISCONNECT_REQ, disconnect, PendingIntent.FLAG_UPDATE_CURRENT);

        // both activities above have launchMode="singleTask" in the AndroidManifest.xml file, so if the task is already running, it will be resumed
        final PendingIntent pendingIntent = PendingIntent.getActivities(this, OPEN_ACTIVITY_REQ, new Intent[]{parentIntent, targetIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ToolboxApplication.CONNECTED_DEVICE_CHANNEL);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(getString(R.string.app_name)).setContentText(getDeviceName() + " is connected");
        builder.setSmallIcon(R.drawable.ic_stat_notify_hts);
        builder.setShowWhen(defaults != 0).setDefaults(defaults).setAutoCancel(true).setOngoing(true);
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_action_bluetooth, getString(R.string.hts_notification_action_disconnect), disconnectAction));

        return builder.build();
    }

    /**
     * Cancels the existing notification. If there is no active notification this method does nothing
     */
    private void cancelNotification() {
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }

    /**
     * This broadcast receiver listens for {@link #ACTION_DISCONNECT} that may be fired by pressing Disconnect action button on the notification.
     */
    private final BroadcastReceiver disconnectActionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Logger.i(getLogSession(), "[Notification] Disconnect action pressed");
            if (isConnected())
                getBinder().disconnect();
            else
                stopSelf();
        }
    };
}

