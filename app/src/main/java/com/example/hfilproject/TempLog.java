package com.example.hfilproject;

public class TempLog {
    private String Celsius;
    private String Farenheit;
    private  String Status;
    private String Day;
    private String Time;

    TempLog(String Celsius,String Farenheit,String Status,String Day,String Time){
        this.Celsius = Celsius;
        this.Farenheit = Farenheit;
        this.Status = Status;
        this.Day = Day;
        this.Time = Time;
    }

    public String getCelsius() {
        return Celsius;
    }

    public void setCelsius(String celsius) {
        Celsius = celsius;
    }

    public String getFarenheit() {
        return Farenheit;
    }

    public void setFarenheit(String farenheit) {
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
