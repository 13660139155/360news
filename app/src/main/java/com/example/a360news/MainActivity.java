package com.example.a360news;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a360news.Fragment.MineFragment;
import com.example.a360news.Fragment.NewsFragment;
import com.example.a360news.db.FileDatabase;
import com.example.a360news.db.SPFDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.json.Label;
import com.example.a360news.keep.Temp;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.example.a360news.MainActivity.IS_NETWORK_AVAILABLE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        TextWatcher{

    public static boolean IS_NETWORK_AVAILABLE = true;//是否有网络
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private static final String TAG = "MainActivity";
    DrawerLayout drawerLayout;//滑动菜单
    NavigationView navigationView;//左菜单
    ActionBar actionBar;
    Toolbar toolbarMain;
    EditText editText;//搜索框
    EditText navEditText;
    ImageView navSearch;
    ImageView imageViewDelete;//输入框内的删除键
    ImageView imageViewTabNews;
    ImageView imageViewTabMine;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbarMain = (Toolbar)findViewById(R.id.tool_bar1);
        drawerLayout = (DrawerLayout)findViewById(R.id.draw_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        editText = (EditText)findViewById(R.id.edit_view_edit);
        imageViewTabMine = (ImageView)findViewById(R.id.image_view_tab_mine);
        imageViewTabNews = (ImageView)findViewById(R.id.image_view_tab_news_selected);
        navEditText = (EditText)findViewById(R.id.edit_view_edit2);
        navSearch = (ImageView)findViewById(R.id.image_view_image_search2);
        imageViewDelete = (ImageView)findViewById(R.id.image_view_delete2);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();

        editText.setOnClickListener(this);
        navSearch.setOnClickListener(this);
        navEditText.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);
        navEditText.addTextChangedListener(this);//监听editview里的文本改变事件
        setSupportActionBar(toolbarMain);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.atm);
        }
        navigationView.setNavigationItemSelectedListener(this);
        imageViewTabMine.setOnClickListener(this);
        imageViewTabNews.setOnClickListener(this);
        registerReceiver(networkChangeReceiver, intentFilter);
        replaceFragment(new NewsFragment());
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
        switch ((menuItem.getItemId())){
            case R.id.item_entertainment:
               NewsListActivity.actionStart(MainActivity.this, "娱乐");
                break;
            case R.id.item_film:
                NewsListActivity.actionStart(MainActivity.this, "电影");
                break;
            case R.id.item_finance:
                NewsListActivity.actionStart(MainActivity.this, "财经");
                break;
            case R.id.item_game:
                NewsListActivity.actionStart(MainActivity.this, "游戏");
                break;
            case R.id.item_internatonal:
                NewsListActivity.actionStart(MainActivity.this, "国际");
                break;
            case R.id.item_life:
                NewsListActivity.actionStart(MainActivity.this, "生活");
                break;
            case R.id.item_military:
                NewsListActivity.actionStart(MainActivity.this, "军事");
                break;
            case R.id.item_NBA:
                NewsListActivity.actionStart(MainActivity.this, "NBA");
                break;
            case R.id.item_newLife:
                NewsListActivity.actionStart(MainActivity.this,  "新时代");
                break;
            case R.id.item_science:
                NewsListActivity.actionStart(MainActivity.this,  "科技");
                break;
            case R.id.item_car:
                NewsListActivity.actionStart(MainActivity.this,  "汽车");
                break;
            case R.id.item_society:
                NewsListActivity.actionStart(MainActivity.this, "社会");
                break;
            case R.id.item_sport:
                NewsListActivity.actionStart(MainActivity.this,  "运动");
                break;
            case R.id.item_fashion:
                NewsListActivity.actionStart(MainActivity.this,  "时尚");
                break;
            case R.id.item_tiyu:
                NewsListActivity.actionStart(MainActivity.this,  "体育");
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
            case R.id.image_view_tab_news_selected:
                replaceFragment(new NewsFragment());
                actionBar.show();
                imageViewTabNews.setImageResource(R.drawable.tab_news_selected);
                imageViewTabMine.setImageResource(R.drawable.tab_mine);
                break;
            case R.id.image_view_tab_mine:
                replaceFragment(new MineFragment());
                actionBar.hide();
                imageViewTabMine.setImageResource(R.drawable.tab_mine_selected);
                imageViewTabNews.setImageResource(R.drawable.tab_news);
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

    /**
     * 启动主活动
     * @param context 上下文
     */
    public static void actionStart(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 动态添加碎片
     * @param fragment
     * t 要添加的碎片
     */
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment);
        fragmentTransaction.commit();
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
