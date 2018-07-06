package com.example.leeicheng.dogbook.articles;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by leeicheng on 2018/5/19.
 */

public class Message implements Serializable {
    int id,dogId,articleId;
    String content;
    Date postingTime;

    public Message(int dogId, int articleId, String content) {
        this.dogId = dogId;
        this.articleId = articleId;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDogId() {
        return dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(Date postingTime) {
        this.postingTime = postingTime;
    }
}
