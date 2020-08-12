package com.example.hfilproject;

public class NotificationItem {

    private long time,time1 ;

    private int category;

    private String notification;

    public NotificationItem(String notification,long time,long time1,int category) {
        this.time = time;
        this.time1=time1;
        this.category = category;
        this.notification = notification;
    }

    public long getTime1() {
        return time1;
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
