package com.example.a360news;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a360news.db.FileDatabase;
import com.example.a360news.db.SQLDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.keep.Temp;
import com.example.a360news.unit.FavoriteAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static com.example.a360news.MainActivity.IS_NETWORK_AVAILABLE;

public class FavoriteActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imageViewBack;
    TextView textViewCompile;
    RecyclerView recyclerView;
    FavoriteAdapter favoriteAdapter;
    LinearLayoutManager linearLayoutManager;
    RelativeLayout relativeLayout;
    SQLDatabase sqlDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        imageViewBack = (ImageView)findViewById(R.id.image_view_back_fav);
        textViewCompile = (TextView)findViewById(R.id.text_view_compile);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_favorite);
        relativeLayout = (RelativeLayout)findViewById(R.id.relative_layout_delete);
        imageViewBack.setOnClickListener(this);
        textViewCompile.setOnClickListener(this);
        textViewCompile.setTag(false);
        sqlDatabase = new SQLDatabase(FavoriteActivity.this, "Store", null, 1);

        ArrayList<Data> arrayList = new ArrayList<>();
        Temp.dataListId = quryFromSQL("NewsId", "newsId");
        Temp.imageUrl = quryFromSQL("ImageUrl", "imageUrl");

        /** 判断是否隐藏编辑控件 */
        if(Temp.dataListId.size() == 0){
            textViewCompile.setVisibility(View.INVISIBLE);
        }else {
            textViewCompile.setVisibility(View.VISIBLE);
        }

        /* 从缓存获得收藏 */
        if(Temp.treeMapData.size() != 0){
            linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            for(Map.Entry<String, Data> entry : Temp.treeMapData.entrySet()){
                arrayList.add(entry.getValue());
            }
            favoriteAdapter = new FavoriteAdapter(arrayList, Temp.treeMapBitmap);
            recyclerView.setAdapter(favoriteAdapter);
        }else if(Temp.dataListId.size() != 0){/* 从本地获得收藏 */
            linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            Temp.treeMapData.clear();
            for (int i = 0; i < Temp.dataListId.size(); i++){
                Data data = FileDatabase.loadFromFile(Temp.dataListId.get(i));
                if(data != null){
                    Temp.treeMapData.put(data.getNewsId(), data);
                }
            }
            for(Map.Entry<String, Data> entry : Temp.treeMapData.entrySet()){
                arrayList.add(entry.getValue());
            }
            Temp.treeMapBitmap.clear();
            for(int i = 0; i < Temp.imageUrl.size(); i++){
                Bitmap bitmap = FileDatabase.loadBitmap(Temp.imageUrl.get(i));
                Temp.treeMapBitmap.put(Temp.imageUrl.get(i), bitmap);
            }
            favoriteAdapter = new FavoriteAdapter(arrayList, Temp.treeMapBitmap);
            recyclerView.setAdapter(favoriteAdapter);
        }
    }

    /**
     * 启动活动
     */
    public static void actionStart(Context context){
        Intent intent = new Intent(context, FavoriteActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_view_back_fav:
                finish();
                break;
            case R.id.text_view_compile:
                if(!((boolean)textViewCompile.getTag())){//没有被点击过
                    favoriteAdapter.tag(true);
                    textViewCompile.setText("取消");
                    textViewCompile.setTag(true);
                }else {
                    favoriteAdapter.tag(false);
                    textViewCompile.setText("编辑");
                    textViewCompile.setTag(false);
                }
            default:
                break;
        }
    }

    /**
     *  从数据库中查询数据
     * @param bookName 表名
     * @return NewsID或ImageUrl的ArrayList
     */
    private ArrayList<String> quryFromSQL(String bookName, String colName){
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = sqlDatabase.getWritableDatabase();
        Cursor cursor = db.query(bookName, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String url = cursor.getString(cursor.getColumnIndex(colName));
                arrayList.add(url);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }
}
