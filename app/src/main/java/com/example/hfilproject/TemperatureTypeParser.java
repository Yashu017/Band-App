package com.example.hfilproject;

import no.nordicsemi.android.ble.data.Data;

public class TemperatureTypeParser {

    public static String parse(final Data data) {
        return parse(data, 0);
    }

    /* package */static String parse(final Data data, final int offset) {
        final int type = data.getValue()[offset];

        switch (type) {
            case 1:
                return "Armpit";
            case 2:
                return "Body (general)";
            case 3:
                return "Ear (usually ear lobe)";
            case 4:
                return "Finger";
            case 5:
                return "Gastro-intestinal Tract";
            case 6:
                return "Mouth";
            case 7:
                return "Rectum";
            case 8:
                return "Toe";
            case 9:
                return "Tympanum (ear drum)";
            default:
                return "Unknown";
        }
    }
}
