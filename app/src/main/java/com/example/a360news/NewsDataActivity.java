package com.example.a360news;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a360news.db.FileDatabase;
import com.example.a360news.db.SPFDatabase;
import com.example.a360news.db.SQLDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.keep.Temp;
import com.example.a360news.unit.HttpUnit;
import com.example.a360news.unit.MyPopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

public class NewsDataActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imageViewBack2;
    Toolbar toolbarData;
    TextView textViewContent;
    TextView textViewEditor;
    TextView textViewPoster;
    TextView textViewPublisher;
    TextView textViewTitle;
    TextView textViewDate;
    ImageView imageViewImage;
    Data data;
    Bitmap bitmap;
    ImageView imageViewPoint;
    /* 数据库 */
    SQLDatabase sqlDatabase;

    private MyPopupWindow myPopupWindow;//弹出框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_data);
        imageViewBack2 = findViewById(R.id.image_view_back2);
        toolbarData = findViewById(R.id.tool_bar4);
        textViewPublisher = findViewById(R.id.text_view_publisher);
        textViewContent = findViewById(R.id.text_view_news_content);
        textViewPoster = findViewById(R.id.text_view_news_poster);
        textViewEditor = findViewById(R.id.text_view_news_editor);
        textViewDate = findViewById(R.id.text_view_news_date);
        textViewTitle = findViewById(R.id.text_view_news_title);
        imageViewImage = findViewById(R.id.image_view_data_image);
        imageViewPoint = findViewById(R.id.image_view_point);

        imageViewBack2.setOnClickListener(this);
        imageViewPoint.setOnClickListener(this);
        sqlDatabase = new SQLDatabase(NewsDataActivity.this, "Store", null, 1);
        setSupportActionBar(toolbarData);
        Intent intent = getIntent();
        data = (Data)intent.getSerializableExtra("data");
        showNews(data);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.image_view_back2:
                finish();
                break;
            case R.id.image_view_point:
                myPopupWindow = new MyPopupWindow(NewsDataActivity.this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch(v.getId()){
                        case R.id.text_preserve:
                            if(Temp.treeMapData.get(data.getNewsId()) == null){
                                if(data != null && bitmap != null){
                                    Temp.treeMapBitmap.put(data.getNewsImageUrls().get(0), bitmap);
                                    insertInSQL("ImageUrl", "imageUrl", data.getNewsImageUrls().get(0));
                                    FileDatabase.saveBitmap(data.getNewsImageUrls().get(0), bitmap);
                                }
                                if(data != null){
                                    Temp.treeMapData.put(data.getNewsId(), data);
                                    insertInSQL("NewsId", "newsId", data.getNewsId());
                                    FileDatabase.saveInFile(data, data.getNewsId());
                                    Toast.makeText(NewsDataActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(NewsDataActivity.this, "新闻已收藏过", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.text_shared:
                            Intent intent = new Intent(Intent.ACTION_SEND);
            //                String newURl = data.getNewsImageUrls().get(0).replace("/", "");
            //                File file = new File(MyApplication.getContext().getFilesDir(), newURl);
            //                if(file != null && file.exists()) {
            //                    intent.setType("image/*");
            //                    //由文件得到路径
            //                    Uri uri = Uri.fromFile(file);
            //                    intent.putExtra(Intent.EXTRA_STREAM, uri);
            //                }else{
            //                    intent.setType("text/plain");
            //                }
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "我是标题");
                            intent.putExtra(Intent.EXTRA_TEXT,  data.getNewsTitle() + "\n\n"
                                                                + data.getNewsPublishDataStr() + "\n"
                                                                + "编辑：" + data.getNewsPublisher() + "\n\n"
                                                                + data.getNewsContent()
                                                                + data.getNewsUrl());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(intent, "分享到"));
                            break;
                        default:
                            break;
                        }
                        myPopupWindow.dismiss();
                    }
                });
                myPopupWindow.showOnView(imageViewPoint);
                break;
            default:
                break;
        }
    }

    /**
     * 显示新闻内容
     * @param data
     */
    private void showNews(Data data){
        textViewDate.setText(data.getNewsPublishDataStr());
        textViewTitle.setText(data.getNewsTitle());
        textViewPublisher.setText(data.getNewsPublisher());
        textViewContent.setText(data.getNewsContent());
        textViewPoster.setText(data.getNewsPublisher());
        imageViewImage.setImageResource(R.drawable.launch);
        if(Temp.IS_DATAACTIVITY == 0){
            bitmap = FileDatabase.loadBitmap(data.getNewsImageUrls().get(0));
            if(bitmap != null){
                imageViewImage.setImageBitmap(bitmap);
            }else{
                new loadImage().execute(data.getNewsImageUrls().get(0));
            }
        }else {
            Temp.IS_DATAACTIVITY = 0;
            bitmap = FileDatabase.loadBitmap(data.getNewsImageUrls().get(0));
            if(bitmap != null){
                imageViewImage.setImageBitmap(bitmap);
            }else{
                new loadImage().execute(data.getNewsImageUrls().get(0));
            }
        }

    }

    public class loadImage extends AsyncTask<String, Integer, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];
            bitmap = HttpUnit.getOneImageBitmap(imageUrl);
            if(bitmap != null){
                FileDatabase.saveBitmap(imageUrl, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmaps) {
            if(bitmap != null){
                imageViewImage.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 向数据库中插入数据
     * @param bookName 表名
     * @param colName  列名
     * @param data 数据
     */
     private void insertInSQL(String bookName, String colName, String data){
        SQLiteDatabase db = sqlDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(colName, data);
        db.insert(bookName, null, contentValues);
        contentValues.clear();
    }

    /**
     * 启动NewsDataActivity活动
     * @param context
     * @param data
     */
    public static void actionStart(Context context, Data data){
        Intent intent = new Intent(context, NewsDataActivity.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }

    /**
     * 改变背景透明度
     */
    private void changeBackgoundAlpha(float alpha){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }
}
