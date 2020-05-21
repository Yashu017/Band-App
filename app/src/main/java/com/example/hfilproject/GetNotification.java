package com.example.hfilproject;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetNotification {

    @SerializedName("errorCode")
    private int errorCode;

    @SerializedName("notification")
   ArrayList<NotificationItem> notification;

    public ArrayList<NotificationItem> getNotification() {
        return notification;
    }
}
