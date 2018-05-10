package com.example.a360news.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * sql数据库，对新闻数据进行保存与读取
 * Created by asus on 2018/4/22.
 */

public class SQLDatabase extends SQLiteOpenHelper {

    private Context mContext;

    private static final String CREATE_IMAGEURL = "create table ImageUrl("
            + "id integer primary key autoincrement, "
            + "imageUrl text)";
    private static final String CREATE_NEWSID = "create table NewsId("
            + "id integer primary key autoincrement, "
            + "newsId text)";
    private static final String CREATE_BITMAP = "create table Bitmap("
            + "id integer primary key autoincrement, "
            + "bytes blob)";

    /**
     * 数据库构造器
     * @param context   上下文
     * @param name      数据库名称
     * @param factory   一般为null
     * @param version  数据库版本
     */
    public SQLDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IMAGEURL);
        db.execSQL(CREATE_NEWSID);
        db.execSQL(CREATE_BITMAP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
