package com.example.hfilproject;

public class UserLocation {
    private int id;
    private String location;
    private String token;
    private String ErrorCode;

    public UserLocation(int id, String location, String token, String errorCode) {
        this.location = location;
        this.token = token;
        this.ErrorCode = errorCode;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}
