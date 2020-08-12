package com.example.hfilproject;

public class TempItem {
    private long time ;

    private float temperature;

    public TempItem(long time, float temperature) {
        this.time = time;
        this.temperature = temperature;
    }

    public long getTime() {
        return time;
    }

    public float getTemperature() {
        return temperature;
    }
}
