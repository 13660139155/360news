package com.example.a360news.unit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * 网络工具包
 * Created by asus on 2018/4/22.
 */

public class HttpUnit{

    /**
     *  发起网络请求
     * @param address 发起网络请求
     * @param httpCallBackListener  发起网络请求的回调接口
     */
    public static void sendHttpRequest(final String address, final HttpCallBackListener httpCallBackListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL(address);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.setReadTimeout(6000);
                    httpURLConnection.setDoInput(true);
                    //httpURLConnection.setDoOutput(true);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        response.append(line);
                    }
                    if(httpCallBackListener != null){
                        //回调
                        httpCallBackListener.onFinish(response.toString());
                    }
                } catch (MalformedURLException e) {
                    if(httpCallBackListener != null){
                        //回调
                        httpCallBackListener.onError(e);
                    }
                } catch (ProtocolException e) {
                    if(httpCallBackListener != null){
                        httpCallBackListener.onError(e);
                    }
                } catch (IOException e) {
                    if(httpCallBackListener != null){
                        httpCallBackListener.onError(e);
                    }
                }finally {
                    if(httpURLConnection != null){
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 向网络请求图片
     * @param imageUrlList 图片合集的url
     * @return Bitmap数组
     */
    public static ArrayList<Bitmap> getImageBitmap(ArrayList<String> imageUrlList){
        ArrayList<Bitmap> arrayImageList = new ArrayList<>();
        URL imgUrl = null;
        Bitmap bitmap = null;
        Bitmap newBitmap = null;
        try {
            for(int i = 0; i < imageUrlList.size(); i++){
                String url = imageUrlList.get(i);
                imgUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
                arrayImageList.add(bitmap);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayImageList;
    }

    /**
     * 向网络请求一张图片
     * @param imageUrl 图片的url
     * @return Bitmap
     */
    public static Bitmap getOneImageBitmap(String imageUrl){
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
