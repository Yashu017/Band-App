package com.example.hfilproject;

public class TempLog {
    private float Celsius;
    private float Farenheit;
    private  String Status;
    private String Day;
    private String Time;

    TempLog(float Celsius,float Farenheit,String Status,String Day,String Time){
        this.Celsius = Celsius;
        this.Farenheit = Farenheit;
        this.Status = Status;
        this.Day = Day;
        this.Time = Time;
    }

    public float getCelsius() {
        return Celsius;
    }

    public void setCelsius(float celsius) {
        Celsius = celsius;
    }

    public float getFarenheit() {
        return Farenheit;
    }

    public void setFarenheit(float farenheit) {
        Farenheit = farenheit;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
