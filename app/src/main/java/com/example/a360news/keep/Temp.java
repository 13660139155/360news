package com.example.a360news.keep;

import android.graphics.Bitmap;

import com.example.a360news.json.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by asus on 2018/5/3.
 */
public class Temp {

    /**
     * 被缓存图片的id
     */
    public static ArrayList<String> bitmapUrl = new ArrayList<>();

    /**
     * 图片id的维护表
     */
    public static ArrayList<String> imageUrl = new ArrayList<>();

    /**
     * 缓存被收藏的图片
     */
    public static TreeMap<String, Bitmap> treeMapBitmap = new TreeMap<>();

    /**
     * 新闻id维护表
     */
    public static ArrayList<String> dataListId = new ArrayList<>();

    /**
     * 被收藏的新闻的缓存
     */
    public static TreeMap<String, Data> treeMapData = new TreeMap();

    /** 是否是收藏夹启动新闻数据页活动*/
    public static int IS_DATAACTIVITY;

    public static int h = 0;
    public static int q = 0;
    public static int w = 0;
    public static int e = 0;
    public static int r = 0;
    public static int t = 0;
    public static int y = 0;
    public static int u = 0;
    public static int i = 0;
    public static int o = 0;
    public static int p = 0;
    public static int a = 0;
    public static int s = 0;
    public static int d = 0;
    public static int f = 0;
    public static int g = 0;

}
