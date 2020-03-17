package com.zhao.pojo;


import java.util.Date;

import static com.zhao.util.Constant.PROGRESS_ARRAY;

public class Mark {

    private int uid;
    private int acId;
    private String comment;
    private int rating;
    private String tag;
    private int progress;
    private Date createDate;
    private User user;
    private AcItems acItems;

    @Override
    public String toString() {
        return "Mark{" +
                "uid=" + uid +
                ", acId=" + acId +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", tag='" + tag + '\'' +
                ", progress=" + progress +
                ", createDate=" + createDate +
                ", user=" + user +
                ", acItems=" + acItems +
                '}';
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAcId() {
        return acId;
    }

    public void setAcId(int acId) {
        this.acId = acId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AcItems getAcItems() {
        return acItems;
    }

    public void setAcItems(AcItems acItems) {
        this.acItems = acItems;
    }

    public String progress(int progress) {
        return PROGRESS_ARRAY[progress];
    }

    public String star(int rating) {
        StringBuilder str = new StringBuilder("暂无");
        if (rating > 0) {
            str = new StringBuilder();
            for (int i = 0; i < rating; i++) {
                str.append('★');
            }
            for (int i = rating; i < 5; i++) {
                str.append('☆');
            }
        }
        return str.toString();
    }
}
