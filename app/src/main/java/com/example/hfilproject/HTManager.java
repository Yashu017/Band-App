package com.example.hfilproject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.UUID;

import no.nordicsemi.android.ble.common.callback.ht.TemperatureMeasurementDataCallback;
import no.nordicsemi.android.ble.common.profile.ht.TemperatureType;
import no.nordicsemi.android.ble.common.profile.ht.TemperatureUnit;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.log.LogContract;

public class HTManager extends BatteryManager<HTManagerCallbacks> {
    /** Health Thermometer service UUID */
    final static UUID HT_SERVICE_UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    /** Health Thermometer Measurement characteristic UUID */
    private static final UUID HT_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A1C-0000-1000-8000-00805f9b34fb");

    private BluetoothGattCharacteristic htCharacteristic;

    HTManager(final Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected BatteryManagerGattCallback getGattCallback() {
        return new HTManagerGattCallback();
    }

    /**
     * BluetoothGatt callbacks for connection/disconnection, service discovery,
     * receiving indication, etc..
     */
    private class HTManagerGattCallback extends BatteryManagerGattCallback {
        @Override
        protected void initialize() {
            super.initialize();
            setIndicationCallback(htCharacteristic)
                    .with(new TemperatureMeasurementDataCallback() {
                        @Override
                        public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
                            log(LogContract.Log.Level.APPLICATION, "\"" + TemperatureMeasurementParser.parse(data) + "\" received");
                            super.onDataReceived(device, data);
                        }

                        @Override
                        public void onTemperatureMeasurementReceived(@NonNull final BluetoothDevice device,
                                                                     final float temperature,
                                                                     @TemperatureUnit final int unit,
                                                                     @Nullable final Calendar calendar,
                                                                     @Nullable @TemperatureType final Integer type) {
                            callbacks.onTemperatureMeasurementReceived(device, temperature, unit, calendar, type);
                        }
                    });
            enableIndications(htCharacteristic).enqueue();
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(HT_SERVICE_UUID);
            if (service != null) {
                htCharacteristic = service.getCharacteristic(HT_MEASUREMENT_CHARACTERISTIC_UUID);
            }
            return htCharacteristic != null;
        }

        @Override
        protected void onDeviceDisconnected() {
            super.onDeviceDisconnected();
            htCharacteristic = null;
        }
    }
}

