package com.example.hfilproject;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.UUID;

public class HTActivity extends BleProfileServiceReadyActivity<HTService.HTSBinder> {
    @SuppressWarnings("unused")
    private final String TAG = "HTSActivity";

    private TextView tempValueView;
    private TextView unitView;
    private TextView batteryLevelView;
    static float postValue;
    private UartService mService = null;

    @Override
    protected void onCreateView(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_feature_hts);
        setGUI();
        service_init();
    }

    @Override
    protected void onInitialize(final Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, makeIntentFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void setGUI() {
        tempValueView = findViewById(R.id.text_hts_value);
        unitView = findViewById(R.id.text_hts_unit);
        batteryLevelView = findViewById(R.id.battery);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUnits();
    }

    @Override
    protected void setDefaultUI() {
        tempValueView.setText(R.string.not_available_value);
        batteryLevelView.setText(R.string.not_available);

        setUnits();
    }

    private void setUnits() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int unit = Integer.parseInt(preferences.getString(SettingsFragment.SETTINGS_UNIT, String.valueOf(SettingsFragment.SETTINGS_UNIT_DEFAULT)));

        switch (unit) {
            case SettingsFragment.SETTINGS_UNIT_C:
                this.unitView.setText(R.string.hts_unit_celsius);
                break;
            case SettingsFragment.SETTINGS_UNIT_F:
                this.unitView.setText(R.string.hts_unit_fahrenheit);
                break;
            case SettingsFragment.SETTINGS_UNIT_K:
                this.unitView.setText(R.string.hts_unit_kelvin);
                break;
        }
    }

    @Override
    protected void onServiceBound(final HTService.HTSBinder binder) {
        onTemperatureMeasurementReceived(binder.getTemperature());
    }

    @Override
    protected void onServiceUnbound() {
        // not used
    }

    @Override
    protected int getLoggerProfileTitle() {
        return R.string.hts_feature_title;
    }

    @Override
    protected int getAboutTextId() {
        return R.string.hts_about_text;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.settings_and_about, menu);
        return true;
    }

    @Override
    protected boolean onOptionsItemSelected(final int itemId) {
        switch (itemId) {
            case R.id.action_settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected int getDefaultDeviceName() {
        return R.string.hts_default_name;
    }

    @Override
    protected UUID getFilterUUID() {

        return HTManager.HT_SERVICE_UUID;
    }

    @Override
    protected Class<? extends BleProfileService> getServiceClass() {
        return HTService.class;
    }

    @Override
    public void onServicesDiscovered(@NonNull final BluetoothDevice device, boolean optionalServicesFound) {
        // this may notify user or show some views
    }

    @Override
    public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
        super.onDeviceDisconnected(device);
        batteryLevelView.setText(R.string.not_available);
    }

    private void onTemperatureMeasurementReceived(Float value) {
        if (value != null) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            final int unit = Integer.parseInt(preferences.getString(SettingsFragment.SETTINGS_UNIT,
                    String.valueOf(SettingsFragment.SETTINGS_UNIT_DEFAULT)));

            switch (unit) {
                case SettingsFragment.SETTINGS_UNIT_F:
                    value = value * 1.8f + 32f;
                    break;
                case SettingsFragment.SETTINGS_UNIT_K:
                    value += 273.15f;
                    break;
                case SettingsFragment.SETTINGS_UNIT_C:
                    break;
            }
            tempValueView.setText(getString(R.string.hts_value, value));
        } else {
            tempValueView.setText(R.string.not_available_value);
        }
    }

    public void onBatteryLevelChanged(final int value) {
        batteryLevelView.setText(getString(R.string.battery, value));
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();

            if (HTService.BROADCAST_HTS_MEASUREMENT.equals(action)) {
                final float value = intent.getFloatExtra(HTService.EXTRA_TEMPERATURE, 0.0f);
                // Update GUI
                onTemperatureMeasurementReceived(value);
            } else if (HTService.BROADCAST_BATTERY_LEVEL.equals(action)) {
                final int batteryLevel = intent.getIntExtra(HTService.EXTRA_BATTERY_LEVEL, 0);
                // Update GUI
                onBatteryLevelChanged(batteryLevel);
            }
        }
    };

    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HTService.BROADCAST_HTS_MEASUREMENT);
        intentFilter.addAction(HTService.BROADCAST_BATTERY_LEVEL);
        return intentFilter;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };

    private Handler mHandler = new Handler() {
        @Override

        //Handler events that received from UART service
        public void handleMessage(Message msg) {

        }
    };


    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            Toast.makeText(HTActivity.this, "Message Received from Band:"+text, Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }

        }
    };

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }
}

