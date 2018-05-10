package com.example.a360news.json;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻的所有数据
 * Created by asus on 2018/4/22.
 */

public class NewsAllData {
    // 是否有下一页(hasNext)
    private boolean newsHasNext;
    // 返回的状态码(retcode)
    private String newsRetcode;
    // 本次查询的api名(appCode)
    private String newsApiName;
    // 本次查询的api类型(dataType)
    private String newsApiType;
    // 翻页值(pageToken)
    private String newsPageToken;
    //Data
    private List<Data> newsData;

    public List<Data> getNewsData() {
        return newsData;
    }

    public void setNewsData(ArrayList<Data> dataList) {
        this.newsData = newsData;
    }

    public boolean isNewsHasNext() {
        return newsHasNext;
    }

    public void setNewsHasNext(boolean newsHasNext) {
        this.newsHasNext = newsHasNext;
    }

    public String getNewsPageToken() {
        return newsPageToken;
    }

    public void setNewsPageToken(String newsPageToken) {
        this.newsPageToken = newsPageToken;
    }
}
