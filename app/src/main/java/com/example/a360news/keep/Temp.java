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
    public  static ArrayList<String> imageUrl = new ArrayList<>();

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

    /** 是否第一次启动主活动,默认是 */
    public static int IS_STARTACTIVITY = 1;

    /** 是否是收藏夹启动新闻数据页活动*/
    public static int IS_DATAACTIVITY;

    /** 判断标签是否第一次进入， 默认是 */
    public static int LIFE = 1;
    public static int ENTERTAINMENT = 1;
    public static int FILM = 1;
    public static int FINANCE = 1;
    public static int GAME = 1;
    public static int INTERNATIONAL = 1;
    public static int MILITARY = 1;
    public static int NBA = 1;
    public static int NEWLIFE = 1;
    public static int SCIENCE = 1;
    public static int CAR = 1;
    public static int SOCIETY = 1;
    public static int SPORT =1;
    public static int FASHION = 1;
    public static int TIYU = 1;
}
