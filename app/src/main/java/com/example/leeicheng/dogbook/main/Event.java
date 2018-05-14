package com.example.leeicheng.dogbook.main;


import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    int eventId,type;
    String title, overview,location;
    Date date;

    public Event(String title, String overview, String location, Date date) {
        this.title = title;
        this.overview = overview;
        this.location = location;
        this.date = date;
    }

    public Event(int eventId, int type, String title, String overview, Date date) {
        this.eventId = eventId;
        this.type = type;
        this.title = title;
        this.overview = overview;
        this.date = date;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
