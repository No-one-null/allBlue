package com.zhao.pojo;


import java.util.Date;
import java.util.List;

import static com.zhao.util.CommonUtil.getImgSrc;

public class AcNews {

    private long newsId;
    private String newsTitle;
    private String newsAuthor;
    private String newsContent;
    private Date newsDate;
    private String newsType;
    private int status;
    private String firstImg;

    @Override
    public String toString() {
        return "AcNews{" +
                "newsId=" + newsId +
                ", newsTitle='" + newsTitle + '\'' +
                ", newsAuthor='" + newsAuthor + '\'' +
                ", firstImg='" + firstImg + '\'' +
                ", newsDate=" + newsDate +
                ", newsType='" + newsType + '\'' +
                ", status=" + status +
                '}';
    }

    public long getNewsId() {
        return newsId;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsAuthor() {
        return newsAuthor;
    }

    public void setNewsAuthor(String newsAuthor) {
        this.newsAuthor = newsAuthor;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public Date getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(Date newsDate) {
        this.newsDate = newsDate;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFirstImg() {
        List<String> imgs = getImgSrc(this.newsContent);
        if (imgs.size() > 0) {
            return imgs.get(0);
        }
        return "";
    }
}
