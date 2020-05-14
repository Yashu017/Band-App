package com.example.hfilproject;

public class HexUtils {
    public static String displayHex(byte[] data) {
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data)
            stringBuilder.append(String.format("%02X ", byteChar));
        return stringBuilder.toString();
    }

}

