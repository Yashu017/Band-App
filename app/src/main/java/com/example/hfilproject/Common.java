package com.example.hfilproject;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

class Common {
    public static final String KEY_REQUESTING_LOCATION_UPDATES = "Location_Updates_Enable";

    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "Unknown Location": new StringBuilder()
                .append(mLocation.getLongitude())
                .append("/")
                .append(mLocation.getLatitude()).toString();
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
