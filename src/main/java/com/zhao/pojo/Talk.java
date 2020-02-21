package com.zhao.pojo;


import java.util.Date;

public class Talk {

  private int tid;
  private String content;
  private String pictures;
  private String username;
  private Date time;
  private String topic;

  @Override
  public String toString() {
    return "Talk{" +
            "tid=" + tid +
            ", content='" + content + '\'' +
            ", pictures='" + pictures + '\'' +
            ", username='" + username + '\'' +
            ", time=" + time +
            ", topic='" + topic + '\'' +
            '}';
  }

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
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


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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

}
