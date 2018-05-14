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

    private int i = 0;
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
                    dataList = SPFDatabase.extractData(keyWord);//被缓存的新闻数据数据
                    if(dataList.size() != 0){
                        showNewsFormDatabase(dataList);
                    }
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
            setSearchVerticalDataList(keyWord);
        }
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
        final String keyWord = intent.getStringExtra("keyWord");
        String word = keyWord;
        if(is == true){
            word = refreshLabel(keyWord);
        }else {
            for(int j = 0; j < i; j++){
                word += word;
            }
            i++;
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
        }else {
            for(int j = 0; j < i; j++){
                word+=word;
            }
            i++;
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
                                ArrayList<Data> dataList2 = JSONUnit.praseNewsResponse(response);
                                if(dataList2.size() != 0){
                                    dataAdapter.addItem(dataList2);
                                    dataAdapter.notifyDataSetChanged();
                                    Toast.makeText(NewsListActivity.this, "发现了" + dataList2.size() + "条新闻", Toast.LENGTH_SHORT).show();
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
                words = new String[]{keyWord, "爱生活", "品味生活", "生活的含义", "谈生活", "活在当下", "今日新鲜事", "美好的生活", "一天的生活", "新生活", "热爱生活", "珍惜生活", "享受生活"};
                if(Temp.q == words.length){
                    Temp.q = 0;
                }
                key = words[Temp.q];
                Temp.q++;
               break;
            case "汽车":
                words = new String[]{keyWord, "爱汽车", "汽车之家", "推荐汽车", "谈汽车", "汽车趣闻", "汽车那些事", "汽车之家", "汽车要闻", "如何保养汽车", "汽车迷", "汽车零件", "汽车保养", "汽车资讯"};
                if(Temp.w == words.length){
                    Temp.w = 0;
                }
                key = words[Temp.w];
                Temp.w++;
                break;
            case "新时代":
                words = new String[]{keyWord, "人民新时代", "新世纪", "习近平谈世纪", "人民与新时代", "新时代的变化", "新世纪的生活", "谱写新天地", "翻天覆地地变化", "新时代的我们", "迈向新生活", "迈向新时代", "谈新时代"};
                if(Temp.e == words.length){
                    Temp.e = 0;
                }
                key = words[Temp.e];
                Temp.e++;
                break;
            case "科技":
                words = new String[]{keyWord, "爱科学", "科学技术", "科学家们", "科学那些事", "科学新发现", "探索科学", "科学网", "环球科学", "科学研究", "走进科学", "科学新发现", "生活中的科学"};
                if(Temp.r == words.length){
                    Temp.r = 0;
                }
                key = words[Temp.r];
                Temp.r++;
                break;
            case "娱乐":
                words = new String[]{keyWord, "爱娱乐", "娱乐前线", "娱乐八卦", "娱乐频道", "搜狐娱乐", "娱乐新闻", "娱乐看点", "娱乐星天地", "娱乐圈", "娱乐资讯", "娱乐最前线", "娱乐明星"};
                if(Temp.t == words.length){
                    Temp.t = 0;
                }
                key = words[Temp.t];
                Temp.t++;
                break;
            case "运动":
                words = new String[]{keyWord, "爱运动", "运动那些事", "运动要注意什么", "健康运动", "运动减肥", "运动的好处", "跑步", "运动健身", "体育健身", "运动新闻", "运动资讯"};
                if(Temp.y == words.length){
                    Temp.y = 0;
                }
                key = words[Temp.y];
                Temp.y++;
                break;
            case "财经":
                words = new String[]{keyWord, "财经前线", "财经报", "财经频道", "今日财经", "财经网", "股票行情", "凤凰财经", "国际财经", "新浪财经", "专家解读财经", "财经资讯", "财经资讯"};
                if(Temp.u == words.length){
                    Temp.u = 0;
                }
                key = words[Temp.u];
                Temp.u++;
                break;
            case "NBA":
                words = new String[]{keyWord, "nba", "NBA比赛", "NBA球星", "NBA新闻", "NBA最前线", "NBA赛季", "NBA搜狐", "腾讯NBA", "NBA那些事", "NBA资讯", "nba资讯"};
                if(Temp.i == words.length){
                    Temp.i = 0;
                }
                key = words[Temp.i];
                Temp.i++;
                break;
            case "军事":
                words = new String[]{keyWord, "军迷", "军人", "军事爱好者", "军事前沿", "军事报道", "军事纪实", "军事频道", "军事装备", "中国军事", "环球军事", "军事新闻", "军事资讯"};
                if(Temp.o == words.length){
                    Temp.o = 0;
                }
                key = words[Temp.o];
                Temp.o++;
                break;
            case "国际":
                words = new String[]{keyWord, "国际焦点", "国际事件", "国际新闻", "国际时讯", "国际贸易", "国际在线", "国际最新消息", "国际频道", "国际网", "国际资讯"};
                if(Temp.p == words.length){
                    Temp.p = 0;
                }
                key = words[Temp.p];
                Temp.p++;
                break;
            case "电影":
                words = new String[]{keyWord, "爱电影", "推荐电影", "电影新闻", "电影最前线", "电影迷", "电影爱好", "电影快讯", "腾讯电影新闻", "影视行业新闻", "电影资讯"};
                if(Temp.a == words.length){
                    Temp.a = 0;
                }
                key = words[Temp.a];
                Temp.a++;
                break;
            case "体育":
                words = new String[]{keyWord, "体育前线", "体育比赛", "爱体育", "体育新闻", "新浪体育", "腾讯体育", "体育明星", "搜狐体育", "国际体育", "体育频道", "体育资讯"};
                if(Temp.s == words.length){
                    Temp.s = 0;
                }
                key = words[Temp.s];
                Temp.s++;
                break;
            case "游戏":
                words = new String[]{keyWord, "爱游戏", "推荐游戏", "游戏人生", "新浪游戏", "腾讯游戏", "搜狐游戏", "游戏新闻", "游戏频道", "游戏资讯", "打游戏", "玩游戏"};
                if(Temp.d == words.length){
                    Temp.d = 0;
                }
                key = words[Temp.d];
                Temp.d++;
                break;
            case "时尚":
                words = new String[]{keyWord, "爱时尚", "时尚生活", "时尚芭莎", "时尚新闻", "时尚潮流", "时尚频道", "时尚穿搭", "环球时尚", "腾讯时尚", "时尚资讯"};
                if(Temp.f == words.length){
                    Temp.f =0;
                }
                key = words[Temp.f];
                Temp.f++;
                break;
            case "社会":
                words = new String[]{keyWord, "社会热点", "社会焦点", "社会新闻", "社会热点新闻", "社会资讯", "社会频道", "社会事件", "中新社会", "民生社会", "社会访谈", "社会民生"};
                if(Temp.g == words.length){
                    Temp.g = 0;
                }
                key = words[Temp.g];
                Temp.g++;
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
        listView.addFooterView(loadmoreView,null,false);//加入刷新布局
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
