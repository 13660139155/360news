package com.example.a360news.unit;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 一些工具
 * Created by asus on 2018/4/25.
 */

public class Unitity extends AppCompatActivity {

    private static ProgressDialog progressDialog;

    /**
     * 显示加载框
     * @param context 上下文
     */
    public static void showProgressDialog(Context context){
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
    public static void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * 加载WedView布局
     * @param address 网址
     * @param webView WebView布局
     */
  public static void loadWedView(String address, WebView webView){
      webView.getSettings().setJavaScriptEnabled(true);
      webView.setWebViewClient(new WebViewClient());
      webView.loadUrl(address);
  }

    /**
     * 缩放图片
     * @param bm 要缩放图片
     * @param newWidth 宽度
     * @param newHeight 高度
     * @return 处理后的图片
     */
    public static  Bitmap  scaleImage(Bitmap bm, int newWidth, int newHeight){
        if (bm == null){
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);
//        if (bm != null & !bm.isRecycled()){
//            bm.recycle();//销毁原图片
//            bm = null;
//        }
        return newbm;
    }


    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
}
