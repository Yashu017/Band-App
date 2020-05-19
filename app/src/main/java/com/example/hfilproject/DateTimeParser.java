package com.example.hfilproject;

import java.util.Calendar;
import java.util.Locale;

import no.nordicsemi.android.ble.common.callback.DateTimeDataCallback;
import no.nordicsemi.android.ble.data.Data;

public class DateTimeParser {
    /**
     * Parses the date and time info.
     *
     * @param data
     * @return time in human readable format
     */
    public static String parse(final Data data) {
        return parse(data, 0);
    }

    /**
     * Parses the date and time info. This data has 7 bytes
     *
     * @param data
     * @param offset
     *            offset to start reading the time
     * @return time in human readable format
     */
    /* package */static String parse(final Data data, final int offset) {
        final Calendar calendar = DateTimeDataCallback.readDateTime(data, offset);
        return String.format(Locale.US, "%1$te %1$tb %1$tY, %1$tH:%1$tM:%1$tS", calendar);
    }
}

