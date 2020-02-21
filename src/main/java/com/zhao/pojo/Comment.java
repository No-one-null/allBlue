package com.zhao.pojo;


public class Comment {

  private int id;
  private int fromUid;
  private int toUid;
  private String content;
  private String topic;
  private int tid;

  @Override
  public String toString() {
    return "Comment{" +
            "id=" + id +
            ", fromUid=" + fromUid +
            ", toUid=" + toUid +
            ", content='" + content + '\'' +
            ", topic='" + topic + '\'' +
            ", tid=" + tid +
            '}';
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


  public int getFromUid() {
    return fromUid;
  }

  public void setFromUid(int fromUid) {
    this.fromUid = fromUid;
  }


  public int getToUid() {
    return toUid;
  }

  public void setToUid(int toUid) {
    this.toUid = toUid;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }


  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }

}
