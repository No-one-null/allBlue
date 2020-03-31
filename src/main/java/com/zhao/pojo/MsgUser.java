package com.zhao.pojo;


public class MsgUser {

    private long id;
    private long mid;
    private int uid;
    private String type;
    private int status;
    private MsgContent content;

    @Override
    public String toString() {
        return "MsgUser{" +
                "id=" + id +
                ", mid=" + mid +
                ", uid=" + uid +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", content=" + content +
                '}';
    }

    public MsgUser() {
    }

    public MsgUser(long mid, int uid, String type) {
        this.mid = mid;
        this.uid = uid;
        this.type = type;
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

    public MsgContent getContent() {
        return content;
    }

    public void setContent(MsgContent content) {
        this.content = content;
    }
}
