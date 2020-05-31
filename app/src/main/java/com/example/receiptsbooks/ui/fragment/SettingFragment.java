package com.example.receiptsbooks.ui.fragment;

import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.ui.activity.IMainActivity;
import com.example.receiptsbooks.ui.activity.MainActivity;
import com.example.receiptsbooks.ui.adapter.SettingContentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SettingFragment extends BaseFragment implements SettingContentAdapter.OnSettingContentItemClickListener {

    private List<String> mDataContent = new ArrayList<>();
    private List<Integer> mDataIcon = new ArrayList<>();

    @BindView(R.id.setting_rv_content)
    public RecyclerView mContentList;
    private SettingContentAdapter mContentAdapter;

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        //添加布局管理器
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        //创建适配器
        mContentAdapter = new SettingContentAdapter();
        mDataIcon.add(R.mipmap.budget_icon);
        mDataIcon.add(R.mipmap.chart_icon);
        mDataContent.add("预算中心");
        mDataContent.add("图表分析");
        //添加
        mContentList.setAdapter(mContentAdapter);

        //设置数据
        mContentAdapter.setData(mDataIcon,mDataContent);
    }

    @Override
    protected void initListener() {
        mContentAdapter.setOnSettingContentItemClickListener(this);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onSettingItemClick(int position) {
        FragmentActivity activity = getActivity();
        if (activity instanceof IMainActivity) {
            if (position == 0){
                ((MainActivity)activity).switch2BudgetCenter();
            }else if (position == 1){
                ((MainActivity)activity).switch2ChartAnalysis();
            }
        }

    }
}
