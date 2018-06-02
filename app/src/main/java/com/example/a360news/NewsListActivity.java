package com.example.a360news;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a360news.json.Data;
import com.example.a360news.adapter.DataListAdapter;
import com.example.a360news.interfance.HttpCallBackListener;
import com.example.a360news.unit.HttpUnit;
import com.example.a360news.unit.JSONUnit;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity
        implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener,
        TextWatcher{

    private static final String TAG = "NewsListActivity";
    private ArrayList dataList = new ArrayList<>();
    private View loadmoreView;
    private  Intent intent;
    private  DataListAdapter dataAdapter;
    ImageView imageBack;
    ImageView imageSearch;
    Toolbar toolbarList;
    EditText editText3;
    ImageView imageViewdelete3;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    Boolean isLoading = false;//表示是否正处于加载状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        loadmoreView = LayoutInflater.from(NewsListActivity.this).inflate(R.layout.footer_view_layout, null);//获得刷新视图
        loadmoreView.setVisibility(View.GONE);//默认情况下不可见
        toolbarList = (Toolbar)findViewById(R.id.tool_bar3);
        imageBack = (ImageView)findViewById(R.id.image_view_back);
        editText3 = (EditText)findViewById(R.id.edit_view_edit3);
        imageSearch = (ImageView)findViewById(R.id.image_view_image_search3);
        listView = (ListView)findViewById(R.id.list_view_newsList);
        imageViewdelete3 = (ImageView)findViewById(R.id.image_view_delete3);
        imageBack.setOnClickListener(this);
        imageSearch.setOnClickListener(this);
        imageViewdelete3.setOnClickListener(this);
        editText3.addTextChangedListener(this);
        setSupportActionBar(toolbarList);

        intent = getIntent();
         String key = intent.getStringExtra("keyWord");
        editText3.setText(key.toString());
        /** 移动光标到后面 */
        Editable able = editText3.getText();
        int position = able.length();
        Selection.setSelection(able,position);

        setSearchVerticalDataList(key);

        listView.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout_list);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnScrollListener(this);
    }

    /**
     * 搜索列表
     * @param keyWord
     */
    private void setSearchVerticalDataList(final String keyWord) {
        String key = encode(keyWord);
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + key + "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        /* 初始化dataList */
        /* 请求api */
        HttpUnit.sendHttpRequest(address,
                new HttpCallBackListener() {
                    @Override
                    public void onFinish(final String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataList = JSONUnit.praseNewsResponse(response);
                                if(dataList.size() != 0){
                                    dataAdapter = new DataListAdapter(NewsListActivity.this, R.layout.data_item_layout, dataList);
                                    listView.addFooterView(loadmoreView,null,false);//加入刷新布局
                                    listView.setAdapter(dataAdapter);
                                    Toast.makeText(NewsListActivity.this, "发现了" + dataList.size() + "条新闻", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(NewsListActivity.this, "加载失败,换个关键词再试试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewsListActivity.this, "加载失败...", Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 用ListView实现下拉刷新
     */
    @Override
    public void onRefresh() {
        intent = getIntent();
        String key = intent.getStringExtra("keyWord");
        String keyWord = encode(key);
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + keyWord + "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        /* 初始化dataList */
        /* 请求api */
        HttpUnit.sendHttpRequest(address,
                new HttpCallBackListener() {
                    @Override
                    public void onFinish(final String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Data> dataList1 = JSONUnit.praseNewsResponse(response);
                                if(dataList1.size() != 0){
                                    if(dataList.size() != 0){
                                        dataAdapter.addMoreItem(dataList1);
                                        dataAdapter.notifyDataSetChanged();
                                    }else {
                                        dataList = dataList1;
                                        dataAdapter = new DataListAdapter(NewsListActivity.this, R.layout.data_item_layout, dataList1);
                                        listView.setAdapter(dataAdapter);
                                    }
                                }
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(NewsListActivity.this, "更新了" + dataList1.size() + "条新闻", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(NewsListActivity.this, "刷新失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 用ListView实现上拉加载,当滑动状态发生改变的时候执行
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            //当不滚动的时候
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                //判断是否是最底部
                if((view.getLastVisiblePosition() + 1) == view.getCount()){
                    if(!isLoading){
                        //设置刷新界面可见
                        loadmoreView.setVisibility(View.VISIBLE);
                       //dataAdapter.notifyDataSetChanged();
                        //不处于加载状态的话对其进行加载
                        isLoading = true;

                    }
                    refreshNewsList();
                }
                break;
        }
    }

    /** 正在滑动的时候执行 */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * 上拉刷新新闻列表
     */
    private void refreshNewsList(){
        intent = getIntent();
        String key = intent.getStringExtra("keyWord");
        final String keyWord = encode(key);
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + keyWord + "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        /* 初始化dataList */
        /* 请求api */
        HttpUnit.sendHttpRequest(address,
                new HttpCallBackListener() {
                    @Override
                    public void onFinish(final String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ArrayList<Data> dataList1 = JSONUnit.praseNewsResponse(response);
                                if(dataList1.size() != 0){
                                    if(dataList.size() != 0){
                                        dataAdapter.addMoreItem(dataList1);
                                        dataAdapter.notifyDataSetChanged();
                                    }else {
                                        dataList = dataList1;
                                        dataAdapter = new DataListAdapter(NewsListActivity.this, R.layout.data_item_layout, dataList1);
                                        listView.setAdapter(dataAdapter);
                                    }
                                }
                                loadComplete();//刷新结束
                                Toast.makeText(NewsListActivity.this, "更新了" + dataList1.size() + "条新闻", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewsListActivity.this, "刷新失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                    }
                });
    }

    /** 控件点击事件 */
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.image_view_back:
                editText3.setText("");
                finish();
                break;
            case R.id.image_view_image_search3:
                if(editText3.getText().toString() != null){
                    String keyWord = editText3.getText().toString();
                    intent.putExtra("keyWord", keyWord);
                    setSearchVerticalDataList(keyWord);
                }
                break;
            case R.id.image_view_delete3:
                if(editText3.getText().toString() != null){
                    editText3.setText("");
                    imageViewdelete3.setVisibility(View.GONE);
                }
            default:
                break;
        }
    }

    /**
     * ListView子项的点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Data data = (Data) dataList.get(position);
        NewsDataActivity.actionStart(NewsListActivity.this, data);
    }

    /**
     *  启动新闻列表界面的活动
     */
    public static void actionStart(Context context, String keyWord){
        Intent intent = new Intent(context, NewsListActivity.class);
        intent.putExtra("keyWord", keyWord);
        context.startActivity(intent);
    }

    /**
     * 编码
     */
    private String encode(String string){
        String str = new String();
        try {
            str = URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 解码
     */
    private String decode(String code){
        String str = new String();
        try {
           str = URLDecoder.decode(code, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 加载完成
     */
    public void loadComplete()
    {
        loadmoreView.setVisibility(View.GONE);//设置刷新界面不可见
        isLoading = false;//设置正在刷新标志位false
        NewsListActivity.this.invalidateOptionsMenu();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        imageViewdelete3.setVisibility(View.GONE);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        imageViewdelete3.setVisibility(View.VISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
