package com.example.a360news.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.a360news.FavoriteActivity;
import com.example.a360news.R;

/**
 * Created by asus on 2018/5/1.
 */

public class MineFragment extends Fragment implements View.OnClickListener{

    LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_frag, container, false);
        linearLayout = (LinearLayout)view.findViewById(R.id.linear_layout_keep);
        linearLayout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.linear_layout_keep:
                FavoriteActivity.actionStart(getActivity());
                break;
            default:
                break;
        }
    }
}
