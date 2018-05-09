package com.example.leeicheng.dogbook.chats;

import java.util.Set;


public class StateMessage {
    private String type;
    private int dog;
    private Set<Integer> dogs;

    public StateMessage(String type, int dog, Set<Integer> dogs) {
        super();
        this.type = type;
        this.dog = dog;
        this.dogs = dogs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDog() {
        return dog;
    }

    public void setDog(int dog) {
        this.dog = dog;
    }

    public Set<Integer> getDogs() {
        return dogs;
    }

    public void setDogs(Set<Integer> dogs) {
        this.dogs = dogs;
    }


}
