package com.example.leeicheng.dogbook.mydog;


import java.util.Date;

public class Dog {
    private int ownerId;
    private String name, gender, variety;
    private Date birthday;
    private int age;


    public Dog(int ownerId,String name, Date birthday, int age, String gender, String variety) {
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
}
