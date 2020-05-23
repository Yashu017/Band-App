package com.example.hfilproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class ScanDialog extends DialogFragment {


    private OnDeviceSelectedListener listener;

    private final static String PARAM_UUID = "param_uuid";
    private final static long SCAN_DURATION = 5000;

    private final static int REQUEST_PERMISSION_REQ_CODE = 34; // any 8-bit number

    private BluetoothAdapter bluetoothAdapter;
    private DeviceListAdapter adapter;
    private final Handler handler = new Handler();
    private Button scanButton;

    private View permissionRationale;

    private ParcelUuid uuid;

    private boolean scanning = false;


    public static ScanDialog getInstance(final UUID uuid){
        final ScanDialog dialog = new ScanDialog();
        final Bundle args = new Bundle();
        if (uuid != null)
            args.putParcelable(PARAM_UUID, new ParcelUuid(uuid));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args != null && args.containsKey(PARAM_UUID)) {
            uuid = args.getParcelable(PARAM_UUID);
        }

        final BluetoothManager manager = (BluetoothManager)  getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager != null) {
            bluetoothAdapter = manager.getAdapter();
        }
    }

    @Override
    public void onDestroyView() {
        stopScan();
        super.onDestroyView();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_device_selection, null);
        final ListView listview = dialogView.findViewById(android.R.id.list);

        listview.setEmptyView(dialogView.findViewById(android.R.id.empty));
        listview.setAdapter(adapter = new DeviceListAdapter());


        builder.setTitle(R.string.scanner_title);
        final AlertDialog dialog = builder.setView(dialogView).create();
        listview.setOnItemClickListener((parent, view, position, id) -> {
            stopScan();
            dialog.dismiss();
            final ExtendedBluetoothDevice d = (ExtendedBluetoothDevice) adapter.getItem(position);
            listener.onDeviceSelected(d.device, d.name);
        });
        permissionRationale = dialogView.findViewById(R.id.permission_rationale); // this is not null only on API23+

        scanButton = dialogView.findViewById(R.id.action_cancel);
        scanButton.setOnClickListener(v -> {
            if (v.getId() == R.id.action_cancel) {
                if (scanning) {
                    dialog.cancel();
                } else {
                    startScan();
                }
            }
        });

        addBoundDevices();
        if (savedInstanceState == null)
            startScan();
        return dialog;
        // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       // LayoutInflater inflater = getActivity().getLayoutInflater();
       // View view = inflater.inflate(R.layout.activity_device_list,null);
      //  builder.setView(view)
            //    .setTitle("Select Device:");

     //  return builder.create();
    }

    public interface OnDeviceSelectedListener {
        /**
         * Fired when user selected the device.
         *
         * @param device
         *            the device to connect to
         * @param name
         *            the device name. Unfortunately on some devices {@link BluetoothDevice#getName()}
         *            always returns <code>null</code>, i.e. Sony Xperia Z1 (C6903) with Android 4.3.
         *            The name has to be parsed manually form the Advertisement packet.
         */
        void onDeviceSelected(@NonNull final BluetoothDevice device, @Nullable final String name);

        /**
         * Fired when scanner dialog has been cancelled without selecting a device.
         */
        void onDialogCanceled();
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        try {
            this.listener = (OnDeviceSelectedListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDeviceSelectedListener");
        }
    }


    private void startScan() {
        // Since Android 6.0 we need to obtain Manifest.permission.ACCESS_FINE_LOCATION to be able to scan for
        // Bluetooth LE devices. This is related to beacons as proximity devices.
        // On API older than Marshmallow the following code does nothing.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) && permissionRationale.getVisibility() == View.GONE) {
                permissionRationale.setVisibility(View.VISIBLE);
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            }
            return;
        }

        // Hide the rationale message, we don't need it anymore.
        if (permissionRationale != null)
            permissionRationale.setVisibility(View.GONE);

        adapter.clearDevices();
        scanButton.setText(R.string.scanner_action_cancel);

        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        final ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000).setUseHardwareBatchingIfSupported(false).build();
        final List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(uuid).build());
        scanner.startScan(filters, settings, scanCallback);

        scanning = true;
        handler.postDelayed(() -> {
            if (scanning) {
                stopScan();
            }
        }, SCAN_DURATION);
    }

    private void stopScan() {
        if (scanning) {
            scanButton.setText(R.string.scanner_action_scan);

            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(scanCallback);

            scanning = false;
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
            // do nothing
        }

        @Override
        public void onBatchScanResults(@NonNull final List<ScanResult> results) {
            adapter.update(results);
        }

        @Override
        public void onScanFailed(final int errorCode) {
            // should never be called
        }
    };

    private void addBoundDevices() {
        final Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        adapter.addBondedDevices(devices);
    }
}
