package com.example.hfilproject;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetTemp {

    @SerializedName("errorCode")
    private int errorCode;

    @SerializedName("notification")
    ArrayList<TempItem> temperature;

    public ArrayList<TempItem> getTemperature() {
        return temperature;
    }
}
