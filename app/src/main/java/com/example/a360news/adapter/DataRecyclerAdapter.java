package com.example.a360news.adapter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a360news.MyApplication;
import com.example.a360news.NewsDataActivity;
import com.example.a360news.R;
import com.example.a360news.db.FileDatabase;
import com.example.a360news.json.Data;
import com.example.a360news.keep.Temp;
import com.example.a360news.unit.HttpUnit;
import com.example.a360news.unit.ImageLoader;
import com.example.a360news.unit.Unitity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * 新闻列表的RecyclerView的Adapter
 * Created by asus on 2018/4/26.
 */

//上拉加载动画
public class DataRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Data> dataList = new ArrayList<>();

    private TreeMap<String, Bitmap> treeMapBitmap;

    private RecyclerView recyclerView;

    private ImageLoader imageLoader;

    //1,加入布局状态标志-用来判断此时加载是普通Item还是foot view
    private static final int ITEM_TYPE = 0;//普通item
    private static final int FOOTER_TYPE = 1;//footer item
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int  LOADING_MORE = 1;
    //没有更多数据
    public static final int NO_MORE_DATA = 2;
    //上拉加载更多状态-默认为0
    private int load_more_status = PULLUP_LOAD_MORE;

    public DataRecyclerAdapter(List<Data> list){
        this.dataList = list;
        treeMapBitmap = new TreeMap<>();
        imageLoader = ImageLoader.getInstance();
    }

    //4.接着onCreateViewHolder(ViewGroup parent,int viewType)加载布局的时候根据viewType的类型来选择指定的布局创建，返回即可
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE){
            recyclerView = (RecyclerView)parent;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item_layout, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.dataTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = viewHolder.getAdapterPosition();
                    Data data = dataList.get(pos);
                    NewsDataActivity.actionStart(v.getContext(), data);
                }
            });
            return viewHolder;
        }else if(viewType == FOOTER_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_layout, parent, false);
            FootViewHolder footViewHolder = new FootViewHolder(view);
            return footViewHolder;
        }
       return  null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position){
        if(holder instanceof ViewHolder){
            final Data data = dataList.get(position);
            String url = data.getNewsImageUrls().get(0);
            ((ViewHolder)holder).editorTextView.setText(data.getNewsPublisher());
            ((ViewHolder)holder).dataTextView.setText(data.getNewsTitle());
            ((ViewHolder)holder).viewTextView.setText(data.getNewsViewCount() + "评");
            //先设置图片占位符
            ((ViewHolder)holder).dataImageView.setImageResource(R.drawable.launch);
            //为imageView设置Tag,内容是该imageView等待加载的图片url
            ((ViewHolder)holder).dataImageView.setTag(url);
            //Bitmap bitmap = treeMapBitmap.get(data.getNewsImageUrls().get(0));
            Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(data.getNewsImageUrls().get(0));
            Bitmap bitmap1 = FileDatabase.loadBitmap(data.getNewsImageUrls().get(0));
            if(bitmap != null){
                ((ViewHolder)holder).dataImageView.setImageBitmap(bitmap);
            }else if (bitmap1 != null){
                ((ViewHolder)holder).dataImageView.setImageBitmap(bitmap1);
            } else{
                new AsyncTask<String, Integer, Bitmap>() {
                    private String url;
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        url = params[0];
                        Bitmap bitmap = HttpUnit.getOneImageBitmap(url);
                        if(bitmap != null){
                            Temp.bitmapUrl.add(url);
                            //treeMapBitmap.put(url, bitmap);
                            imageLoader.addBitmapToMemoryCache(url, bitmap);
                            FileDatabase.saveBitmap(url, bitmap);
                        }
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        ImageView imageView = (ImageView)recyclerView.findViewWithTag(url);
                        if(imageView != null && bitmap != null){
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }.execute(url);
            }
        }else if(holder instanceof FootViewHolder){
            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    ((FootViewHolder)holder).footProgressBar.setVisibility(View.GONE);
                    ((FootViewHolder)holder).footTextView.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    ((FootViewHolder)holder).footProgressBar.setVisibility(View.VISIBLE);
                    ((FootViewHolder)holder).footTextView.setText("正在加载...");
                    break;
                case NO_MORE_DATA:
                    ((FootViewHolder)holder).footProgressBar.setVisibility(View.GONE);
                    ((FootViewHolder)holder).footTextView.setText("没有更多数据了");
                    break;
                default:
                    break;
            }
        }

    }

    //2,重写getItemCount()方法,返回的Item数量在数据的基础上面+1，增加一项FootView布局项
    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    //3.重写getItemViewType方法来判断返回加载的布局的类型
    @Override
    public int getItemViewType(int position) {
        //最后一个item设置为footerView
        if(position + 1 == getItemCount()){
            return FOOTER_TYPE;
        }else{
            return ITEM_TYPE;
        }
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void changeMoreStatus(int status){
        load_more_status = status;
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<Data> datas){
        ArrayList<Data> list = new ArrayList<>();
        list.addAll(this.dataList);
        this.dataList.removeAll(this.dataList);
        this.dataList.addAll(datas);
        this.dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreItem(ArrayList<Data> datas){
        this.dataList.addAll(datas);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView dataTextView;
        TextView editorTextView;
        TextView viewTextView;
        ImageView dataImageView;

        public ViewHolder(View view){
            super(view);
            dataTextView = (TextView)view.findViewById(R.id.text_view_title);
            editorTextView = (TextView)view.findViewById(R.id.text_view_editor);
            viewTextView = (TextView)view.findViewById(R.id.text_view_viewCount);
            dataImageView = (ImageView)view.findViewById(R.id.image_view_news_image);
        }
    }

    /**
     * 底部FootView布局
     */
    class FootViewHolder extends  RecyclerView.ViewHolder{
        private TextView footTextView;
        private ProgressBar footProgressBar;

        public FootViewHolder(View view) {
            super(view);
            footTextView = (TextView)view.findViewById(R.id.text_view_footer);
            footProgressBar = (ProgressBar)view.findViewById(R.id.load_more_progress_bar);
        }

    }
}
