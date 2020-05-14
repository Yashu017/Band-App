package com.example.hfilproject.BLE;

import java.util.HashMap;
import java.util.UUID;

public class SampleGattAttributes {

    private static HashMap<String, String> attributes = new HashMap<>();
    public static String HT_MEASUREMENT_CHARACTERISTIC_UUID = "00002A1C-0000-1000-8000-00805f9b34fb";
    static {
        attributes.put("00001809-0000-1000-8000-00805f9b34fb", "HT_SERVICE_UUID");
        attributes.put(HT_MEASUREMENT_CHARACTERISTIC_UUID, "Health Thermometer");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
