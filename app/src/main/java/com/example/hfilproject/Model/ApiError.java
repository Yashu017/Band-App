package com.example.hfilproject.Model;

public class ApiError {
private int statusCode;
private String endPoint;
private String message="Unknown Error";

    public int getStatusCode() {
        return statusCode;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getMessage() {
        return message;
    }
}
