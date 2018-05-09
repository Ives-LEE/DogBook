package com.example.leeicheng.dogbook.chats;

public class Chat {

    private int chatId,senderId,receiverId,chatroomId;
    private String message,type;

    public Chat(int senderId, int chatroomId, String message,String type) {
        super();
        this.type = type;
        this.senderId = senderId;
        this.chatroomId = chatroomId;
        this.message = message;
    }

    public Chat(int chatId, int senderId, int chatroomId, String message,String type) {
        super();
        this.chatId = chatId;
        this.senderId = senderId;
        this.chatroomId = chatroomId;
        this.message = message;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(int chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
