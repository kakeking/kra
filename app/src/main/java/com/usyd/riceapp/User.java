package com.usyd.riceapp;

public class User {
    private String phoneNum;
    private String token;
    public User(){}

    public User(String phoneNum, String token){
        this.phoneNum = phoneNum;
        this.token = token;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
