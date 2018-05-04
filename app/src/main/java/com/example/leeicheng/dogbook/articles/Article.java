package com.example.leeicheng.dogbook.articles;

/**
 * Created by leeicheng on 2018/5/2.
 */

public class Article {

    int dogId,status;
    String content,location;

    public Article(int dogId,String content, String location, int status) {
        this.dogId = dogId;
        this.status = status;
        this.content = content;
        this.location = location;
    }

    public int getDogId() {
        return dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
