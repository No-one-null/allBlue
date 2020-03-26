package com.zhao.pojo;

import org.hibernate.validator.constraints.Length;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Talk {

    private int tid;
    private int uid;
    private User user;
    @Length(min = 1, max = 300)
    private String content;
    private String pictures;
    private String[] pictureArr;
    private Date time;
    private String topic;
    private String[] topicArr;
    private int status;
    private int countComplaint;

    @Override
    public String toString() {
        return "Talk{" +
                "tid=" + tid +
                ", uid=" + uid +
                ", user=" + user +
//                ", content='" + content + '\'' +
                ", pictures='" + pictures + '\'' +
                ", pictureArr=" + Arrays.toString(pictureArr) +
                ", time=" + time +
                ", topic='" + topic + '\'' +
                ", topicArr=" + Arrays.toString(topicArr) +
                ", status=" + status +
                ", countComplaint=" + countComplaint +
                '}';
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public void setPictureArr(String[] pictureArr) {
        this.pictureArr = pictureArr;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTopicArr(String[] topicArr) {
        this.topicArr = topicArr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCountComplaint() {
        return countComplaint;
    }

    public void setCountComplaint(int countComplaint) {
        this.countComplaint = countComplaint;
    }

    public String[] getTopicArr() {
        if (this.topic != null && !this.topic.equals("")) {
            topicArr = this.topic.split(",");
        }
        return topicArr;
    }

    public String[] getPictureArr() {
        if (this.pictures != null && !this.pictures.equals("")) {
            pictureArr = this.pictures.split(",");
        }
        return pictureArr;
    }
}
