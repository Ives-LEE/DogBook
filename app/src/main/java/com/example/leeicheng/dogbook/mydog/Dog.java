package com.example.leeicheng.dogbook.mydog;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Dog {

    private int ownerId;

    private int dogId;

    private String name;

    private String gender;

    private String variety;

    private Date birthday;

    private int age;

    public Dog(int dogId) {
        super();
        this.dogId = dogId;
    }

    public Dog(String name, String gender, String variety, int age, Date birthday) {
        super();
        this.name = name;
        this.gender = gender;
        this.variety = variety;
        this.birthday = birthday;
        this.age = age;
    }

    public Dog(int ownerId, String name, String gender, String variety, int age, Date birthday) {
        this.ownerId = ownerId;
        this.name = name;
        this.birthday = birthday;
        this.age = age;
        this.gender = gender;
        this.variety = variety;
    }


    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }


    public int getDogId() {
        return dogId;
    }


    public void setDogId(int dogId) {
        this.dogId = dogId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    @Override
    public String toString() {
        String text = "ownerId = " + ownerId +
                "dogId = " + dogId +
                "name = " + name +
                "gender = " + gender +
                "variety = " + variety +
                "birthday = " + birthday +
                "age " + age;

        return text;
    }
}
