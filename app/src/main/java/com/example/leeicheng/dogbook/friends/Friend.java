package com.example.leeicheng.dogbook.friends;

public class Friend {

    private int Id;
    private String name;
    private int image;

    public Friend(int Id,   String name, int image) {
        super();
        this.Id = Id;
        this.image = image;
        this.name = name;

    }


    public int getId() {
        return Id;
    }

    public void setId(int dogId) {
        this.Id = dogId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
