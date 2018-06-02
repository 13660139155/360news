package com.example.a360news.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a360news.R;
import com.example.a360news.db.FileDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.keep.Temp;
import com.example.a360news.unit.HttpUnit;
import com.example.a360news.unit.Unitity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * ListView的ArrayAdapter
 * Created by asus on 2018/4/23.
 */

public class DataListAdapter extends ArrayAdapter<Data>{

    private ListView listView;
    private int resourceId;
    private ArrayList<Data> arrayList;
    private TreeMap<String, Bitmap> treeMapBitmap;

    public DataListAdapter(Context context, int resource, List<Data> list) {
        super(context, resource);
        this.resourceId = resource;
        this.arrayList = (ArrayList<Data>) list;
        treeMapBitmap = new TreeMap<>();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            if(listView == null){
                listView = (ListView)parent;
            }
            final Data data = getItem(position);
            String url = data.getNewsImageUrls().get(0);
            View view;
            final MyViewHolder viewHolder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new MyViewHolder();
                viewHolder.dataTextView = (TextView)view.findViewById(R.id.text_view_title);
                viewHolder.editorTextView = (TextView)view.findViewById(R.id.text_view_editor);
                viewHolder.viewTextView = (TextView)view.findViewById(R.id.text_view_viewCount);
                viewHolder.dataImageView = (ImageView) view.findViewById(R.id.image_view_news_image);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (MyViewHolder)view.getTag();
            }
            viewHolder.dataTextView.setText(data.getNewsTitle());
            viewHolder.editorTextView.setText(data.getNewsPublisher());
            viewHolder.viewTextView.setText(data.getNewsCommentCount() + "评");
            viewHolder.dataImageView.setImageResource(R.drawable.launch);
            viewHolder.dataImageView.setTag(url);
            Bitmap bitmap = treeMapBitmap.get(data.getNewsImageUrls().get(0));
            Bitmap bitmap2 = Unitity.scaleImage(FileDatabase.loadBitmap(data.getNewsImageUrls().get(0)), 500, 350);
            if(bitmap != null){
                viewHolder.dataImageView.setImageBitmap(bitmap);
            }else if(bitmap2 != null) {
                viewHolder.dataImageView.setImageBitmap(bitmap2);
            }else {
                new AsyncTask<String, Integer, Bitmap>() {

                    private String url;

                    @Override
                    protected Bitmap doInBackground(String... params) {
                        url = params[0];
                        Bitmap bitmap = HttpUnit.getOneImageBitmap(url);
                        Bitmap newBitmap = Unitity.scaleImage(bitmap, 500, 350);
                        if(newBitmap != null){
                            Temp.bitmapUrl.add(url);
                            treeMapBitmap.put(url, newBitmap);
                            FileDatabase.saveBitmap(url, newBitmap);
                        }
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        ImageView imageView = (ImageView)listView.findViewWithTag(url);
                        if(imageView != null && bitmap != null){
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }.execute(url);
            }
            return  view;
        }

    @Override
    public int getCount() {
        return arrayList.size();
    }


    @Nullable
    @Override
    public Data getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  0;
    }

    class MyViewHolder{
          TextView dataTextView;
          TextView editorTextView;
          TextView viewTextView;
          ImageView dataImageView;
    }

    public void addItem(ArrayList<Data> datas){
        ArrayList<Data> list = new ArrayList<>();
        list.addAll(this.arrayList);
        this.arrayList.removeAll(this.arrayList);
        this.arrayList.addAll(datas);
        this.arrayList.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreItem(ArrayList<Data> datas){
        this.arrayList.addAll(datas);
        notifyDataSetChanged();
    }

}
