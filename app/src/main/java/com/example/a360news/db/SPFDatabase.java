package com.example.a360news.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.a360news.MainActivity;
import com.example.a360news.MyApplication;
import com.example.a360news.NewsDataActivity;
import com.example.a360news.json.Data;
import com.example.a360news.json.NewsAllData;
import com.example.a360news.unit.JSONUnit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * 缓存新闻
 * Created by asus on 2018/4/25.
 */

public class SPFDatabase {

    /**
     * 储存新闻数据
     * @param fileName 文件名
     * @param data 要储存的数据
     */
    public static void preferenceData(String fileName, String data){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
        editor.putString(fileName, data);
        editor.apply();
    }

    /**
     * 取出新闻数据
     * @param fileName 文件名
     * @return 要取出的数据
     */
    public static ArrayList<Data> extractData(String fileName){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return JSONUnit.praseNewsResponse(preferences.getString(fileName, ""));
    }
}
