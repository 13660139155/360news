package com.example.a360news.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a360news.MainActivity;
import com.example.a360news.MyApplication;
import com.example.a360news.R;
import com.example.a360news.db.SPFDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.keep.Temp;
import com.example.a360news.unit.DataRecyclerAdapter;
import com.example.a360news.unit.HttpCallBackListener;
import com.example.a360news.unit.HttpUnit;
import com.example.a360news.unit.JSONUnit;
import com.example.a360news.unit.Unitity;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_frag, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_dataList_frag);
        dataList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setOnRefreshListener(this);
        if(Temp.IS_STARTACTIVITY == 1){
            Temp.IS_STARTACTIVITY = 0;
            if(MainActivity.IS_NETWORK_AVAILABLE){
                showProgressDialog(getActivity());
                setVerticalDataList();
            }else{
                ArrayList<Data> dataList2 = SPFDatabase.extractData("data");//新闻数据数据
                if(dataList2 != null){
                    showNewsFormDatabase(dataList2);
                }
            }
        }else {
            ArrayList<Data> dataList2 = SPFDatabase.extractData("data");//新闻数据数据
            if(dataList2 != null){
                showNewsFormDatabase(dataList2);
            }
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
        return view;
    }

    /**
     * 实现下拉刷新RecyclerView列表,监听刷新事件
     */
    @Override
    public void onRefresh() {
        String[] keyWords = {"%E8%A6%81%E9%97%BB", "%E5%A4%B4%E6%9D%A1", "%E7%83%AD%E7%82%B9", "%E6%96%B0%E9%97%BB", "%E8%85%BE%E8%AE%AF"};
        String keyWord = keyWords[(int)(Math.random()*10)%5];
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + keyWord + "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
       // String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&uid=5816621612_5816621612&contentType=1&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        HttpUnit.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(final String response) {
                //缓存数据
                SPFDatabase.preferenceData("data", response);
                if(getActivity() == null){
                    return;
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SPFDatabase.preferenceData("data", response);
                            ArrayList<Data> dataList3 = JSONUnit.praseNewsResponse(response);
                            if(dataList3 != null){
                                dataRecyclerAdapter.addItem(dataList3);
                                dataRecyclerAdapter.notifyDataSetChanged();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(MyApplication.getContext(), "更新了" + dataList3.size() + "条新闻", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MyApplication.getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
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
        String[] keyWords = {"%E8%A6%81%E9%97%BB", "%E5%A4%B4%E6%9D%A1", "%E7%83%AD%E7%82%B9", "%E6%96%B0%E9%97%BB", "%E8%85%BE%E8%AE%AF"};
        String keyWord = keyWords[(int) (Math.random() * 10) % 5];
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + keyWord + "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
       // String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&uid=5816621612_5816621612&contentType=1&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        HttpUnit.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(final String response) {
                //缓存数据
                SPFDatabase.preferenceData("data", response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SPFDatabase.preferenceData("data", response);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ArrayList<Data> dataList3 = JSONUnit.praseNewsResponse(response);
                        if (dataList3 != null) {
                            dataRecyclerAdapter.addMoreItem(dataList3);
                            dataRecyclerAdapter.changeMoreStatus(DataRecyclerAdapter.PULLUP_LOAD_MORE);
                            dataRecyclerAdapter.notifyDataSetChanged();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MyApplication.getContext(), "更新了" + dataList3.size() + "条新闻", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyApplication.getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
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
        String[] keyWords = {"%E8%A6%81%E9%97%BB", "%E5%A4%B4%E6%9D%A1", "%E7%83%AD%E7%82%B9", "%E6%96%B0%E9%97%BB", "%E8%85%BE%E8%AE%AF"};
        String keyWord = keyWords[(int)(Math.random()*10)%5];
        String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&kw=" + keyWord + "&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        //String address = "http://120.76.205.241:8000/news/toutiao?pageToken=0&uid=5816621612_5816621612&contentType=1&apikey=XdRGP2cPPTxE6WRTssnh4jC7HJLcSdevBsgnYowEbnS321J7QzZBBg6OZe6ATiIu";
        /* 初始化dataList */
        /* 请求api */
        HttpUnit.sendHttpRequest(address,
        new HttpCallBackListener() {
            @Override
            public void onFinish(final String response) {
                if(getActivity() == null){
                    closeProgressDialog();
                    return;
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dataList = JSONUnit.praseNewsResponse(response);
                            if(dataList.size() != 0){
                                dataRecyclerAdapter = new DataRecyclerAdapter(dataList);
                                linearLayoutManager = new LinearLayoutManager(MyApplication.getContext());
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                recyclerView.setAdapter(dataRecyclerAdapter);
                                Toast.makeText(getActivity(), "加载完成", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), "加载失败...", Toast.LENGTH_SHORT).show();
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

}
