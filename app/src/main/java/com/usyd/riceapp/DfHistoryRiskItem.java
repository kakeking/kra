package com.usyd.riceapp;

public class DfHistoryRiskItem {
    public int days;
    public String risk;
    public double temp;
    public int humidity;
    public double rainfall;
    public DfHistoryRiskItem(int days, String risk, double temp, int humidity, double rainfall){
        this.days = days;
        this.temp = temp;
        this.risk = risk;
        this.humidity = humidity;
        this.rainfall = rainfall;
    }
}
