package com.example.hfilproject.Model;

public class User {

    public String name,phoneNumber,age,address,bluetoothId,quarantineType,status;
    public String token;
    public String errorCode;
    String _id;

    public User(String name, String phoneNumber, String age, String address, String bluetoothId, String quarantineType, String status) {
        this.name = name;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.address = address;
        this.bluetoothId = bluetoothId;
        this.quarantineType = quarantineType;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String gettoken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBluetoothId() {
        return bluetoothId;
    }

    public void setBluetoothId(String bluetoothId) {
        this.bluetoothId = bluetoothId;
    }

    public String getQuarantineType() {
        return quarantineType;
    }

    public void setQuarantineType(String quarantineType) {
        this.quarantineType = quarantineType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void settoken(String token) {
        this.token = token;
    }
}

