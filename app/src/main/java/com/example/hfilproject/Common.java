package com.example.hfilproject;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

class Common {
    public static final String KEY_REQUESTING_LOCATION_UPDATES = "Location_Updates_Enable";

    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "Unknown Location": new StringBuilder()
                .append(mLocation.getLongitude())
                .append("/")
                .append(mLocation.getLatitude()).toString();
    }

    public static CharSequence getLocationTitle(UpdateBackgroundLocation updateBackgroundLocation) {
        return String.format("Location Updated :%1$s", DateFormat.getDateInstance().format(new Date()));
    }

    public static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES,value)
                .apply();
    }

    public static boolean requestLoctionUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES,false);
    }
}
