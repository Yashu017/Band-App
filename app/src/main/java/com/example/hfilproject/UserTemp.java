package com.example.hfilproject;

public class UserTemp {

    private int id;
    private int temperature;
    private String token;
    private String ErrorCode;

    public UserTemp(int id, int temperature, String token, String ErrorCode) {
        this.temperature = temperature;
        this.token = token;
        this.ErrorCode = ErrorCode;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
