package com.example.a360news.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.a360news.R;

import java.util.ArrayList;

/**
 * 图片适配器
 * Created by ASUS on 2018/6/2.
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter <ImageRecyclerAdapter.MyViewHolder>{

    private ArrayList<Bitmap> mBitmaps;
    private ArrayList<String> mBitmapsUrl;

    public ImageRecyclerAdapter(ArrayList<Bitmap> bitmaps, ArrayList<String> bitmapsUrl) {
        this.mBitmaps = bitmaps;
        this.mBitmapsUrl = bitmapsUrl;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Bitmap bitmap = mBitmaps.get(position);
        String url = mBitmapsUrl.get(position);
        holder.imageView.setTag(url);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return mBitmaps.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.image_view_news_image);
        }
    }

    public void addImage(Bitmap bitmap){
    }
}
