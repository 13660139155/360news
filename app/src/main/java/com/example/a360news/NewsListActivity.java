package com.example.a360news;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a360news.db.SPFDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.keep.Temp;
import com.example.a360news.unit.DataListAdapter;
import com.example.a360news.unit.DataRecyclerAdapter;
import com.example.a360news.unit.HttpCallBackListener;
import com.example.a360news.unit.HttpUnit;
import com.example.a360news.unit.JSONUnit;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NewsListActivity extends AppCompatActivity
        implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener,
        TextWatcher{

    private static final String TAG = "NewsListActivity";
    private ArrayList dataList = new ArrayList<>();
    private View loadmoreView;
    ImageView imageBack;
    ImageView imageSearch;
    Toolbar toolbarList;
    Intent intent;
    EditText editText3;
    ImageView imageViewdelete3;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    DataListAdapter dataAdapter;
    Boolean isLoading = false;//表示是否正处于加载状态
    boolean is;

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
        String keyWord = intent.getStringExtra("keyWord");
        editText3.setText(keyWord.toString());
        /** 移动光标到后面 */
        Editable able = editText3.getText();
        int position = able.length();
        Selection.setSelection(able,position);
        is = isLabel(keyWord);//判断是不是标签
        if(is == true){
            if(MainActivity.IS_NETWORK_AVAILABLE){
                int key = isFrist(keyWord);//如果是1表示在有网络的时候第一次进入，从网上加载数据，如果不是1则从缓存加载
                if(key == 1){
                    setVerticalDataList(keyWord);
                }else {
                    dataList = SPFDatabase.extractData(keyWord);//被缓存的新闻数据数据
                    if(dataList.size() != 0){
                        showNewsFormDatabase(dataList);
                    }
                }
            }else {
                dataList = SPFDatabase.extractData(keyWord);//新闻数据数据
                if(dataList.size() != 0){
                    showNewsFormDatabase(dataList);
                }
            }
        }else {
            setVerticalDataList(keyWord);
        }
        listView.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout_list);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnScrollListener(this);
    }

    /**
     * 用ListView实现下拉刷新
     */
    @Override
    public void onRefresh() {
        final String keyWord = intent.getStringExtra("keyWord");
        String word = keyWord;
        if(is == true){
            word = refreshLabel(keyWord);
        }
        String key = encode(word);
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + key + "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        /* 初始化dataList */
        /* 请求api */
        HttpUnit.sendHttpRequest(address,
                new HttpCallBackListener() {
                    @Override
                    public void onFinish(final String response) {
                        //缓存数据
                        if(is == true){
                            SPFDatabase.preferenceData(keyWord, response);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Data> dataList1 = JSONUnit.praseNewsResponse(response);
                                if(dataList1.size() != 0){
                                    dataAdapter.addItem(dataList1);
                                    dataAdapter.notifyDataSetChanged();
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
        final String keyWord = intent.getStringExtra("keyWord");
        String word = keyWord;
        if(is == true){
            word = refreshLabel(keyWord);
        }
        String key = encode(word);
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + key + "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        /* 初始化dataList */
        /* 请求api */
        HttpUnit.sendHttpRequest(address,
                new HttpCallBackListener() {
                    @Override
                    public void onFinish(final String response) {
                        //缓存数据
                        if(is == true){
                            SPFDatabase.preferenceData(keyWord, response);
                        }
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
                                    dataAdapter.addMoreItem(dataList1);
                                    dataAdapter.notifyDataSetChanged();
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
                    String text = editText3.getText().toString();
                    intent.putExtra("keyWord", text);
                    String key = encode(text);
                    setVerticalDataList(key);
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
     * 设置纵向新闻列表滚动
     */
    public void setVerticalDataList(final String keyWord){
        String word = keyWord;
        if(is == true){
            word = refreshLabel(keyWord);
        }
        String key = encode(word);
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + key+ "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        /* 初始化dataList */
        /* 请求api */
        HttpUnit.sendHttpRequest(address,
                new HttpCallBackListener() {
                    @Override
                    public void onFinish(final String response) {
                        if(is == true){
                            SPFDatabase.preferenceData(keyWord, response);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataList = JSONUnit.praseNewsResponse(response);
                                if(dataList.size() != 0){
                                    dataAdapter = new DataListAdapter(NewsListActivity.this, R.layout.data_item_layout, dataList);
                                    listView.addFooterView(loadmoreView,null,false);//加入刷新布局
                                    listView.setAdapter(dataAdapter);
                                    Toast.makeText(NewsListActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(NewsListActivity.this, "请求太频繁,稍后再试", Toast.LENGTH_SHORT).show();
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
     * 判断请求的关键字是否属于标签，是则判断标签是否有缓存
     */
    public boolean isLabel(String keyWord){
        boolean is = false;
        switch(keyWord){
            case "生活":
            case "汽车":
            case "新时代":
            case "科技":
            case "娱乐":
            case "运动":
            case "财经":
            case "NBA":
            case "军事":
            case "国际":
            case "电影":
            case "体育":
            case "游戏":
            case "时尚":
            case "社会":
                is = true;
                break;
            default:
                is = false;
                break;
        }
        return is;
    }

    /** 标签刷新 */
    public String refreshLabel(String keyWord){
        String key = keyWord;
        String[] words;
        switch(keyWord){
            case "生活":
                words = new String[]{keyWord, "life", "品味生活"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "汽车":
                words = new String[]{keyWord, "life", "品味生活"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "新时代":
                words = new String[]{keyWord, "newLife", "新世纪"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "科技":
                words = new String[]{keyWord, "science", "科学"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "娱乐":
                words = new String[]{keyWord, "entertainment", "娱乐前线"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "运动":
                words = new String[]{keyWord, "sport", "健身"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "财经":
                words = new String[]{keyWord, "finance", "经济"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "NBA":
                words = new String[]{keyWord, "nba", "NBA比赛"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "军事":
                words = new String[]{keyWord, "military", "军人"};
                key = words[(int) (Math.random() * 10) % 3];
            case "国际":
                words = new String[]{keyWord, "internatonal", "国际事件"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "电影":
                words = new String[]{keyWord, "film", "看电影"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "体育":
                words = new String[]{keyWord, "体育前线", "体育比赛"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "游戏":
                words = new String[]{keyWord, "game", "好玩的游戏"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "时尚":
                words = new String[]{keyWord, "fashion", "时尚生活"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            case "社会":
                words = new String[]{keyWord, "society", "社会焦点"};
                key = words[(int) (Math.random() * 10) % 3];
                break;
            default:
                break;
        }
        return key;
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
        //listView.removeFooterView(loadmoreView);//如果是最后一页的话，则将其从ListView中移出
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

    /**
     * 从本地缓存新闻
     */
    public void showNewsFormDatabase(List<Data> datas){
        dataAdapter = new DataListAdapter(NewsListActivity.this, R.layout.data_item_layout, datas);
        listView.setAdapter(dataAdapter);
    }

    public int isFrist(String keyWord){
        int key = 0;
        switch(keyWord){
            case "生活":
                key = Temp.LIFE;
                if(Temp.LIFE == 1){
                    Temp.LIFE = 0;
                }
                break;
            case "汽车":
                key = Temp.CAR;
                if(Temp.CAR == 1){
                    Temp.CAR = 0;
                }
                break;
            case "新时代":
                key = Temp.NEWLIFE;
                if(Temp.NEWLIFE == 1){
                    Temp.NEWLIFE = 0;
                }
                break;
            case "科技":
                key = Temp.SCIENCE;
                if(Temp.SCIENCE== 1){
                    Temp.SCIENCE = 0;
                }
                break;
            case "娱乐":
                key = Temp.ENTERTAINMENT;
                if(Temp.ENTERTAINMENT == 1){
                    Temp.ENTERTAINMENT = 0;
                }
                break;
            case "运动":
                key = Temp.SPORT;
                if(Temp.SPORT == 1){
                    Temp.SPORT = 0;
                }
                break;
            case "财经":
                key = Temp.FINANCE;
                if(Temp.FINANCE == 1){
                    Temp.FINANCE = 0;
                }
                break;
            case "NBA":
                key = Temp.NBA;
                if(Temp.NBA == 1){
                    Temp.NBA = 0;
                }
                break;
            case "军事":
                key = Temp.MILITARY;
                if(Temp.MILITARY == 1){
                    Temp.MILITARY = 0;
                }
                break;
            case "国际":
                key = Temp.INTERNATIONAL;
                if(Temp.INTERNATIONAL == 1){
                    Temp.INTERNATIONAL = 0;
                }
                break;
            case "电影":
                key = Temp.FILM;
                if(Temp.FILM == 1){
                    Temp.FILM = 0;
                }
                break;
            case "体育":
                key = Temp.TIYU;
                if(Temp.TIYU == 1){
                    Temp.TIYU = 0;
                }
                break;
            case "游戏":
                key = Temp.GAME;
                if(Temp.GAME == 1){
                    Temp.GAME = 0;
                }
                break;
            case "时尚":
                key = Temp.FASHION;
                if(Temp.FASHION == 1){
                    Temp.FASHION = 0;
                }
                break;
            case "社会":
                key = Temp.SOCIETY;
                if(Temp.SOCIETY == 1){
                    Temp.SOCIETY = 0;
                }
                break;
            default:
                break;
        }
        return key;
    }
}
