package com.example.hfilproject;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String SETTINGS_UNIT = "settings_hts_unit";
    public static final int SETTINGS_UNIT_C = 0; // [C]
    public static final int SETTINGS_UNIT_F = 1; // [F]
    public static final int SETTINGS_UNIT_K = 2; // [K]
    public static final int SETTINGS_UNIT_DEFAULT = SETTINGS_UNIT_C;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.settings_hts);
    }
}
