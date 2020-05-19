package com.example.hfilproject;

import java.util.Locale;

import no.nordicsemi.android.ble.data.Data;

public class TemperatureMeasurementParser {
    private static final byte TEMPERATURE_UNIT_FLAG = 0x01; // 1 bit
    private static final byte TIMESTAMP_FLAG = 0x02; // 1 bits
    private static final byte TEMPERATURE_TYPE_FLAG = 0x04; // 1 bit

    public static String parse(final Data data) {
        int offset = 0;
        final int flags = data.getIntValue(Data.FORMAT_UINT8, offset++);

        /*
         * false 	Temperature is in Celsius degrees
         * true 	Temperature is in Fahrenheit degrees
         */
        final boolean fahrenheit = (flags & TEMPERATURE_UNIT_FLAG) > 0;

        /*
         * false 	No Timestamp in the packet
         * true 	There is a timestamp information
         */
        final boolean timestampIncluded = (flags & TIMESTAMP_FLAG) > 0;

        /*
         * false 	Temperature type is not included
         * true 	Temperature type included in the packet
         */
        final boolean temperatureTypeIncluded = (flags & TEMPERATURE_TYPE_FLAG) > 0;

        final float tempValue = data.getFloatValue(Data.FORMAT_FLOAT, offset);
        offset += 4;

        String dateTime = null;
        if (timestampIncluded) {
            dateTime = DateTimeParser.parse(data, offset);
            offset += 7;
        }

        String type = null;
        if (temperatureTypeIncluded) {
            type = TemperatureTypeParser.parse(data, offset);
            // offset++;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append(String.format(Locale.US, "%.02f", tempValue));

        if (fahrenheit)
            builder.append("°F");
        else
            builder.append("°C");

        if (timestampIncluded)
            builder.append("\nTime: ").append(dateTime);
        if (temperatureTypeIncluded)
            builder.append("\nType: ").append(type);
        return builder.toString();
    }
}
