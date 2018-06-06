package com.example.a360news.unit;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.a360news.R;

/**
 * Created by ASUS on 2018/6/4.
 */

public class MyPopupWindow extends PopupWindow {

    private View mView;
    TextView textViewLocation;
    TextView textViewShared;

    public MyPopupWindow(Context context, int width, int height, View.OnClickListener onClick) {
        super(context);

        mView = LayoutInflater.from(context).inflate(R.layout.popup_window, null);
        textViewLocation = (TextView) mView.findViewById(R.id.text_preserve);
        textViewShared = (TextView) mView.findViewById(R.id.text_shared);

        textViewLocation.setOnClickListener(onClick);
        textViewShared.setOnClickListener(onClick);

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(width);
        //设置PopupWindow弹出窗体的高
        this.setHeight(height);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.PopupWindow);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xfff);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }

    /**
     * 让弹出窗显示在某控件上面
     * @param v 某控件
     */
    public void showOnView(View v){
        this.showAsDropDown(v);
    }

}
