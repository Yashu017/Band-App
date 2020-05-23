package com.example.hfilproject;

public class Notification {

    private String message;

    Notification(String title, String time, String message) {

        this.message = message;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
