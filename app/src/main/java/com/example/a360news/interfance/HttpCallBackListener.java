package com.example.a360news.interfance;

/**
 * Created by asus on 2018/4/22.
 */

/**
 * 发起网络请求的回调接口
 */
public interface HttpCallBackListener {
    /**
     * 在这里对子线程返回的数据进行相应的操作
     * @param response  返回的数据
     */
    void onFinish(String response);

    /**
     * 在这里对子线程返回的异常进行相应的操作
     * @param e 返回的异常
     */
    void onError(Exception e);
}
