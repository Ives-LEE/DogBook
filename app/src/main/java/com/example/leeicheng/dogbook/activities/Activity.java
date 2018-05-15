package com.example.leeicheng.dogbook.activities;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Activity implements Serializable {

    private int id;
    private String name;
    private String location_address;
    private double location_latitude;
    private double location_longitude;
    private String activity_date;
    private String content;

    public Activity(int id, String name, String location_address, double location_latitude, double location_longitude, String activity_date, String content) {
        this.id = id;
        this.name = name;
        this.location_address = location_address;
        this.location_latitude = location_latitude;
        this.location_longitude = location_longitude;
        this.activity_date = activity_date;
        this.content = content;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getLocation_address() {
        return location_address;
    }

    public void setLocation_address(String location_address) {
        this.location_address = location_address;
    }

    public double getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(double location_latitude) {
        this.location_latitude = location_latitude;
    }

    public double getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(double location_longitude) {
        this.location_longitude = location_longitude;
    }


    public String getActivity_date() {
        return activity_date;
    }

    public void setActivity_date(String activity_date) {
        this.activity_date = activity_date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



}
