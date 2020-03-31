package com.zhao.pojo;

import java.util.Date;

public class MsgContent {

    private long id;
    private String title;
    private String message;
    private Date createDate;

    public MsgContent() {
    }

    public MsgContent(String title, String message, Date createDate) {
        this.title = title;
        this.message = message;
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "MsgContent{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", createDate=" + createDate +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

}
