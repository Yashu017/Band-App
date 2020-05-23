package com.example.hfilproject;

public class NotificationItem {

    private long time ;

    private int category;

    private String notification;

    public NotificationItem(String notification,long time,int category) {
        this.time = time;
        this.category = category;
        this.notification = notification;
    }

    public long getTime() {
        return time;
    }

    public int getCategory() {
        return category;
    }

    public String getNotification() {
        return notification;
    }
}
