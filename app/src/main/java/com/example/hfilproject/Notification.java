package com.example.hfilproject;

public class Notification {

    private String message;
    private String time;
    Notification(String title, String time, String message) {

        this.message = message;
        this.time = time;
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
}
