package com.example.hfilproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.UUID;

public class UartActivity extends BleProfileServiceReadyActivity<UartService.UartBinder> implements UARTInterface {

    UartService mService = null;
    TextView message;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onServiceBound(UartService.UartBinder binder) {
        mService = binder.getService();
    }

    @Override
    protected void onServiceUnbound() {

    }

    @Override
    protected Class<? extends BleProfileService> getServiceClass() {
        return UartService.class;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
           setContentView(R.layout.activity_uart);
           setUI();
    }

    private void setUI() {
        message = findViewById(R.id.uartMessage);
    }

    @Override
    protected void setDefaultUI() {

    }

    @Override
    protected int getDefaultDeviceName() {
        return 0;
    }

    @Override
    protected int getAboutTextId() {
        return 0;
    }

    @Override
    protected UUID getFilterUUID() {
        return null;
    }



    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;

            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "UART Service Connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                          //  Toast.makeText(UartActivity.this, "Message Received from Band:"+text, Toast.LENGTH_SHORT).show();
                            message.setText(text);

                        } catch (Exception e) {
                            Log.e("UartActivity", e.toString());
                        }
                    }
                });
            }

        }
    };

    @Override
    public void send(String text) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }
}
