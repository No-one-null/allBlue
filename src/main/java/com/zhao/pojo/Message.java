package com.zhao.pojo;

import java.util.Date;

public class Message {

    private long id;
    private long mid;
    private int uid;
    private String type;
    private int status;
    private String title;
    private String message;
    private Date createDate;
    private MsgContent content;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", mid=" + mid +
                ", uid=" + uid +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", createDate=" + createDate +
                ", content=" + content +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public MsgContent getContent() {
        return content;
    }

    public void setContent(MsgContent content) {
        this.content = content;
    }
}