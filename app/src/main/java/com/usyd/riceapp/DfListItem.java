package com.usyd.riceapp;

public class DfListItem {
    public String display_name;
    public String name;
    public Integer image=null;
    public DfListItem(String display_name, String name, Integer image){
        this.name = name;
        this.display_name = display_name;
        this.image = image;
    }
}
