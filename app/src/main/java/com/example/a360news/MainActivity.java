package com.example.a360news;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a360news.fragment.NewsFragment;
import com.example.a360news.adapter.FragmentAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        TextWatcher{

    public static boolean IS_NETWORK_AVAILABLE = true;//是否有网络
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private static final String TAG = "MainActivity";
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;//页面
    private String[] titles;
    private ArrayList<String> titleArrayList;//标题
    DrawerLayout drawerLayout;//滑动菜单
    NavigationView navigationView;//左菜单
    ActionBar actionBar;
    Toolbar toolbarMain;
    EditText editText;//搜索框
    EditText navEditText;
    ImageView navSearch;
    ImageView imageViewDelete;//输入框内的删除键
    ViewPager viewPager;
    TabLayout tabLayout;
    LinearLayout llNavHeader;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
        setContentView(R.layout.activity_main);

        toolbarMain = findViewById(R.id.tool_bar1);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.navigation_view);
        editText = findViewById(R.id.edit_view_edit);
        llNavHeader = (LinearLayout) navigationView.getHeaderView(0);
        navEditText = llNavHeader.findViewById(R.id.edit_view_edit2);
        navSearch = llNavHeader.findViewById(R.id.image_view_image_search2);
        imageViewDelete = llNavHeader.findViewById(R.id.image_view_delete2);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        editText.setOnClickListener(this);
        navSearch.setOnClickListener(this);
        navEditText.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);
        navEditText.addTextChangedListener(this);//监听editview里的文本改变事件
        setSupportActionBar(toolbarMain);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.atm);
        }
        navigationView.setNavigationItemSelectedListener(this);

        fragmentArrayList = new ArrayList<>();
        titleArrayList = new ArrayList<>();
        titles = new String[]{"要闻", "生活", "汽车", "新时代", "科技", "娱乐", "运动", "财经", "NBA", "军事", "国际", "电影", "体育", "游戏", "社会", "时尚"};
        for(int i = 0; i < titles.length; i++){
            fragmentArrayList.add(NewsFragment.newFragment(titles[i]));
            titleArrayList.add(titles[i]);
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
        }
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentArrayList, titleArrayList);
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来
        //MODE_SCROLLABLE可滑动的展示
        //MODE_FIXED固定展示
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.setOffscreenPageLimit(15);//预加载页数

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbarMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //获取mDrawerLayout中的第一个子布局，也就是布局中的RelativeLayout
                //获取抽屉的view
                View mContent = drawerLayout.getChildAt(0);
                float scale = 1 - slideOffset;
                float endScale = 0.8f + scale * 0.2f;
                float startScale = 1 - 0.3f * scale;

                //设置左边菜单滑动后的占据屏幕大小
                drawerView.setScaleX(startScale);
                drawerView.setScaleY(startScale);
                //设置菜单透明度
                drawerView.setAlpha(0.6f + 0.4f * (1 - scale));

                //设置内容界面水平和垂直方向偏转量
        //在滑动时内容界面的宽度为 屏幕宽度减去菜单界面所占宽度
        mContent.setTranslationX(drawerView.getMeasuredWidth() * (1 - scale));
        //设置内容界面操作无效（比如有button就会点击无效）
        mContent.invalidate();
        //设置右边菜单滑动后的占据屏幕大小
        mContent.setScaleX(endScale);
        mContent.setScaleY(endScale);
    }
};
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
    }

    /**
     * 滑动菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * 左菜单item点击事件
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.item_keep:
                FavoriteActivity.actionStart(MainActivity.this);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 处理各种控件点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_view_edit:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.image_view_image_search:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.image_view_image_search2:
                if(navEditText.getText().toString() != null){
                    NewsListActivity.actionStart(MainActivity.this, navEditText.getText().toString());
                }
                break;
            case R.id.image_view_delete2:
               if(navEditText.getText().toString() != null){
                   navEditText.setText("");
                   imageViewDelete.setVisibility(View.GONE);
               }
                break;
            default:
                break;
        }
    }

    /** 文本改变前 */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
       imageViewDelete.setVisibility(View.GONE);
    }

    /** 文本改变时 */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
       imageViewDelete.setVisibility(View.VISIBLE);
    }

    /** 文本改变后 */
    @Override
    public void afterTextChanged(Editable s) {
    }


    /**
     * 接受系统网络广播
     * Created by asus on 2018/4/27.
     */
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                IS_NETWORK_AVAILABLE = true;
            }else{
                IS_NETWORK_AVAILABLE = false;
                Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
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
     * 关闭软键盘
     * @param mEditText 输入框
     * @param mContext 上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

}
