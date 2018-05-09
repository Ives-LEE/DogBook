package com.example.leeicheng.dogbook.chats;

/**
 * Created by leeicheng on 2018/5/6.
 */

public class Room {
    int roomId,dogOne,dogTwo;

    public Room(int roomId, int dogOne, int dogTwo) {
        super();
        this.roomId = roomId;
        this.dogOne = dogOne;
        this.dogTwo = dogTwo;
    }

    public Room(int roomId, int dogTwo) {
        super();
        this.roomId = roomId;
        this.dogTwo = dogTwo;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getDogOne() {
        return dogOne;
    }

    public void setDogOne(int dogOne) {
        this.dogOne = dogOne;
    }

    public int getDogTwo() {
        return dogTwo;
    }

    public void setDogTwo(int dogTwo) {
        this.dogTwo = dogTwo;
    }
}
