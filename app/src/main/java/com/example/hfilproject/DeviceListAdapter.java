package com.example.hfilproject;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

class DeviceListAdapter extends BaseAdapter {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    private final ArrayList<ExtendedBluetoothDevice> listBondedValues = new ArrayList<>();
    private final ArrayList<ExtendedBluetoothDevice> listValues = new ArrayList<>();

    DeviceListAdapter() {
    }

    /**
     * Sets a list of bonded devices.
     * @param devices list of bonded devices.
     */
    void addBondedDevices(@NonNull final Set<BluetoothDevice> devices) {
        for (BluetoothDevice device : devices) {
            listBondedValues.add(new ExtendedBluetoothDevice(device));
        }
        notifyDataSetChanged();
    }

    /**
     * Updates the list of not bonded devices.
     * @param results list of results from the scanner
     */
    public void update(@NonNull final List<ScanResult> results) {
        for (final ScanResult result : results) {
            final ExtendedBluetoothDevice device = findDevice(result);
            if (device == null) {
                listValues.add(new ExtendedBluetoothDevice(result));
            } else {
                device.name = result.getScanRecord() != null ? result.getScanRecord().getDeviceName() : null;
                device.rssi = result.getRssi();
            }
        }
        notifyDataSetChanged();
    }

    private ExtendedBluetoothDevice findDevice(@NonNull final ScanResult result) {
        for (final ExtendedBluetoothDevice device : listBondedValues)
            if (device.matches(result))
                return device;
        for (final ExtendedBluetoothDevice device : listValues)
            if (device.matches(result))
                return device;
        return null;
    }

    void clearDevices() {
        listValues.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        final int bondedCount = listBondedValues.size() + 1; // 1 for the title
        final int availableCount = listValues.isEmpty() ? 2 : listValues.size() + 1; // 1 for title, 1 for empty text
        if (bondedCount == 1)
            return availableCount;
        return bondedCount + availableCount;
    }

    @Override
    public Object getItem(final int position) {
        final int bondedCount = listBondedValues.size() + 1; // 1 for the title
        if (listBondedValues.isEmpty()) {
            if (position == 0)
                return R.string.scanner_subtitle_not_bonded;
            else
                return listValues.get(position - 1);
        } else {
            if (position == 0)
                return R.string.scanner_subtitle_bonded;
            if (position < bondedCount)
                return listBondedValues.get(position - 1);
            if (position == bondedCount)
                return R.string.scanner_subtitle_not_bonded;
            return listValues.get(position - bondedCount - 1);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(final int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0)
            return TYPE_TITLE;

        if (!listBondedValues.isEmpty() && position == listBondedValues.size() + 1)
            return TYPE_TITLE;

        if (position == getCount() - 1 && listValues.isEmpty())
            return TYPE_EMPTY;

        return TYPE_ITEM;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, @Nullable final View oldView, @NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final int type = getItemViewType(position);

        View view = oldView;
        switch (type) {
            case TYPE_EMPTY:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_empty, parent, false);
                }
                break;
            case TYPE_TITLE:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_title, parent, false);
                }
                final TextView title = (TextView) view;
                title.setText((Integer) getItem(position));
                break;
            default:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_row, parent, false);
                    final ViewHolder holder = new ViewHolder();
                    holder.name = view.findViewById(R.id.name);
                    holder.address = view.findViewById(R.id.address);
                    holder.rssi = view.findViewById(R.id.rssi);
                    view.setTag(holder);
                }

                final ExtendedBluetoothDevice device = (ExtendedBluetoothDevice) getItem(position);
                final ViewHolder holder = (ViewHolder) view.getTag();
                final String name = device.name;
                holder.name.setText(name != null ? name : parent.getContext().getString(R.string.not_available));
                holder.address.setText(device.device.getAddress());

                if (!device.isBonded || device.rssi != ExtendedBluetoothDevice.NO_RSSI) {
                    final int rssiPercent = (int) (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f));
                    holder.rssi.setImageLevel(rssiPercent);
                    holder.rssi.setVisibility(View.VISIBLE);
                } else {
                    holder.rssi.setVisibility(View.GONE);
                }
                break;
        }

        return view;
    }

    private class ViewHolder {
        private TextView name;
        private TextView address;
        private ImageView rssi;
    }
}
