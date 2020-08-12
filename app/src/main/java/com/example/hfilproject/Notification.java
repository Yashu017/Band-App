package com.example.hfilproject;

public class Notification {

    private String message;
    private String time,time1;
    Notification(String title, String time, String time1, String message) {

        this.message = message;
        this.time = time;
        this.time1=time1;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }
}
