package com.example.a360news.unit;

import com.example.a360news.db.SPFDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.json.NewsAllData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 解析JSON数据，返回新闻Data
 * Created by asus on 2018/4/22.
 */

public class JSONUnit {

    /**
     *  把JSON数据解析成JSONArray
     * @param jsonResponse 网络请求返回的JSON数据
     * @return 返回Data
     */
    public static ArrayList<Data> praseNewsResponse(String jsonResponse){
        ArrayList<Data> dataList = null;
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        JSONArray jsonArray2;
        Data data = null;
        JSONObject jsonObject2;
        ArrayList<String> imageUrlList;
        StringBuffer stringBuffer;
        try {
            dataList = new ArrayList<>();
            jsonObject = new JSONObject(jsonResponse);
            jsonArray = jsonObject.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++){
                jsonObject2 = jsonArray.getJSONObject(i);
                data = new Data();
                imageUrlList = new ArrayList<>();
                jsonArray2 = jsonObject2.getJSONArray("imageUrls");
                for(int j = 0; j < jsonArray2.length(); j++){
                    String str = (String) jsonArray2.get(0);
                    imageUrlList.add(str);
                }
                data.setNewsCommentCount(jsonObject2.getString("commentCount"));
                data.setNewsPublishDataStr(jsonObject2.getString("publishDateStr"));
                data.setNewsPublisher(jsonObject2.getString("posterScreenName"));
                data.setNewsTitle(jsonObject2.getString("title"));
                data.setNewsUrl(jsonObject2.getString("url"));
//                stringBuffer = new StringBuffer(jsonObject2.getString("content"));
//                int size = data.getNewsTitle().length()+5;
//                stringBuffer.delete(0, size);
//                data.setNewsContent(stringBuffer.toString());
                data.setNewsContent(jsonObject2.getString("content"));
//                data.setNewsDisLikeCount(jsonObject2.getString("dislikeCount"));
//                data.setNewsLikeCount(jsonObject2.getString("likeCount"));
//                data.setNewsViewCount(jsonObject2.getString("viewCount"));
                data.setNewsId(jsonObject2.getString("id"));
                data.setNewsImageUrls(imageUrlList);
                dataList.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

}
