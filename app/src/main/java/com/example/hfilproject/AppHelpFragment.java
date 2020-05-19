package com.example.hfilproject;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AppHelpFragment extends DialogFragment {
    private static final String ARG_TEXT = "ARG_TEXT";
    private static final String ARG_VERSION = "ARG_VERSION";

    public static AppHelpFragment getInstance(final int aboutResId, final boolean appendVersion) {
        final AppHelpFragment fragment = new AppHelpFragment();

        final Bundle args = new Bundle();
        args.putInt(ARG_TEXT, aboutResId);
        args.putBoolean(ARG_VERSION, appendVersion);
        fragment.setArguments(args);

        return fragment;
    }

    public static AppHelpFragment getInstance(final int aboutResId) {
        final AppHelpFragment fragment = new AppHelpFragment();

        final Bundle args = new Bundle();
        args.putInt(ARG_TEXT, aboutResId);
        args.putBoolean(ARG_VERSION, false);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Bundle args = requireArguments();
        final StringBuilder text = new StringBuilder(getString(args.getInt(ARG_TEXT)));

        final boolean appendVersion = args.getBoolean(ARG_VERSION);
        if (appendVersion) {
            try {
                final String version = requireContext().getPackageManager()
                        .getPackageInfo(requireContext().getPackageName(), 0).versionName;
                text.append(getString(R.string.about_version, version));
            } catch (final PackageManager.NameNotFoundException e) {
                // do nothing
            }
        }
        return new AlertDialog.Builder(requireContext())
                .setTitle("ABOUT")
                .setMessage(text)
                .setPositiveButton(R.string.ok, null)
                .create();
    }
}
