package com.example.a360news.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a360news.MainActivity;
import com.example.a360news.MyApplication;
import com.example.a360news.R;
import com.example.a360news.db.SPFDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.keep.Temp;
import com.example.a360news.adapter.DataRecyclerAdapter;
import com.example.a360news.interfance.HttpCallBackListener;
import com.example.a360news.unit.HttpUnit;
import com.example.a360news.unit.JSONUnit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 新闻主页面碎片
 * Created by asus on 2018/4/25.
 */

public class NewsFragment extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;//新闻列表
    SwipeRefreshLayout swipeRefreshLayout;//下拉刷新控件
    DataRecyclerAdapter dataRecyclerAdapter;//新闻列表的适配器
    ArrayList<Data> dataList;//新闻数据集合
    LinearLayoutManager linearLayoutManager;//RecyclerView的方向管理

    private ProgressDialog progressDialog;
    private int lastVisibleItem;//新闻列表的最后一项
    private final static String KEY = "key";
    private String key;
    private boolean IS_FIRST = true;//是否第一次启动主活动,默认是
    private boolean IS_VISIBLE = false;//Fragment是否可见
    private boolean IS_LOAD = false;//View是否加载
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_frag, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_dataList_frag);
        dataList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setOnRefreshListener(this);

        Bundle bundle = getArguments();
        if (bundle != null){
            key = bundle.getString(KEY);
        }

        /** 用recyclerview实现上拉加载功能 */
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //当不滚动的时候，判断是否是最底部
                if(newState == RecyclerView.SCROLL_STATE_IDLE  && (lastVisibleItem + 1) == dataRecyclerAdapter.getItemCount()){
                    dataRecyclerAdapter.changeMoreStatus(DataRecyclerAdapter.LOADING_MORE);
                    refreshNewsData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
        IS_LOAD = true;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(IS_VISIBLE && IS_LOAD){
            loadData(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IS_VISIBLE = false;
        IS_LOAD = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            IS_VISIBLE = true;
        }
        if(IS_VISIBLE && IS_LOAD){
            loadData(true);
        }
    }

    /**
     * 当Fragment可见，并且View已经加载才加载数据
     * @param isLoad
     */
    private void loadData(boolean isLoad){
        if(isLoad){
            if(IS_FIRST){//是否第一次进入
                IS_FIRST = false;
                if(MainActivity.IS_NETWORK_AVAILABLE){
                    showProgressDialog(getActivity());
                    ArrayList<Data> dataList2 = SPFDatabase.extractData(key);//新闻数据数据
                    showNewsFormDatabase(dataList2);
                    setVerticalDataList();
                }else{
                    ArrayList<Data> dataList2 = SPFDatabase.extractData(key);//新闻数据数据
                    if(dataList2.size() != 0){
                        showNewsFormDatabase(dataList2);
                    }else {
                        Toast.makeText(getActivity(), "请打开网络，获取新闻失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                ArrayList<Data> dataList2 = SPFDatabase.extractData(key);//新闻数据数据
                if(dataList2.size() != 0){
                    showNewsFormDatabase(dataList2);
                }
            }
        }
    }

    /**
     * 实现下拉刷新RecyclerView列表,监听刷新事件
     */
    @Override
    public void onRefresh() {
        String keyWord = refreshKey(key);
        String address = "http://120.76.205.241:8000/news/qihoo?kw=" + keyWord + "&site=qq.com&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        HttpUnit.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(final String response) {
                //缓存数据
                SPFDatabase.preferenceData(key, response);
                if(getActivity() == null){
                    return;
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Data> dataList3 = JSONUnit.praseNewsResponse(response);
                            if(dataList3 != null){
                                dataRecyclerAdapter.addItem(dataList3);
                                dataRecyclerAdapter.notifyDataSetChanged();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            Snackbar.make(recyclerView, "更新了" + dataList3.size() + "条新闻", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                if(getActivity() == null){
                    return;
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(recyclerView, "更新了" + "刷新失败", Snackbar.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 刷新新闻数据
     */
    private void refreshNewsData() {
        String keyWord = refreshKey(key);
        String address = "http://120.76.205.241:8000/news/qihoo?kw=" + keyWord + "&site=qq.com&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        HttpUnit.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(final String response) {
                //缓存数据
                SPFDatabase.preferenceData(key, response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Data> dataList3 = JSONUnit.praseNewsResponse(response);
                        if (dataList3 != null) {
                            dataRecyclerAdapter.addMoreItem(dataList3);
                            dataRecyclerAdapter.changeMoreStatus(DataRecyclerAdapter.PULLUP_LOAD_MORE);
                            dataRecyclerAdapter.notifyDataSetChanged();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(recyclerView, "更新了" + dataList3.size() + "条新闻", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(recyclerView, "刷新失败", Snackbar.LENGTH_SHORT).show();
                        dataRecyclerAdapter.changeMoreStatus(DataRecyclerAdapter.NO_MORE_DATA);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                e.printStackTrace();
            }
        });
    }

    /**
     * 从本地缓存新闻
     */
    public void showNewsFormDatabase(List<Data> datas){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        dataRecyclerAdapter = new DataRecyclerAdapter(datas);
        recyclerView.setAdapter(dataRecyclerAdapter);
    }

    /**
     * 设置纵向新闻列表滚动
     */
    public void setVerticalDataList(){
        String keyWord = refreshKey(key);
        String address = "http://120.76.205.241:8000/news/qihoo?kw=" + keyWord + "&site=qq.com&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        /* 初始化dataList */
        /* 请求api */
        HttpUnit.sendHttpRequest(address,
        new HttpCallBackListener() {
            @Override
            public void onFinish(final String response) {
                SPFDatabase.preferenceData(key, response);
                if(getActivity() == null){
                    closeProgressDialog();
                    return;
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dataList = JSONUnit.praseNewsResponse(response);
                            if(dataList.size() != 0){
                                dataRecyclerAdapter.addItem(dataList);
                                dataRecyclerAdapter.notifyDataSetChanged();
                                Snackbar.make(recyclerView, "发现了" + dataList.size() + "条新闻", Snackbar.LENGTH_SHORT);
                            }else {
                                Toast.makeText(getActivity(), "请求太频繁,稍后再试", Toast.LENGTH_SHORT).show();
                            }
                            closeProgressDialog();
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                if(getActivity() == null){
                    closeProgressDialog();
                    return;
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Snackbar.make(recyclerView, "更新了" + "加载失败", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                e.printStackTrace();
            }
        });
    }

    /**
     * 显示加载框
     * @param context 上下文
     */
    public void showProgressDialog(Context context){

        if(progressDialog == null){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭加载框
     */
    public void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /** 标签刷新 */
    private String refreshKey(String keyWord){
        String key = keyWord;
        String[] words;
        switch(keyWord){
            case "要闻":
                words = new  String[]{"今日头条", "今日要闻", "今日资讯", "腾讯新闻", "新闻", "腾讯要闻", "头条", "腾讯资讯", "新浪新闻", "今日关注", "新浪要闻", "新闻最前线", "新浪资讯", "搜狐新闻", "新闻焦点", "搜狐要闻", "资讯", "搜狐资讯", "新闻快报", "新闻资讯", "要闻"};
                if(Temp.h == words.length){
                    Temp.h = 0;
                }
                key = words[Temp.h];
                Temp.h++;
                break;
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
        return encode(key);
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
     * 创建一个NewsFragment实例
     * @param key
     * @return
     */
    public static Fragment newFragment(String key){
        Fragment fragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }
}
