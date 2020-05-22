package com.example.receiptsbooks.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;

import butterknife.BindView;

public class SettingFragment extends BaseFragment {

    @BindView(R.id.setting_rv_content)
    public RecyclerView mContentList;

    @Override
    protected void initView(View rootView) {
        //添加布局管理器
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        //创建适配器

        //添加
//        mContentList.setAdapter();
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_setting;
    }
}
