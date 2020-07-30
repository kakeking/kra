package com.usyd.riceapp;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class DfRecord {
    private String recordId;
    private @ServerTimestamp Date plantDate;
    private @ServerTimestamp Date createDate;
    private GeoPoint location;
    private String location_name;
    private ArrayList<String> riceVariety;
    private ArrayList<String> nitrogenType;
    private int seedingRate;
    private int nitrogenAmount;
    public DfRecord(){}
    public DfRecord(String recordId, Date plantDate, Date createDate, GeoPoint location, ArrayList<String> riceVariety, ArrayList<String> nitrogenType, int seedingRate, int nitrogenAmount, String location_name){
        this.plantDate = plantDate;
        this.location = location;
        this.riceVariety = riceVariety;
        this.nitrogenType = nitrogenType;
        this.nitrogenAmount = nitrogenAmount;
        this.seedingRate = seedingRate;
        this.createDate = createDate;
        this.location_name = location_name;
    }

    public Date getPlantDate() {
        return plantDate;
    }

    public void setPlantDate(Date plantDate) {
        this.plantDate = plantDate;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public ArrayList<String> getRiceVariety() {
        return riceVariety;
    }

    public ArrayList<String> getNitrogenType() {
        return nitrogenType;
    }

    public void setNitrogenType(ArrayList<String> nitrogenType) {
        this.nitrogenType = nitrogenType;
    }
    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public void setRiceVariety(ArrayList<String> riceVariety) {
        this.riceVariety = riceVariety;
    }
    public int getSeedingRate() {
        return seedingRate;
    }

    public void setSeedingRate(int seedingRate) {
        this.seedingRate = seedingRate;
    }

    public int getNitrogenAmount() {
        return nitrogenAmount;
    }
    public void setNitrogenAmount(int nitrogenAmount) {
        this.nitrogenAmount = nitrogenAmount;
    }



    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
