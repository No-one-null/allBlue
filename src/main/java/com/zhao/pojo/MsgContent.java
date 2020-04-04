package com.zhao.pojo;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

public class MsgContent {

    private long id;
    @Length(max = 25,min=2,message = "标题字数在2~25")
    private String title;
    @NotEmpty
    @Length(max = 300,message = "内容字数不能大于300")
    private String message;
    @NotBlank
    private String type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
