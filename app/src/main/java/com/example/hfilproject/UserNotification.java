package com.example.hfilproject;

public class UserNotification {
    private int id;
    private String Notification;
    private String token;
    private String ErrorCode;
    private int category;

    public UserNotification(int id, String notification, int category, String token, String errorCode) {
        this.id = id;
        this.Notification = notification;
        this.token = token;
        this.ErrorCode = errorCode;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotification() {
        return Notification;
    }

    public void setNotification(String notification) {
        Notification = notification;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
