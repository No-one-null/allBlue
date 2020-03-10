package com.zhao.pojo;


import java.util.Date;

public class Comment {

    private int id;
    private int topicId;
    private int uid;
    private User user;
    private int toUid;
    private User toUser;
    private String content;
    private Date time;
    private int status;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", topicId=" + topicId +
                ", uid=" + uid +
                ", user=" + user +
                ", toUid=" + toUid +
                ", toUser=" + toUser +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", status=" + status +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getToUid() {
        return toUid;
    }

    public void setToUid(int toUid) {
        this.toUid = toUid;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}