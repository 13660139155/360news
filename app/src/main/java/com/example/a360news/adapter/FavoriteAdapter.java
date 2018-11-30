package com.example.a360news.adapter;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a360news.MyApplication;
import com.example.a360news.NewsDataActivity;
import com.example.a360news.R;
import com.example.a360news.db.FileDatabase;
import com.example.a360news.db.SQLDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.keep.Temp;

import java.util.ArrayList;
import java.util.TreeMap;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 收藏夹的适配器
 * Created by asus on 2018/5/3.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private ArrayList<Data> arrayList;
    private TreeMap<String, Bitmap> treeMapBitmap = new TreeMap<>();
    private boolean IS_VISIBLE = false;
    private SQLDatabase sqlDatabase = new SQLDatabase(MyApplication.getContext(), "Store", null, 1);

    public FavoriteAdapter(ArrayList<Data> arrayList, TreeMap<String, Bitmap> treeMapBitmap){
        this.arrayList = arrayList;
        this.treeMapBitmap = treeMapBitmap;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView dataTextView;
        TextView editorTextView;
        TextView viewTextView;
        ImageView dataImageView;
        TextView deleteTextView;
        RelativeLayout relativeLayout;

        public ViewHolder(View view){
            super(view);

            dataTextView = (TextView)view.findViewById(R.id.text_view_title);
            editorTextView = (TextView)view.findViewById(R.id.text_view_editor);
            viewTextView = (TextView)view.findViewById(R.id.text_view_viewCount);
            dataImageView = (ImageView)view.findViewById(R.id.image_view_news_image);
            deleteTextView = (TextView)view.findViewById(R.id.text_view_delete);
            relativeLayout = (RelativeLayout)view.findViewById(R.id.relative_layout_delete);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item_layout, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data data = arrayList.get(viewHolder.getAdapterPosition());
                if(v.getId() == R.id.text_view_delete){
                    //删除
                    arrayList.remove(viewHolder.getAdapterPosition());
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                    notifyDataSetChanged();

                    Temp.treeMapData.remove(data.getNewsId());
                    Temp.treeMapBitmap.remove(data.getNewsImageUrls().get(0));
                    deleteFromSQL("NewsId", "newsId == ?", data.getNewsId());
                    deleteFromSQL("ImageUrl", "imageUrl == ?", data.getNewsImageUrls().get(0));
                    Temp.imageUrl.remove(data.getNewsImageUrls().get(0));
                    Temp.dataListId.remove(data.getNewsId());
                    FileDatabase.deleteData(data.getNewsId());
                    FileDatabase.deleteBitmap(data.getNewsImageUrls().get(0));
                }
            }
        });
        viewHolder.dataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data data = arrayList.get(viewHolder.getAdapterPosition());
                if(v.getId() == R.id.text_view_title){
                   NewsDataActivity.actionStart(v.getContext(), data);
                    Temp.IS_DATAACTIVITY = 1;
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Data data = arrayList.get(position);
        if(!IS_VISIBLE){
            holder.relativeLayout.setVisibility(View.GONE);
        } else {
            holder.relativeLayout.setVisibility(View.VISIBLE);
        }
        holder.editorTextView.setText(data.getNewsPublisher());
        holder.dataTextView.setText(data.getNewsTitle());
        holder.viewTextView.setText(data.getNewsViewCount() + "评");
        Bitmap bitmap = treeMapBitmap.get(data.getNewsImageUrls().get(0));
        holder.dataImageView.setImageResource(R.drawable.launch);
        if(bitmap != null){
            holder.dataImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // 添加数据
    public void addData(int position, Data data) {
    //在list中添加数据，并通知条目加入一条
        arrayList.add(position, data);
        //添加动画
        notifyItemInserted(position);
       // notifyDataSetChanged();
    }

    // 删除数据
    public void removeData(int position) {
        arrayList.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    /**
     * 标记
     * @param bool
     */
    public void tag(boolean bool){
        this.IS_VISIBLE = bool;
        notifyDataSetChanged();
    }

    /**
     * 从数据库中删除数据
     * @param bookName 表名
     * @param deleteData 要删除的数据
     */
    private void deleteFromSQL(String bookName, String where, String deleteData){
        SQLiteDatabase db = sqlDatabase.getWritableDatabase();
        db.delete(bookName, where, new String[]{deleteData});
    }
}
