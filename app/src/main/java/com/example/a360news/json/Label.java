package com.example.a360news.json;

/**
 * 标签
 * Created by asus on 2018/4/25.
 */

public class Label {

    private String name;
    private int viewId;

    public Label(String name, int viewId){
        this.name = name;
        this.viewId = viewId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }
}
