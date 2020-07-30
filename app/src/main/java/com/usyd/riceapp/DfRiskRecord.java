package com.usyd.riceapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class DfRiskRecord {
    private String id;
    private double temp;
    private double rainfall;
    private String risk;
    private int humidity;
    private @ServerTimestamp Date predictDate;
    private @ServerTimestamp Date plantDate;
    public DfRiskRecord(){}
    public DfRiskRecord(String id, String risk , int humidity, double temp, double rainfall, Date predictDate, Date plantDate){
        this.id = id;
        this.risk = risk;
        this.humidity = humidity;
        this.temp = temp;
        this.rainfall = rainfall;
        this.predictDate = predictDate;
        this.plantDate = plantDate;
    }
    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }
    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getRainfall() {
        return rainfall;
    }

    public void setRainfall(double rainfall) {
        this.rainfall = rainfall;
    }


    public Date getPredictDate() {
        return predictDate;
    }

    public void setPredictDate(Date predictDate) {
        this.predictDate = predictDate;
    }

    public Date getPlantDate() {
        return plantDate;
    }

    public void setPlantDate(Date plantDate) {
        this.plantDate = plantDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
