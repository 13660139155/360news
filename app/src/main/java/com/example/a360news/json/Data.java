package com.example.a360news.json;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

/**
 * 新闻的主要数据
 * Created by asus on 2018/4/22.
 */

public class Data implements Serializable{

    //发布者id（posterId）
    private String newsPublisherId;
    //新闻内容（content）
    private String newsContent;
    //发布者名称(posterScreenName)
    private String newsPublisher;
    // 新闻链接(url)
    private String newsUrl;
    //发布时间（UTC时间)(publishDateStr)
    private String newsPublishDataStr;
    //标题(title)
    private String newsTitle;
    // 发布日期时间(publishDate)
    private Number newsPublishData;
    // 评论数(commentCount)
    private String newsCommentCount;
    //新闻id（id）
    private String newsId;
    //图片合集（imageUrls）
    private List<String> newsImageUrls;
    //新闻的踩数（dislikeCount）
    private String newsDisLikeCount;
    //新闻的点赞数（likeCount）
    private String newsLikeCount;
    //观看数（viewCount）
    private String newsViewCount;

    public String getNewsViewCount() {
        return newsViewCount;
    }

    public void setNewsViewCount(String newsViewCount) {
        this.newsViewCount = newsViewCount;
    }

    public String getNewsLikeCount() {
        return newsLikeCount;
    }

    public void setNewsLikeCount(String newsLikeCount) {
        this.newsLikeCount = newsLikeCount;
    }

    public String getNewsDisLikeCount() {
        return newsDisLikeCount;
    }

    public void setNewsDisLikeCount(String newsDisLikeCount) {
        this.newsDisLikeCount = newsDisLikeCount;
    }

    public String getNewsCommentCount() {
        return newsCommentCount;
    }

    public void setNewsCommentCount(String newsCommentCount) {
        this.newsCommentCount = newsCommentCount;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getNewsPublishDataStr() {
        return newsPublishDataStr;
    }

    public void setNewsPublishDataStr(String newsPublishDataStr) {
        this.newsPublishDataStr = newsPublishDataStr;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getNewsPublisher() {
        return newsPublisher;
    }

    public void setNewsPublisher(String newsPublisher) {
        this.newsPublisher = newsPublisher;
    }

    public List<String> getNewsImageUrls() {
        return newsImageUrls;
    }

    public void setNewsImageUrls(List<String> newsImageUrls) {
        this.newsImageUrls = newsImageUrls;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

}
